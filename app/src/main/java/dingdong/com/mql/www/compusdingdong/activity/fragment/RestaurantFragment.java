package dingdong.com.mql.www.compusdingdong.activity.fragment;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import dingdong.com.mql.www.compusdingdong.R;
import dingdong.com.mql.www.compusdingdong.activity.MainActivity;
import dingdong.com.mql.www.compusdingdong.activity.adapter.MealListAdapter;
import dingdong.com.mql.www.compusdingdong.activity.bean.DbActivityBean;
import dingdong.com.mql.www.compusdingdong.activity.bean.DbMealBean;
import dingdong.com.mql.www.compusdingdong.activity.bean.MealBean;
import dingdong.com.mql.www.compusdingdong.activity.databasedao.DbMealDao;
import dingdong.com.mql.www.compusdingdong.activity.tools.ImgUtil;


public class RestaurantFragment extends Fragment {
    private static final String TAG = "RestaurantFragment";
    public static Boolean isLoadMealMore;

    private DbMealDao mDbMealDao;
    private RecyclerView mMealRecycleView;
    private MealListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<DbMealBean> mMealBeenList;
    private int mCurrentPage;
    private List<DbMealBean> mNewLoadBeanList;
    private int mTheFirstLoadFragment;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public RestaurantFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RestaurantFragment newInstance(String param1, String param2) {
        RestaurantFragment fragment = new RestaurantFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_restaurant, container, false);
        initView(v);
        initLinister();
        loadDataFromDatabase();//从数据库加载数据
        if (MainActivity.isNetworkConnected(getActivity())){
            getMealDataFromBmob();//从Bmob获数据
        }
        return v;
    }

    private void initView(View view){
        isLoadMealMore = true;
        mCurrentPage = 1;
        mTheFirstLoadFragment = 1;
        mNewLoadBeanList = new ArrayList<>();
        mMealBeenList = new ArrayList<>();
        mDbMealDao = new DbMealDao(getActivity());
        mMealRecycleView = (RecyclerView)view.findViewById(R.id.Meal_RV_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.Meal_SRL_refresh);
    }

    private void initLinister(){
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage = 1;
                if (MainActivity.isNetworkConnected(getActivity())){
                    getMealDataFromBmob();
                    isLoadMealMore = true;
                }else {
                    Toast.makeText(getActivity(),"网络未连接",Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        });
        mMealRecycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSlidingToLast=false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                GridLayoutManager manager=(GridLayoutManager) recyclerView.getLayoutManager();
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    int ladtVisibleItem=manager.findLastCompletelyVisibleItemPosition();
                    int totolItemCount=manager.getItemCount();
                    if(ladtVisibleItem==(totolItemCount-1)&&isSlidingToLast){
                        if (isLoadMealMore){
                            if (MainActivity.isNetworkConnected(getActivity())){
                                getMealMore();//获取更多Activity
                            }else {
                                Toast.makeText(getActivity(),"网络未连接",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0){
                    isSlidingToLast=true;
                }else{
                    isSlidingToLast=false;
                }
            }
        });
    }

    private void getMealMore(){
        BmobQuery<MealBean> query = new BmobQuery<MealBean>();
        query.setLimit(10);
        query.setSkip(10 * mCurrentPage);
        mCurrentPage = mCurrentPage+1;
        query.findObjects(new FindListener<MealBean>() {
            @Override
            public void done(List<MealBean> list, BmobException e) {
                if (e == null){
                    if (list.size() == 0){
                        isLoadMealMore = false;
                    }
                    mealBeanToDbMealBean(list);
                    addMaroDataToRecycleView();
                }else {
                    Toast.makeText(getActivity(),"获取数据失败",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "done: 加载更多"+e.getErrorCode());
                }
            }
        });
    }

    private void addMaroDataToRecycleView(){
        mMealBeenList.addAll(mNewLoadBeanList);
        mAdapter.notifyDataSetChanged();
    }

    private void getMealDataFromBmob(){
        if (!mSwipeRefreshLayout.isRefreshing()){//如果没有刷新效果，开启刷新效果
            mSwipeRefreshLayout.setRefreshing(true);
        }
        BmobQuery<MealBean> query = new BmobQuery<MealBean>();
        query.setLimit(10);
        query.findObjects(new FindListener<MealBean>() {
            @Override
            public void done(List<MealBean> list, BmobException e) {
                if (e == null){
                    Log.d(TAG, "done: "+list.size());
                    addDatabase(list);
                }else{
                    Toast.makeText(getActivity(),"获取数据失败，请重试",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "done: "+e.getErrorCode());
                }
            }
        });
    }

    private void addDatabase(final List<MealBean> beans){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDbMealDao.deleteAll();//清空表
                for (MealBean bean : beans) {
                    byte[] bmobfilebyte = null;
                    if (bean.getMealpicture() != null) {
                        BmobFile bmobfile = bean.getMealpicture();//获取图片BmobFile
                        bmobfilebyte = ImgUtil.getImageFromURL(bmobfile.getFileUrl());//获取图片二进制数组
                    }
                    DbMealBean dbbean = new DbMealBean(
                            bean.getObjectId(),
                            bmobfilebyte,
                            bean.getCreatuser(),
                            bean.getMealname(),
                            bean.getMealsite(),
                            bean.getMealintroduction());
                    mDbMealDao.add(dbbean);
                }
                loadDataFromDatabase();//更新数据库完毕，重新从数据库加载数据
            }
        }).start();
    }

    private void loadDataFromDatabase(){
        mMealBeenList = mDbMealDao.queryForAll();//查询
        setRecycleView();
    }

    private void setRecycleView(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMealRecycleView.setLayoutManager(new GridLayoutManager(getActivity(),2));
                mAdapter = new MealListAdapter(mMealBeenList,getActivity());
                mMealRecycleView.setAdapter(mAdapter);
                setFooterView(mMealRecycleView);//为RecycleView设置Footer
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }


    private void setFooterView(RecyclerView view){
        View footer = LayoutInflater.from(getActivity()).inflate(R.layout.item_listfooter, view, false);
        mAdapter.setFooterView(footer);
    }

    /**
     * 将mealBean转化为DbMealBean
     * @param mealbeens
     */
    private void mealBeanToDbMealBean(final List<MealBean> mealbeens){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mNewLoadBeanList.clear();
                for (MealBean bean: mealbeens) {
                    byte[] bmobfilebyte = null;
                    if (bean.getMealpicture() != null){
                        BmobFile bmobfile = bean.getMealpicture();
                        bmobfilebyte = ImgUtil.getImageFromURL(bmobfile.getFileUrl());//获取图片二进制数组
                    }
                    DbMealBean Dbean = new DbMealBean(
                            bean.getObjectId(),
                            bmobfilebyte,
                            bean.getCreatuser(),
                            bean.getMealname(),
                            bean.getMealsite(),
                            bean.getMealintroduction()
                    );
                    mNewLoadBeanList.add(Dbean);
                }
            }
        }).start();
    }
}
