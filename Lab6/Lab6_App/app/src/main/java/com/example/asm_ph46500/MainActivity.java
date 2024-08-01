package com.example.asm_ph46500;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.asm_ph46500.adapter.DistributorAdapter;
import com.example.asm_ph46500.model.Distributor;
import com.example.asm_ph46500.services.ApiServices;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvMain;
    private ArrayList<Distributor> listModel;
    private DistributorAdapter distributorAdapter;
    private ImageButton btnAdd;
    private EditText edSearch;
    private ApiServices apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvMain = findViewById(R.id.rcv_distributor);
        listModel = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiServices.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiServices.class);

        distributorAdapter = new DistributorAdapter(listModel, MainActivity.this, apiService);
        rvMain.setLayoutManager(new LinearLayoutManager(this));
        rvMain.setAdapter(distributorAdapter);

        fetchDistributorData();

        btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(v -> showAddDistributorDialog());

        edSearch = findViewById(R.id.ed_search);
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchDistributorData() {
        Call<ArrayList<Distributor>> call = apiService.getListDistributor();
        call.enqueue(new Callback<ArrayList<Distributor>>() {
            @Override
            public void onResponse(Call<ArrayList<Distributor>> call, Response<ArrayList<Distributor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listModel.clear();
                    listModel.addAll(response.body());
                    distributorAdapter.notifyDataSetChanged();
                } else {
                    Log.e("Main", "Lỗi phản hồi: " + response.message());
                    Toast.makeText(MainActivity.this, "Không tải được nhà phân phối", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Distributor>> call, Throwable t) {
                Log.e("Main", "Lỗi: ", t);
                Toast.makeText(MainActivity.this, "Lỗi mạng. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddDistributorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add, null);
        builder.setView(dialogView);

        EditText edtName = dialogView.findViewById(R.id.et_name);
        Button btnSave = dialogView.findViewById(R.id.btn_submit);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng điền tất cả các trường", Toast.LENGTH_SHORT).show();
                return;
            }

            Distributor newDistributor = new Distributor("a", name);
            Call<Distributor> call = apiService.addDistributor(newDistributor);
            call.enqueue(new Callback<Distributor>() {
                @Override
                public void onResponse(Call<Distributor> call, Response<Distributor> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        fetchDistributorData();
                        distributorAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Đã thêm nhà phân phối thành công", Toast.LENGTH_SHORT).show();



                    } else {
                        Toast.makeText(MainActivity.this, "Không thêm được nhà phân phối: " + response.message(), Toast.LENGTH_SHORT).show();
                        Log.e("AddDistributor", "Lỗi phản hồi: " + response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<Distributor> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Lỗi mạng. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                    Log.e("AddDistributor", "Lỗi: ", t);
                }
            });
        });
    }

    private void filter(String text) {
        ArrayList<Distributor> filteredList = new ArrayList<>();
        for (Distributor distributor : listModel) {
            if (distributor.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(distributor);
            }
        }
        distributorAdapter.updateList(filteredList);
    }
}
