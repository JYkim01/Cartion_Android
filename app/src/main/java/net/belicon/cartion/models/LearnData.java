
package net.belicon.cartion.models;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LearnData implements Serializable {

    @SerializedName("useAppList")
    @Expose
    private List<UseAppList> useAppList = null;
    private final static long serialVersionUID = 6995739389893053247L;

    public List<UseAppList> getUseAppList() {
        return useAppList;
    }

    public void setUseAppList(List<UseAppList> useAppList) {
        this.useAppList = useAppList;
    }

}
