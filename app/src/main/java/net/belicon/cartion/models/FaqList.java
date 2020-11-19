
package net.belicon.cartion.models;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaqList implements Serializable
{

    @SerializedName("faqId")
    @Expose
    private Object faqId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("body")
    @Expose
    private String body;
    private final static long serialVersionUID = 672649057506456843L;

    public Object getFaqId() {
        return faqId;
    }

    public void setFaqId(Object faqId) {
        this.faqId = faqId;
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

}
