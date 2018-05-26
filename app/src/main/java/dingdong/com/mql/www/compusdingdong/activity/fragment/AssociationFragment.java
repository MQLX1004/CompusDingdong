package dingdong.com.mql.www.compusdingdong.activity.fragment;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import dingdong.com.mql.www.compusdingdong.activity.adapter.AssociationListAdapter;
import dingdong.com.mql.www.compusdingdong.activity.bean.ActivityBean;
import dingdong.com.mql.www.compusdingdong.activity.bean.DbActivityBean;
import dingdong.com.mql.www.compusdingdong.activity.database.DatabaseHelper;
import dingdong.com.mql.www.compusdingdong.activity.databasedao.DbActivityDao;
import dingdong.com.mql.www.compusdingdong.activity.tools.ImgUtil;


public class AssociationFragment extends Fragment {
    private static final String TAG = "AssociationFragment";
    public static boolean isLoadMore;//是否还能加载更多的标志，true可以加载更多，false没有更多了

    private DbActivityDao mDbActivityDao;//数据表操作控制对象
    private RecyclerView mRecycleView;
    private AssociationListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;//下拉刷新控件
    private int mCurrentPage;//当前显示的活动页码
    private List<DbActivityBean> mActivityBeanList;//当前显示在页面中的Activity集合
    private List<DbActivityBean> mNewLoadBeanList;//上拉加载后，新加载进来的数据



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AssociationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AssociationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AssociationFragment newInstance(String param1, String param2) {
        AssociationFragment fragment = new AssociationFragment();
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
        View v = inflater.inflate(R.layout.fragment_association, container, false);
        initView(v);
        initLinister();
        loadDataFromDatabase();//从数据库加载（缓存）数据
        if (MainActivity.isNetworkConnected(getActivity())){
            getActivityData();//从Bmob获取（最新）数据
        }else {
            Toast.makeText(getActivity(),"网络未连接",Toast.LENGTH_SHORT).show();
        }
        return v;
    }
    private void initView(View v){
        mCurrentPage = 1;
        isLoadMore = true;
        mNewLoadBeanList = new ArrayList<>();
        mDbActivityDao = new DbActivityDao(getActivity());
        mRecycleView = (RecyclerView)v.findViewById(R.id.Association_RV_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.Association_SRL_refresh);
    }
    private void initLinister(){
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//下拉刷新后开始的操作
                mCurrentPage = 1;
                if (MainActivity.isNetworkConnected(getActivity())){
                    getActivityData();//从bmob获取数据并刷新
                    isLoadMore = true;
                }else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(),"网络未连接",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //监听如果滑动到Footer，则加载更多
        mRecycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSlidingToLast=false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager=(LinearLayoutManager)recyclerView.getLayoutManager();
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    int ladtVisibleItem=manager.findLastCompletelyVisibleItemPosition();
                    int totolItemCount=manager.getItemCount();
                    if(ladtVisibleItem==(totolItemCount-1)&&isSlidingToLast){
                        if (isLoadMore){
                            if (MainActivity.isNetworkConnected(getActivity())){
                                getActivityMore();//获取更多Activity
                            }
                            else {
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

    /**
     * 用于上拉加载更多，从bmob获取更多数据
     */
    private void getActivityMore(){
        BmobQuery<ActivityBean> query = new BmobQuery<ActivityBean>();
        query.setLimit(10);
        query.setSkip(10 * mCurrentPage);
        mCurrentPage = mCurrentPage+1;
        query.findObjects(new FindListener<ActivityBean>() {
            @Override
            public void done(List<ActivityBean> list, BmobException e) {
                if (e == null){
                    if (list.size() == 0){//如果获取的数等于0，那么说明下次没有数据可以获取了
                        isLoadMore = false;
                    }
                    activitybeanToDbactivity(list);
                    addMaroDataToRecycleView();
                }else {
                    Toast.makeText(getActivity(),"获取数据失败，请重试",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "done: 加载更多"+e.getErrorCode());
                }
            }
        });
    }

    private void addMaroDataToRecycleView(){
        mActivityBeanList.addAll(mNewLoadBeanList);//将数据添加到
        mAdapter.notifyDataSetChanged();
    }



    /**
     * 从bmob获取最新数据
     */
    private void getActivityData(){
        if (!mSwipeRefreshLayout.isRefreshing()){//如果没有刷新效果，开启刷新效果
            mSwipeRefreshLayout.setRefreshing(true);
        }
        BmobQuery<ActivityBean> query = new BmobQuery<ActivityBean>();//创建bmob查询对象
        query.setLimit(10);//设置最多查询行数
        query.findObjects(new FindListener<ActivityBean>() {
            @Override
            public void done(List<ActivityBean> list, BmobException e) {
                if (e == null){
                    addDatabase(list);//获取查询数据后，更新数据库
                }else {
                    Toast.makeText(getActivity(),"获取数据失败，请重试",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "done: "+e.getErrorCode());
                }
            }
        });
    }

    /**
     * 设置RecycleView
     */
    private void setRecycleView(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
                mAdapter = new AssociationListAdapter(getActivity(),mActivityBeanList);
                mRecycleView.setAdapter(mAdapter);
                setFooterView(mRecycleView);//为RecycleView设置Footer
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 从数据库加载数据
     */
    private void loadDataFromDatabase(){
        QueryBuilder<DbActivityBean , String> queryBuilder = mDbActivityDao.getqueryQuery();//创建查询对象
        queryBuilder.orderBy("activityTime",false);//按开始时间排序
        try {
            mActivityBeanList = queryBuilder.query();//查询
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setRecycleView();//从数据库加载数据后，设置RecycleView
    }

    /**
     * 将ActivityBean转化为DbActivityBean
     * @param list
     */
    private void activitybeanToDbactivity(final List<ActivityBean> list){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mNewLoadBeanList.clear();
                for (ActivityBean activitybean : list) {
                    byte[] bmobfilebyte = null;
                    if (activitybean.getActivitypicture() != null){
                        BmobFile bmobfile = activitybean.getActivitypicture();//获取图片BmobFile
                        bmobfilebyte = ImgUtil.getImageFromURL(bmobfile.getFileUrl());//获取图片二进制数组
                    }
                    //创建数据库表对应对象
                    DbActivityBean Dbbean = new DbActivityBean(
                            activitybean.getObjectId(),
                            activitybean.getCreatuser(),
                            activitybean.getActivitytime(),
                            activitybean.getActivitysite(),
                            activitybean.getActivityname(),
                            activitybean.getActivityinterduction(),
                            bmobfilebyte
                    );
                    mNewLoadBeanList.add(Dbbean);
                }
            }
        }).start();

    }

    /**
     * 向数据库中添加数据（更新为最新数据，用作缓存）
     * @param list
     */
    private void addDatabase(final List<ActivityBean> list){
        new Thread(new Runnable() {//存储数据库耗时较长，开启子线程，否则引发9015
            @Override
            public void run() {//开启子线程
                mDbActivityDao.deleteAll();//清空表
                //for循环从list中取出数据
                for (ActivityBean activitybean : list) {
                    byte[] bmobfilebyte = null;
                    if (activitybean.getActivitypicture() != null){
                        BmobFile bmobfile = activitybean.getActivitypicture();//获取图片BmobFile
                        bmobfilebyte = ImgUtil.getImageFromURL(bmobfile.getFileUrl());//获取图片二进制数组
                    }
                    //创建数据库表对应对象
                    DbActivityBean Dbbean = new DbActivityBean(
                            activitybean.getObjectId(),
                            activitybean.getCreatuser(),
                            activitybean.getActivitytime(),
                            activitybean.getActivitysite(),
                            activitybean.getActivityname(),
                            activitybean.getActivityinterduction(),
                            bmobfilebyte
                    );
                    mDbActivityDao.add(Dbbean);//添加到数据库
                }
                loadDataFromDatabase();//更新数据库完毕，重新从数据库加载数据
            }
        }).start();
    }

    /**
     * 为RecycleViw设置Footer
     * @param view
     */
    private void setFooterView(RecyclerView view){
        View footer = LayoutInflater.from(getActivity()).inflate(R.layout.item_listfooter, view, false);
        mAdapter.setFooterView(footer);
    }
}
