package net.belicon.cartion.models;

public class PasswordModify {

    private String password;

    public PasswordModify() {
    }

    public PasswordModify(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "PasswordModify{" +
                "password='" + password + '\'' +
                '}';
    }
}
