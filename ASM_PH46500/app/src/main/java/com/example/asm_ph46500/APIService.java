package com.example.asm_ph46500;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {
    String DOMAIN = "http://192.168.1.189:3000/";

    @GET("/api/list")
    Call<List<CarModel>> getCars();
    @GET("api/car/{id}")
    Call<CarModel> getCar(@Path("id") String carId);
    @DELETE("api/delete/{id}")
    Call<Void> deleteCar(@Path("id") String id);
    @POST("/api/add")
    Call<CarModel> addCar(@Body CarModel carModel);
    @PUT("/update/{id}")
    Call<CarModel> updateCar(@Path("id") String carId, @Body CarModel car);

}

