package com.example.shopy.model;

public class Product {

    private String name;
    private String category;
    private String price;
    private int quantity;
    private String currency;
    private String description;
    private String imageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Product() {
    }

    public Product(String name, String category, String price, int quantity, String currency, String description, String imageUrl) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.currency = currency;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
