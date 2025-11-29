package com.example.android_assignment1;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefHelper {
    private static final String PREF_NAME = "TripPrefs";
    private static final String TRIPS_KEY = "trips";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public SharedPrefHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveTrips(List<Trip> trips) {
        String json = gson.toJson(trips);
        sharedPreferences.edit().putString(TRIPS_KEY, json).apply();
    }

    public List<Trip> getTrips() {
        String json = sharedPreferences.getString(TRIPS_KEY, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Trip>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public boolean addTrip(Trip trip) {
        try {
            List<Trip> trips = getTrips();
            trip.setId(String.valueOf(System.currentTimeMillis()));
            trips.add(trip);
            saveTrips(trips);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrip(Trip updatedTrip) {
        try {
            List<Trip> trips = getTrips();
            boolean found = false;
            for (int i = 0; i < trips.size(); i++) {
                if (trips.get(i).getId().equals(updatedTrip.getId())) {
                    trips.set(i, updatedTrip);
                    found = true;
                    break;
                }
            }
            if (found) {
                saveTrips(trips);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTrip(String tripId) {
        try {
            List<Trip> trips = getTrips();
            boolean removed = false;
            for (int i = 0; i < trips.size(); i++) {
                if (trips.get(i).getId().equals(tripId)) {
                    trips.remove(i);
                    removed = true;
                    break;
                }
            }
            if (removed) {
                saveTrips(trips);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Trip getTripById(String tripId) {
        List<Trip> trips = getTrips();
        for (Trip trip : trips) {
            if (trip.getId().equals(tripId)) {
                return trip;
            }
        }
        return null;
    }
}