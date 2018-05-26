package dingdong.com.mql.www.compusdingdong.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import dingdong.com.mql.www.compusdingdong.R;
import dingdong.com.mql.www.compusdingdong.activity.adapter.AssociationListAdapter;
import dingdong.com.mql.www.compusdingdong.activity.adapter.MyActivityAdapter;
import dingdong.com.mql.www.compusdingdong.activity.bean.ActivityBean;
import dingdong.com.mql.www.compusdingdong.activity.bean.DbActivityBean;
import dingdong.com.mql.www.compusdingdong.activity.bean.MyUser;
import dingdong.com.mql.www.compusdingdong.activity.tools.ImgUtil;

public class MyActivityActivity extends AppCompatActivity {
    private static final String TAG = "MyActivityActivity";
    public static boolean isMyActivityLoadMore;//是否还能加载更多的标志，true可以加载更多，false没有更多了

    private ImageButton mBack;
    private RecyclerView mRecycleView;
    private MyActivityAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;//下拉刷新控件
    private int mCurrentPage;//当前显示的活动页码
    private List<DbActivityBean> mActivityBeanList;//当前显示在页面中的Activity集合
    private List<DbActivityBean> mNewLoadBeanList;//上拉加载后，新加载进来的数据
    private int mTheFiretOpen;
    private TextView mTextViewNoneActivity;

    public static Intent newIntent(Context context){
        Intent intent=new Intent(context,MyActivityActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setExitTransition(new Slide(Gravity.RIGHT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activity);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
            layoutParams.flags=(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|layoutParams.flags);
        }
        initView();
        initLinister();
        setRecycleView();
        getActivityData();
    }
    private void initView(){
        EventBus.getDefault().register(this);//注册
        mBack = (ImageButton)findViewById(R.id.fanhui_button);
        mCurrentPage = 1;
        mTheFiretOpen = 1;
        mTextViewNoneActivity = (TextView)findViewById(R.id.Association_TV_noneActivity);
        isMyActivityLoadMore = true;
        mNewLoadBeanList = new ArrayList<>();
        mActivityBeanList = new ArrayList<>();
        mRecycleView = (RecyclerView)findViewById(R.id.Association_RV_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.Association_SRL_refresh);
    }
    private void initLinister(){

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//下拉刷新后开始的操作
                mCurrentPage = 1;
                if (MainActivity.isNetworkConnected(MyActivityActivity.this)){
                    getActivityData();//从bmob获取数据并刷新
                    isMyActivityLoadMore = true;
                }else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MyActivityActivity.this,"网络未连接",Toast.LENGTH_SHORT).show();
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
                        if (isMyActivityLoadMore){
                            if (MainActivity.isNetworkConnected(MyActivityActivity.this)){
                                getActivityMore();//获取更多Activity
                            }
                            else {
                                Toast.makeText(MyActivityActivity.this,"网络未连接",Toast.LENGTH_SHORT).show();
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

    private void getActivityMore(){
        BmobQuery<ActivityBean> query = new BmobQuery<ActivityBean>();
        query.setLimit(10);
        query.addWhereEqualTo("creatuser", BmobUser.getCurrentUser(MyUser.class).getObjectId());
        query.setSkip(10 * mCurrentPage);
        mCurrentPage = mCurrentPage+1;
        query.findObjects(new FindListener<ActivityBean>() {
            @Override
            public void done(List<ActivityBean> list, BmobException e) {
                if (e == null){
                    if (list.size() == 0){//如果获取的数等于0，那么说明下次没有数据可以获取了
                        isMyActivityLoadMore = false;
                    }
                    activitybeanToDbactivity(list);
                }else {
                    Toast.makeText(MyActivityActivity.this,"获取数据失败，请重试",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "done: 加载更多"+e.getErrorCode());
                }
            }
        });
    }

    private void getActivityData(){
        if (!mSwipeRefreshLayout.isRefreshing()){//如果没有刷新效果，开启刷新效果
            mSwipeRefreshLayout.setRefreshing(true);
        }
        mActivityBeanList.clear();
        BmobQuery<ActivityBean> query = new BmobQuery<ActivityBean>();//创建bmob查询对象
        query.setLimit(10);//设置最多查询行数
        query.addWhereEqualTo("creatuser", BmobUser.getCurrentUser(MyUser.class).getObjectId());
        query.findObjects(new FindListener<ActivityBean>() {
            @Override
            public void done(List<ActivityBean> list, BmobException e) {
                if (e == null){
                    Log.d(TAG, "done: "+list.size());
                    if (list.size() == 0 && mTheFiretOpen == 1){//防止出现用户没有发布过活动却出现列表的情况
                            mSwipeRefreshLayout.setVisibility(View.GONE);
                            mTextViewNoneActivity.setVisibility(View.VISIBLE);
                    }else {
                        activitybeanToDbactivity(list);
                        mTheFiretOpen = 2;
                    }
                }else {
                    Toast.makeText(MyActivityActivity.this,"获取数据失败，请重试",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addMaroDataToRecycleView(){
        MyActivityActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityBeanList.addAll(mNewLoadBeanList);//将数据添加到
                mAdapter.notifyDataSetChanged();
                if (mSwipeRefreshLayout.isRefreshing()){//如果有刷新效果
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void setRecycleView(){
        MyActivityActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecycleView.setLayoutManager(new LinearLayoutManager(MyActivityActivity.this));
                mAdapter = new MyActivityAdapter(MyActivityActivity.this,mActivityBeanList);
                mRecycleView.setAdapter(mAdapter);
                setFooterView(mRecycleView);//为RecycleView设置Footer
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void setFooterView(RecyclerView view){
        View footer = LayoutInflater.from(MyActivityActivity.this).inflate(R.layout.item_listfooter, view, false);
        mAdapter.setFooterView(footer);
    }


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
                addMaroDataToRecycleView();
            }
        }).start();
    }

    //事件处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(String messageEvent){
        if (messageEvent.equals("delete")){
            getActivityData();//从bmob获取数据并刷新
            isMyActivityLoadMore = true;
            mCurrentPage = 1;
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
    }
}
