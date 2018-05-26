package dingdong.com.mql.www.compusdingdong.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import dingdong.com.mql.www.compusdingdong.R;
import dingdong.com.mql.www.compusdingdong.activity.bean.SeatUserBean;

public class MySeatActivity extends AppCompatActivity {
    private static final String TAG = "MySeatActivity";

    private TextView mSeatNumber;
    private Button mCancleSeat;
    private ImageButton mBack;
    private String mCurrentSeatUserId = null;

    public static Intent newIntent(Context context){
        Intent intent=new Intent(context,MySeatActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setExitTransition(new Slide(Gravity.RIGHT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_seat);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
            layoutParams.flags=(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|layoutParams.flags);
        }
        initView();
        initLinister();
        setCurrentSeatNumber();
    }

    private void initView(){
        mBack = (ImageButton)findViewById(R.id.fanhui_button);
        mSeatNumber = (TextView)findViewById(R.id.Myseat_TV_museatnumber);
        mCancleSeat = (Button) findViewById(R.id.Myseat_Btn_cancleseat);
    }
    private void initLinister(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCancleSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentSeatUserId != null){
                    cancleSeat();//取消占座
                }else {

                }
            }
        });
    }

    private void cancleSeat(){
            SeatUserBean b = new SeatUserBean();
            b.setSeatuser("");
            b.update(mCurrentSeatUserId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null){
                        mCurrentSeatUserId = null;
                        mSeatNumber.setText("你还没有座位，快去占一个吧！");
                        Toast.makeText(MySeatActivity.this,"离开成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d(TAG, "done: "+e.getErrorCode());
                    }
                }
            });
    }

    private void setCurrentSeatNumber(){
        BmobQuery<SeatUserBean> beanBmobQuery = new BmobQuery<SeatUserBean>();
        beanBmobQuery.addWhereEqualTo("seatuser", BmobUser.getCurrentUser(SeatUserBean.class).getObjectId());
        beanBmobQuery.findObjects(new FindListener<SeatUserBean>() {
            @Override
            public void done(List<SeatUserBean> list, BmobException e) {
                if (e == null){
                    if (list.size() != 0){
                        mCurrentSeatUserId = list.get(0).getObjectId();
                        mSeatNumber.setText("你当前的座位号是："+list.get(0).getSeatnumber());
                    }else{
                        mSeatNumber.setText("你还没有座位，快去占一个吧！");
                    }
                }else{
                    Log.d(TAG, "done: "+e.getErrorCode());
                }
            }
        });
    }
}
