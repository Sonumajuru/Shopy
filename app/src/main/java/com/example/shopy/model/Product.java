package com.example.shopy.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String id;
    private String uuid;
    private String title;
    private String category;
    private double price;
    private String currency;
    private String shortDesc;
    private String imageUrl;
    private double rating;
    private String favStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getFavStatus() {
        return favStatus;
    }

    public void setFavStatus(String favStatus) {
        this.favStatus = favStatus;
    }

    public Product() {
    }

    public Product(String id, String uuid, String title, String category, double price, String currency,
                   String shortDesc, String imageUrl, double rating, String favStatus) {

        this.id = id;
        this.uuid = uuid;
        this.title = title;
        this.category = category;
        this.price = price;
        this.currency = currency;
        this.shortDesc = shortDesc;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.favStatus = favStatus;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    protected Product(Parcel in) {
        id = in.readString();
        title = in.readString();
        category = in.readString();
        price = in.readDouble();
        currency = in.readString();
        shortDesc = in.readString();
        imageUrl = in.readString();
        rating = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(category);
        dest.writeDouble(price);
        dest.writeString(currency);
        dest.writeString(shortDesc);
        dest.writeString(imageUrl);
        dest.writeDouble(rating);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
