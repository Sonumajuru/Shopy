package com.genesistech.njangi.model;

public class User {

    private String firstName;
    private String lastName;
    private boolean male;
    private boolean female;
    private String address;
    private String language;
    private String country;
    private String email;
    private String password;
    private String telNumber;
    private String uniqueID;
    private String date;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String firstName, String lastName, boolean male, boolean female, String address, String language,
                String country, String email, String password, String telNumber, String uniqueID, String date)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.male = male;
        this.female = female;
        this.address = address;
        this.language = language;
        this.country = country;
        this.email = email;
        this.password = password;
        this.telNumber = telNumber;
        this.uniqueID = uniqueID;
        this.date = date;
    }
}
