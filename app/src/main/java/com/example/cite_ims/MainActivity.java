package com.example.cite_ims;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up NavController with the NavHostFragment
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Set the default fragment when the app starts
        if (savedInstanceState == null) {
            navController.navigate(R.id.fragment_home); // Default to HomeFragment
        }

        // Set up the bottom navigation bar
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_inventory:
                    navController.navigate(R.id.fragment_inventory);  // Navigate to InventoryFragment
                    break;
                case R.id.nav_reports:
                    navController.navigate(R.id.fragment_reports);  // Navigate to ReportsFragment
                    break;
                case R.id.nav_profile:
                    navController.navigate(R.id.fragment_user_profile);  // Navigate to UserProfileFragment
                    break;
            }
            return true;
        });

        // Set the default fragment in bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_inventory); // Default to InventoryFragment
    }
}
