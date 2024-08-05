package com.example.asm_ph46500;

public class BillDetailModel {
    private String _id;
    private String billId;
    private String productId;
    private int quantity;
    private double price;
    private  String name;
    private double total;

    // Constructors
    public BillDetailModel() {
    }

    public BillDetailModel(String productId, int quantity, double price,String name,String billId) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.total = quantity * price;
        this.name=name;
        this.billId=billId;
    }

    // Getters and Setters
    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
