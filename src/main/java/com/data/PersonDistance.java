package com.data;

import lombok.Getter;
import lombok.Setter;

public class PersonDistance extends PersonAddress {
    @Getter @Setter
    private int distanceValue;
    @Getter @Setter
    private String distanceText;


    public PersonDistance(String name) {
        super(name);
    }

    public PersonDistance(String name, int distanceValue, String distanceText) {
        super(name);
        this.distanceValue = distanceValue;
        this.distanceText = distanceText;
    }

    public PersonDistance(String name, String address, int distanceValue, String distanceText) {
        super(name, address);
        this.distanceValue = distanceValue;
        this.distanceText = distanceText;
    }

    public PersonDistance(String name, String address) {
        super(name, address);
    }
}
