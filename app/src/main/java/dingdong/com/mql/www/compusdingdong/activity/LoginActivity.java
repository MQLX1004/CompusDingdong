package dingdong.com.mql.www.compusdingdong.activity;

import android.app.ActivityOptions;
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
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import dingdong.com.mql.www.compusdingdong.R;
import dingdong.com.mql.www.compusdingdong.activity.bean.MyUser;
import dingdong.com.mql.www.compusdingdong.activity.view.MyEditText;

public class LoginActivity extends AppCompatActivity {
    public static final String LoginSuccess = "login_success";

    private TextView mRegisterTextView;
    private ImageButton mBack;
    private MyEditText mAccount;
    private MyEditText mPassword;
    private Button mLoginButton;

    public static Intent newIntent(Context context){
        Intent intent=new Intent(context,LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setExitTransition(new Slide(Gravity.RIGHT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
            layoutParams.flags=(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|layoutParams.flags);
        }

        initView();
        initLinsiter();
    }
    private void initView(){
        mRegisterTextView = (TextView)findViewById(R.id.Login_TV_register);
        mBack = (ImageButton)findViewById(R.id.fanhui_button);
        mAccount = (MyEditText)findViewById(R.id.Login_ET_account);
        mPassword = (MyEditText)findViewById(R.id.Login_ET_password);
        mLoginButton = (Button)findViewById(R.id.Login_Btn_login_button);
    }
    private void initLinsiter(){
        mRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= RegisterActivity.newIntent(LoginActivity.this);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckText()){
                    MyUser user = new MyUser();
                    user.setUsername(mAccount.getText().toString());
                    user.setPassword(mPassword.getText().toString());
                    user.login(new SaveListener<MyUser>() {
                        @Override
                        public void done(MyUser myUser, BmobException e) {
                            if (e == null){
                                PostLoginSuccess();
                                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(LoginActivity.this,"登录失败，请检查邮箱或密码",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private void PostLoginSuccess(){
        new Thread() {
            @Override
            public void run() {
                //发送事件
                EventBus.getDefault().post(LoginSuccess);
            }
        }.run();
    }
    private boolean CheckText(){
        if (mAccount.getText().toString().equals("")){
            Toast.makeText(LoginActivity.this,"请输入邮箱",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mPassword.getText().toString().equals("")){
            Toast.makeText(LoginActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
