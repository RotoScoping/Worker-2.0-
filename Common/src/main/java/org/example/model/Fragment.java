package org.example.model;

import java.io.Serializable;

public class Fragment implements Serializable {

    private int id;
    private int totalPackages;

    private byte[] data;

    public Fragment(int id, int totalPackages, byte[] data) {
        this.id = id;
        this.totalPackages = totalPackages;
        this.data = data;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotalPackages() {
        return totalPackages;
    }

    public void setTotalPackages(int totalPackages) {
        this.totalPackages = totalPackages;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
