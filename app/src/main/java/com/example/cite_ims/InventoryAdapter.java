package com.example.cite_ims;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private Context context;
    private ArrayList<InventoryItem> inventoryList;
    private DBHelper dbHelper;

    public InventoryAdapter(Context context, ArrayList<InventoryItem> inventoryList, DBHelper dbHelper) {
        this.context = context;
        this.inventoryList = inventoryList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.quantityTextView.setText("Quantity: " + item.getQuantity());
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, quantityTextView;

        public InventoryViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(android.R.id.text1);
            quantityTextView = itemView.findViewById(android.R.id.text2);
        }
    }
}
