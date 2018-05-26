package dingdong.com.mql.www.compusdingdong.activity.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by MQL on 2018/5/22.
 */

public class MealBean extends BmobObject implements Serializable {
    private BmobFile mealpicture;
    private String creatuser;
    private String mealname;
    private String mealsite;
    private String mealintroduction;

    public BmobFile getMealpicture() {
        return mealpicture;
    }

    public void setMealpicture(BmobFile mealpicture) {
        this.mealpicture = mealpicture;
    }

    public String getCreatuser() {
        return creatuser;
    }

    public void setCreatuser(String creatuser) {
        this.creatuser = creatuser;
    }

    public String getMealname() {
        return mealname;
    }

    public void setMealname(String mealname) {
        this.mealname = mealname;
    }

    public String getMealsite() {
        return mealsite;
    }

    public void setMealsite(String mealsite) {
        this.mealsite = mealsite;
    }

    public String getMealintroduction() {
        return mealintroduction;
    }

    public void setMealintroduction(String mealintroduction) {
        this.mealintroduction = mealintroduction;
    }
}
