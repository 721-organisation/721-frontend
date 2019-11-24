package com.travel721;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.travel721.activity.EventMoreInfoActivity;
import com.travel721.analytics.AnalyticsHelper;
import com.travel721.card.AdCard;
import com.travel721.card.Card;
import com.travel721.card.EventCard;
import com.travel721.card.FeedbackCard;

import java.util.List;
import java.util.Objects;

import static com.travel721.Constants.getRandomOverlay;
import static com.travel721.utility.ColourFinder.getColourMatchedOverlay;

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
        switch (viewType) {
            case 0:
                return new ViewHolder(inflater.inflate(R.layout.card_event, parent, false));
            case 1:
                return new ViewHolder(inflater.inflate(R.layout.card_feedback, parent, false));
            case 2:
                ViewHolder vh = new ViewHolder(inflater.inflate(R.layout.card_ad, parent, false));
                MobileAds.initialize(parent.getContext(), initializationStatus -> {
                });

                AdView mAdView = vh.itemView.findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        getEvents().remove(vh.getAdapterPosition());
                        notifyDataSetChanged();
                    }

                });
                mAdView.loadAd(adRequest);

                return vh;
        }
        // This should never happen
        return new ViewHolder(inflater.inflate(R.layout.card_event, parent, false));
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
            ConstraintLayout linearLayout2 = v.findViewById(R.id.eventCardTopLinearLayout);
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
            imageView.setImageDrawable(null);

            final ImageView overlayImageView = holder.itemView.findViewById(R.id.overlayImageView);
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(imageView.getContext());
            circularProgressDrawable.setStrokeWidth(15f);
            circularProgressDrawable.setCenterRadius(90f);
            circularProgressDrawable.start();

            Log.v("Glide", ec.getImgURL());
            Glide.with(v.getContext())
                    .load(ec.getImgURL())
                    .placeholder(circularProgressDrawable)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                    .signature(new ObjectKey(ec.getEventSourceID()))
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .error(Glide.with(imageView).load(R.drawable.ic_broken_img_bmp))
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            imageView.setImageDrawable(circularProgressDrawable);
                        }

                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            imageView.setImageDrawable(resource);

                            // Extract a colour from the image to set the overlay with
                            Palette.from(((BitmapDrawable) resource).getBitmap()).generate(p -> {
                                int defaultColour = ContextCompat.getColor(imageView.getContext(), R.color.colorAccent);
                                int drawable = getColourMatchedOverlay(Objects.requireNonNull(p).getDominantColor(defaultColour), overlayImageView.getContext());
                                Drawable overlayDrawable = ContextCompat.getDrawable(imageView.getContext(), drawable);
                                Glide.with(v.getContext())
                                        .load(overlayDrawable)
                                        .into(overlayImageView);
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