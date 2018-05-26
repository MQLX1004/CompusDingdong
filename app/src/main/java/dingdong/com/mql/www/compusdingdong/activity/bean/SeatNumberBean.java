package dingdong.com.mql.www.compusdingdong.activity.bean;

/**
 * 用于EventBus数据传输，携带标识属性和座位号
 * Created by MQL on 2018/5/19.
 */

public class SeatNumberBean {
    private String sign;
    private String seatnumber;

    public SeatNumberBean(String sign, String seatnumber) {
        this.sign = sign;
        this.seatnumber = seatnumber;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSeatnumber() {
        return seatnumber;
    }

    public void setSeatnumber(String seatnumber) {
        this.seatnumber = seatnumber;
    }
}
