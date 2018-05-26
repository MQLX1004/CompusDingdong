package dingdong.com.mql.www.compusdingdong.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import dingdong.com.mql.www.compusdingdong.R;
import dingdong.com.mql.www.compusdingdong.activity.bean.MyUser;
import dingdong.com.mql.www.compusdingdong.activity.tools.ImgUtil;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    public static final int CHOOSE_PHOTO = 2;
    private ImageButton mBack;
    private CircleImageView mHeadPicture;
    private TextView mUserName;
    private TextView mAccount;
    private TextView mPassword;
    private TextView mPasswordSure;
    private Button mRegister;
    private BmobFile mBmobFile = null;

    public static Intent newIntent(Context context){
        Intent intent=new Intent(context,RegisterActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setExitTransition(new Slide(Gravity.RIGHT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
            layoutParams.flags=(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|layoutParams.flags);
        }
        initView();
        initLinister();
    }
    private void initView(){
        mBack = (ImageButton)findViewById(R.id.fanhui_button);
        mHeadPicture = (CircleImageView) findViewById(R.id.Register_CI_userpicture);
        mAccount = (TextView)findViewById(R.id.Register_ET_account);
        mPassword = (TextView)findViewById(R.id.Register_ET_password);
        mPasswordSure = (TextView)findViewById(R.id.Register_ET_passwordsuer);
        mUserName = (TextView)findViewById(R.id.Register_ET_username);
        mRegister = (Button)findViewById(R.id.Register_Btn_register_button);
    }
    private void initLinister(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mHeadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },1);
                }else{
                    openAlbum();
                }
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ChackText()){
                    MyUser user = new MyUser();
                    if (mBmobFile != null){
                        user.setHeadPicture(mBmobFile);
                    }
                    user.setNickname(mUserName.getText().toString());
                    user.setUsername(mAccount.getText().toString());
                    user.setEmail(mAccount.getText().toString());
                    user.setPassword(mPassword.getText().toString());
                    user.setPresiden(false);
                    user.signUp(new SaveListener<MyUser>() {
                        @Override
                        public void done(MyUser myUser, BmobException e) {
                            if (e == null){
                                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                            }else{
                                if (e.getErrorCode() == 202){
                                    Toast.makeText(RegisterActivity.this,"该邮箱已经注册",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean ChackText(){
        if (mUserName.getText().toString().equals("")){
            Toast.makeText(RegisterActivity.this,"请输入用户名",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mAccount.getText().toString().equals("")){
            Toast.makeText(RegisterActivity.this,"请输入邮箱",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mPassword.getText().toString().equals("")){
            Toast.makeText(RegisterActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mPassword.getText().toString().equals(mPasswordSure.getText().toString())){
            Toast.makeText(RegisterActivity.this,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(RegisterActivity.this,"没有权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    Bitmap bitmap = null;
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        bitmap = ImgUtil.handleImageOnKitKat(this, data);  //ImgUtil是自己实现的一个工具类
                    } else {
                        //4.4以下系统使用这个方法处理图片
                        bitmap = ImgUtil.handleImageBeforeKitKat(this, data);
                    }
                    mHeadPicture.setImageBitmap(bitmap);
                    Uri uri = data.getData();
                    String path = ImgUtil.getImagePath(RegisterActivity.this,uri,null);
                    UpdataHeadImg(path);
                }
                break;
            default:break;
        }
    }
    private void UpdataHeadImg(String path){
        File file = new File(path);
        mBmobFile = new BmobFile(file);
        mBmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    Toast.makeText(RegisterActivity.this, "上传文件成功:" + mBmobFile.getFileUrl(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegisterActivity.this, "上传文件失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value) {
                //返回上传进度,并显示出来
            }
        });
    }
}
