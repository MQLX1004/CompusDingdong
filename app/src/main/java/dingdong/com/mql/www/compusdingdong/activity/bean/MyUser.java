package dingdong.com.mql.www.compusdingdong.activity.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Bmob用户Bean
 * Created by MQL on 2018/5/18.
 */

public class MyUser extends BmobUser {
    private boolean isPresiden;
    private String nickname;
    private BmobFile headPicture;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isPresiden() {
        return isPresiden;
    }

    public void setPresiden(boolean presiden) {
        isPresiden = presiden;
    }

    public BmobFile getHeadPicture() {
        return headPicture;
    }

    public void setHeadPicture(BmobFile headPicture) {
        this.headPicture = headPicture;
    }
}
