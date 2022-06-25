package com.genesistech.njangi.model;

import java.util.List;
public class Order {

    // Use order Model to generate Invoice
    private int customerID; //UUID Buyer
    protected String custName;
    protected String custAddress;
    private int orderNumber;
    private int quantity;
    private double totalPrice;
    private double shipFee;
    private int SellerID; //UUID seller
    protected String SellerName;
    protected String SellerAddress;
    private List<Product> productList;
    public int getCustomerID() {
        return customerID;
    }
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
    public String getCustName() {
        return custName;
    }
    public void setCustName(String custName) {
        this.custName = custName;
    }
    public String getCustAddress() {
        return custAddress;
    }
    public void setCustAddress(String custAddress) {
        this.custAddress = custAddress;
    }
    public int getOrderNumber() {
        return orderNumber;
    }
    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public double getShipFee() {
        return shipFee;
    }
    public void setShipFee(double shipFee) {
        this.shipFee = shipFee;
    }
    public int getSellerID() {
        return SellerID;
    }
    public void setSellerID(int sellerID) {
        SellerID = sellerID;
    }
    public String getSellerName() {
        return SellerName;
    }
    public void setSellerName(String sellerName) {
        SellerName = sellerName;
    }
    public String getSellerAddress() {
        return SellerAddress;
    }
    public void setSellerAddress(String sellerAddress) {
        SellerAddress = sellerAddress;
    }
    public List<Product> getProductList() {
        return productList;
    }
    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
    public Order(int customerID, String custName, String custAddress, int orderNumber, int quantity,
                 double totalPrice, double shipFee, int sellerID, String sellerName,
                 String sellerAddress, List<Product> productList) {
        this.customerID = customerID;
        this.custName = custName;
        this.custAddress = custAddress;
        this.orderNumber = orderNumber;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.shipFee = shipFee;
        SellerID = sellerID;
        SellerName = sellerName;
        SellerAddress = sellerAddress;
        this.productList = productList;
    }
}
