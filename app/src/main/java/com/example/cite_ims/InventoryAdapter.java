package com.example.cite_ims;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> implements Filterable {
    private List<InventoryItem> inventoryList;
    private final List<InventoryItem> inventoryListFull; // Full copy of the inventory list for filtering
    private final DBHelper dbHelper;
    private final boolean isAdmin;

    public InventoryAdapter(List<InventoryItem> inventoryList, DBHelper dbHelper, boolean isAdmin) {
        this.inventoryList = inventoryList;
        this.inventoryListFull = new ArrayList<>(inventoryList); // Initialize the full list
        this.dbHelper = dbHelper;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryList.get(holder.getBindingAdapterPosition());
        holder.name.setText(item.getName());
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        if (isAdmin) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.editButton.setOnClickListener(v -> showEditItemDialog(holder, item, holder.getBindingAdapterPosition()));

            holder.deleteButton.setOnClickListener(v -> {
                dbHelper.deleteItem(item.getItemId());
                inventoryList.remove(holder.getBindingAdapterPosition());
                notifyItemRemoved(holder.getBindingAdapterPosition());
            });
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public void updateItems(List<InventoryItem> items) {
        this.inventoryList = items;
        this.inventoryListFull.clear();
        this.inventoryListFull.addAll(items); // Update the full list as well
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return inventoryFilter;
    }

    private final Filter inventoryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<InventoryItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(inventoryListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (InventoryItem item : inventoryListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            inventoryList.clear();
            inventoryList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity;
        Button editButton, deleteButton;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            quantity = itemView.findViewById(R.id.quantity);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    private void showEditItemDialog(InventoryViewHolder holder, InventoryItem item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Edit Item");

        LinearLayout layout = new LinearLayout(holder.itemView.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameEditText = new EditText(holder.itemView.getContext());
        nameEditText.setText(item.getName());
        layout.addView(nameEditText);

        final EditText quantityEditText = new EditText(holder.itemView.getContext());
        quantityEditText.setText(String.valueOf(item.getQuantity()));
        quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(quantityEditText);

        builder.setView(layout);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String name = nameEditText.getText().toString().trim();
            int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
            dbHelper.updateItem(item.getItemId(), name, quantity);
            item.setName(name);
            item.setQuantity(quantity);
            notifyItemChanged(position);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
