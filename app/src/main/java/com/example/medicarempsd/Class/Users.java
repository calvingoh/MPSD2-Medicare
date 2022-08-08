package com.example.medicarempsd.Class;

public class Users {
    private String userId;
    private String name;
    private String phone;
    private String dob;
    private String type;
    private String email;
    private String image = "";
    private String specialization;

    private String qualification;

    public Users(String userId, String name, String phone, String dob, String type, String email, String image,
                 String specialization, String qualification) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.dob = dob;
        this.type = type;
        this.image = image;
        this.email = email;
        this.specialization = specialization;

        this.qualification = qualification;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Users() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
