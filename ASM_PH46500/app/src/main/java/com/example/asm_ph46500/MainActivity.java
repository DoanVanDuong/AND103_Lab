package com.example.asm_ph46500;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    ListView lvMain;
    List<CarModel> listCarModel;
    ImageButton btnAdd;
    CarAdapter carAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvMain = findViewById(R.id.listviewMain);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);

        Call<List<CarModel>> call = apiService.getCars();

        call.enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                if (response.isSuccessful()) {
                    listCarModel = response.body();
                    carAdapter = new CarAdapter(MainActivity.this, listCarModel);
                    lvMain.setAdapter(carAdapter);
                } else {
                    Log.e("Main", "Response error: " + response.message());
                    Toast.makeText(MainActivity.this, "Failed to load cars", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CarModel>> call, Throwable t) {
                Log.e("Main", "Error: ", t);
                Toast.makeText(MainActivity.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });

        btnAdd = findViewById(R.id.imgBtnAdd);
        btnAdd.setOnClickListener(v -> showAddCarDialog(apiService));
    }

    private void showAddCarDialog(APIService apiService) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.iteam_add_update, null);
        builder.setView(dialogView);

        EditText edtTen = dialogView.findViewById(R.id.edtTen);
        EditText edtNam = dialogView.findViewById(R.id.edtNam);
        EditText edtHang = dialogView.findViewById(R.id.edtHang);
        EditText edtGia = dialogView.findViewById(R.id.edtGia);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String ten = edtTen.getText().toString().trim();
            String namSXStr = edtNam.getText().toString().trim();
            String hang = edtHang.getText().toString().trim();
            String giaStr = edtGia.getText().toString().trim();

            if (ten.isEmpty() || namSXStr.isEmpty() || hang.isEmpty() || giaStr.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int namSX;
            double gia;

            try {
                namSX = Integer.parseInt(namSXStr);
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Invalid year", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                gia = Double.parseDouble(giaStr);
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }

            CarModel newCar = new CarModel("a", ten, namSX, hang, gia);

            Call<CarModel> call = apiService.addCar(newCar);

            call.enqueue(new Callback<CarModel>() {
                @Override
                public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                    if (response.isSuccessful()) {
                        CarModel addedCar = response.body();
                        if (addedCar != null) {
                            // Show confirmation dialog to the user
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Confirm")
                                    .setMessage("Do you want to see the details?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        Call<CarModel> call1 = apiService.getCar(addedCar.get_id());
                                        call1.enqueue(new Callback<CarModel>() {
                                            @Override
                                            public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                                                if (response.isSuccessful()) {
                                                    CarModel detailedCar = response.body();
                                                    if (detailedCar != null) {
                                                        listCarModel.add(detailedCar);
                                                        carAdapter.notifyDataSetChanged();
                                                        Toast.makeText(MainActivity.this, "Car details loaded", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(MainActivity.this, "Failed to get car details: " + response.message(), Toast.LENGTH_SHORT).show();
                                                    Log.e("AddCar", "Get car details error: " + response.errorBody().toString());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<CarModel> call, Throwable t) {
                                                Toast.makeText(MainActivity.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
                                                Log.e("AddCar", "Get car details error: ", t);
                                            }
                                        });
                                    })
                                    .setNegativeButton("No", (dialog, which) -> {
                                        Toast.makeText(MainActivity.this, "Car added successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to add car: " + response.message(), Toast.LENGTH_SHORT).show();
                        Log.e("AddCar", "Response error: " + response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<CarModel> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
                    Log.e("AddCar", "Error: ", t);
                }
            });

            dialog.dismiss();
        });
    }

}
