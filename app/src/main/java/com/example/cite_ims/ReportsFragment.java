package com.example.cite_ims;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ReportsFragment extends Fragment {

    private DBHelper dbHelper;
    private TextView totalItemsTextView, totalQuantityTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        dbHelper = new DBHelper(getContext());

        totalItemsTextView = view.findViewById(R.id.totalItemsTextView);
        totalQuantityTextView = view.findViewById(R.id.totalQuantityTextView);

        loadReports();

        return view;
    }

    private void loadReports() {
        int totalItems = dbHelper.getInventoryCount();
        int totalQuantity = dbHelper.getInventoryTotalQuantity();

        totalItemsTextView.setText("Total Items: " + totalItems);
        totalQuantityTextView.setText("Total Quantity: " + totalQuantity);
    }
}