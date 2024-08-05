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
    String DOMAIN = "http://192.168.179.214:3000/";

    @GET("/api/list")
    Call<List<CarModel>> getCars();

    @GET("/api/car/{id}")
    Call<CarModel> getCar(@Path("id") String carId);

    @DELETE("/api/delete/{id}")
    Call<Void> deleteCar(@Path("id") String id);

    @POST("/api/add")
    Call<CarModel> addCar(@Body CarModel carModel);

    @PUT("/api/update/{id}")
    Call<CarModel> updateCar(@Path("id") String carId, @Body CarModel car);

    @GET("/api/cart/{userId}")
    Call<CartModel> getCart(@Path("userId") String userId);

    @POST("/api/addcart")
    Call<CartModel> addCart(@Body CartModel cartModel);

    @PUT("/api/updatecart/{userId}")
    Call<CartModel> updateCart(@Path("userId") String userId, @Body CartModel cartModel);

    @PUT("/api/updatecart/{cartId}")
    Call<CartModel> updateCartId(@Path("cartId") String cartId, @Body CartModel cartModel);

    @DELETE("/api/deletecart/{userId}")
    Call<Void> deleteCart(@Path("userId") String userId);

    @DELETE("/api/deleteSp/{itemId}")
    Call<Void> deleteCartId(@Path("itemId") String itemId);

    // Bill APIs
    @GET("/api/bill/{userId}")
    Call<List<BillModel>> getBills(@Path("userId") String userId);


    @POST("/api/addbill")
    Call<BillModel> addBill(@Body BillModel billModel);

    @PUT("/api/updatebill/{id}")
    Call<BillModel> updateBill(@Path("id") String billId, @Body BillModel billModel);

    @PUT("/api/update-bill-status/{idBill}")
    Call<BillModel> update_bill_status(@Path("idBill") String billId);

    @DELETE("/api/deletebill/{id}")
    Call<Void> deleteBill(@Path("id") String billId);

    // BillDetail APIs
    @GET("/api/billdetail/{id}")
    Call<BillDetailModel> getBillDetail(@Path("id") String billDetailId);

    @POST("/api/addbilldetail")
    Call<BillDetailModel> addBillDetail(@Body BillDetailModel billDetailModel);

    @PUT("/api/updatebilldetail/{id}")
    Call<BillDetailModel> updateBillDetail(@Path("id") String billDetailId, @Body BillDetailModel billDetailModel);

    @DELETE("/api/deletebilldetail/{id}")
    Call<Void> deleteBillDetail(@Path("id") String billDetailId);
    @GET("/api/products-from-billdetail/{billId}")
    Call<List<CarModel>> getProductsFromBillDetail(@Path("billId") String billId);

}

