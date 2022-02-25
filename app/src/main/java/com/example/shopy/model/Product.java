package com.example.shopy.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Product implements Parcelable {

    private String id;
    private String prodID;
    private String uuid;
    private String seller;
    private String store;
    private String title;
    private String category;
    private double price;
    private String currency;
    private String description;
    private List<String> images;
    private double rating;
    private String favStatus;
    private List<Product> productList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProdID() {
        return prodID;
    }

    public void setProdID(String prodID) {
        this.prodID = prodID;
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

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getCurrency() {
        return currency;
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

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Product() {
    }

    public Product(String id, String uuid, String seller, String title, String category, double price, String currency,
                   String description, List<String> images, double rating, String favStatus, String prodID, String store) {

        this.id = id;
        this.prodID = prodID;
        this.uuid = uuid;
        this.title = title;
        this.seller = seller;
        this.category = category;
        this.price = price;
        this.currency = currency;
        this.description = description;
        this.images = images;
        this.rating = rating;
        this.favStatus = favStatus;
        this.store = store;
        productList = new ArrayList<>();
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
        prodID = in.readString();
        seller = in.readString();
        title = in.readString();
        category = in.readString();
        price = in.readDouble();
        currency = in.readString();
        description = in.readString();
        in.readList(images, Product.class.getClassLoader());
        rating = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(prodID);
        dest.writeString(seller);
        dest.writeString(title);
        dest.writeString(category);
        dest.writeDouble(price);
        dest.writeString(currency);
        dest.writeString(description);
        dest.writeList(images);
        dest.writeDouble(rating);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("prodID", prodID);
        result.put("uuid", uuid);
        result.put("seller", seller);
        result.put("title", title);
        result.put("category", category);
        result.put("price", price);
        result.put("currency", currency);
        result.put("description", description);
        result.put("images", images);
        result.put("rating", rating);
        result.put("favStatus", favStatus);
        result.put("store", store);

        return result;
    }
}
