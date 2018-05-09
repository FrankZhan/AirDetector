package com.nami.Entity;

public class Device {
    private String MAC;   // Uid
    private String name;  //DeviceTag
    public Device(){
        MAC = null;
        name = null;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMAC() {
        return MAC;
    }

    public String getName() {
        return name;
    }
}
