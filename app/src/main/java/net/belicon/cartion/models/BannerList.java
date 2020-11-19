
package net.belicon.cartion.models;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BannerList implements Serializable
{

    @SerializedName("bannerId")
    @Expose
    private String bannerId;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("linkUrl")
    @Expose
    private String linkUrl;
    @SerializedName("seq")
    @Expose
    private String seq;
    @SerializedName("registerTime")
    @Expose
    private String registerTime;
    @SerializedName("modifyTime")
    @Expose
    private String modifyTime;
    private final static long serialVersionUID = 3854296967493866378L;

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

}
