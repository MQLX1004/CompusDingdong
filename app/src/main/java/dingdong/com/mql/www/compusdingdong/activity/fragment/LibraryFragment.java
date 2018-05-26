package dingdong.com.mql.www.compusdingdong.activity.fragment;


import android.content.Intent;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import dingdong.com.mql.www.compusdingdong.R;
import dingdong.com.mql.www.compusdingdong.activity.MainActivity;
import dingdong.com.mql.www.compusdingdong.activity.QRScannerActivity;
import dingdong.com.mql.www.compusdingdong.activity.bean.MyUser;
import dingdong.com.mql.www.compusdingdong.activity.bean.SeatNumberBean;
import dingdong.com.mql.www.compusdingdong.activity.bean.SeatUserBean;


public class LibraryFragment extends Fragment {
    private static final String TAG = "LibraryFragment";
    public static final int ScanRequestCode = 12;

    private FrameLayout mScanFragent;
    private LinearLayout mCountdownLayout;
    private TextView mCountdownText;
    private TextView mSeatNumber;
    private Button mCancelSeat;
    private CountDownTimer timer;
    private String mCurrentSeatNumber;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
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
        EventBus.getDefault().register(this);//注册
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_library, container, false);
        // Inflate the layout for this fragment
        initView(v);
        initLinister();
        return v;
    }

    private void initView(View view){
        mCountdownLayout = (LinearLayout)view.findViewById(R.id.Library_LL_CountDown);
        mSeatNumber = (TextView)view.findViewById(R.id.Library_TV_seatnumber);
        mScanFragent = (FrameLayout) view.findViewById(R.id.Library_FL_Scan);
        mCountdownText = (TextView) view.findViewById(R.id.Library_TV_Daojishi);
        mCancelSeat = (Button)view.findViewById(R.id.Library_Btn_cancel);
        timer = new CountDownTimer(60*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mCountdownText.setText("距离占座成功还有"+millisUntilFinished/1000+"秒");
            }

            @Override
            public void onFinish() {
                querySeatId();
                Toast.makeText(getActivity(),"占座成功，可以到“我的座位”查看",Toast.LENGTH_SHORT).show();
                CancelSeat();
            }
        };
    }

    private void querySeatId(){
        BmobQuery<SeatUserBean> query = new BmobQuery<SeatUserBean>();
        query.addWhereEqualTo("seatnumber",mCurrentSeatNumber);
        query.findObjects(new FindListener<SeatUserBean>() {
            @Override
            public void done(List<SeatUserBean> list, BmobException e) {
                if (e == null){
                    SeatUserBean s = new SeatUserBean();
                    s.setSeatuser(BmobUser.getCurrentUser(MyUser.class).getObjectId());
                    s.update(list.get(0).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                Log.d(TAG, "done: 更新成功");
                            }else {
                                Log.d(TAG, "done: "+e.getErrorCode());
                            }
                        }
                    });
                }else {
                    Log.d(TAG, "done: "+e.getErrorCode());
                }
            }
        });
    }

    private void initLinister(){
        mScanFragent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isNetworkConnected(getActivity())){
                    if (MainActivity.isHaveCurrentUser()){
                        //打开扫描二维码Activity
                        Intent intent= new Intent(getActivity(), QRScannerActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(),"网络未连接",Toast.LENGTH_SHORT).show();
                }

            }
        });
        mCancelSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelSeat();//取消计时
            }
        });
    }

    /**
     * 取消计时
     */
    private void CancelSeat(){
        timer.cancel();
        mScanFragent.setVisibility(View.VISIBLE);
        mCountdownLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
    }

    //事件处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(SeatNumberBean messageEvent){
        if (messageEvent.getSign().equals(QRScannerActivity.SeatNumber)){
            mCurrentSeatNumber = messageEvent.getSeatnumber();
            BmobQuery<SeatUserBean> q1 = new BmobQuery<SeatUserBean>();
            q1.addWhereEqualTo("seatnumber",mCurrentSeatNumber);
            q1.count(SeatUserBean.class, new CountListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null){
                        if (integer != 0){
                            BmobQuery<SeatUserBean> q = new BmobQuery<SeatUserBean>();
                            q.addWhereEqualTo("seatuser",BmobUser.getCurrentUser(MyUser.class).getObjectId());
                            q.count(SeatUserBean.class, new CountListener() {
                                @Override
                                public void done(Integer integer, BmobException e) {
                                    if (e == null){
                                        if (integer == 0){
                                            StartCountDown(mCurrentSeatNumber);
                                        }else {
                                            Toast.makeText(getActivity(),"一个用户只允许占一个座",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(getActivity(),"此座位号不存在",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }
    }
    private void StartCountDown(String seatnumber){
        mScanFragent.setVisibility(View.GONE);
        mCountdownLayout.setVisibility(View.VISIBLE);
        mSeatNumber.setText("当前的座位号为："+seatnumber);
        timer.start();
    }

}
