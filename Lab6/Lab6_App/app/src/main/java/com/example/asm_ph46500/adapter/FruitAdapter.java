package com.example.asm_ph46500.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.asm_ph46500.R;
import com.example.asm_ph46500.model.Distributor;
import com.example.asm_ph46500.model.Fruit;
import com.example.asm_ph46500.services.ApiServices;
import com.example.asm_ph46500.view.UpdateFruitsActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.FruitViewHolder> {

    private ArrayList<Fruit> fruitList;
    private Context context;
    private ApiServices apiService;

    public FruitAdapter(ArrayList<Fruit> fruitList, Context context, ApiServices apiService) {
        this.fruitList = fruitList;
        this.context = context;
        this.apiService = apiService;
    }

    @NonNull
    @Override
    public FruitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fruit, parent, false);
        return new FruitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FruitViewHolder holder, int position) {
        Fruit fruit = fruitList.get(position);
        holder.tvName.setText(fruit.getName());
        holder.tvPrice.setText(String.valueOf(fruit.getPrice()));
        holder.tvQuantity.setText(String.valueOf(fruit.getQuantity()));

        String url = fruit.getImage().get(0);
        String newUrl = url.replace("localhost", "10.0.2.2");
        Glide.with(context)
                .load(newUrl)
                .thumbnail(Glide.with(context).load(R.drawable.image))
                .into(holder.imgFruit);

        holder.btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateFruitsActivity.class);
                intent.putExtra(

                        "fruit", fruit); // Passing the Fruit object
                context.startActivity(intent);
            }

        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFruit(fruit, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fruitList.size();
    }

    private void deleteFruit(Fruit fruit, int position) {
        Call<Fruit> call = apiService.deleteFruits(fruit.get_id());
        call.enqueue(new Callback<Fruit>() {
            @Override
            public void onResponse(Call<Fruit> call, Response<Fruit> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fruitList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, fruitList.size());
                    Toast.makeText(context, "Fruit đã xóa thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Không xóa được fruit: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Fruit> call, Throwable t) {
                Toast.makeText(context, "Lỗi mạng. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateList(ArrayList<Fruit> newList) {
        fruitList = new ArrayList<>();
        fruitList.addAll(newList);
        notifyDataSetChanged();
    }

    public class FruitViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        ImageView imgFruit;
        ImageButton btnUp, btnDelete;

        public FruitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_priceQuantity);
            tvQuantity = itemView.findViewById(R.id.tv_des);
            imgFruit = itemView.findViewById(R.id.img);
            btnUp = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
