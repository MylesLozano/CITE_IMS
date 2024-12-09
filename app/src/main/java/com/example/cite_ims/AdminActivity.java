package com.example.cite_ims;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 100;

    private InventoryAdapter inventoryAdapter;
    private DBHelper dbHelper;
    private List<InventoryItem> inventoryList = new ArrayList<>();
    private Uri imageUri;  // Variable to store selected image URI

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DBHelper(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ImageButton addItemButton = findViewById(R.id.addItemButton);
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

        // Register the ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                    }
                }
        );
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

        final ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.ic_add_image); // Placeholder image
        layout.addView(imageView);

        imageView.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestStoragePermission();
            }
        });

        builder.setView(layout);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameEditText.getText().toString().trim();
            int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
            String imagePath = imageUri != null ? imageUri.toString() : null;
            dbHelper.addItem(name, quantity, imagePath);
            loadInventoryItems();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This permission is needed to select images from your gallery")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(AdminActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Denied")
                    .setMessage("Permission to access storage is required to select images")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .create().show();
        }
    }
}
