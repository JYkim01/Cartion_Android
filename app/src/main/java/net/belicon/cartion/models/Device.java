
package net.belicon.cartion.models;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device implements Serializable
{

    @SerializedName("deviceId")
    @Expose
    private String deviceId;
    @SerializedName("deviceMac")
    @Expose
    private String deviceMac;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("deviceName")
    @Expose
    private String deviceName;
    @SerializedName("useYn")
    @Expose
    private String useYn;
    @SerializedName("registerTime")
    @Expose
    private Object registerTime;
    @SerializedName("modifyTime")
    @Expose
    private Object modifyTime;
    private final static long serialVersionUID = 7203697211777977843L;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getUseYn() {
        return useYn;
    }

    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }

    public Object getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Object registerTime) {
        this.registerTime = registerTime;
    }

    public Object getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Object modifyTime) {
        this.modifyTime = modifyTime;
    }

}
