package com.example.cite_ims;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        sessionManager = new UserSessionManager(this);

        // Validate session
        if (!sessionManager.isUserLoggedIn() || !"admin".equals(sessionManager.getRole())) {
            sessionManager.logoutUser(); // Ensure only admins can access this activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.inflateMenu(R.menu.admin_menu); // Ensure menu resource exists
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_inventory:
                    selectedFragment = new InventoryFragment();
                    break;
                case R.id.nav_users:
                    selectedFragment = new UserManagementFragment();
                    break;
                case R.id.nav_reports:
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
        bottomNavigationView.setSelectedItemId(R.id.nav_inventory);
    }
}
