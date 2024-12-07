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
    private Button addButton;
    private RecyclerView inventoryRecyclerView;
    private InventoryAdapter inventoryAdapter;
    private ArrayList<InventoryItem> inventoryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        dbHelper = new DBHelper(getContext());

        nameEditText = view.findViewById(R.id.nameEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        quantityEditText = view.findViewById(R.id.quantityEditText);
        addButton = view.findViewById(R.id.addButton);
        inventoryRecyclerView = view.findViewById(R.id.inventoryRecyclerView);

        inventoryList = new ArrayList<>();
        inventoryAdapter = new InventoryAdapter(getContext(), inventoryList, dbHelper);
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inventoryRecyclerView.setAdapter(inventoryAdapter);

        loadInventory();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    loadInventory();
                    nameEditText.setText("");
                    descriptionEditText.setText("");
                    quantityEditText.setText("");
                } else {
                    Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void loadInventory() {
        inventoryList.clear();
        inventoryList.addAll(dbHelper.getAllInventoryItems());
        inventoryAdapter.notifyDataSetChanged();
    }
}
