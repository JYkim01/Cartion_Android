
package net.belicon.cartion.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ManualData implements Serializable
{

    @SerializedName("appManualList")
    @Expose
    private List<AppManualList> appManualList = null;
    private final static long serialVersionUID = 4008580077020781708L;

    public List<AppManualList> getAppManualList() {
        return appManualList;
    }

    public void setAppManualList(List<AppManualList> appManualList) {
        this.appManualList = appManualList;
    }

}
