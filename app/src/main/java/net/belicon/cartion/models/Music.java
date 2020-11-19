
package net.belicon.cartion.models;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Music {

    private Integer offset;
    private Integer limit;
    private Search search;

    public Music() {
    }

    public Music(Integer offset, Integer limit, Search search) {
        this.offset = offset;
        this.limit = limit;
        this.search = search;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    @Override
    public String toString() {
        return "Music{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", search=" + search +
                '}';
    }
}
