package com.data;


import lombok.Getter;
import lombok.Setter;

public class PersonDistance{
    @Getter @Setter
    private int distanceValue;
    @Getter @Setter
    private String distanceText;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String address;

    public PersonDistance(String name, String address, int distanceValue, String distanceText) {
       this.name=name;
       this.address = address;
        this.distanceValue = distanceValue;
        this.distanceText = distanceText;
    }


}

