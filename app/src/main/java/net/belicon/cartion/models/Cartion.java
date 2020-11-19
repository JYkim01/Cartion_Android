package net.belicon.cartion.models;

public class Cartion {

    private String deviceId;
    private String deviceMac;
    private String deviceName;

    public Cartion() {
    }

    public Cartion(String deviceId, String deviceMac, String deviceName) {
        this.deviceId = deviceId;
        this.deviceMac = deviceMac;
        this.deviceName = deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public String toString() {
        return "Cartion{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceMac='" + deviceMac + '\'' +
                ", deviceName='" + deviceName + '\'' +
                '}';
    }
}
