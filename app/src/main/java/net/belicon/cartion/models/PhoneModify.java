package net.belicon.cartion.models;

public class PhoneModify {

    private String phoneNumber;

    public PhoneModify() {
    }

    public PhoneModify(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "PhoneModify{" +
                "phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
