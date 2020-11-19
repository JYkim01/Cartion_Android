
package net.belicon.cartion.models;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data implements Serializable {

    @SerializedName("eulaYn")
    @Expose
    private String eulaYn;

    @SerializedName("token")
    @Expose
    private Token token;

    private final static long serialVersionUID = -5518982808057983136L;

    public String getEulaYn() {
        return eulaYn;
    }

    public void setEulaYn(String eulaYn) {
        this.eulaYn = eulaYn;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

}
