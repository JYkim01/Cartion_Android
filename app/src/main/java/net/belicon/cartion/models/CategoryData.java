
package net.belicon.cartion.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryData implements Serializable
{

    @SerializedName("categoryList")
    @Expose
    private List<CategoryList> categoryList = null;
    private final static long serialVersionUID = 5904467979894734909L;

    public List<CategoryList> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryList> categoryList) {
        this.categoryList = categoryList;
    }

}
