package net.belicon.cartion.models;

public class Switch {

    private int seq;
    private String type;
    private String name;

    public Switch(int seq, String type, String name) {
        this.seq = seq;
        this.type = type;
        this.name = name;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Switch{" +
                "seq=" + seq +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
