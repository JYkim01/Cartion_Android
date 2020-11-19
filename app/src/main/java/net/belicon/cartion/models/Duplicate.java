
package net.belicon.cartion.models;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Duplicate implements Serializable
{

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("data")
    @Expose
    private DuplicateData data;
    @SerializedName("success")
    @Expose
    private Boolean success;
    private final static long serialVersionUID = -8396747556983686763L;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public DuplicateData getData() {
        return data;
    }

    public void setData(DuplicateData data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "Duplicate{" +
                "status='" + status + '\'' +
                ", statusCode=" + statusCode +
                ", data=" + data +
                ", success=" + success +
                '}';
    }
}
