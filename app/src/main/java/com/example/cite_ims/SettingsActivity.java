package com.example.cite_ims;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadThemePreference(); // Load the theme before setting the content view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new DBHelper(this);

        Button changeUsernameButton = findViewById(R.id.changeUsernameButton);
        Button changePasswordButton = findViewById(R.id.changePasswordButton);
        Button enableNotificationsButton = findViewById(R.id.enableNotificationsButton);
        Button themeSelectionButton = findViewById(R.id.themeSelectionButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button deleteAccountButton = findViewById(R.id.deleteAccountButton);
        Button appVersionButton = findViewById(R.id.appVersionButton);
        Button termsOfServiceButton = findViewById(R.id.termsOfServiceButton);
        Button privacyPolicyButton = findViewById(R.id.privacyPolicyButton);

        changeUsernameButton.setOnClickListener(v -> showChangeUsernameDialog());

        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());

        enableNotificationsButton.setOnClickListener(v -> toggleNotifications());

        themeSelectionButton.setOnClickListener(v -> showThemeSelectionDialog());

        logoutButton.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finish();
        });

        deleteAccountButton.setOnClickListener(v -> showDeleteAccountDialog());

        appVersionButton.setOnClickListener(v -> showAppVersion());

        termsOfServiceButton.setOnClickListener(v -> showTermsOfService());

        privacyPolicyButton.setOnClickListener(v -> showPrivacyPolicy());
    }

    private void loadThemePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        String theme = sharedPreferences.getString("theme", "Light");
        if (theme.equals("Light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (theme.equals("Dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private void saveThemePreference(String theme) {
        SharedPreferences sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", theme);
        editor.apply();
    }

    private void showThemeSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Theme");

        String[] themes = {"Light", "Dark"};
        builder.setItems(themes, (dialog, which) -> {
            String selectedTheme = themes[which];
            if (selectedTheme.equals("Light")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (selectedTheme.equals("Dark")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            // Save the selected theme to SharedPreferences
            saveThemePreference(selectedTheme);
            recreate(); // Restart the activity to apply the theme change
        });

        builder.show();
    }

    private void toggleNotifications() {
        SharedPreferences sharedPreferences = getSharedPreferences("notification_prefs", MODE_PRIVATE);
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        notificationsEnabled = !notificationsEnabled;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifications_enabled", notificationsEnabled);
        editor.apply();

        String message = notificationsEnabled ? "Notifications enabled" : "Notifications disabled";
        Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showChangeUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Username");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText oldUsernameEditText = new EditText(this);
        oldUsernameEditText.setHint("Old Username");
        layout.addView(oldUsernameEditText);

        final EditText newUsernameEditText = new EditText(this);
        newUsernameEditText.setHint("New Username");
        layout.addView(newUsernameEditText);

        builder.setView(layout);
        builder.setPositiveButton("Change", (dialog, which) -> {
            String oldUsername = oldUsernameEditText.getText().toString().trim();
            String newUsername = newUsernameEditText.getText().toString().trim();
            if (dbHelper.updateUsername(oldUsername, newUsername)) {
                Toast.makeText(SettingsActivity.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingsActivity.this, "Failed to update username", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText usernameEditText = new EditText(this);
        usernameEditText.setHint("Username");
        layout.addView(usernameEditText);

        final EditText newPasswordEditText = new EditText(this);
        newPasswordEditText.setHint("New Password");
        newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(newPasswordEditText);

        builder.setView(layout);
        builder.setPositiveButton("Change", (dialog, which) -> {
            String username = usernameEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            if (dbHelper.updatePassword(username, newPassword)) {
                Toast.makeText(SettingsActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingsActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText usernameEditText = new EditText(this);
        usernameEditText.setHint("Username");
        layout.addView(usernameEditText);

        builder.setView(layout);
        builder.setPositiveButton("Delete", (dialog, which) -> {
            String username = usernameEditText.getText().toString().trim();
            if (dbHelper.deleteUser(username)) {
                Toast.makeText(SettingsActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(SettingsActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showAppVersion() {
        Toast.makeText(this, "App Version: 1.0.0", Toast.LENGTH_SHORT).show();
    }

    private void showTermsOfService() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms of Service");
        builder.setMessage("Here are the terms of service...");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void showPrivacyPolicy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Privacy Policy");
        builder.setMessage("Here is the privacy policy...");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
