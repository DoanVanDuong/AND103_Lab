package com.example.asm_ph46500;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnQuantityChangeListener {

    private static final String TAG = "CartActivity";
    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private List<CartItemModel> cartItems;
    private TextView textViewTotalPrice;
    private Button buttonCheckout, buttonThanhToan;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        recyclerViewCart = findViewById(R.id.rvCartItems);
        textViewTotalPrice = findViewById(R.id.tvTotalPrice);
        buttonThanhToan = findViewById(R.id.btnBuyNow);
        buttonCheckout = findViewById(R.id.btnCheckout);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIService.class);

        // Fetch cart items
        fetchCartItems();

        // Set up button click listeners
        buttonThanhToan.setOnClickListener(v -> handlePayment());
        buttonCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, BillActivity.class);
            startActivity(intent);
        });
    }

    private void fetchCartItems() {
        Call<CartModel> call = apiService.getCart("1");
        call.enqueue(new Callback<CartModel>() {
            @Override
            public void onResponse(Call<CartModel> call, Response<CartModel> response) {
                if (response.isSuccessful()) {
                    CartModel cartModel = response.body();
                    if (cartModel != null) {
                        cartItems = cartModel.getItems();
                        setupRecyclerView();
                        updateTotalPrice();
                    }
                } else {
                    Log.e(TAG, "Request failed. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CartModel> call, Throwable t) {
                Log.e(TAG, "Request failed: " + t.getMessage());
            }
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCart.setAdapter(cartAdapter);
    }

    @Override
    public void onQuantityChanged() {
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        textViewTotalPrice.setText(String.format("Total: $%.2f", totalPrice));
    }

    private void handlePayment() {
        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        StringBuilder productsSummary = new StringBuilder();
        for (CartItemModel item : cartItems) {
            productsSummary.append(item.getName())
                    .append(" (x")
                    .append(item.getQuantity())
                    .append(")\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Purchase")
                .setMessage("Products:\n" + productsSummary.toString() + "\nTotal: $" + String.format("%.2f", totalAmount))
                .setPositiveButton("Confirm", (dialog, which) -> createBillAndBillDetails(totalAmount))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createBillAndBillDetails(double totalAmount) {
        BillModel bill = new BillModel("1", "Pending Payment",
                cartItems.stream().map(CartItemModel::getProductId).collect(Collectors.toList()), totalAmount);

        apiService.addBill(bill).enqueue(new Callback<BillModel>() {
            @Override
            public void onResponse(Call<BillModel> call, Response<BillModel> response) {
                if (response.isSuccessful()) {
                    BillModel createdBill = response.body();
                    if (createdBill != null) {
                        createBillDetails(createdBill.getId());
                    }
                } else {
                    Log.e(TAG, "Failed to create bill: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<BillModel> call, Throwable t) {
                Log.e(TAG, "Error creating bill: " + t.getMessage());
            }
        });
    }

    private void createBillDetails(String billId) {
        for (CartItemModel item : cartItems) {
            BillDetailModel billDetail = new BillDetailModel(
                    item.getProductId(),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getName(),
                    billId
            );

            apiService.addBillDetail(billDetail).enqueue(new Callback<BillDetailModel>() {
                @Override
                public void onResponse(Call<BillDetailModel> call, Response<BillDetailModel> response) {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Failed to add bill detail: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<BillDetailModel> call, Throwable t) {
                    Log.e(TAG, "Error adding bill detail: " + t.getMessage());
                }
            });
        }
    }
}
