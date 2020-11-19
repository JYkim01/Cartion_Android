package net.belicon.cartion.models;

import java.io.File;

public class Down {

    private String name;
    private File file;

    public Down(String name, File file) {
        this.name = name;
        this.file = file;
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

    @Override
    public String toString() {
        return "Down{" +
                "name='" + name + '\'' +
                ", file=" + file +
                '}';
    }
}
