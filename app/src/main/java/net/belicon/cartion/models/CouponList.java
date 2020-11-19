
package net.belicon.cartion.models;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CouponList implements Serializable
{

    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("couponId")
    @Expose
    private String couponId;
    @SerializedName("mobileSwitch")
    @Expose
    private String mobileSwitch;
    @SerializedName("couponName")
    @Expose
    private String couponName;
    @SerializedName("couponValue")
    @Expose
    private String couponValue;
    @SerializedName("couponText")
    @Expose
    private String couponText;
    private final static long serialVersionUID = -4596356326095010328L;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getMobileSwitch() {
        return mobileSwitch;
    }

    public void setMobileSwitch(String mobileSwitch) {
        this.mobileSwitch = mobileSwitch;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getCouponValue() {
        return couponValue;
    }

    public void setCouponValue(String couponValue) {
        this.couponValue = couponValue;
    }

    public String getCouponText() {
        return couponText;
    }

    public void setCouponText(String couponText) {
        this.couponText = couponText;
    }

}
