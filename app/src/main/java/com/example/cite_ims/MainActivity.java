package com.example.cite_ims;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Determine if user is admin or regular user (replace with actual logic)
        boolean isAdmin = determineUserType(); // Implement this method to check user type

        // Inflate the appropriate menu
        if (isAdmin) {
            bottomNavigationView.inflateMenu(R.menu.admin_menu);
        } else {
            bottomNavigationView.inflateMenu(R.menu.user_menu);
        }

        // Handle navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_inventory:
                    selectedFragment = new InventoryFragment();
                    break;
                case R.id.nav_profile: // For user_menu
                case R.id.nav_users:   // For admin_menu
                    selectedFragment = new UserManagementFragment();
                    break;
                case R.id.nav_reports: // For admin_menu only
                    selectedFragment = new ReportsFragment();
                    break;
                default:
                    return false;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment)
                    .commit();
            return true;
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.nav_inventory); // Default fragment
    }
    private boolean determineUserType() {
        // Replace with actual user type check logic, e.g., from session, database, or intent extras
        UserSessionManager sessionManager = new UserSessionManager(this);
        String role = sessionManager.getRole(); // Assume "admin" or "user"
        return "admin".equals(role);
    }
}