
package net.belicon.cartion.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CouponData implements Serializable
{

    @SerializedName("couponList")
    @Expose
    private List<CouponList> couponList = null;
    private final static long serialVersionUID = 5297011453290632655L;

    public List<CouponList> getCouponList() {
        return couponList;
    }

    public void setCouponList(List<CouponList> couponList) {
        this.couponList = couponList;
    }

}
