package com.example.asm_ph46500;

public class CartItemModel {
    private int quantity;
    private String productId;
    private String name;
    private double price;

    // Constructors
    public CartItemModel() {
    }

    public CartItemModel(String productId, int quantity,String name, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.name=name;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
