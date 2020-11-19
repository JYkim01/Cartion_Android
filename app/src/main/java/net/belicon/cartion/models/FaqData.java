
package net.belicon.cartion.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaqData implements Serializable
{

    @SerializedName("faqList")
    @Expose
    private List<FaqList> faqList = null;
    private final static long serialVersionUID = -1451865576575248842L;

    public List<FaqList> getFaqList() {
        return faqList;
    }

    public void setFaqList(List<FaqList> faqList) {
        this.faqList = faqList;
    }

}
