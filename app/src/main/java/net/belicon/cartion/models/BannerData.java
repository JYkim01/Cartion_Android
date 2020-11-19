
package net.belicon.cartion.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BannerData implements Serializable
{

    @SerializedName("bannerList")
    @Expose
    private List<BannerList> bannerList = null;
    private final static long serialVersionUID = -2024760539233649745L;

    public List<BannerList> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerList> bannerList) {
        this.bannerList = bannerList;
    }

}
