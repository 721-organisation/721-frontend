package com.travel721;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {
    private CardStackAdapter() {
    }

    public CardStackAdapter(@NonNull List<EventCard> eventCardList) {
        this.events = eventCardList;
    }

    public List<EventCard> getEvents() {
        return events;
    }

    public void setEvents(List<EventCard> events) {
        this.events = events;
    }

    private List<EventCard> events;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.event_card_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final EventCard ec = events.get(position);
        TextView currTV;
        View v = holder.itemView;
        currTV = v.findViewById(R.id.eventTitle);
        currTV.setText(ec.getName());
        currTV = v.findViewById(R.id.eventVenue);
        currTV.setText(ec.getVenueName());
        currTV = v.findViewById(R.id.eventDate);
        currTV.setText(ec.getFormattedDate().toString());
        currTV = v.findViewById(R.id.eventTime);
        currTV.setText(ec.getTime());
        currTV = v.findViewById(R.id.eventPrice);
        currTV.setText(ec.getPrice());
        Glide.with(holder.itemView.findViewById(R.id.imageView2))
                .load(ec.getImgURL())
                .into((ImageView) holder.itemView.findViewById(R.id.imageView2));
    }

    @Override
    public int getItemCount() {
        if (events == null) {
            return 0;
        } else {
            return events.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
