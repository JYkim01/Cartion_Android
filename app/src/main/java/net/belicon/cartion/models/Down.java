package net.belicon.cartion.models;

import java.io.File;

public class Down {

    private String name;
    private File file;
    private String hornId;
    private String categoryName;
    private String hornType;

    public Down(String name, File file, String hornId, String categoryName, String hornType) {
        this.name = name;
        this.file = file;
        this.hornId = hornId;
        this.categoryName = categoryName;
        this.hornType = hornType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getHornId() {
        return hornId;
    }

    public void setHornId(String hornId) {
        this.hornId = hornId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getHornType() {
        return hornType;
    }

    public void setHornType(String hornType) {
        this.hornType = hornType;
    }

    @Override
    public String toString() {
        return "Down{" +
                "name='" + name + '\'' +
                ", file=" + file +
                ", hornId='" + hornId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", hornType='" + hornType + '\'' +
                '}';
    }
}
