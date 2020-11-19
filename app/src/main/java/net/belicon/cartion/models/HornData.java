
package net.belicon.cartion.models;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HornData implements Serializable {

    @SerializedName("hornList")
    @Expose
    private List<HornList> hornList = null;
    private final static long serialVersionUID = -7552662461901099405L;

    public List<HornList> getHornList() {
        return hornList;
    }

    public void setHornList(List<HornList> hornList) {
        this.hornList = hornList;
    }

}
