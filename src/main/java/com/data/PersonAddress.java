package com.data;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

public class PersonAddress extends Address {
    @Getter @Setter
    private String name;

    public PersonAddress() {
        super();
    }
    public PersonAddress(String name) {
        super();
    }

    public PersonAddress(String name, String address) {
        super(address);
        this.name=name;
    }

    public static PersonAddress fromCsv(String row){
        String[] values = row.split(",",2);
        PersonAddress personAddress = new PersonAddress();
        personAddress.setName(values[0]);
        String[] newArray = Arrays.copyOfRange(values, 1, values.length);
        personAddress.setAddress(values[1]);
        return personAddress;
    }

}
