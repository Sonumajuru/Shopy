package com.example.shopy.model;

public class User {

    private String name;
    private String surname;
    private boolean male;
    private boolean female;
    private String address;
    private String language;
    private String country;
    private boolean buyer;
    private boolean seller;
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

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public boolean isFemale() {
        return female;
    }

    public void setFemale(boolean female) {
        this.female = female;
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

    public boolean isBuyer() {
        return buyer;
    }

    public void setBuyer(boolean buyer) {
        this.buyer = buyer;
    }

    public boolean isSeller() {
        return seller;
    }

    public void setSeller(boolean seller) {
        this.seller = seller;
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

    public User(String name, String surname, boolean male, boolean female, String address, String language,
                String country, boolean buyer, boolean seller, String email, String password, String retypePassword, String deviceToken)
    {
        this.name = name;
        this.surname = surname;
        this.male = male;
        this.female = female;
        this.address = address;
        this.language = language;
        this.country = country;
        this.buyer = buyer;
        this.seller = seller;
        this.email = email;
        this.password = password;
        this.retypePassword = retypePassword;
        this.deviceToken = deviceToken;
    }
}
