
package com.example.asm_ph46500.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Distributor implements Parcelable {
    @SerializedName("_id")
    private String id;
    private String name;

    // Constructor
    public Distributor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Parcelable implementation
    protected Distributor(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    public static final Creator<Distributor> CREATOR = new Creator<Distributor>() {
        @Override
        public Distributor createFromParcel(Parcel in) {
            return new Distributor(in);
        }

        @Override
        public Distributor[] newArray(int size) {
            return new Distributor[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

