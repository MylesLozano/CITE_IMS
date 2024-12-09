package com.example.cite_ims;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private InventoryAdapter inventoryAdapter;
    private DBHelper dbHelper;
    private List<InventoryItem> inventoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DBHelper(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Button addItemButton = findViewById(R.id.addItemButton);
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        SearchView searchView = findViewById(R.id.searchView);

        inventoryAdapter = new InventoryAdapter(inventoryList, dbHelper, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(inventoryAdapter);

        loadInventoryItems();

        addItemButton.setOnClickListener(v -> showAddItemDialog());

        settingsButton.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, SettingsActivity.class)));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                inventoryAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                inventoryAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void loadInventoryItems() {
        inventoryList = dbHelper.getAllItems();
        inventoryAdapter.updateItems(inventoryList);
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Item");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameEditText = new EditText(this);
        nameEditText.setHint("Name");
        layout.addView(nameEditText);

        final EditText quantityEditText = new EditText(this);
        quantityEditText.setHint("Quantity");
        quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(quantityEditText);

        builder.setView(layout);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameEditText.getText().toString().trim();
            int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
            dbHelper.addItem(name, quantity);
            loadInventoryItems();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
