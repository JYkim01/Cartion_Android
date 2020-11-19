package net.belicon.cartion.models;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class UserMobileList {

    private List<UserMobile> userMobiles;

    public UserMobileList() {
    }

    public UserMobileList(List<UserMobile> userMobiles) {
        this.userMobiles = userMobiles;
    }

    public List<UserMobile> getUserMobiles() {
        return userMobiles;
    }

    public void setUserMobiles(List<UserMobile> userMobiles) {
        this.userMobiles = userMobiles;
    }

    @Override
    public String toString() {
        return "UserMobileList{" +
                "userMobiles=" + userMobiles +
                '}';
    }
}
