package com.example.asm_ph46500.services;


import com.example.asm_ph46500.model.Distributor;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServices {
    String DOMAIN= "http://192.168.1.189:3000/";

    @GET("api/get-list-distributor")
    Call<ArrayList<Distributor>> getListDistributor();
    @GET("api/get-distributor-by-id/{id}")
    Call<Distributor> getDistributor(@Path("id") String id);
    @GET("api/search-distributor")
    Call<ArrayList<Distributor>> searchDistributor(@Query("key") String key);

    @POST("api/add-distributor")
    Call<Distributor> addDistributor(@Body Distributor distributor);

    @PUT("api/update-distributor-by-id/{id}")
    Call<Distributor> updateDistributor(@Path("id") String id,@Body Distributor distributor);

    @DELETE("api/destroy-distributor-by-id/{id}")
    Call<Distributor> deleteDistributor(@Path("id") String id);
}

