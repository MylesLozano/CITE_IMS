package com.example.cite_ims;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private Button adminButton;
    private Button userButton;
    private DBHelper dbHelper;
    private String selectedRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        CheckBox showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        adminButton = findViewById(R.id.adminButton);
        userButton = findViewById(R.id.userButton);
        Button loginButton = findViewById(R.id.loginButton);
        dbHelper = new DBHelper(this);

        adminButton.setOnClickListener(v -> {
            selectedRole = "admin";
            adminButton.setBackgroundColor(Color.GRAY);
            userButton.setBackgroundColor(Color.LTGRAY);
        });

        userButton.setOnClickListener(v -> {
            selectedRole = "user";
            userButton.setBackgroundColor(Color.GRAY);
            adminButton.setBackgroundColor(Color.LTGRAY);
        });

        loginButton.setOnClickListener(v -> loginUser());

        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordEditText.setSelection(passwordEditText.length());
        });

        // Add default admin user if not exists
        if (dbHelper.checkUserExists("admin", "admin")) {
            dbHelper.addUser("admin", "admin", "admin");
        }

        // Add default normal user if not exists
        if (dbHelper.checkUserExists("user", "user")) {
            dbHelper.addUser("user", "user", "user");
        }
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || selectedRole.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please select a role and enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.getUser(username, password);
        if (cursor != null && cursor.moveToFirst()) {
            String role = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_USER_ROLE));
            if (role.equals(selectedRole)) {
                if (role.equals("admin")) {
                    Toast.makeText(LoginActivity.this, "Admin Login Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "User Login Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, UserActivity.class));
                }
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Role mismatch. Please select the correct role.", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } else {
            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }
}
