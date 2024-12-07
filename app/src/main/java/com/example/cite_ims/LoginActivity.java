package com.example.cite_ims;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private EditText usernameEditText, passwordEditText;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new UserSessionManager(this);

        // Check if the user is already logged in
        if (sessionManager.isUserLoggedIn()) {
            navigateToActivityBasedOnRole(sessionManager.getRole());
            finish(); // Close the login activity
            return;
        }

        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Fetch user details using the provided username
            Cursor cursor = dbHelper.getUserByUsername(username);
            if (cursor != null && cursor.moveToFirst()) {
                try {
                    String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PASSWORD));
                    String role = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ROLE));

                    // Check if the entered password matches
                    if (storedPassword.equals(password)) {
                        // Save the login session
                        sessionManager.createLoginSession(username, role);

                        // Navigate based on the user role
                        navigateToActivityBasedOnRole(role);
                        finish(); // Close the login activity
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    cursor.close(); // Ensure the cursor is closed to avoid memory leaks
                }
            } else {
                Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Navigates to the appropriate activity based on the user role.
     *
     * @param role The role of the user (e.g., "admin" or "user").
     */
    private void navigateToActivityBasedOnRole(String role) {
        Intent intent;
        if ("admin".equals(role)) {
            intent = new Intent(LoginActivity.this, AdminActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, UserActivity.class);
        }
        startActivity(intent);
    }
}
