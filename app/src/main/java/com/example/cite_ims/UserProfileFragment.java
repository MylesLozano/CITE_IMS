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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        UserSessionManager sessionManager = new UserSessionManager(getContext());
        TextView usernameTextView = view.findViewById(R.id.usernameTextView);
        TextView roleTextView = view.findViewById(R.id.roleTextView);

        if (sessionManager.isUserLoggedIn()) {
            String username = sessionManager.getUsername();
            String role = sessionManager.getRole();

            usernameTextView.setText("Username: " + username);
            roleTextView.setText("Role: " + role);
        } else {
            usernameTextView.setText("Not logged in");
            roleTextView.setText("");
        }

        return view;
    }
}
