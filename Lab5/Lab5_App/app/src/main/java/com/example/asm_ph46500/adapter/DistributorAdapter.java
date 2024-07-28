package com.example.asm_ph46500.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_ph46500.R;
import com.example.asm_ph46500.model.Distributor;
import com.example.asm_ph46500.services.ApiServices;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DistributorAdapter extends RecyclerView.Adapter<DistributorAdapter.DistributorViewHolder> {
    private ArrayList<Distributor> list;
    private Context context;
    private ApiServices apiService;

    public DistributorAdapter(ArrayList<Distributor> list, Context context, ApiServices apiService) {
        this.list = list;
        this.context = context;
        this.apiService = apiService;
    }

    @NonNull
    @Override
    public DistributorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.iteam_distributor, parent, false);
        return new DistributorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DistributorViewHolder holder, int position) {
        Distributor distributor = list.get(position);
        holder.tvName.setText(distributor.getName());
        holder.btnEdit.setOnClickListener(v -> {
            showUpdateDistributorDialog(distributor, position);
        });
        holder.btnDelete.setOnClickListener(v -> {
            deleteDistributor(distributor, position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class DistributorViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageButton btnEdit, btnDelete;

        public DistributorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    private void showUpdateDistributorDialog(Distributor distributor, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add, null);
        builder.setView(dialogView);

        EditText edtName = dialogView.findViewById(R.id.et_name);
        Button btnSave = dialogView.findViewById(R.id.btn_submit);
        edtName.setText(distributor.getName());

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(context, "Vui lòng điền vào tất cả các trường", Toast.LENGTH_SHORT).show();
                return;
            }
            distributor.setName(name);
            Call<Distributor> call = apiService.updateDistributor(distributor.getId(), distributor);
            call.enqueue(new Callback<Distributor>() {
                @Override
                public void onResponse(Call<Distributor> call, Response<Distributor> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        list.set(position, distributor);
                        notifyItemChanged(position);
                        dialog.dismiss();
                        Toast.makeText(context, "Distributor đã cập nhật thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Không cập nhật được nhà phân phối: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Distributor> call, Throwable t) {
                    Toast.makeText(context, "Lỗi mạng. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void deleteDistributor(Distributor distributor, int position) {
        Call<Distributor> call = apiService.deleteDistributor(distributor.getId());
        call.enqueue(new Callback<Distributor>() {
            @Override
            public void onResponse(Call<Distributor> call, Response<Distributor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                    Toast.makeText(context, "Distributor đã xóa thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Không xóa được distributor: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Distributor> call, Throwable t) {
                Toast.makeText(context, "Lỗi mạng. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updateList(ArrayList<Distributor> newList) {
        list = new ArrayList<>();
        list.addAll(newList);
        notifyDataSetChanged();
    }
}
