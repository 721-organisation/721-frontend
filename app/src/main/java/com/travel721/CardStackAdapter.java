package com.travel721;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IntDef;
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
import com.travel721.activity.Email721TeamRedirectActivity;
import com.travel721.activity.EventMoreInfoActivity;
import com.travel721.analytics.AnalyticsHelper;
import com.travel721.card.AdCard;
import com.travel721.card.Card;
import com.travel721.card.ContactUsFeedbackCard;
import com.travel721.card.EventCard;
import com.travel721.card.FeedbackCard;
import com.travel721.card.FeedbackToFirebaseCard;
import com.travel721.card.InstagramFeedbackCard;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            EVENT_CARD,
            FEEDBACK_CARD,
            AD_CARD,
            CONTACT_US_FEEDBACK_CARD,
            FEEDBACK_TO_FIREBASE_CARD,
            INSTAGRAM_FEEDBACK_CARD
    })
    public @interface CardTypes {
    }

    public static final int EVENT_CARD = 0;
    public static final int FEEDBACK_CARD = 1;
    public static final int AD_CARD = 2;
    public static final int CONTACT_US_FEEDBACK_CARD = 3;
    public static final int FEEDBACK_TO_FIREBASE_CARD = 4;
    public static final int INSTAGRAM_FEEDBACK_CARD = 5;


    // Field for cards in the stack
    private List<Card> events;

    public CardStackAdapter(@NonNull List<Card> eventCardList) {
        this.events = eventCardList;
    }

    public List<Card> getEvents() {
        return events;
    }

    @Override
    public int getItemViewType(@CardTypes int position) {
        if (events.get(position) instanceof EventCard)
            return EVENT_CARD;
        if (events.get(position) instanceof FeedbackCard)
            return FEEDBACK_CARD;
        if (events.get(position) instanceof AdCard)
            return AD_CARD;
        if (events.get(position) instanceof ContactUsFeedbackCard)
            return CONTACT_US_FEEDBACK_CARD;
        if (events.get(position) instanceof FeedbackToFirebaseCard)
            return FEEDBACK_TO_FIREBASE_CARD;
        if (events.get(position) instanceof InstagramFeedbackCard)
            return INSTAGRAM_FEEDBACK_CARD;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, @CardTypes int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case EVENT_CARD:
                return new ViewHolder(inflater.inflate(R.layout.card_event, parent, false));
            case FEEDBACK_CARD:
                return new ViewHolder(inflater.inflate(R.layout.card_feedback, parent, false));
            case AD_CARD:
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
            case CONTACT_US_FEEDBACK_CARD:
                return new ViewHolder(inflater.inflate(R.layout.card_feedback_contact_us, parent, false));
//            case FEEDBACK_TO_FIREBASE_CARD:
//                return new ViewHolder(inflater.inflate(R.layout.card_feedback_firebase, parent, false));
            case INSTAGRAM_FEEDBACK_CARD:
                return new ViewHolder(inflater.inflate(R.layout.card_feedback_instagram, parent, false));
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
            if (ec.getSourceTag().toUpperCase().equals("BUSINESS")) {
                currTV.setText(v.getResources().getString(R.string.partner_721));
            } else {
                currTV.setText(ec.getSourceTag());
            }
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
        if (events.get(position) instanceof ContactUsFeedbackCard) {
            holder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(holder.itemView.getContext(), Email721TeamRedirectActivity.class);
                holder.itemView.getContext().startActivity(i);
            });
        }
        if (events.get(position) instanceof InstagramFeedbackCard) {
            holder.itemView.setOnClickListener(v -> {
                String url = "https://www.instagram.com/721app/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                holder.itemView.getContext().startActivity(i);
            });
        }
        if (events.get(position) instanceof FeedbackToFirebaseCard) {
            String question = ((FeedbackToFirebaseCard) events.get(position)).getQuestion();
            TextView feedbackTagline = holder.itemView.findViewById(R.id.feedbackTagline);
            feedbackTagline.setText(question);
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