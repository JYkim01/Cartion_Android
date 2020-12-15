
package net.belicon.cartion.models;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HornList implements Serializable {

    @SerializedName("hornId")
    @Expose
    private String hornId;
    @SerializedName("hornName")
    @Expose
    private String hornName;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("wavPath")
    @Expose
    private String wavPath;
    @SerializedName("adpcmPath")
    @Expose
    private String adpcmPath;
    @SerializedName("multipartFiles")
    @Expose
    private Object multipartFiles;
    private final static long serialVersionUID = -8832792040383059628L;

    public HornList(String hornId, String hornName, String categoryName, String wavPath, String adpcmPath) {
        this.hornId = hornId;
        this.hornName = hornName;
        this.categoryName = categoryName;
        this.wavPath = wavPath;
        this.adpcmPath = adpcmPath;
    }

    public String getHornId() {
        return hornId;
    }

    public void setHornId(String hornId) {
        this.hornId = hornId;
    }

    public String getHornName() {
        return hornName;
    }

    public void setHornName(String hornName) {
        this.hornName = hornName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getWavPath() {
        return wavPath;
    }

    public void setWavPath(String wavPath) {
        this.wavPath = wavPath;
    }

    public String getAdpcmPath() {
        return adpcmPath;
    }

    public void setAdpcmPath(String adpcmPath) {
        this.adpcmPath = adpcmPath;
    }

    public Object getMultipartFiles() {
        return multipartFiles;
    }

    public void setMultipartFiles(Object multipartFiles) {
        this.multipartFiles = multipartFiles;
    }

    @Override
    public String toString() {
        return "HornList{" +
                "hornId='" + hornId + '\'' +
                ", hornName='" + hornName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", wavPath='" + wavPath + '\'' +
                ", adpcmPath='" + adpcmPath + '\'' +
                '}';
    }
}
