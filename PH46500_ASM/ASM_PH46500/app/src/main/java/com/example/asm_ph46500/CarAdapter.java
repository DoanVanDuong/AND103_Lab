package com.example.asm_ph46500;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarAdapter extends BaseAdapter {
    private List<CarModel> carModelList;
    private Context context;
    private String userEmail;
    public CarAdapter(Context context, List<CarModel> carModelList, String userEmail) {
        this.context = context;
        this.carModelList = carModelList;
        this.userEmail = userEmail;
    }

    @Override
    public int getCount() {
        return carModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return carModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_car, viewGroup, false);

        TextView tvID = rowView.findViewById(R.id.tvId);
        ImageView imgAvatar = rowView.findViewById(R.id.imgAvatatr);
        TextView tvName = rowView.findViewById(R.id.tvName);
        ImageButton btnUp = rowView.findViewById(R.id.imgBtnUp);
        ImageButton btnDe = rowView.findViewById(R.id.imgBtnDe);
        TextView tvNamSX = rowView.findViewById(R.id.tvNamSX);
        TextView tvHang = rowView.findViewById(R.id.tvHang);
        TextView tvGia = rowView.findViewById(R.id.tvGia);

        final CarModel car = carModelList.get(position);

        tvName.setText(String.valueOf(car.getTen()));
        tvNamSX.setText(String.valueOf(car.getNamSX()));
        tvHang.setText(String.valueOf(car.getHang()));
        tvGia.setText(String.valueOf(car.getGia()));
        // Delete button click listener
        btnDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCar(car.get_id());
            }
        });
        btnUp.setOnClickListener(v -> showUpdateDialog(car));
        rowView.setOnClickListener(v -> showCarDetailsDialog(car));
        btnDe.setVisibility(isAdmin(userEmail) ? View.VISIBLE : View.GONE);
        btnUp.setVisibility(isAdmin(userEmail) ? View.VISIBLE : View.GONE);

        return rowView;

    }

    private void deleteCar(String carId) {
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirm Delete");
            builder.setMessage("Are you sure you want to delete this car?");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(APIService.DOMAIN)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    APIService apiService = retrofit.create(APIService.class);

                    Call<Void> call = apiService.deleteCar(carId);

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                removeCar(carId);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Car deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to delete car", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(context, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(context, "Unable to display dialog. Activity is not running.", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeCar(String carId) {
        // Find and remove the car from the list
        for (CarModel car : carModelList) {
            if (car.get_id().equals(carId)) {
                carModelList.remove(car);
                break;
            }
        }
    }

    private void showUpdateDialog(CarModel car) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Car");

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.iteam_add_update, null);
        builder.setView(dialogView);

        EditText edtTen = dialogView.findViewById(R.id.edtTen);
        EditText edtNam = dialogView.findViewById(R.id.edtNam);
        EditText edtHang = dialogView.findViewById(R.id.edtHang);
        EditText edtGia = dialogView.findViewById(R.id.edtGia);

        edtTen.setText(car.getTen());
        edtNam.setText(String.valueOf(car.getNamSX()));
        edtHang.setText(car.getHang());
        edtGia.setText(String.valueOf(car.getGia()));
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {

        });
        builder.setPositiveButton("Update", (dialog, which) -> {
            String ten = edtTen.getText().toString().trim();
            String namSXStr = edtNam.getText().toString().trim();
            String hang = edtHang.getText().toString().trim();
            String giaStr = edtGia.getText().toString().trim();

            if (ten.isEmpty() || namSXStr.isEmpty() || hang.isEmpty() || giaStr.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int namSX;
            double gia;

            try {
                namSX = Integer.parseInt(namSXStr);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid year", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                gia = Double.parseDouble(giaStr);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }

            CarModel updatedCar = new CarModel(car.get_id(), ten, namSX, hang, gia);
            updateCar(updatedCar);
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateCar(CarModel car) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);

        Call<CarModel> call = apiService.updateCar(car.get_id(), car);
        call.enqueue(new Callback<CarModel>() {
            @Override
            public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                if (response.isSuccessful()) {
                    CarModel updatedCar = response.body();
                    if (updatedCar != null) {
                        for (int i = 0; i < carModelList.size(); i++) {
                            if (carModelList.get(i).get_id().equals(updatedCar.get_id())) {
                                carModelList.set(i, updatedCar);
                                break;
                            }
                        }
                        notifyDataSetChanged();
                        Toast.makeText(context, "Car updated successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to update car: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CarModel> call, Throwable t) {
                Toast.makeText(context, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isAdmin(String email) {
        return "admin@gmail.com".equals(email);
    }

    private void showCarDetailsDialog(CarModel car) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Car Details");

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_car_details, null);
        builder.setView(dialogView);

        TextView tvName = dialogView.findViewById(R.id.tvName);
        TextView tvNamSX = dialogView.findViewById(R.id.tvNamSX);
        TextView tvHang = dialogView.findViewById(R.id.tvHang);
        TextView tvGia = dialogView.findViewById(R.id.tvGia);

        tvName.setText(car.getTen());
        tvNamSX.setText(String.valueOf(car.getNamSX()));
        tvHang.setText(car.getHang());
        tvGia.setText(String.valueOf(car.getGia()));
       if(!isAdmin(userEmail)){
        builder.setPositiveButton("Add to Cart", (dialog, which) -> addToCart(car));

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();}
    }

    private void addToCart(CarModel car) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);

        String userId = "1"; // Use the actual user ID

        // First, retrieve the existing cart
        Call<CartModel> getCartCall = apiService.getCart(userId);
        getCartCall.enqueue(new Callback<CartModel>() {
            @Override
            public void onResponse(Call<CartModel> call, Response<CartModel> response) {
                if (response.isSuccessful()) {
                    CartModel existingCart = response.body();
                    if (existingCart != null) {
                        // If cart exists, add the new item to the cart
                        List<CartItemModel> cartItems = existingCart.getItems();

                        // Check if the item already exists in the cart
                        boolean itemExists = false;
                        for (CartItemModel item : cartItems) {
                            if (item.getProductId().equals(car.get_id())) {
                                // If the item exists, just update the quantity
                                item.setQuantity(item.getQuantity() + 1);
                                itemExists = true;
                                break;
                            }
                        }

                        // If the item doesn't exist, add it to the cart
                        if (!itemExists) {
                            CartItemModel cartItem = new CartItemModel(car.get_id(), 1, car.getTen(), car.getGia());
                            cartItems.add(cartItem);
                        }

                        // Update the total price
                        double totalPrice = 0;
                        for (CartItemModel item : cartItems) {
                            totalPrice += item.getPrice() * item.getQuantity();
                        }

                        existingCart.setItems(cartItems);
                        existingCart.setTotalPrice(totalPrice);

                        // Send the updated cart to the server
                        Call<CartModel> updateCartCall = apiService.updateCartId(existingCart.getId(),existingCart);
                        updateCartCall.enqueue(new Callback<CartModel>() {
                            @Override
                            public void onResponse(Call<CartModel> call, Response<CartModel> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(context, "Added to cart successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to update cart: " + response.message(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<CartModel> call, Throwable t) {
                                Toast.makeText(context, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else if (response.code() == 404) {
                    // If cart not found (404), create a new one
                    createNewCart(apiService, car);
                } else {
                    Toast.makeText(context, "Failed to retrieve cart: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartModel> call, Throwable t) {
                Toast.makeText(context, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void createNewCart(APIService apiService, CarModel car) {
        List<CartItemModel> cartItems = new ArrayList<>();
        CartItemModel cartItem = new CartItemModel(car.get_id(), 1, car.getTen(), car.getGia());
        cartItems.add(cartItem);

        String userId = "1";
        double totalPrice = car.getGia() * cartItem.getQuantity();
        CartModel newCart = new CartModel(userId, cartItems, totalPrice);

        Call<CartModel> call = apiService.addCart(newCart);
        call.enqueue(new Callback<CartModel>() {
            @Override
            public void onResponse(Call<CartModel> call, Response<CartModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "New cart created and item added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to create cart: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartModel> call, Throwable t) {
                Toast.makeText(context, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
