package com.data;

import lombok.Getter;
import lombok.Setter;

public class Address {
    @Getter @Setter
    private String address;

    public Address(String address) {
        this.address = address;
    }

    public Address() {
    }
}
