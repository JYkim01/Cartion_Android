
package net.belicon.cartion.models;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DuplicateData implements Serializable
{

    @SerializedName("isAvailable")
    @Expose
    private Boolean isAvailable;
    private final static long serialVersionUID = -802463271051106033L;

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "DuplicateData{" +
                "isAvailable=" + isAvailable +
                '}';
    }
}
