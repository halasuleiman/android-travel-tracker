package com.example.android_assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class TripDetailActivity extends AppCompatActivity {

    private TextView tvTripName, tvDestination, tvDates, tvBudget, tvPeople, tvPriority;
    private TextView tvCurrency, tvEmergency, tvAccommodation, tvPurpose, tvTotalDays;
    private TextView tvDocuments;
    private LinearLayout checklistContainer;
    private Button btnEdit, btnDelete, btnBack;

    private SharedPrefHelper sharedPrefHelper;
    private Trip currentTrip;
    private List<CheckBox> checkBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        initViews();
        setupListeners();
        loadTripDetails();
    }

    private void initViews() {
        tvTripName = findViewById(R.id.tvTripName);
        tvDestination = findViewById(R.id.tvDestination);
        tvDates = findViewById(R.id.tvDates);
        tvBudget = findViewById(R.id.tvBudget);
        tvPeople = findViewById(R.id.tvPeople);
        tvPriority = findViewById(R.id.tvPriority);
        tvCurrency = findViewById(R.id.tvCurrency);
        tvEmergency = findViewById(R.id.tvEmergency);
        tvAccommodation = findViewById(R.id.tvAccommodation);
        tvPurpose = findViewById(R.id.tvPurpose);
        tvTotalDays = findViewById(R.id.tvTotalDays);
        tvDocuments = findViewById(R.id.tvDocuments);
        checklistContainer = findViewById(R.id.checklistContainer);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        sharedPrefHelper = new SharedPrefHelper(this);
        checkBoxes = new ArrayList<>();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            if (currentTrip != null) {
                Intent intent = new Intent(TripDetailActivity.this, EditTripActivity.class);
                intent.putExtra("TRIP_ID", currentTrip.getId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Cannot edit: Trip data not loaded", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadTripDetails() {
        String tripId = getIntent().getStringExtra("trip_id");

        if (tripId != null) {
            List<Trip> allTrips = sharedPrefHelper.getTrips();
            for (Trip trip : allTrips) {
                if (trip.getId().equals(tripId)) {
                    currentTrip = trip;
                    displayTripDetails();
                    setupChecklist();
                    break;
                }
            }
        }

        if (currentTrip == null) {
            Toast.makeText(this, "Trip not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayTripDetails() {
        tvTripName.setText(currentTrip.getTripName());
        tvDestination.setText(currentTrip.getDestination());
        tvDates.setText(currentTrip.getStartDate() + " to " + currentTrip.getEndDate());
        tvBudget.setText(String.valueOf(currentTrip.getBudget()));
        tvPeople.setText(String.valueOf(currentTrip.getNumberOfPeople()));
        tvPriority.setText(currentTrip.getPriority());
        tvCurrency.setText(currentTrip.getCurrency());
        tvEmergency.setText(currentTrip.getEmergencyNumber());
        tvAccommodation.setText(currentTrip.getAccommodation());
        tvPurpose.setText(currentTrip.getTripPurpose());
        tvTotalDays.setText(currentTrip.getTotalDays() + " days");

        List<String> documents = currentTrip.getAvailableDocuments();
        if (documents != null && !documents.isEmpty()) {
            StringBuilder docsText = new StringBuilder();
            for (String doc : documents) {
                docsText.append("• ").append(doc).append("\n");
            }
            tvDocuments.setText(docsText.toString());
        } else {
            tvDocuments.setText("No documents available");
        }

        setPriorityColor();
    }

    private void setupChecklist() {
        checklistContainer.removeAllViews();
        checkBoxes.clear();

        String[] checklistItems = {
                "Book Hotel", "Buy Flight Tickets", "Get Travel Insurance",
                "Exchange Currency", "Pack Luggage", "Confirm Transportation",
                "Print Documents", "Set Home Security"
        };

        for (String item : checklistItems) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(item);
            checkBox.setTextSize(16);
            checkBox.setTextColor(0xFF2E7D32);
            checkBox.setPadding(0, 12, 0, 12);
            checkBox.setButtonTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));

            checklistContainer.addView(checkBox);
            checkBoxes.add(checkBox);
        }
    }

    private void setPriorityColor() {
        switch (currentTrip.getPriority()) {
            case "Important":
                tvPriority.setBackgroundColor(0xFFFF9800);
                break;
            case "Critical":
                tvPriority.setBackgroundColor(0xFFF44336);
                break;
            default:
                tvPriority.setBackgroundColor(0xFF4CAF50);
        }
        tvPriority.setTextColor(0xFFFFFFFF);
        tvPriority.setPadding(16, 8, 16, 8);
    }

    private void showDeleteConfirmation() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Trip")
                .setMessage("Are you sure you want to delete '" + currentTrip.getTripName() + "'?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sharedPrefHelper.deleteTrip(currentTrip.getId());
                    Toast.makeText(this, "Trip deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}