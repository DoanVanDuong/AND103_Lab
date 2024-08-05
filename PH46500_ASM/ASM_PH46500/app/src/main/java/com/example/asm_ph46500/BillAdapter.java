package com.example.asm_ph46500;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private List<BillModel> billList;
    private Context context;
    private APIService apiService;

    public BillAdapter(Context context, List<BillModel> billList, APIService apiService) {
        this.context = context;
        this.billList = billList;
        this.apiService = apiService;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bill_detail_item, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        BillModel bill = billList.get(position);

        // Set bill status and total amount
        holder.tvBillStatus.setText("Status: " + bill.getStatus());
        holder.tvTotalAmount.setText("Total Amount: " + String.format("$%.2f",bill.getTotalAmount()));

        // Hide or show the Pay button based on the bill status
        if ("Paid".equals(bill.getStatus())) {
            holder.btnPay.setVisibility(View.GONE);
        } else {
            holder.btnPay.setVisibility(View.VISIBLE);
        }

        // Fetch and display products in RecyclerView
        Call<List<CarModel>> productsCall = apiService.getProductsFromBillDetail(bill.getId());
        productsCall.enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CarModel> products = response.body();
                    // Update RecyclerView with products
                    BillDetailAdapter productsAdapter = new BillDetailAdapter(context, products);
                    holder.rcv.setLayoutManager(new LinearLayoutManager(context));
                    holder.rcv.setAdapter(productsAdapter);
                } else {
                    Toast.makeText(context, "No products found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CarModel>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up Pay button click listener
        holder.btnPay.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Payment")
                    .setMessage("Do you want to pay this bill?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Call<BillModel> call = apiService.update_bill_status(bill.getId());
                        call.enqueue(new Callback<BillModel>() {
                            @Override
                            public void onResponse(Call<BillModel> call, Response<BillModel> response) {
                                if (response.isSuccessful()) {
                                    // Update bill status and refresh the RecyclerView
                                    bill.setStatus("Paid");
                                    notifyDataSetChanged(); // Refresh the RecyclerView
                                    Toast.makeText(context, "Bill paid successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to pay the bill", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<BillModel> call, Throwable t) {
                                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView tvBillStatus, tvTotalAmount;
        Button btnPay;
        RecyclerView rcv;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBillStatus = itemView.findViewById(R.id.tvBillStatus);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            btnPay = itemView.findViewById(R.id.btnPay);
            rcv = itemView.findViewById(R.id.rvBillProducts);
        }
    }
}
