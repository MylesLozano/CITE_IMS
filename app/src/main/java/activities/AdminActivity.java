package activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cite_ims.DBHelper;
import com.example.cite_ims.InventoryAdapter;
import com.example.cite_ims.InventoryItem;
import com.example.cite_ims.R;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private InventoryAdapter inventoryAdapter;
    private DBHelper dbHelper;
    private List<InventoryItem> inventoryList = new ArrayList<>();
    private Uri imageUri;  // Variable to store selected image URI

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<String> newPhotoPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DBHelper(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ImageButton addItemButton = findViewById(R.id.addItemButton);
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        ImageButton backButton = findViewById(R.id.backButton);
        SearchView searchView = findViewById(R.id.searchView);

        // Initialize the ActivityResultLaunchers before creating the adapter
        initializeActivityResultLaunchers();

        inventoryAdapter = new InventoryAdapter(inventoryList, dbHelper, true, requestPermissionLauncher, newPhotoPicker);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(inventoryAdapter);

        loadInventoryItems();

        addItemButton.setOnClickListener(v -> showAddItemDialog());

        settingsButton.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, SettingsActivity.class)));

        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

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

    private void initializeActivityResultLaunchers() {
        // Register the ActivityResultLaunchers
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                        newPhotoPicker.launch("image/*");
                    } else {
                        Toast.makeText(this, "Please grant permission", Toast.LENGTH_LONG).show();
                    }
                }
        );

        newPhotoPicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Log.d("AdminActivity", "Image selected: " + uri);
                        imageUri = uri;

                        // Update the item image
                        inventoryAdapter.updateCurrentItemImage(uri);
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
        imageView.setImageResource(R.drawable.ic_add_image);
        layout.addView(imageView);

        imageView.setOnClickListener(v -> {
            Log.d("AdminActivity", "ImageView clicked");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        builder.setView(layout);
        builder.setPositiveButton("Add", (dialog, which) -> {
            try {
                String name = nameEditText.getText().toString().trim();
                int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
                String imagePath = imageUri != null ? imageUri.toString() : null;
                Log.d("AdminActivity", "Adding item: " + name + ", Quantity: " + quantity + ", Image URI: " + imagePath);
                dbHelper.addItem(name, quantity, imagePath);
                loadInventoryItems();
            } catch (Exception e) {
                Log.e("AdminActivity", "Error adding item", e);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}