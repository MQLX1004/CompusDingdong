package dingdong.com.mql.www.compusdingdong.activity.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;
import dingdong.com.mql.www.compusdingdong.R;
import dingdong.com.mql.www.compusdingdong.activity.LoginActivity;
import dingdong.com.mql.www.compusdingdong.activity.MainActivity;
import dingdong.com.mql.www.compusdingdong.activity.MyActivityActivity;
import dingdong.com.mql.www.compusdingdong.activity.MySeatActivity;
import dingdong.com.mql.www.compusdingdong.activity.RegisterActivity;
import dingdong.com.mql.www.compusdingdong.activity.ReleaseActivity;
import dingdong.com.mql.www.compusdingdong.activity.ReleaseMealActivity;
import dingdong.com.mql.www.compusdingdong.activity.UserSetActivity;
import dingdong.com.mql.www.compusdingdong.activity.bean.MyUser;
import dingdong.com.mql.www.compusdingdong.activity.tools.BitmapCbyte;
import dingdong.com.mql.www.compusdingdong.activity.tools.ImgUtil;
import dingdong.com.mql.www.compusdingdong.activity.view.MyEditText;


public class MeFragment extends Fragment {
    private static final String TAG = "MeFragment";

    private TextView mloginAndName;
    private TextView mReleaseActivity;
    private TextView mMySeat;
    private TextView mActivity;
    private TextView mReleaseMeal;
    private CircleImageView mImageView;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public MeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeFragment newInstance(String param1, String param2) {
        MeFragment fragment = new MeFragment();
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
        View v = inflater.inflate(R.layout.fragment_me, container, false);
        // Inflate the layout for this fragment

        EventBus.getDefault().register(this);//注册

        initView(v);
        if (MainActivity.isNetworkConnected(getActivity())){
            if (BmobUser.getCurrentUser(MyUser.class) != null){
                setNameAndHead();//设置用户名和头像
            }else{
                setDefalutNameAndHead();//设置默认的用户名和头像
            }
        }else {
            setDefalutNameAndHead();//设置默认的用户名和头像
        }
        initClick();
        return v;
    }

    private void initView(View view){
        mImageView = (CircleImageView)view.findViewById(R.id.Me_CI_user);
        mloginAndName = (TextView) view.findViewById(R.id.Me_TV_yonghuminbg);
        mReleaseActivity = (TextView)view.findViewById(R.id.Me_TV_releaseactivity);
        mMySeat = (TextView)view.findViewById(R.id.Me_TV_myseat);
        mActivity = (TextView)view.findViewById(R.id.Me_TV_myactivity);
        mReleaseMeal = (TextView)view.findViewById(R.id.Me_TV_releasemeal);
    }
    private void initClick(){
        mloginAndName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isNetworkConnected(getActivity())){
                    //判断当前是否有用户
                    if (BmobUser.getCurrentUser(MyUser.class) == null){
                        Intent intent= LoginActivity.newIntent(getActivity());
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    }else{
                        Intent intent= UserSetActivity.newIntent(getActivity());
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    }
                }else {
                    Toast.makeText(getActivity(),"网络未连接",Toast.LENGTH_SHORT).show();
                }

            }
        });
        mReleaseActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isNetworkConnected(getActivity())){
                    if (MainActivity.isHaveCurrentUser()){
                        Intent intent= ReleaseActivity.newIntent(getActivity());
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    }else {
                        Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(),"网络未连接",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mMySeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isNetworkConnected(getActivity())){
                    if (MainActivity.isHaveCurrentUser()){
                        Intent intent= MySeatActivity.newIntent(getActivity());
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    }else {
                        Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(),"网络未连接",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isNetworkConnected(getActivity())){
                    if (MainActivity.isHaveCurrentUser()){
                        Intent intent= MyActivityActivity.newIntent(getActivity());
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    }else {
                        Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(),"网络未连接",Toast.LENGTH_SHORT).show();
                }

            }
        });
        mReleaseMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isNetworkConnected(getActivity())){
                    if (MainActivity.isHaveCurrentUser()){
                        Intent intent= ReleaseMealActivity.newIntent(getActivity());
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    }else {
                        Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(),"网络未连接",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    //设置当前用户的用户名和头像
    private void setNameAndHead(){
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        mloginAndName.setText(user.getNickname());
        setPictureFromUser(user);
//        mImageView.setBackground(ImgUtil.bitmapToDrawable(getActivity(), BitmapCbyte.getBitmap(bytes)));
        Log.d(TAG, "setNameAndHead: "+user.getHeadPicture().getFileUrl());
    }
    private void setPictureFromUser(final MyUser user){
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = ImgUtil.getImageFromURL(user.getHeadPicture().getFileUrl());
                Bitmap bitmap = BitmapCbyte.getBitmap(bytes);
                setHeadPicture(bitmap);
            }
        }).start();
    }

    private void setHeadPicture(final Bitmap bitmap){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageView.setImageBitmap(bitmap);
            }
        });
    }

    //设置当前的用户名和头像
    private void setDefalutNameAndHead(){
        mloginAndName.setText("登录");
        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.dingdong));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(String messageEvent){
        if (messageEvent.equals(LoginActivity.LoginSuccess)){
            setNameAndHead();
        }if (messageEvent.equals(UserSetActivity.EixtLogin)){
            setDefalutNameAndHead();
        }
    }
}
