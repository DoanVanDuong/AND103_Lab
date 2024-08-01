package com.example.asm_ph46500.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Fruit implements Parcelable {
    private String _id;
    private String name;
    private String quantity;
    private String status;
    private String price;
    private String description;
    private ArrayList<String> image;

    @SerializedName("id_distributor")
    private Distributor distributor;

    public Fruit(String _id, String name, String quantity, String status, String price, String description, ArrayList<String> image, Distributor distributor) {
        this._id = _id;
        this.name = name;
        this.quantity = quantity;
        this.status = status;
        this.price = price;
        this.description = description;
        this.image = image;
        this.distributor = distributor;
    }

    protected Fruit(Parcel in) {
        _id = in.readString();
        name = in.readString();
        quantity = in.readString();
        status = in.readString();
        price = in.readString();
        description = in.readString();
        image = in.createStringArrayList();
        distributor = in.readParcelable(Distributor.class.getClassLoader());
    }

    public static final Creator<Fruit> CREATOR = new Creator<Fruit>() {
        @Override
        public Fruit createFromParcel(Parcel in) {
            return new Fruit(in);
        }

        @Override
        public Fruit[] newArray(int size) {
            return new Fruit[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(quantity);
        dest.writeString(status);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeStringList(image);
        dest.writeParcelable(distributor, flags);
    }

    // Default constructor
    public Fruit() {}

    // Getters and setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public Distributor getDistributor() {
        return distributor;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }
}
