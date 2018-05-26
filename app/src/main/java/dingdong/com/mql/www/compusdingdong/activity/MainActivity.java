package dingdong.com.mql.www.compusdingdong.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import dingdong.com.mql.www.compusdingdong.R;
import dingdong.com.mql.www.compusdingdong.activity.bean.MyUser;
import dingdong.com.mql.www.compusdingdong.activity.fragment.AssociationFragment;
import dingdong.com.mql.www.compusdingdong.activity.fragment.LibraryFragment;
import dingdong.com.mql.www.compusdingdong.activity.fragment.MeFragment;
import dingdong.com.mql.www.compusdingdong.activity.fragment.RestaurantFragment;

public class MainActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;//Fragment管理器
    private BottomNavigationView mBottomNavigationView;//底部菜单栏
    private AssociationFragment mAssociationFragment;//社团活动Fragment
    private LibraryFragment mLibraryFragment;//图书馆Fragment
    private MeFragment mMeFragment;//我的Fragment
    private RestaurantFragment mRestaurantFragment;//餐厅Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "825ca61fced1407a3d64d66b9049d772");//初始化bmob
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
            layoutParams.flags=(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|layoutParams.flags);
        }

        mAssociationFragment = AssociationFragment.newInstance(null,null);
        mLibraryFragment = LibraryFragment.newInstance(null,null);
        mMeFragment = MeFragment.newInstance(null,null);
        mRestaurantFragment = RestaurantFragment.newInstance(null,null);
        initView();
        setBottomBar();
    }
    private void initView(){
        //实例化底部菜单栏控件
        mBottomNavigationView=(BottomNavigationView)findViewById(R.id.Main_BNV_bottom);
        //获取Frangment管理器
        mFragmentManager=getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .add(R.id.Main_FL_content,mMeFragment)
                .add(R.id.Main_FL_content,mLibraryFragment)
                .add(R.id.Main_FL_content,mRestaurantFragment)
                .add(R.id.Main_FL_content,mAssociationFragment)
                .commit();
    }
    private void setBottomBar(){
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction=getSupportFragmentManager()
                        .beginTransaction();//碎片管理器创建一个事务
                hideAllFragment(transaction);//隐藏所有碎片
                //根据点击的菜单选择显示的Fragment
                switch (item.getItemId()){
                    case R.id.ME:
                        if (mMeFragment==null){
                            mMeFragment = MeFragment.newInstance(null,null);
                            transaction.add(R.id.Main_FL_content,mMeFragment);
                        }else{
                            transaction.show(mMeFragment);
                        }
                        break;
                    case R.id.ST:
                        if (mAssociationFragment == null){
                            mAssociationFragment = AssociationFragment.newInstance(null,null);
                            transaction.add(R.id.Main_FL_content,mAssociationFragment);
                        }else{
                            transaction.show(mAssociationFragment);
                        }
                        break;
                    case R.id.CT:
                        if (mRestaurantFragment==null){
                            mRestaurantFragment = RestaurantFragment.newInstance(null,null);
                            transaction.add(R.id.Main_FL_content,mRestaurantFragment);
                        }else{
                            transaction.show(mRestaurantFragment);
                        }
                        break;
                    case R.id.TSG:
                        if (mLibraryFragment == null){
                            mLibraryFragment = LibraryFragment.newInstance(null,null);
                            transaction.add(R.id.Main_FL_content,mLibraryFragment);
                        }else{
                            transaction.show(mLibraryFragment);
                        }
                        break;
                }
                transaction.commit();
                return true;
            }
        });
    }
    /**
     * 隐藏所有Fragment
     * @param transaction
     */
    private void hideAllFragment(FragmentTransaction transaction){
        if (mAssociationFragment!=null){
            transaction.hide(mAssociationFragment);
        }
        if (mMeFragment != null){
            transaction.hide(mMeFragment);
        }
        if (mLibraryFragment!=null){
            transaction.hide(mLibraryFragment);
        }
        if (mRestaurantFragment != null){
            transaction.hide(mRestaurantFragment);
        }
    }

    /**
     * 判断当前是否有用户
     * @return
     */
    public static boolean isHaveCurrentUser(){
        if (BmobUser.getCurrentUser(MyUser.class) != null){
            return true;
        }
        return false;
    }

    /**
     * 判断网络是否已连接
     */
    public static boolean isNetworkConnected(Context context){
        ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getActiveNetworkInfo()!=null){
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

}
