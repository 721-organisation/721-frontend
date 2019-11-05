package com.travel721;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

import static com.travel721.ColourFinder.getColourMatchedOverlay;
import static com.travel721.Constants.getRandomOverlay;

/**
 * This is the adapter for the Event Card
 *
 * @author Bhav
 */
public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {
    // Field for cards in the stack
    private List<Card> events;

    public CardStackAdapter(@NonNull List<Card> eventCardList) {
        this.events = eventCardList;
    }

    public List<Card> getEvents() {
        return events;
    }

    @Override
    public int getItemViewType(int position) {
        if (events.get(position) instanceof EventCard)
            return 0;
        if (events.get(position) instanceof FeedbackCard)
            return 1;
        if (events.get(position) instanceof AdCard)
            return 2;
        return -1;
    }

    public void setEvents(List<Card> events) {
        this.events = events;
    }

    /**
     * Inflates a new card to the layout
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType) {
            case 0:
                v = inflater.inflate(R.layout.event_card_layout, parent, false);
                return new ViewHolder(v);
            case 1:
                v = inflater.inflate(R.layout.feedback_card_layout, parent, false);
                return new ViewHolder(v);
            case 2:
                v = inflater.inflate(R.layout.ad_card_layout, parent, false);
                MobileAds.initialize(parent.getContext(), initializationStatus -> {
                });

                AdView mAdView = v.findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
                return new ViewHolder(v);
        }
        // This should never happen
        return new ViewHolder(inflater.inflate(R.layout.event_card_layout, parent, false));
    }

    /**
     * Sets the text and image on an Event Card
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (events.get(position) instanceof EventCard) {
            final EventCard ec = (EventCard) events.get(position);
            // Set TextView values
            TextView currTV;
            View v = holder.itemView;
            LinearLayout linearLayout2 = v.findViewById(R.id.eventCardTopLinearLayout);
            linearLayout2.setOnClickListener(view -> {
                AnalyticsHelper.logEvent(v.getContext(), AnalyticsHelper.USER_SWIPED_DOWN, null);
                Intent i = new Intent(v.getContext(), EventMoreInfoActivity.class);
                i.putExtra("eventCard", events.get(position));
                v.getContext().startActivity(i);
            });

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
            currTV.setText(currTV.getResources().getString(R.string.price, ec.getPrice())); // String resource used for i18n
            currTV = v.findViewById(R.id.eventSourceLabel);
            currTV.setText(ec.getSourceTag());
            // Slightly complicated to load the image, using a 3rd party library
            final ImageView imageView = holder.itemView.findViewById(R.id.eventImage);


            final ImageView overlayImageView = holder.itemView.findViewById(R.id.overlayImageView);
            GlideApp.with(imageView.getContext())
                    .load(ec.getImgURL())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(25)))
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            imageView.setImageDrawable(resource);
                            // Extract a colour from the image to set the overlay with
                            Palette.from(((BitmapDrawable) resource).getBitmap()).generate(p -> {
                                int defaultColour = ContextCompat.getColor(imageView.getContext(), R.color.colorAccent);
                                int drawable = getColourMatchedOverlay(p.getDominantColor(defaultColour), overlayImageView.getContext());
                                Drawable overlayDrawable = ContextCompat.getDrawable(imageView.getContext(), drawable);
                                overlayImageView.setImageDrawable(overlayDrawable);
                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Unnecessary but required
                        }
                    });
            ImageView iv = v.findViewById(R.id.overlayImageView);
            iv.setImageDrawable(ContextCompat.getDrawable(overlayImageView.getContext(), getRandomOverlay()));
        }
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