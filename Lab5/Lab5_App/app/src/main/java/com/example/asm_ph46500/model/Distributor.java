package com.example.asm_ph46500.model;


public class Distributor {

    private String _id;
    private String name ;

    public Distributor(String id, String name) {
        this._id = id;
        this.name = name;

    }

    public Distributor() {
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
