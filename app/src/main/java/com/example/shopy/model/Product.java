package com.example.shopy.model;

public class Product {

    private String category;
    private String price;
    private int quantity;
    private String currency;
    private String description;

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

    public Product() {
    }

    public Product(String category, String price, int quantity, String currency, String description) {
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.currency = currency;
        this.description = description;
    }
}
