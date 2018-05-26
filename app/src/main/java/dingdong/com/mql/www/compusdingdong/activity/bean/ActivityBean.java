package dingdong.com.mql.www.compusdingdong.activity.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Bmob中用到的与bmob数据库表对应交互的类
 * Created by MQL on 2018/5/20.
 */

public class ActivityBean extends BmobObject {
    private BmobFile activitypicture;
    private String activityname;
    private String activitytime;
    private String activitysite;
    private String activityinterduction;
    private String creatuser;

    public String getCreatuser() {
        return creatuser;
    }

    public void setCreatuser(String creatuser) {
        this.creatuser = creatuser;
    }

    public BmobFile getActivitypicture() {
        return activitypicture;
    }

    public void setActivitypicture(BmobFile activitypicture) {
        this.activitypicture = activitypicture;
    }

    public String getActivityname() {
        return activityname;
    }

    public void setActivityname(String activityname) {
        this.activityname = activityname;
    }

    public String getActivitytime() {
        return activitytime;
    }

    public void setActivitytime(String activitytime) {
        this.activitytime = activitytime;
    }

    public String getActivitysite() {
        return activitysite;
    }

    public void setActivitysite(String activitysite) {
        this.activitysite = activitysite;
    }

    public String getActivityinterduction() {
        return activityinterduction;
    }

    public void setActivityinterduction(String activityinterduction) {
        this.activityinterduction = activityinterduction;
    }
}
