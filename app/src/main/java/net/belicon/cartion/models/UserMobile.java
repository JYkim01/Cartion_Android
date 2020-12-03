package net.belicon.cartion.models;

import io.realm.RealmObject;

public class UserMobile extends RealmObject {

    private String userId;
    private int mobileSwitch;
    private int seq;
    private String hornName;
    private String categoryName;
    private String hornType;
    private String hornId;

    public UserMobile() {
    }

    public UserMobile(String userId, int mobileSwitch, int sqe, String hornType, String hornId) {
        this.userId = userId;
        this.mobileSwitch = mobileSwitch;
        this.seq = seq;
        this.hornType = hornType;
        this.hornId = hornId;
    }

    public UserMobile(String userId, int mobileSwitch, int seq, String hornName, String categoryName, String hornType, String hornId) {
        this.userId = userId;
        this.mobileSwitch = mobileSwitch;
        this.seq = seq;
        this.hornName = hornName;
        this.categoryName = categoryName;
        this.hornType = hornType;
        this.hornId = hornId;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMobileSwitch() {
        return mobileSwitch;
    }

    public void setMobileSwitch(int mobileSwitch) {
        this.mobileSwitch = mobileSwitch;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
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

    @Override
    public String toString() {
        return "UserMobile{" +
                "userId='" + userId + '\'' +
                ", mobileSwitch=" + mobileSwitch +
                ", seq=" + seq +
                ", hornName='" + hornName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", hornType='" + hornType + '\'' +
                ", hornId='" + hornId + '\'' +
                '}';
    }
}
