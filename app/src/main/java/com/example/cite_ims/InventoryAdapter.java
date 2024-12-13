package com.example.cite_ims;

import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> implements Filterable {
    private List<InventoryItem> inventoryList;
    private final List<InventoryItem> inventoryListFull;
    private final DBHelper dbHelper;
    private final boolean isAdmin;

    private final ActivityResultLauncher<String> requestPermissionLauncher;
    private final ActivityResultLauncher<String> newPhotoPicker;

    private InventoryItem currentEditingItem;

    private OnImageSelectedListener imageSelectedListener; // Store the listener

    // Interface for image selection callback
    public interface OnImageSelectedListener {
        void onImageSelected(Uri imageUri);
    }

    // Setter for image selection listener
    public void setOnImageSelectedListener(OnImageSelectedListener listener) {
        this.imageSelectedListener = listener;
    }

    // Constructor for Admin context
    public InventoryAdapter(List<InventoryItem> inventoryList, DBHelper dbHelper, boolean isAdmin,
                            ActivityResultLauncher<String> requestPermissionLauncher,
                            ActivityResultLauncher<String> newPhotoPicker) {
        this.inventoryList = inventoryList;
        this.inventoryListFull = new ArrayList<>(inventoryList);
        this.dbHelper = dbHelper;
        this.isAdmin = isAdmin;
        this.requestPermissionLauncher = requestPermissionLauncher;
        this.newPhotoPicker = newPhotoPicker;
    }

    // Constructor for User context
    public InventoryAdapter(List<InventoryItem> inventoryList, DBHelper dbHelper, boolean isAdmin) {
        this.inventoryList = inventoryList;
        this.inventoryListFull = new ArrayList<>(inventoryList);
        this.dbHelper = dbHelper;
        this.isAdmin = isAdmin;
        this.requestPermissionLauncher = null;
        this.newPhotoPicker = null;
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

        // Load the image using Glide
        if (item.getImageUri() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUri())
                    .placeholder(R.drawable.ic_placeholder_image)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_placeholder_image);
        }

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
        this.inventoryListFull.addAll(items);
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
            inventoryList.addAll((List<InventoryItem>) results.values);
            notifyDataSetChanged();
        }
    };

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity;
        ImageView imageView;
        Button editButton, deleteButton;

        InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            quantity = itemView.findViewById(R.id.quantity);
            imageView = itemView.findViewById(R.id.itemImageView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    private void showEditItemDialog(InventoryViewHolder holder, InventoryItem item, int position) {
        currentEditingItem = item;

        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Edit Item");

        LinearLayout layout = new LinearLayout(holder.itemView.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameEditText = new EditText(holder.itemView.getContext());
        nameEditText.setText(item.getName());
        layout.addView(nameEditText);

        final EditText quantityEditText = new EditText(holder.itemView.getContext());
        quantityEditText.setText(String.valueOf(item.getQuantity()));
        layout.addView(quantityEditText);

        final ImageView imageView = new ImageView(holder.itemView.getContext());

        // Load current image
        if (item.getImageUri() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUri())
                    .placeholder(R.drawable.ic_placeholder_image)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_placeholder_image);
        }

        layout.addView(imageView);

        imageView.setOnClickListener(v -> {
            if (requestPermissionLauncher != null && newPhotoPicker != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                newPhotoPicker.launch("image/*");
            }
        });

        builder.setView(layout);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String name = nameEditText.getText().toString();
            int quantity = Integer.parseInt(quantityEditText.getText().toString());

            item.setName(name);
            item.setQuantity(quantity);

            dbHelper.updateItem(item.getItemId(), name, quantity, item.getImageUri());
            notifyItemChanged(position);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Method to update the image URI for the current editing item
    public void updateCurrentItemImage(Uri imageUri) {
        if (currentEditingItem != null) {
            // Update the item with the new image URI
            currentEditingItem.setImageUri(imageUri.toString());

            // Update the item in the database
            dbHelper.updateItem(
                    currentEditingItem.getItemId(),
                    currentEditingItem.getName(),
                    currentEditingItem.getQuantity(),
                    imageUri.toString()
            );

            // Notify the adapter to update the UI
            notifyItemChanged(inventoryList.indexOf(currentEditingItem));
        }
    }
}