package com.data;


public class PersonDistance extends PersonAddress  {
    private int distanceValue;
    private String distanceText;

    public PersonDistance(String name, String address, int distanceValue, String distanceText) {
        super(name, address);
        this.distanceValue = distanceValue;
        this.distanceText = distanceText;
    }

    public int getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(int distanceValue) {
        this.distanceValue = distanceValue;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }
}

