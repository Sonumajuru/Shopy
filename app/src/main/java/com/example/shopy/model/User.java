package com.example.shopy.model;

public class User {

    private String name;
    private String surname;
    private boolean checkBokMale;
    private boolean checkBokFemale;
    private String address;
    private String language;
    private String country;
    private String userType;
    private String email;
    private String password;
    private String retypePassword;
    private String deviceToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean isCheckBokMale() {
        return checkBokMale;
    }

    public void setCheckBokMale(boolean checkBokMale) {
        this.checkBokMale = checkBokMale;
    }

    public boolean isCheckBokFemale() {
        return checkBokFemale;
    }

    public void setCheckBokFemale(boolean checkBokFemale) {
        this.checkBokFemale = checkBokFemale;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRetypePassword() {
        return retypePassword;
    }

    public void setRetypePassword(String retypePassword) {
        this.retypePassword = retypePassword;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String surname, boolean checkBokMale, boolean checkBokFemale, String address, String language,
                String country, String userType, String email, String password, String retypePassword, String deviceToken)
    {
        this.name = name;
        this.surname = surname;
        this.checkBokMale = checkBokMale;
        this.checkBokFemale = checkBokFemale;
        this.address = address;
        this.language = language;
        this.country = country;
        this.userType = userType;
        this.email = email;
        this.password = password;
        this.retypePassword = retypePassword;
        this.deviceToken = deviceToken;
    }
}
