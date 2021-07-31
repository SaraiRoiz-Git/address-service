package com.data;
import java.util.Arrays;

public class PersonAddress {

    private String name;
    private String address;

    public PersonAddress() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public PersonAddress(String name) {
        super();
    }

    public PersonAddress(String name, String address) {
        this.address = address;
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
