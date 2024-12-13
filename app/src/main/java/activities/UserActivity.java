package activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
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

public class UserActivity extends AppCompatActivity {
    private InventoryAdapter inventoryAdapter;
    private DBHelper dbHelper;
    private List<InventoryItem> inventoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        dbHelper = new DBHelper(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        ImageButton backButton = findViewById(R.id.backButton);
        SearchView searchView = findViewById(R.id.searchView);

        inventoryAdapter = new InventoryAdapter(inventoryList, dbHelper, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(inventoryAdapter);

        loadInventoryItems();

        settingsButton.setOnClickListener(v -> startActivity(new Intent(UserActivity.this, SettingsActivity.class)));

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

    private void loadInventoryItems() {
        inventoryList = dbHelper.getAllItems();
        inventoryAdapter.updateItems(inventoryList);
    }
}
