
package net.belicon.cartion.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoticeData implements Serializable
{

    @SerializedName("noticeList")
    @Expose
    private List<NoticeList> noticeList = null;
    private final static long serialVersionUID = -3618774946834076305L;

    public List<NoticeList> getNoticeList() {
        return noticeList;
    }

    public void setNoticeList(List<NoticeList> noticeList) {
        this.noticeList = noticeList;
    }

}
