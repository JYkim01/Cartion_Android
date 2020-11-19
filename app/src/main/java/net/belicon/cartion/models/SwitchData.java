
package net.belicon.cartion.models;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SwitchData implements Serializable {

    @SerializedName("mobileSwitch")
    @Expose
    private int mobileSwitch;

    @SerializedName("hornList")
    @Expose
    private List<SwitchList> hornList = null;
    private final static long serialVersionUID = -7552662461901099405L;

    public int getMobileSwitch() {
        return mobileSwitch;
    }

    public void setMobileSwitch(int mobileSwitch) {
        this.mobileSwitch = mobileSwitch;
    }

    public List<SwitchList> getHornList() {
        return hornList;
    }

    public void setHornList(List<SwitchList> hornList) {
        this.hornList = hornList;
    }

}
