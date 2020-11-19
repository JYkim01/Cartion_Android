package net.belicon.cartion.models;

import java.util.ArrayList;
import java.util.List;

public class ExpandData {

    private String title;
    private List<String> child;

    public ExpandData() {
    }

    public ExpandData(String title) {
        this.title = title;
        this.child = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getChild() {
        return child;
    }

    public void setChild(List<String> child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "ExpandData{" +
                "title='" + title + '\'' +
                ", child=" + child +
                '}';
    }
}
