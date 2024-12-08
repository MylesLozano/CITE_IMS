package com.example.cite_ims;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class InventoryFragment extends Fragment {
    private DBHelper dbHelper;
    private EditText nameEditText, descriptionEditText, quantityEditText;
    private InventoryAdapter inventoryAdapter;
    private ArrayList<InventoryItem> inventoryList;
    private InventoryItem selectedItem = null; // Track the selected item for update/delete

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        dbHelper = new DBHelper(getContext());
        nameEditText = view.findViewById(R.id.nameEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        quantityEditText = view.findViewById(R.id.quantityEditText);
        Button addButton = view.findViewById(R.id.addButton);
        Button updateButton = view.findViewById(R.id.updateButton);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        RecyclerView inventoryRecyclerView = view.findViewById(R.id.inventoryRecyclerView);

        inventoryList = new ArrayList<>();
        inventoryAdapter = new InventoryAdapter(getContext(), inventoryList, this::onItemSelected);
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inventoryRecyclerView.setAdapter(inventoryAdapter);

        loadInventory();

        addButton.setOnClickListener(v -> addItem());
        updateButton.setOnClickListener(v -> updateItem());
        deleteButton.setOnClickListener(v -> deleteItem());

        return view;
    }

    private void addItem() {
        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String quantityStr = quantityEditText.getText().toString();

        if (name.isEmpty() || description.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        long result = dbHelper.addInventoryItem(name, description, quantity);

        if (result != -1) {
            Toast.makeText(getContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
            clearInputFields();
            loadInventory();
        } else {
            Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateItem() {
        if (selectedItem == null) {
            Toast.makeText(getContext(), "Select an item to update", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String quantityStr = quantityEditText.getText().toString();

        if (name.isEmpty() || description.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        int result = dbHelper.updateInventoryItem(selectedItem.getId(), name, description, quantity);

        if (result > 0) {
            Toast.makeText(getContext(), "Item updated successfully", Toast.LENGTH_SHORT).show();
            clearInputFields();
            selectedItem = null;
            loadInventory();
        } else {
            Toast.makeText(getContext(), "Failed to update item", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteItem() {
        if (selectedItem == null) {
            Toast.makeText(getContext(), "Select an item to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.deleteInventoryItem(selectedItem.getId());
        Toast.makeText(getContext(), "Item deleted successfully", Toast.LENGTH_SHORT).show();
        clearInputFields();
        selectedItem = null;
        loadInventory();
    }

    private void loadInventory() {
        inventoryList.clear();
        inventoryList.addAll(dbHelper.getAllInventoryItems());
        inventoryAdapter.notifyDataSetChanged();
    }

    private void onItemSelected(InventoryItem item) {
        selectedItem = item;
        nameEditText.setText(item.getName());
        descriptionEditText.setText(item.getDescription());
        quantityEditText.setText(String.valueOf(item.getQuantity()));
    }

    private void clearInputFields() {
        nameEditText.setText("");
        descriptionEditText.setText("");
        quantityEditText.setText("");
    }
}
