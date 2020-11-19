
package net.belicon.cartion.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyData implements Serializable
{

    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("devices")
    @Expose
    private List<Device> devices = null;
    @SerializedName("userId")
    @Expose
    private String userId;
    private final static long serialVersionUID = -1334819705063727225L;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
