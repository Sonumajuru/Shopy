package com.example.shopy.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {

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
    private String cartStatus;

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

    public String isCartStatus() {
        return cartStatus;
    }

    public void setCartStatus(String cartStatus) {
        this.cartStatus = cartStatus;
    }

    public CartItem() {
    }

    public CartItem(String item_title, String shortDesc, String key_id, String item_image,
                    double item_price, double item_rating, String currency, String uuid, String category, String cartStatus) {
        this.uuid = uuid;
        this.title = item_title;
        this.shortDesc = shortDesc;
        this.key_id = key_id;
        this.category = category;
        this.price = item_price;
        this.currency = currency;
        this.imageUrl = item_image;
        this.rating = item_rating;
        this.favStatus = favStatus;
        this.cartStatus = cartStatus;
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    protected CartItem(Parcel in) {
        key_id = in.readString();
        title = in.readString();
        category = in.readString();
        price = in.readDouble();
        currency = in.readString();
        shortDesc = in.readString();
        imageUrl = in.readString();
        rating = in.readDouble();
        cartStatus = in.readString();
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
        dest.writeString(cartStatus);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
