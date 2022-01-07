package com.example.shopy.model;

public class Product {

    private String id;
    private String title;
    private String category;
    private double price;
    private String currency;
    private String shortDesc;
    private String imageUrl;
    private double rating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Product() {
    }

    public Product(String id, String title, String category, double price,
                   String currency, String shortDesc, String imageUrl, double rating) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
        this.currency = currency;
        this.shortDesc = shortDesc;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }
}
