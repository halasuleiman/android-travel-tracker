package com.example.android_assignment1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TripAdapter.OnItemClickListener {

    private RecyclerView rvTrips;
    private TextInputEditText etSearch;
    private FloatingActionButton btnAddTrip;
    private LinearLayout emptyStateContainer; // Changed from TextView to LinearLayout
    private TextView tvEmptyMessage, tvTripsCount, tvEmptyTitle;
    private ImageView ivClearSearch, ivEmptyState;

    private TripAdapter tripAdapter;
    private List<Trip> tripList;
    private SharedPrefHelper sharedPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupListeners();
        setupBackPressedHandler();
        loadTrips();
    }

    private void initViews() {
        rvTrips = findViewById(R.id.rvTrips);
        etSearch = findViewById(R.id.etSearch);
        btnAddTrip = findViewById(R.id.btnAddTrip);
        emptyStateContainer = findViewById(R.id.emptyStateContainer); // Fixed
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        tvTripsCount = findViewById(R.id.tvTripsCount);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        ivClearSearch = findViewById(R.id.ivClearSearch);
        ivEmptyState = findViewById(R.id.ivEmptyState);

        sharedPrefHelper = new SharedPrefHelper(this);
    }

    private void setupRecyclerView() {
        tripList = new ArrayList<>();
        tripAdapter = new TripAdapter(tripList, this);
        rvTrips.setLayoutManager(new LinearLayoutManager(this));
        rvTrips.setAdapter(tripAdapter);
    }

    private void setupListeners() {
        btnAddTrip.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTripActivity.class);
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTrips(s.toString());
                ivClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ivClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            ivClearSearch.setVisibility(View.GONE);
            loadTrips();
        });
    }

    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!etSearch.getText().toString().trim().isEmpty()) {
                    clearSearch();
                } else {
                    if (isEnabled()) {
                        setEnabled(false);
                        MainActivity.super.onBackPressed();
                    }
                }
            }
        });
    }

    private void loadTrips() {
        tripList.clear();
        List<Trip> allTrips = sharedPrefHelper.getTrips();
        tripList.addAll(allTrips);
        tripAdapter.updateList(tripList);
        updateEmptyState();
        updateTripsCounter();
    }

    private void filterTrips(String query) {
        List<Trip> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(tripList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Trip trip : sharedPrefHelper.getTrips()) {
                if (trip.getTripName().toLowerCase().contains(lowerCaseQuery) ||
                        trip.getDestination().toLowerCase().contains(lowerCaseQuery) ||
                        (trip.getTripPurpose() != null && trip.getTripPurpose().toLowerCase().contains(lowerCaseQuery)) ||
                        (trip.getAccommodation() != null && trip.getAccommodation().toLowerCase().contains(lowerCaseQuery))) {
                    filteredList.add(trip);
                }
            }
        }
        tripAdapter.updateList(filteredList);
        updateEmptyState();
        updateTripsCounter();
    }

    private void updateEmptyState() {
        if (tripAdapter.getItemCount() == 0) {
            emptyStateContainer.setVisibility(View.VISIBLE); // Fixed
            rvTrips.setVisibility(View.GONE);

            String searchText = etSearch.getText().toString().trim();
            if (!searchText.isEmpty()) {
                tvEmptyTitle.setText("No Trips Found");
                tvEmptyMessage.setText("No trips found for '" + searchText + "'\nTry different keywords or clear search");
            } else {
                tvEmptyTitle.setText("No Trips Yet");
                tvEmptyMessage.setText("Start your adventure by adding your first trip!\n\nClick the + button below to begin your journey 🗺️");
            }
        } else {
            emptyStateContainer.setVisibility(View.GONE); // Fixed
            rvTrips.setVisibility(View.VISIBLE);
        }
    }

    private void updateTripsCounter() {
        int count = tripAdapter.getItemCount();
        String countText = count + " Trip" + (count != 1 ? "s" : "");
        tvTripsCount.setText(countText);
    }

    @Override
    public void onItemClick(Trip trip) {
        Intent intent = new Intent(this, TripDetailActivity.class);
        intent.putExtra("trip_id", trip.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Trip trip) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("🗑️ Delete Trip");
        builder.setMessage("Are you sure you want to delete '" + trip.getTripName() + "'?\n\nThis action cannot be undone.");
        builder.setPositiveButton("DELETE", (dialog, which) -> {
            boolean success = sharedPrefHelper.deleteTrip(trip.getId());
            if (success) {
                loadTrips();
                showToast("🗑️ Trip deleted successfully");
            } else {
                showToast("❌ Failed to delete trip");
            }
        });
        builder.setNegativeButton("CANCEL", null);
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF5252"));
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#2D4E57"));

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrips();
    }

    public void editTrip(Trip trip) {
        Intent intent = new Intent(this, EditTripActivity.class);
        intent.putExtra("trip_id", trip.getId());
        startActivity(intent);
    }

    public void clearSearch() {
        etSearch.setText("");
        ivClearSearch.setVisibility(View.GONE);
        loadTrips();
    }

    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    public int getFilteredTripsCount() {
        return tripAdapter.getItemCount();
    }

    public int getTotalTripsCount() {
        return sharedPrefHelper.getTrips().size();
    }

    public boolean isFiltering() {
        return !etSearch.getText().toString().trim().isEmpty();
    }
}