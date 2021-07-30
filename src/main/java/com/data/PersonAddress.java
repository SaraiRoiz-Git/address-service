package com.data;

import lombok.Getter;
import lombok.Setter;

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
    }

    public static PersonAddress fromCsv(String row){
        String[] values = row.split(",");
        PersonAddress personAddress = new PersonAddress();
        personAddress.setName(values[0]);
        personAddress.setAddress(values[1]);
        return personAddress;
    }

}
