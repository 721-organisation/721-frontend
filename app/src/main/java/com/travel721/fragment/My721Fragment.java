package com.travel721.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.travel721.R;
import com.travel721.activity.EventMoreInfoActivity;
import com.travel721.activity.SettingsActivity;
import com.travel721.analytics.AnalyticsHelper;
import com.travel721.analytics.ReleaseAnalyticsEvent;
import com.travel721.card.EventCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.content.Context.MODE_PRIVATE;
import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.eventProfileLikedSearchFilter;
import static com.travel721.Constants.eventSearchFilter;

public class My721Fragment extends Fragment {
    String api_access_token;


    // This is where to make the bundle info
    public static My721Fragment newInstance(String api_access_token) {
        My721Fragment lef = new My721Fragment();
        lef.api_access_token = api_access_token;
        return lef;
    }

    RequestQueue queue;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_721, container, false);
        queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));

        final LinearLayout linearLayout = root.findViewById(R.id.eventListCardHolder);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            // Instantiate the RequestQueue.

            String fiid = Objects.requireNonNull(task.getResult()).getToken();
            String url = API_ROOT_URL + "eventProfiles?access_token=" + api_access_token + "&filter=" + eventProfileLikedSearchFilter(fiid);
            // Request a string response from the provided URL.
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        // Display the first 500 characters of the response string.
                        Log.v("RES", response);
