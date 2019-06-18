package com.travel721;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.List;

public class ListEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_list_events);
        List<EventCard> eventCards = getIntent().getParcelableArrayListExtra("events");
        LinearLayout linearLayout = findViewById(R.id.eventListCardHolder);
        View card;
        for (EventCard e : eventCards) {
            //LinearLayout layout = (LinearLayout) findViewById(R.id.info);
            card = getLayoutInflater().inflate(R.layout.event_list_card, null);
            ImageView imageView = card.findViewById(R.id.eventCardImage);
            Glide.with(this)
                    .load(e.getImgURL())
                    .placeholder(R.drawable.loading_spinner)
                    .into(imageView);
            imageView.setHorizontalFadingEdgeEnabled(true);
            imageView.setFadingEdgeLength(40);

            TextView tv = card.findViewById(R.id.eventCardName);
            tv.setText(e.getName());
            tv = card.findViewById(R.id.eventCardDateTime);
            tv.setText(e.getFormattedDate() + " " + e.getTime());
            tv = card.findViewById(R.id.eventCardVenue);
            tv.setText(e.getVenueName());
            linearLayout.addView(card);
        }
    }
}
