package dingdong.com.mql.www.compusdingdong.activity.bean;

import cn.bmob.v3.BmobObject;

/**
 * Bmob座位号与用户对应Bean
 * Created by MQL on 2018/5/20.
 */

public class SeatUserBean extends BmobObject {
    private String seatnumber;
    private String seatuser;

    public String getSeatnumber() {
        return seatnumber;
    }

    public void setSeatnumber(String seatnumber) {
        this.seatnumber = seatnumber;
    }

    public String getSeatuser() {
        return seatuser;
    }

    public void setSeatuser(String seatuser) {
        this.seatuser = seatuser;
    }
}
