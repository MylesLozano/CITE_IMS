package com.example.cite_ims;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UserProfileFragment extends Fragment {

    private TextView usernameTextView, roleTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        usernameTextView = view.findViewById(R.id.usernameTextView);
        roleTextView = view.findViewById(R.id.roleTextView);

        // You can get the current user’s details from the database
        // Example: Display the logged-in user's information (assuming the user is logged in and info is available)

        String username = "user123";  // Replace with dynamic data
        String role = "user";  // Replace with dynamic data

        usernameTextView.setText("Username: " + username);
        roleTextView.setText("Role: " + role);

        return view;
    }
}
