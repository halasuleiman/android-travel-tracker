package com.example.android_assignment1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private List<Trip> tripList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Trip trip);
        void onItemLongClick(Trip trip);
    }

    public TripAdapter(List<Trip> tripList, OnItemClickListener listener) {
        this.tripList = tripList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        holder.bind(trip, listener);
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public void updateList(List<Trip> newList) {
        tripList = newList;
        notifyDataSetChanged();
    }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tvTripName, tvDestination, tvDates, tvBudget, tvPriority, tvPeople;
        CardView cardView;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTripName = itemView.findViewById(R.id.tvTripName);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvDates = itemView.findViewById(R.id.tvDates);
            tvBudget = itemView.findViewById(R.id.tvBudget);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvPeople = itemView.findViewById(R.id.tvPeople);
            cardView = itemView.findViewById(R.id.cardView);
        }

        public void bind(final Trip trip, final OnItemClickListener listener) {
            tvTripName.setText(trip.getTripName());
            tvDestination.setText(trip.getDestination());
            tvDates.setText(trip.getStartDate() + " - " + trip.getEndDate());
            tvBudget.setText(trip.getBudget() + " " + trip.getCurrency());
            tvPriority.setText(trip.getPriority());
            tvPeople.setText("👥 " + trip.getNumberOfPeople());

            switch (trip.getPriority()) {
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

            itemView.setOnClickListener(v -> listener.onItemClick(trip));
            itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(trip);
                return true;
            });
        }
    }
}