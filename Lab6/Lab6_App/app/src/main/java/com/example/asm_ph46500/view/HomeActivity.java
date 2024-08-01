package com.example.asm_ph46500.view;

import android.content.Intent;
import android.os.Bundle;
import android.service.controls.actions.FloatAction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_ph46500.MainActivity;
import com.example.asm_ph46500.R;
import com.example.asm_ph46500.adapter.DistributorAdapter;
import com.example.asm_ph46500.adapter.FruitAdapter;
import com.example.asm_ph46500.model.Distributor;
import com.example.asm_ph46500.model.Fruit;
import com.example.asm_ph46500.services.ApiServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {
    FloatingActionButton btnadd;
    TextInputEditText edt_ser_price, edt_ser_name;
    private RecyclerView recyclerView;
    private ArrayList<Fruit> listModel;

    private FruitAdapter fruitAdapter;
    private ApiServices apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.rcv_fruit);
        listModel = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiServices.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiServices.class);
        fruitAdapter = new FruitAdapter(listModel, HomeActivity.this, apiService);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        recyclerView.setAdapter(fruitAdapter);

        fetchFruitData();
        edt_ser_name = findViewById(R.id.ed_search_name);
        edt_ser_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edt_ser_price = findViewById(R.id.ed_search_money);
        edt_ser_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMoney(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnadd = findViewById(R.id.btn_add);
        btnadd.setOnClickListener(v -> {
                    startActivity(new Intent(HomeActivity.this, AddFruitsActivity.class));
                    setupRecyclerView();
                    loadFruits();
                }

        );

    }

    private void filterName(String text) {
        ArrayList<Fruit> filteredList1 = new ArrayList<>();

        for (Fruit fruit : listModel) {
            if (fruit.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList1.add(fruit);
            }
        }
        fruitAdapter.updateList(filteredList1);

    }

    private void filterMoney(String text) {
        ArrayList<Fruit> filteredList1 = new ArrayList<>();

        for (Fruit fruit : listModel) {
            if (fruit.getPrice().toLowerCase().contains(text.toLowerCase())) {
                filteredList1.add(fruit);
            }
        }
        fruitAdapter.updateList(filteredList1);

    }

    private void fetchFruitData() {
        Call<ArrayList<Fruit>> call = apiService.getListFruit();
        call.enqueue(new Callback<ArrayList<Fruit>>() {
            @Override
            public void onResponse(Call<ArrayList<Fruit>> call, Response<ArrayList<Fruit>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listModel.clear();
                    listModel.addAll(response.body());
                    fruitAdapter.notifyDataSetChanged();
                } else {
                    Log.e("Main", "Lỗi phản hồi: " + response.message());
                    Toast.makeText(HomeActivity.this, "Không tải được nhà phân phối", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Fruit>> call, Throwable t) {
                Log.e("Main", "Lỗi: ", t);
                Toast.makeText(HomeActivity.this, "Lỗi mạng. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        fruitAdapter = new FruitAdapter(listModel, HomeActivity.this, apiService);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fruitAdapter);
    }

    private void loadFruits() {
        Call<ArrayList<Fruit>> call = apiService.getListFruit();
        call.enqueue(new Callback<ArrayList<Fruit>>() {
            @Override
            public void onResponse(Call<ArrayList<Fruit>> call, Response<ArrayList<Fruit>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listModel.addAll(response.body());
                    fruitAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to load fruits", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Fruit>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}