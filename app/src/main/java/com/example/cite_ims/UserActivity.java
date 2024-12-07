package com.example.cite_ims;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Set the menu for the BottomNavigationView
        bottomNavigationView.inflateMenu(R.menu.user_menu);

        // Set up listener for navigation item selections
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Handle the navigation selection
            switch (item.getItemId()) {
                case R.id.nav_inventory:
                    selectedFragment = new InventoryFragment(); // Navigate to InventoryFragment
                    break;
                case R.id.nav_profile:
                    selectedFragment = new UserProfileFragment(); // Navigate to UserProfileFragment
                    break;
                default:
                    return false;
            }

            // Replace the current fragment with the selected one
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment)
                    .commit();
            return true;
        });

        // Set default fragment when the activity starts
        bottomNavigationView.setSelectedItemId(R.id.nav_inventory); // Default fragment
    }
}
