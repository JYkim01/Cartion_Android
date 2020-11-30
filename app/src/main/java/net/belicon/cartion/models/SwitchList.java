
package net.belicon.cartion.models;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SwitchList implements Serializable {

    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("hornType")
    @Expose
    private String hornType;
    @SerializedName("hornId")
    @Expose
    private String hornId;
    @SerializedName("hornName")
    @Expose
    private String hornName;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("mobileSwitch")
    @Expose
    private Integer mobileSwitch;
    @SerializedName("seq")
    @Expose
    private Integer seq;
    @SerializedName("type")
    @Expose
    private String type;
    private final static long serialVersionUID = -4882857284497398403L;

    public SwitchList(String userId, String hornType, String hornId, String hornName, String categoryName, Integer mobileSwitch, Integer seq, String type) {
        this.userId = userId;
        this.hornType = hornType;
        this.hornId = hornId;
        this.hornName = hornName;
        this.categoryName = categoryName;
        this.mobileSwitch = mobileSwitch;
        this.seq = seq;
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHornType() {
        return hornType;
    }

    public void setHornType(String hornType) {
        this.hornType = hornType;
    }

    public String getHornId() {
        return hornId;
    }

    public void setHornId(String hornId) {
        this.hornId = hornId;
    }

    public String getHornName() {
        return hornName;
    }

    public void setHornName(String hornName) {
        this.hornName = hornName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getMobileSwitch() {
        return mobileSwitch;
    }

    public void setMobileSwitch(Integer mobileSwitch) {
        this.mobileSwitch = mobileSwitch;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SwitchList{" +
                "userId='" + userId + '\'' +
                ", hornType='" + hornType + '\'' +
                ", hornId='" + hornId + '\'' +
                ", hornName='" + hornName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", mobileSwitch=" + mobileSwitch +
                ", seq=" + seq +
                ", type='" + type + '\'' +
                '}';
    }
}
