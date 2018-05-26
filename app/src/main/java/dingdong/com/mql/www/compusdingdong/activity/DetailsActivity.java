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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import dingdong.com.mql.www.compusdingdong.R;
import dingdong.com.mql.www.compusdingdong.activity.bean.ActivityBean;
import dingdong.com.mql.www.compusdingdong.activity.bean.DbActivityBean;
import dingdong.com.mql.www.compusdingdong.activity.bean.SeatNumberBean;
import dingdong.com.mql.www.compusdingdong.activity.tools.BitmapCbyte;
import dingdong.com.mql.www.compusdingdong.activity.tools.ImgUtil;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";

    private DbActivityBean mDbActivityBean;
    private ImageView mpicture;
    private TextView mname;
    private TextView mtime;
    private TextView msite;
    private TextView mintroduction;
    private ImageButton mBack;
    private String flag;
    private Button mdelete;

    public static Intent newIntent(Context context){
        Intent intent=new Intent(context,DetailsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setExitTransition(new Slide(Gravity.RIGHT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
            layoutParams.flags=(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|layoutParams.flags);
        }
        Intent intent = getIntent();
        mDbActivityBean = (DbActivityBean) intent.getSerializableExtra("datad");
        flag = (String)intent.getSerializableExtra("delete");
        Log.d(TAG, "onCreate: "+mDbActivityBean.getActivityId());
        mpicture = (ImageView)findViewById(R.id.Detail_IV_picture);
        mname = (TextView)findViewById(R.id.Detail_TV_name);
        mtime = (TextView)findViewById(R.id.Detail_TV_time);
        msite = (TextView)findViewById(R.id.Detail_TV_site);
        mintroduction = (TextView)findViewById(R.id.Detail_TV_introduction);
        if (mDbActivityBean.getActivityPicture() != null){
            mpicture.setBackground(ImgUtil.bitmapToDrawable(DetailsActivity.this, BitmapCbyte.getBitmap(mDbActivityBean.getActivityPicture())));
        }else {
            mpicture.setBackground(DetailsActivity.this.getResources().getDrawable(R.drawable.dingdong_dack));
        }
        mname.setText(mDbActivityBean.getActivityName());
        mtime.setText(mDbActivityBean.getActivityTime());
        msite.setText(mDbActivityBean.getActivitySite());
        mintroduction.setText(mDbActivityBean.getActivityInterduction());
        mBack = (ImageButton)findViewById(R.id.fanhui_button);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mdelete = (Button)findViewById(R.id.Detail_TV_delete);
        if (flag.equals("1")){
            mdelete.setVisibility(View.VISIBLE);
        }
        mdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityBean bean = new ActivityBean();
                bean.setObjectId(mDbActivityBean.getActivityId());
                bean.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            Toast.makeText(DetailsActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(String.valueOf("delete"));
                            finish();
                        }
                    }
                });
            }
        });
    }


}
