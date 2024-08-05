package com.example.asm_ph46500;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItemModel> cartItems;
    private OnQuantityChangeListener onQuantityChangeListener;

    public CartAdapter(List<CartItemModel> cartItems, OnQuantityChangeListener listener) {
        this.cartItems = cartItems;
        this.onQuantityChangeListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemModel item = cartItems.get(position);
        holder.tvItemName.setText(item.getName());
        holder.tvItemPrice.setText(String.valueOf(item.getPrice()));
        holder.edtQuantity.setText(String.valueOf(item.getQuantity()));

        holder.btnDecrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.edtQuantity.getText().toString());
            if (quantity > 1) {
                quantity--;
                holder.edtQuantity.setText(String.valueOf(quantity));
                item.setQuantity(quantity);
                if (onQuantityChangeListener != null) {
                    onQuantityChangeListener.onQuantityChanged();
                }
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.edtQuantity.getText().toString());
            quantity++;
            holder.edtQuantity.setText(String.valueOf(quantity));
            item.setQuantity(quantity);
            if (onQuantityChangeListener != null) {
                onQuantityChangeListener.onQuantityChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView tvItemName, tvItemPrice;
        EditText edtQuantity;
        ImageButton btnDecrease, btnIncrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItem);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            edtQuantity = itemView.findViewById(R.id.edtQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
        }
    }
}
