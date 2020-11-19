
package net.belicon.cartion.models;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UseAppList implements Serializable
{

    @SerializedName("imageGroup")
    @Expose
    private String imageGroup;
    @SerializedName("imageName")
    @Expose
    private String imageName;
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
    private final static long serialVersionUID = -4241427204458397329L;

    public String getImageGroup() {
        return imageGroup;
    }

    public void setImageGroup(String imageGroup) {
        this.imageGroup = imageGroup;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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
