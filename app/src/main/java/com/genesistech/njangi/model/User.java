package com.genesistech.njangi.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

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
    private String uuid;
    private String date;

    protected User(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        male = in.readByte() != 0;
        female = in.readByte() != 0;
        address = in.readString();
        language = in.readString();
        country = in.readString();
        email = in.readString();
        password = in.readString();
        telNumber = in.readString();
        uniqueID = in.readString();
        uuid = in.readString();
        date = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
                String country, String email, String password, String telNumber, String uniqueID, String date, String uuid)
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
        this.uuid = uuid;
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeByte((byte) (male ? 1 : 0));
        parcel.writeByte((byte) (female ? 1 : 0));
        parcel.writeString(address);
        parcel.writeString(language);
        parcel.writeString(country);
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeString(telNumber);
        parcel.writeString(uniqueID);
        parcel.writeString(uuid);
        parcel.writeString(date);
    }
}
