package com.data;

import lombok.Getter;
import lombok.Setter;

public class PersonDistance extends PersonAddress  {
    @Getter @Setter
    private int distanceValue;
    @Getter @Setter
    private String distanceText;


    public PersonDistance() {
    }

    public PersonDistance(int distanceValue, String distanceText) {
        this.distanceValue = distanceValue;
        this.distanceText = distanceText;
    }


    public PersonDistance(String address, int distanceValue, String distanceText) {
        super(null, address);
        this.distanceValue = distanceValue;
        this.distanceText = distanceText;
    }

    public PersonDistance(String name, String address) {
        super(name, address);
    }


}

