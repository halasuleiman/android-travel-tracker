package com.example.android_assignment1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.Calendar;

public class AddTripActivity extends AppCompatActivity {

    private EditText etDestination, etTripName, etBudget, etEmergencyNumber, etAccommodation, etTripPurpose;
    private TextView tvStartDate, tvEndDate;
    private Spinner spinnerCurrency, spinnerPriority;
    private NumberPicker npPeople;
    private CheckBox cbPassport, cbVisa, cbLicense, cbId, cbTickets;
    private MaterialButton btnSave;

    private SharedPrefHelper sharedPrefHelper;
    private Calendar startCalendar, endCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        initViews();
        setupSpinners();
        setupNumberPicker();
        setupDatePickers();
        setupListeners();

        sharedPrefHelper = new SharedPrefHelper(this);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
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

        btnSave = findViewById(R.id.btnSave);
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
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;

                    if (isStartDate) {
                        tvStartDate.setText(date);
                        startCalendar = calendar;
                    } else {
                        tvEndDate.setText(date);
                        endCalendar = calendar;
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.show();
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveTrip());
    }

    private void saveTrip() {
        String destination = etDestination.getText().toString();
        String tripName = etTripName.getText().toString();
        String startDate = tvStartDate.getText().toString();
        String endDate = tvEndDate.getText().toString();
        int numberOfPeople = npPeople.getValue();
        double budget = Double.parseDouble(etBudget.getText().toString());
        String currency = spinnerCurrency.getSelectedItem().toString();
        String emergencyNumber = etEmergencyNumber.getText().toString();
        String tripPurpose = etTripPurpose.getText().toString();
        String priority = spinnerPriority.getSelectedItem().toString();
        String accommodation = etAccommodation.getText().toString();

        Trip trip = new Trip(destination, tripName, startDate, endDate,
                numberOfPeople, budget, currency, emergencyNumber,
                tripPurpose, priority, accommodation);

        if (cbPassport.isChecked()) trip.getAvailableDocuments().add("Passport");
        if (cbVisa.isChecked()) trip.getAvailableDocuments().add("Visa");
        if (cbLicense.isChecked()) trip.getAvailableDocuments().add("Driver License");
        if (cbId.isChecked()) trip.getAvailableDocuments().add("ID Card");
        if (cbTickets.isChecked()) trip.getAvailableDocuments().add("Flight Tickets");

        sharedPrefHelper.addTrip(trip);

        Toast.makeText(this, "Trip saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}