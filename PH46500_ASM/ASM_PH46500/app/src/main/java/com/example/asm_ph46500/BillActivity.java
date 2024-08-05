package com.example.asm_ph46500;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
// BillActivity.java
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BillActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    APIService apiService;
    private BillAdapter billAdapter;
    private List<BillModel> billList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(APIService.class);

        recyclerView = findViewById(R.id.recyclerViewBills);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchBills();
    }

    private void fetchBills() {
        Call<List<BillModel>> call = apiService.getBills("1");

        call.enqueue(new Callback<List<BillModel>>() {
            @Override
            public void onResponse(Call<List<BillModel>> call, Response<List<BillModel>> response) {
                billList = response.body();
                billAdapter = new BillAdapter(BillActivity.this, billList,apiService);
                recyclerView.setAdapter(billAdapter);
            }

            @Override
            public void onFailure(Call<List<BillModel>> call, Throwable t) {
                // Handle failure
            }
        });
    }
}
