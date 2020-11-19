
package net.belicon.cartion.models;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoticeList implements Serializable
{

    @SerializedName("noticeId")
    @Expose
    private Object noticeId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("pubStartDate")
    @Expose
    private Object pubStartDate;
    @SerializedName("pubEndDate")
    @Expose
    private Object pubEndDate;
    private final static long serialVersionUID = 7747845642646694990L;

    public Object getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Object noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Object getPubStartDate() {
        return pubStartDate;
    }

    public void setPubStartDate(Object pubStartDate) {
        this.pubStartDate = pubStartDate;
    }

    public Object getPubEndDate() {
        return pubEndDate;
    }

    public void setPubEndDate(Object pubEndDate) {
        this.pubEndDate = pubEndDate;
    }

}
