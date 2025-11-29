package com.example.android_assignment1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditTripActivity extends AppCompatActivity {

    private EditText etDestination, etTripName, etBudget, etEmergencyNumber, etAccommodation, etTripPurpose;
    private TextView tvStartDate, tvEndDate;
    private Spinner spinnerCurrency, spinnerPriority;
    private NumberPicker npPeople;
    private CheckBox cbPassport, cbVisa, cbLicense, cbId, cbTickets;
    private MaterialButton buttonUpdate, buttonCancel;

    private SharedPrefHelper sharedPrefHelper;
    private Calendar startCalendar, endCalendar;
    private Trip currentTrip;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        initViews();
        setupSpinners();
        setupNumberPicker();
        setupDatePickers();
        setupListeners();

        sharedPrefHelper = new SharedPrefHelper(this);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        loadTripData();
    }

    private void initViews() {
        etDestination = findViewById(R.id.etDestination);
        etTripName = findViewById(R.id.etTripName);
        etBudget = findViewById(R.id.etBudget);
        etEmergencyNumber = findViewById(R.id.etEmergencyNumber);
        etAccommodation = findViewById(R.id.etAccommodation);
        etTripPurpose = findViewById(R.id.etTripPurpose);

        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);

        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        spinnerPriority = findViewById(R.id.spinnerPriority);

        npPeople = findViewById(R.id.npPeople);

        cbPassport = findViewById(R.id.cbPassport);
        cbVisa = findViewById(R.id.cbVisa);
        cbLicense = findViewById(R.id.cbLicense);
        cbId = findViewById(R.id.cbId);
        cbTickets = findViewById(R.id.cbTickets);

        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonCancel = findViewById(R.id.buttonCancel);
    }

    private void setupSpinners() {
        String[] currencies = {"USD", "EUR", "GBP", "JPY", "SAR", "AED", "EGP"};
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(currencyAdapter);

        String[] priorities = {"Normal", "Important", "Critical"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);
    }

    private void setupNumberPicker() {
        npPeople.setMinValue(1);
        npPeople.setMaxValue(20);
        npPeople.setValue(1);
        npPeople.setWrapSelectorWheel(false);
    }

    private void setupDatePickers() {
        tvStartDate.setOnClickListener(v -> showDatePicker(true));
        tvEndDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startCalendar : endCalendar;

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String date = dateFormatter.format(calendar.getTime());

                    if (isStartDate) {
                        tvStartDate.setText(date);
                        startCalendar.set(year, month, dayOfMonth);

                        // If end date is before start date, update end date
                        if (endCalendar.before(startCalendar)) {
                            endCalendar.set(year, month, dayOfMonth);
                            tvEndDate.setText(date);
                        }
                    } else {
                        // Validate that end date is not before start date
                        if (calendar.before(startCalendar)) {
                            Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        tvEndDate.setText(date);
                        endCalendar.set(year, month, dayOfMonth);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date for start date picker to today
        if (isStartDate) {
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        } else {
            // For end date, set minimum to start date
            datePicker.getDatePicker().setMinDate(startCalendar.getTimeInMillis() - 1000);
        }

        datePicker.show();
    }

    private void setupListeners() {
        buttonUpdate.setOnClickListener(v -> updateTrip());
        buttonCancel.setOnClickListener(v -> finish());

        // Add text watchers for real-time validation
        etBudget.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateBudget();
            }
        });

        etEmergencyNumber.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmergencyNumber();
            }
        });
    }

    private void loadTripData() {
        String tripId = getIntent().getStringExtra("trip_id");

        if (tripId == null) {
            tripId = getIntent().getStringExtra("TRIP_ID"); // Fallback to old key
        }

        if (tripId == null) {
            Toast.makeText(this, "Error: No trip ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<Trip> allTrips = sharedPrefHelper.getTrips();
        for (Trip trip : allTrips) {
            if (trip.getId().equals(tripId)) {
                currentTrip = trip;
                populateForm();
                return;
            }
        }

        Toast.makeText(this, "Trip not found", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void populateForm() {
        if (currentTrip == null) return;

        etTripName.setText(currentTrip.getTripName());
        etDestination.setText(currentTrip.getDestination());

        // Parse and set dates properly
        try {
            if (currentTrip.getStartDate() != null && !currentTrip.getStartDate().isEmpty()) {
                startCalendar.setTime(dateFormatter.parse(currentTrip.getStartDate()));
                tvStartDate.setText(currentTrip.getStartDate());
            }
            if (currentTrip.getEndDate() != null && !currentTrip.getEndDate().isEmpty()) {
                endCalendar.setTime(dateFormatter.parse(currentTrip.getEndDate()));
                tvEndDate.setText(currentTrip.getEndDate());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // Fallback to string values
            tvStartDate.setText(currentTrip.getStartDate());
            tvEndDate.setText(currentTrip.getEndDate());
        }

        npPeople.setValue(currentTrip.getNumberOfPeople());
        etBudget.setText(String.valueOf(currentTrip.getBudget()));
        etEmergencyNumber.setText(currentTrip.getEmergencyNumber());
        etAccommodation.setText(currentTrip.getAccommodation());
        etTripPurpose.setText(currentTrip.getTripPurpose());

        setSpinnerSelection(spinnerCurrency, currentTrip.getCurrency());
        setSpinnerSelection(spinnerPriority, currentTrip.getPriority());

        List<String> documents = currentTrip.getAvailableDocuments();
        if (documents != null) {
            cbPassport.setChecked(documents.contains("Passport"));
            cbVisa.setChecked(documents.contains("Visa"));
            cbLicense.setChecked(documents.contains("Driver License"));
            cbId.setChecked(documents.contains("ID Card"));
            cbTickets.setChecked(documents.contains("Flight Tickets"));
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void updateTrip() {
        if (currentTrip == null) {
            Toast.makeText(this, "No trip data to update", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get and validate all fields
        String destination = etDestination.getText().toString().trim();
        String tripName = etTripName.getText().toString().trim();
        String startDate = tvStartDate.getText().toString();
        String endDate = tvEndDate.getText().toString();
        int numberOfPeople = npPeople.getValue();

        // Validate required fields
        if (destination.isEmpty() || tripName.isEmpty() ||
                startDate.equals("Select date") || startDate.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate dates
        if (!isValidDateRange(startDate, endDate)) {
            Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate budget
        double budget;
        try {
            budget = Double.parseDouble(etBudget.getText().toString());
            if (budget < 0) {
                Toast.makeText(this, "Budget cannot be negative", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid budget", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate emergency number
        String emergencyNumber = etEmergencyNumber.getText().toString().trim();
        if (!emergencyNumber.isEmpty() && !isValidPhoneNumber(emergencyNumber)) {
            Toast.makeText(this, "Please enter a valid emergency number", Toast.LENGTH_SHORT).show();
            return;
        }

        String currency = spinnerCurrency.getSelectedItem().toString();
        String tripPurpose = etTripPurpose.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();
        String accommodation = etAccommodation.getText().toString().trim();

        // Update trip object
        currentTrip.setDestination(destination);
        currentTrip.setTripName(tripName);
        currentTrip.setStartDate(startDate);
        currentTrip.setEndDate(endDate);
        currentTrip.setNumberOfPeople(numberOfPeople);
        currentTrip.setBudget(budget);
        currentTrip.setCurrency(currency);
        currentTrip.setEmergencyNumber(emergencyNumber);
        currentTrip.setTripPurpose(tripPurpose);
        currentTrip.setPriority(priority);
        currentTrip.setAccommodation(accommodation);

        // Update documents
        currentTrip.getAvailableDocuments().clear();
        if (cbPassport.isChecked()) currentTrip.getAvailableDocuments().add("Passport");
        if (cbVisa.isChecked()) currentTrip.getAvailableDocuments().add("Visa");
        if (cbLicense.isChecked()) currentTrip.getAvailableDocuments().add("Driver License");
        if (cbId.isChecked()) currentTrip.getAvailableDocuments().add("ID Card");
        if (cbTickets.isChecked()) currentTrip.getAvailableDocuments().add("Flight Tickets");

        // Save to shared preferences
        boolean success = sharedPrefHelper.updateTrip(currentTrip);

        if (success) {
            Toast.makeText(this, "Trip updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to update trip", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidDateRange(String startDate, String endDate) {
        if (startDate.equals("Select date") || endDate.equals("Select date")) {
            return true; // Let required field validation handle this
        }

        try {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            start.setTime(dateFormatter.parse(startDate));
            end.setTime(dateFormatter.parse(endDate));
            return !end.before(start);
        } catch (ParseException e) {
            return true; // Fallback - let it through
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^[+]?[0-9]{10,13}$");
    }

    private void validateBudget() {
        String budgetText = etBudget.getText().toString();
        if (!budgetText.isEmpty()) {
            try {
                double budget = Double.parseDouble(budgetText);
                if (budget < 0) {
                    etBudget.setError("Budget cannot be negative");
                } else {
                    etBudget.setError(null);
                }
            } catch (NumberFormatException e) {
                etBudget.setError("Invalid number format");
            }
        } else {
            etBudget.setError(null);
        }
    }

    private void validateEmergencyNumber() {
        String number = etEmergencyNumber.getText().toString();
        if (!number.isEmpty() && !isValidPhoneNumber(number)) {
            etEmergencyNumber.setError("Invalid phone number format");
        } else {
            etEmergencyNumber.setError(null);
        }
    }

    // Helper class for text watchers
    private abstract class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}