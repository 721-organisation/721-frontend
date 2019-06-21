package com.travel721;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import static com.travel721.ColourFinder.getColourMatchedOverlay;
import static com.travel721.Constants.getRandomOverlay;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {
    private List<EventCard> events;

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.event_card_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final EventCard ec = events.get(position);
        TextView currTV;
        View v = holder.itemView;
        currTV = v.findViewById(R.id.eventTitle);
        currTV.setText(ec.getName());
        currTV = v.findViewById(R.id.eventCardVenue);
        currTV.setText(ec.getVenueName());
        currTV = v.findViewById(R.id.eventDate);
        currTV.setText(ec.getFormattedDate());
        currTV = v.findViewById(R.id.eventTime);
        currTV.setText(ec.getTime());
        currTV = v.findViewById(R.id.dayOfWeekLabel);
        currTV.setText(ec.getDayOfWeek());
        currTV = v.findViewById(R.id.eventPrice);
        currTV.setText(currTV.getResources().getString(R.string.price, ec.getPrice()));
        currTV = v.findViewById(R.id.eventSourceLabel);
        currTV.setText(ec.getSourceTag());
        final ImageView imageView = holder.itemView.findViewById(R.id.eventImage);
        final ImageView overlayImageView = holder.itemView.findViewById(R.id.overlayImageView);
        Glide.with(imageView.getContext())
                .load(ec.getImgURL())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(25)))
                .placeholder(R.drawable.loading_spinner)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                        Palette.from(((BitmapDrawable) resource).getBitmap()).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette p) {
                                int defaultColour = overlayImageView.getResources().getColor(R.color.colorAccent);

                                int drawable = getColourMatchedOverlay(p.getDarkVibrantColor(defaultColour), overlayImageView.getContext());

                                Drawable overlayDrawable = overlayImageView.getResources().getDrawable(drawable);
                                overlayImageView.setImageDrawable(overlayDrawable);
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        ImageView iv = v.findViewById(R.id.overlayImageView);
        iv.setImageDrawable(v.getResources().getDrawable(getRandomOverlay()));
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
