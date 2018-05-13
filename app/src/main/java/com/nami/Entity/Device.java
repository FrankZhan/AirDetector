package com.nami.Entity;

public class Device {
    private String MAC;   // Uid
    private String name;  //DeviceTag
    public Device(){
        // 便于测试，所以赋予了初值
        MAC = "FF:FF:FF:FF:FF";
        name = "Home";
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
