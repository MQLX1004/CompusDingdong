package dingdong.com.mql.www.compusdingdong.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.v3.BmobUser;
import dingdong.com.mql.www.compusdingdong.R;

public class UserSetActivity extends AppCompatActivity {
    public static final String EixtLogin = "exit_login";

    private ImageButton mBack;
    private Button mExitLogin;

    public static Intent newIntent(Context context){
        Intent intent=new Intent(context,UserSetActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setExitTransition(new Slide(Gravity.RIGHT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_set);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
            layoutParams.flags=(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|layoutParams.flags);
        }

        initView();
        initLinister();

    }

    private void initView(){
        mBack = (ImageButton)findViewById(R.id.fanhui_button);
        mExitLogin = (Button)findViewById(R.id.ExitLogin_Btn_exitlogin_button);
    }
    private void initLinister(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mExitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.logOut();
                PostLogOut();
                Toast.makeText(UserSetActivity.this,"退出登录成功",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void PostLogOut(){
        new Thread() {
            @Override
            public void run() {
                //发送事件
                EventBus.getDefault().post(EixtLogin);
            }
        }.run();
    }
}
