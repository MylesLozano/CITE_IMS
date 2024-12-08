package com.example.cite_ims;

//import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class UserManagementFragment extends Fragment {
    private DBHelper dbHelper;
    private EditText usernameEditText, passwordEditText, roleEditText;
    private UserAdapter userAdapter;
    private ArrayList<User> userList;
    private User selectedUser = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);

        dbHelper = new DBHelper(getContext());
        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        roleEditText = view.findViewById(R.id.roleEditText);
        Button addUserButton = view.findViewById(R.id.addUserButton);
        Button updateUserButton = view.findViewById(R.id.updateUserButton);
        Button deleteUserButton = view.findViewById(R.id.deleteUserButton);
        RecyclerView usersRecyclerView = view.findViewById(R.id.usersRecyclerView);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), userList, this::onUserSelected);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersRecyclerView.setAdapter(userAdapter);

        loadUsers();

        addUserButton.setOnClickListener(v -> addUser());
        updateUserButton.setOnClickListener(v -> updateUser());
        deleteUserButton.setOnClickListener(v -> deleteUser());

        return view;
    }

    private void onUserSelected(User user) {
        selectedUser = user;
        usernameEditText.setText(user.getUsername());
        roleEditText.setText(user.getRole());
    }

    private void addUser() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String role = roleEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbHelper.addUser(username, password, role);
        if (result != -1) {
            Toast.makeText(getContext(), "User added successfully", Toast.LENGTH_SHORT).show();
            clearFields();
            loadUsers();
        } else {
            Toast.makeText(getContext(), "Failed to add user", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUser() {
        if (selectedUser == null) {
            Toast.makeText(getContext(), "Select a user to update", Toast.LENGTH_SHORT).show();
            return;
        }

        String newPassword = passwordEditText.getText().toString();
        String newRole = roleEditText.getText().toString();

        int result = dbHelper.updateUser(selectedUser.getId(), newPassword, newRole);
        if (result > 0) {
            Toast.makeText(getContext(), "User updated successfully", Toast.LENGTH_SHORT).show();
            clearFields();
            selectedUser = null;
            loadUsers();
        } else {
            Toast.makeText(getContext(), "Failed to update user", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteUser() {
        if (selectedUser == null) {
            Toast.makeText(getContext(), "Select a user to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.deleteUser(selectedUser.getId());
        Toast.makeText(getContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
        clearFields();
        selectedUser = null;
        loadUsers();
    }

    private void loadUsers() {
        userList.clear();
        userList.addAll(dbHelper.getAllUsers());
        userAdapter.notifyDataSetChanged();
    }

    private void clearFields() {
        usernameEditText.setText("");
        passwordEditText.setText("");
        roleEditText.setText("");
    }
}
