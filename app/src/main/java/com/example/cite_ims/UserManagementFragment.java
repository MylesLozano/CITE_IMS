package com.example.cite_ims;

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
    private Button addUserButton;
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private ArrayList<User> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);

        dbHelper = new DBHelper(getContext());

        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        roleEditText = view.findViewById(R.id.roleEditText);
        addUserButton = view.findViewById(R.id.addUserButton);
        usersRecyclerView = view.findViewById(R.id.usersRecyclerView);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), userList, dbHelper);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersRecyclerView.setAdapter(userAdapter);

        loadUsers();

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String role = roleEditText.getText().toString();

                if (username.isEmpty() || role.isEmpty()) {
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                long result = dbHelper.addUser(username, password, role);

                if (result != -1) {
                    Toast.makeText(getContext(), "User added successfully", Toast.LENGTH_SHORT).show();
                    loadUsers();
                    usernameEditText.setText("");
                    passwordEditText.setText("");
                    roleEditText.setText("");
                } else {
                    Toast.makeText(getContext(), "Failed to add user", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void loadUsers() {
        userList.clear();
        userList.addAll(dbHelper.getAllUsers());
        userAdapter.notifyDataSetChanged();
    }
}
