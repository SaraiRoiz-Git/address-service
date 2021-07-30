package com.data;

public class AppendAddress {

    final String address;
    final int index;

    public AppendAddress(String address, int index) {
        this.address = address;
        this.index = index;
    }

    public String getAddress() {
        return address;
    }

    public int getIndex() {
        return index;
    }
}
