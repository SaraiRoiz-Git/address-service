package com.data;


public class PersonAddress extends Address {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public static PersonAddress fromCsv(String row){
        String[] values = row.split(",");
        PersonAddress personAddress = new PersonAddress();
        personAddress.setName(values[0]);
        personAddress.setAddress(values[1]);
        return personAddress;
    }

}
