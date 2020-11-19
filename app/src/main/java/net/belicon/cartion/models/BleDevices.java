package net.belicon.cartion.models;

import com.clj.fastble.data.BleDevice;

public class BleDevices {

    private BleDevice bleDevice;
    private String name;
    private String rssi;
    private String address;

    public BleDevices() {
    }

    public BleDevices(BleDevice bleDevice, String name, String rssi, String address) {
        this.bleDevice = bleDevice;
        this.name = name;
        this.rssi = rssi;
        this.address = address;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "BleDevices{" +
                "bleDevice=" + bleDevice +
                ", name='" + name + '\'' +
                ", rssi='" + rssi + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
