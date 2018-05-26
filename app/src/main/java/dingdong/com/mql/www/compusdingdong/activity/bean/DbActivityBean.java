package dingdong.com.mql.www.compusdingdong.activity.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Ormlite框架中用到的于数据库表对应的类
 * Created by MQL on 2018/5/21.
 */

@DatabaseTable
public class DbActivityBean implements Serializable {
    @DatabaseField(id = true)
    private String activityId;
    @DatabaseField
    private String creatUser;
    @DatabaseField
    private String activityTime;
    @DatabaseField
    private String activitySite;
    @DatabaseField
    private String activityName;
    @DatabaseField
    private String activityInterduction;
    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] activityPicture;

    public DbActivityBean() {
    }

    public DbActivityBean(String activityId, String creatUser,
                          String activityTime, String activitySite,
                          String activityName, String activityInterduction,
                          byte[] activityPicture) {
        this.activityId = activityId;
        this.creatUser = creatUser;
        this.activityTime = activityTime;
        this.activitySite = activitySite;
        this.activityName = activityName;
        this.activityInterduction = activityInterduction;
        this.activityPicture = activityPicture;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getCreatUser() {
        return creatUser;
    }

    public void setCreatUser(String creatUser) {
        this.creatUser = creatUser;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public String getActivitySite() {
        return activitySite;
    }

    public void setActivitySite(String activitySite) {
        this.activitySite = activitySite;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityInterduction() {
        return activityInterduction;
    }

    public void setActivityInterduction(String activityInterduction) {
        this.activityInterduction = activityInterduction;
    }

    public byte[] getActivityPicture() {
        return activityPicture;
    }

    public void setActivityPicture(byte[] activityPicture) {
        this.activityPicture = activityPicture;
    }
}
