package br.udesc.esag.participactbrasil.domain.data;


public class DataBluetooth extends Data {

    private static final long serialVersionUID = -4260573805168092975L;

    private String mac;

    private String friendly_name;

    private int deviceClass;

    private int major_class;


    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getFriendly_name() {
        return friendly_name;
    }

    public void setFriendly_name(String friendly_name) {
        this.friendly_name = friendly_name;
    }

    public int getDeviceClass() {
        return deviceClass;
    }

    public void setDeviceClass(int deviceClass) {
        this.deviceClass = deviceClass;
    }

    public int getMajor_class() {
        return major_class;
    }

    public void setMajor_class(int major_class) {
        this.major_class = major_class;
    }

}