//                                List<EventCard> cardArrayList = getIntent().getParcelableArrayListExtra("events");

                        try {
                            JSONArray eventProfileArray = new JSONArray(response);
                            int arrSize = eventProfileArray.length();
                            JSONObject jsonObject;
                            queue.cancelAll(MY_721_FRAGMENT_REQUEST_TAG);
                            queue.stop();
                            ArrayList<EventCard> eventCardArrayList = new ArrayList<>();
                            HashMap<String, String> eventToProfile = new HashMap<>();
                            for (int i = 0; i < eventProfileArray.length(); i++) {
                                jsonObject = eventProfileArray.getJSONObject(i);
                                String eventID = jsonObject.getString("eventSourceId");
                                eventToProfile.put(eventID, jsonObject.getString("id"));
                                String getEventUrl = API_ROOT_URL + "events?access_token=" + api_access_token + "&filter=" + eventSearchFilter(eventID);
                                StringRequest stringRequest1 = new StringRequest(Request.Method.GET, getEventUrl,
                                        response1 -> {
                                            try {
                                                JSONArray eventsFound = new JSONArray(response1);
                                                if (eventsFound.length() == 1) {
                                                    JSONObject eventToShow = eventsFound.getJSONObject(0);
                                                    EventCard eventCard = EventCard.unpackFromJson(eventToShow);

                                                    eventCardArrayList.add(eventCard);

                                                }
                                                Log.v("Inner Request", response1);
                                            } catch (JSONException je) {
                                                Log.e("OOPS", Objects.requireNonNull(je.getLocalizedMessage()));
                                            }
                                        }, error -> {

                                });
                                stringRequest1.setTag(MY_721_FRAGMENT_REQUEST_TAG);
                                queue.add(stringRequest1);
                            }
                            AtomicInteger dealtSize = new AtomicInteger();
                            queue.addRequestFinishedListener(request -> {
                                dealtSize.getAndIncrement();
                                if (dealtSize.get() < arrSize + 1 || isDetached()) {
                                    return;
                                }
                                Collections.sort(eventCardArrayList);
//                                    Collections.reverse(eventCardArrayList);
                                String previousDateTag = "";
                                if (eventCardArrayList.isEmpty()) {
                                    Snackbar.make(root, "No events saved - start swiping to see saved events.", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction(getString(android.R.string.ok), null).show();
                                }
                                for (int i = 0; i < eventCardArrayList.size(); i++) {
                                    boolean requireDateTag = false;
                                    if (i == 0) requireDateTag = true;

                                    View card;
                                    card = getLayoutInflater().inflate(R.layout.card_my721_list_item, null);
                                    ImageView imageView = card.findViewById(R.id.eventCardImage);
                                    CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(imageView.getContext());
                                    circularProgressDrawable.setStrokeWidth(5f);
                                    circularProgressDrawable.setCenterRadius(30f);
                                    circularProgressDrawable.start();
                                    Glide.with(getContext())
                                            .load(eventCardArrayList.get(i).getImgURL())
                                            .placeholder(circularProgressDrawable)
                                            .skipMemoryCache(true) //2
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .transition(DrawableTransitionOptions.withCrossFade(300))
                                            .error(R.drawable.ic_broken_image)
                                            .into(imageView);
                                    imageView.setHorizontalFadingEdgeEnabled(true);
                                    imageView.setFadingEdgeLength(40);

                                    TextView tv = card.findViewById(R.id.eventCardName);
                                    tv.setText(eventCardArrayList.get(i).getName());
                                    tv = card.findViewById(R.id.eventCardDateTime);
                                    tv.setText(eventCardArrayList.get(i).getTime());
                                    tv = card.findViewById(R.id.eventCardVenue);
                                    tv.setText(eventCardArrayList.get(i).getVenueName());
                                    int finalI = i;
                                    card.setOnClickListener(view -> {
                                        Intent intent = new Intent(getContext(), EventMoreInfoActivity.class);
                                        intent.putExtra("eventCard", (Parcelable) eventCardArrayList.get(finalI));
                                        startActivity(intent);
                                    });
                                    String eventProfileId = eventToProfile.get(eventCardArrayList.get(i).getEventSourceID());
                                    int finalI1 = i;
                                    card.findViewById(R.id.deleteImgView).setOnClickListener(view -> {
                                        DeleteEventBottomSheetDialogFragment deleteEventBottomSheetDialogFragment = DeleteEventBottomSheetDialogFragment.newInstance(eventProfileId, eventCardArrayList.get(finalI1).getName(), api_access_token);
                                        deleteEventBottomSheetDialogFragment.show(Objects.requireNonNull(getFragmentManager()),
                                                "delete_sheet_fragment");
                                    });

                                    AnalyticsHelper.logEvent(getContext(), ReleaseAnalyticsEvent.USER_CLICKS_EVENT_IN_LIKED_EVENT_LIST, null);

                                    if (requireDateTag || !eventCardArrayList.get(i).getPrettyDate().equals(previousDateTag)) {
                                        View dateTag = getLayoutInflater().inflate(R.layout.item_date_tag, null);
                                        TextView dateTagTextView = dateTag.findViewById(R.id.dateTag);
                                        dateTagTextView.setText(eventCardArrayList.get(i).getPrettyDate());
                                        linearLayout.addView(dateTag);
                                    }
                                    linearLayout.addView(card);
                                    previousDateTag = eventCardArrayList.get(i).getPrettyDate();
                                }
                            });
                            queue.start();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {

            });

            // Add the request to the RequestQueue.

            stringRequest.setTag(MY_721_FRAGMENT_REQUEST_TAG);
            queue.add(stringRequest);


        });
        root.findViewById(R.id.settingsButton).setOnClickListener(v -> {
            Intent i = new Intent(getContext(), SettingsActivity.class);
            startActivity(i);

        });

        // My 721 Tutorial

        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(getContext().getPackageName(), MODE_PRIVATE);
        if (sharedPreferences.getBoolean("firstmy721visit", true)) {
            Log.v("CSFT", "First Discover Visit");
            // sequence example
            ShowcaseConfig config = new ShowcaseConfig();
            Log.v("COLOR", String.valueOf(Color.argb(255, 254, 96, 96)));
            config.setMaskColor(Color.rgb(254, 96, 96));
            config.setDelay(100); // half second between each showcase view

            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "TutorialMy721");

            sequence.setConfig(config);

            int positions = 0;
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(getActivity())
                            .setTarget(root.findViewById(R.id.eventListCardHolder))
                            .setShapePadding(-75)
                            .setDismissText(R.string.click_to_continue)
                            .setContentText("Experiences you save in Near Me or Discover are shown here")
                            .setMaskColour(Color.argb(200, 254, 96, 96))
                            .build()
            );
            positions++;

            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(getActivity())
                            .setTarget(root.findViewById(R.id.settingsButton))
                            .setDismissText("Got It!")
                            .setContentText("Contact the 721 Team and see additional information about the app here")
                            .setMaskColour(Color.argb(200, 254, 96, 96))
                            .build());
            positions++;


            int numberOfPositions = positions;
            sequence.setOnItemDismissedListener((itemView, position) -> {
                if (position == numberOfPositions) {
                    // Unset first run
                    sharedPreferences.edit().putBoolean("firstmy721visit", false).apply();
                }

            });
            sequence.start();

        }


        return root;
    }


    String MY_721_FRAGMENT_REQUEST_TAG = "My721RequestTag";

    @Override
    public void onDetach() {
        super.onDetach();
        queue.cancelAll(MY_721_FRAGMENT_REQUEST_TAG);
        queue.stop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        queue.cancelAll(MY_721_FRAGMENT_REQUEST_TAG);
        queue.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        queue.cancelAll(MY_721_FRAGMENT_REQUEST_TAG);
        queue.stop();
    }
}
