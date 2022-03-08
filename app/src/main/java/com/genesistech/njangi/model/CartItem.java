package com.genesistech.njangi.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CartItem implements Parcelable {

    private String key_id;
    private String uuid;
    public String seller;
    private String title;
    private String category;
    private double price;
    private String currency;
    private String description;
    private List<String> images;
    private double rating;
    private String favStatus;
    private String cartStatus;
    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

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

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
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

    public CartItem(String item_title, String seller, String description, String key_id, List<String> item_image,
                    double item_price, double item_rating, String currency, String uuid, String category, String cartStatus, int quantity) {
        this.uuid = uuid;
        this.title = item_title;
        this.seller = seller;
        this.description = description;
        this.key_id = key_id;
        this.category = category;
        this.price = item_price;
        this.currency = currency;
        this.images = item_image;
        this.rating = item_rating;
        this.favStatus = favStatus;
        this.cartStatus = cartStatus;
        this.quantity = quantity;
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
        seller = in.readString();
        title = in.readString();
        category = in.readString();
        price = in.readDouble();
        currency = in.readString();
        description = in.readString();
        in.readList(images, Product.class.getClassLoader());
        rating = in.readDouble();
        cartStatus = in.readString();
        quantity = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(key_id);
        dest.writeString(seller);
        dest.writeString(title);
        dest.writeString(category);
        dest.writeDouble(price);
        dest.writeString(currency);
        dest.writeString(description);
        dest.writeList(images);
        dest.writeDouble(rating);
        dest.writeString(cartStatus);
        dest.writeInt(quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
