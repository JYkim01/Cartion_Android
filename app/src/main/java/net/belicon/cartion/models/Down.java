package net.belicon.cartion.models;

import java.io.File;

public class Down {

    private String name;
    private File file;
    private String hornId;

    public Down(String name, File file, String hornId) {
        this.name = name;
        this.file = file;
        this.hornId = hornId;
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

    @Override
    public String toString() {
        return "Down{" +
                "name='" + name + '\'' +
                ", file=" + file +
                ", hornId='" + hornId + '\'' +
                '}';
    }
}
