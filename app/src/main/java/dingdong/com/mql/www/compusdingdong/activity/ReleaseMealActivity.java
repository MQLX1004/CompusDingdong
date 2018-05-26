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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import dingdong.com.mql.www.compusdingdong.R;
import dingdong.com.mql.www.compusdingdong.activity.bean.MealBean;
import dingdong.com.mql.www.compusdingdong.activity.bean.MyUser;
import dingdong.com.mql.www.compusdingdong.activity.tools.ImgUtil;
import dingdong.com.mql.www.compusdingdong.activity.view.MyEditText;

public class ReleaseMealActivity extends AppCompatActivity {
    public static final int ReleaseMealChoosePhoto = 4;

    private ImageView mMealPicture;
    private ImageButton mBack;
    private MyEditText mMealname;
    private MyEditText mMealsite;
    private EditText mMealintroduction;
    private Button mMealSubmit;
    private BmobFile mBmobFile = null;

    public static Intent newIntent(Context context){
        Intent intent=new Intent(context,ReleaseMealActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setExitTransition(new Slide(Gravity.RIGHT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_meal);

        initView();
        initLinister();
    }

    private void initView(){
        mBack = (ImageButton)findViewById(R.id.fanhui_button);
        mMealPicture = (ImageView)findViewById(R.id.RMeal_IM_mealpicture);
        mMealname = (MyEditText)findViewById(R.id.RMeal_ET_mealname);
        mMealsite = (MyEditText)findViewById(R.id.RMeal_ET_mealplace);
        mMealintroduction = (EditText)findViewById(R.id.RMeal_ET_mealintroduction);
        mMealSubmit = (Button)findViewById(R.id.RMeal_Btn_submit_button);
    }
    private void initLinister(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mMealPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ReleaseMealActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ReleaseMealActivity.this,new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },1);
                }else{
                    openAlbum();
                }
            }
        });
        mMealSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ChackText()){
                    MealBean bean = new MealBean();
                    bean.setMealpicture(mBmobFile);
                    bean.setCreatuser(BmobUser.getCurrentUser(MyUser.class).getObjectId());
                    bean.setMealname(mMealname.getText().toString());
                    bean.setMealsite(mMealsite.getText().toString());
                    bean.setMealintroduction(mMealintroduction.getText().toString());
                    bean.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null){
                                Toast.makeText(ReleaseMealActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ReleaseMealActivity.this,"提交失败,请重试",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,ReleaseMealChoosePhoto);
    }

    private boolean ChackText(){
        if (mMealname.getText().toString().equals("")){
            Toast.makeText(ReleaseMealActivity.this,"请输入菜品名称",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mMealsite.getText().toString().equals("")){
            Toast.makeText(ReleaseMealActivity.this,"请输入上架地点",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mMealintroduction.getText().toString().equals("")){
            Toast.makeText(ReleaseMealActivity.this,"请输入菜品简介",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(ReleaseMealActivity.this,"没有权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ReleaseMealChoosePhoto:
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
                    mMealPicture.setBackground(ImgUtil.bitmapToDrawable(ReleaseMealActivity.this,bitmap));
                    Uri uri = data.getData();
                    String path = ImgUtil.getImagePath(ReleaseMealActivity.this,uri,null);
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
                    Toast.makeText(ReleaseMealActivity.this, "上传文件成功:" + mBmobFile.getFileUrl(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ReleaseMealActivity.this, "上传文件失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value) {
                //返回上传进度,并显示出来
            }
        });
    }
}
