package com.example.shopy.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FavItem implements Parcelable {

    private String key_id;
    private String uuid;
    private String title;
    private String category;
    private double price;
    private String currency;
    private String shortDesc;
    private String imageUrl;
    private double rating;
    private String favStatus;

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
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

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public FavItem(String item_title, String shortDesc, String key_id, String item_image,
                   double item_price, double item_rating, String currency, String uuid, String category) {
        this.key_id = key_id;
        this.title = item_title;
        this.price = item_price;
        this.rating = item_rating;
        this.uuid = uuid;
        this.category = category;
        this.shortDesc = shortDesc;
        this.currency = currency;
        this.imageUrl = item_image;
    }

    public static final Creator<FavItem> CREATOR = new Creator<FavItem>() {
        @Override
        public FavItem createFromParcel(Parcel in) {
            return new FavItem(in);
        }

        @Override
        public FavItem[] newArray(int size) {
            return new FavItem[size];
        }
    };

    protected FavItem(Parcel in) {
        key_id = in.readString();
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
        dest.writeString(key_id);
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