package com.example.medicarempsd.Class;

public class Health {
    private String weight;
    private String height;
    private String bmi;
    private String bloodPressure;

    public Health(String weight, String height, String bmi, String bloodPressure) {
        this.weight = weight;
        this.height = height;
        this.bmi = bmi;
        this.bloodPressure = bloodPressure;
    }

    public Health() {
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }
}
