package com.example.asm_ph46500.services;


import com.example.asm_ph46500.model.Distributor;
import com.example.asm_ph46500.model.Fruit;
import com.example.asm_ph46500.model.User;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiServices {
    String DOMAIN= "http://192.168.38.214:3000/";

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
    @Multipart
    @POST("api/register-send-email")
    Call<User> register(
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("email") RequestBody email,
            @Part("name") RequestBody name,
            @Part MultipartBody.Part avartar
    );

    @POST("api/login")
    Call<User> login (@Body User user);

    @GET("api/get-all-fruit")
    Call<ArrayList<Fruit>> getListFruit();

    @Multipart
    @POST("api/add-fruit-with-file-image")
    Call<Fruit> addFruitWithFileImage(@PartMap Map<String, RequestBody> requestBodyMap,
                                                @Part ArrayList<MultipartBody.Part> ds_hinh
    );


    @GET("api/get-page-fruit")
    Call<ArrayList<Fruit>> getPageFruit( @QueryMap Map<String, String> stringMap);


    @Multipart
    @PUT("api/update-fruit-by-id/{id}")
    Call<Fruit> updateFruitWithFileImage(@PartMap Map<String, RequestBody> requestBodyMap,
                                                   @Path("id") String id,
                                                   @Part ArrayList<MultipartBody.Part> ds_hinh
    );

    @DELETE("api/destroy-fruit-by-id/{id}")
    Call<Fruit> deleteFruits(@Path("id") String id);

    @GET("api/get-fruit-by-id/{id}")
    Call<Fruit> getFruitById (@Path("id") String id);



}

