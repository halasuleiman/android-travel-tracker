package com.example.android_assignment1;

import java.util.ArrayList;
import java.util.List;

public class Trip {
    private String id;
    private String destination;
    private String tripName;
    private String startDate;
    private String endDate;
    private int numberOfPeople;
    private double budget;
    private String currency;
    private List<String> checklist;
    private List<String> availableDocuments;
    private String emergencyNumber;
    private String tripPurpose;
    private int totalDays;
    private String priority;
    private String accommodation;

    public Trip() {
        this.checklist = new ArrayList<>();
        this.availableDocuments = new ArrayList<>();
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public Trip(String destination, String tripName, String startDate, String endDate,
                int numberOfPeople, double budget, String currency, String emergencyNumber,
                String tripPurpose, String priority, String accommodation) {
        this();
        this.destination = destination;
        this.tripName = tripName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfPeople = numberOfPeople;
        this.budget = budget;
        this.currency = currency;
        this.emergencyNumber = emergencyNumber;
        this.tripPurpose = tripPurpose;
        this.priority = priority;
        this.accommodation = accommodation;
        this.totalDays = calculateTotalDays();
    }

    private int calculateTotalDays() {
        return 1; // Placeholder
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getTripName() { return tripName; }
    public void setTripName(String tripName) { this.tripName = tripName; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public int getNumberOfPeople() { return numberOfPeople; }
    public void setNumberOfPeople(int numberOfPeople) { this.numberOfPeople = numberOfPeople; }
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public List<String> getChecklist() { return checklist; }
    public void setChecklist(List<String> checklist) { this.checklist = checklist; }
    public List<String> getAvailableDocuments() { return availableDocuments; }
    public void setAvailableDocuments(List<String> availableDocuments) { this.availableDocuments = availableDocuments; }
    public String getEmergencyNumber() { return emergencyNumber; }
    public void setEmergencyNumber(String emergencyNumber) { this.emergencyNumber = emergencyNumber; }
    public String getTripPurpose() { return tripPurpose; }
    public void setTripPurpose(String tripPurpose) { this.tripPurpose = tripPurpose; }
    public int getTotalDays() { return totalDays; }
    public void setTotalDays(int totalDays) { this.totalDays = totalDays; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getAccommodation() { return accommodation; }
    public void setAccommodation(String accommodation) { this.accommodation = accommodation; }
}