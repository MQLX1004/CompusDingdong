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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import dingdong.com.mql.www.compusdingdong.R;
import dingdong.com.mql.www.compusdingdong.activity.bean.ActivityBean;
import dingdong.com.mql.www.compusdingdong.activity.bean.MyUser;
import dingdong.com.mql.www.compusdingdong.activity.tools.ImgUtil;
import dingdong.com.mql.www.compusdingdong.activity.view.MyEditText;

public class ReleaseActivity extends AppCompatActivity {
    private static final String TAG = "ReleaseActivity";
    public static final int ReleaseChoosePhoto = 3;

    private ImageButton mBack;
    private ImageView mActivityPicture;
    private MyEditText mActivityName;
    private MyEditText mActivityTime;
    private MyEditText mActivitySite;
    private EditText mActivityInterduction;
    private Button mSubmitButton;
    private BmobFile mBmobFile = null;

    public static Intent newIntent(Context context){
        Intent intent=new Intent(context,ReleaseActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setExitTransition(new Slide(Gravity.RIGHT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        initView();
        initLinister();
    }
    private void initView(){
        mBack = (ImageButton)findViewById(R.id.fanhui_button);
        mActivityPicture = (ImageView)findViewById(R.id.Release_IM_activitypicture);
        mActivityName = (MyEditText)findViewById(R.id.Release_ET_activityname);
        mActivityTime = (MyEditText)findViewById(R.id.Release_ET_activitytime);
        mActivitySite = (MyEditText)findViewById(R.id.Release_ET_activityplace);
        mActivityInterduction = (EditText)findViewById(R.id.Release_ET_activityintroduction);
        mSubmitButton = (Button)findViewById(R.id.Release_Btn_submit_button);
    }
    private void initLinister(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mActivityPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ReleaseActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ReleaseActivity.this,new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },1);
                }else{
                    openAlbum();
                }
            }
        });
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ChackText()){
                    ActivityBean activityBean = new ActivityBean();
                    activityBean.setActivitypicture(mBmobFile);
                    activityBean.setCreatuser(BmobUser.getCurrentUser(MyUser.class).getObjectId());
                    activityBean.setActivityname(mActivityName.getText().toString());
                    activityBean.setActivitytime(mActivityTime.getText().toString());
                    activityBean.setActivitysite(mActivitySite.getText().toString());
                    activityBean.setActivityinterduction(mActivityInterduction.getText().toString());
                    activityBean.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null){
                                Toast.makeText(ReleaseActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ReleaseActivity.this,"提交失败,请重试",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private boolean ChackText(){
        if (mActivityName.getText().toString().equals("")){
            Toast.makeText(ReleaseActivity.this,"请输入活动名称",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mActivityTime.getText().toString().equals("")){
            Toast.makeText(ReleaseActivity.this,"请输入活动时间",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mActivitySite.getText().toString().equals("")){
            Toast.makeText(ReleaseActivity.this,"请输入活动地点",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mActivityInterduction.getText().toString().equals("")){
            Toast.makeText(ReleaseActivity.this,"请输入活动简介",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,ReleaseChoosePhoto);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(ReleaseActivity.this,"没有权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ReleaseChoosePhoto:
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
                    //将bitmap转化为Drawable后设置为活动View背景
                    mActivityPicture.setBackground(ImgUtil.bitmapToDrawable(ReleaseActivity.this,bitmap));
                    Uri uri = data.getData();
                    String path = ImgUtil.getImagePath(ReleaseActivity.this,uri,null);
                    UpdataHeadImg(path);
                }
                break;
            default:break;
        }
    }
    //向bmob上传活动图片
    private void UpdataHeadImg(String path){

        //压缩图片，返回bitmap格式图片
        Bitmap littlebitmap = ImgUtil.getImageThumbnail(path,200,120);
        //将bitmap图片保存为文件
        File file = ImgUtil.saveImage(littlebitmap);

        mBmobFile = new BmobFile(file);
        mBmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    Toast.makeText(ReleaseActivity.this, "上传文件成功:" + mBmobFile.getFileUrl(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ReleaseActivity.this, "上传文件失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value) {
                //返回上传进度,并显示出来
            }
        });
    }

}
