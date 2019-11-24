package com.travel721.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.travel721.CardStackAdapter;
import com.travel721.PageViewModel;
import com.travel721.R;
import com.travel721.VolleyRequestQueue;
import com.travel721.activity.InitialLoadSplashActivity;
import com.travel721.analytics.AnalyticsHelper;
import com.travel721.card.AdCard;
import com.travel721.card.Card;
import com.travel721.card.EventCard;
import com.travel721.card.FeedbackCard;
import com.travel721.eventcaching.CacheDatabase;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.SLIDE_ANIMATION_DURATION;
import static com.travel721.Constants.eventProfileAllSearchFilter;
import static com.travel721.analytics.AnalyticsHelper.USER_NEGATIVE_FEEDBACK;
import static com.travel721.analytics.AnalyticsHelper.USER_POSITIVE_FEEDBACK;

/**
 * A placeholder fragment containing a simple view.
 */
public class CardSwipeFragment extends Fragment implements CardStackListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    static ArrayList<String> tags = new ArrayList<>();
    private static final int HTTP_STATUS_OK = 200;
    private Context dbAccessContext;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private View root;
    private ArrayList<Card> cardArrayList;
    private CardStackAdapter cardStackAdapter;
    private String CARD_SWIPE_REQUEST_TAG = "CardSwipeRequestTag";
    private VolleyRequestQueue queue;
    private LoadingFragment callingLoader;
    // Keep a track of the CardView and it's adapter
    private CardStackView cardStackView;
    private CardStackLayoutManager cardStackLayoutManager;
    private boolean snackShown = false;

    // This is where to make the bundle info
    public static CardSwipeFragment newInstance(Bundle bundle, LoadingFragment callingLoader) {
        CardSwipeFragment fragment = new CardSwipeFragment();
        fragment.setArguments(bundle);
        fragment.callingLoader = callingLoader;
        return fragment;
    }

    // This is where to make the bundle info
    public static CardSwipeFragment newNonBoundInstance(Bundle bundle) {
        CardSwipeFragment fragment = new CardSwipeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<String> tagsToFilterBy = null;
        cardArrayList = Objects.requireNonNull(getArguments()).getParcelableArrayList("events");
        if (cardArrayList != null) {
            tagsToFilterBy = getArguments().getStringArrayList("tagsToFilterBy");
            ArrayList<Card> cardsToRemove = new ArrayList<>();
            for (Card c : cardArrayList) {
                if (c instanceof EventCard) {
                    if (tagsToFilterBy != null && !tagsToFilterBy.isEmpty()) {
                        for (String s : tagsToFilterBy) {
                            boolean relevant = false;
                            if (((EventCard) c).tags.contains(s)) {
                                relevant = true;
                            }
                            if (!relevant) cardsToRemove.add(c);
                        }
                    }
                    Set<String> fooSet = new LinkedHashSet<>(tags);
                    fooSet.addAll(((EventCard) c).tags);
                    fooSet.addAll(tags);
                    tags = new ArrayList<>();
                    tags.addAll(fooSet);
                }
            }
            cardArrayList.removeAll(cardsToRemove);
        } else {
            cardArrayList = new ArrayList<>();
        }
        PageViewModel pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);

        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        String mode = getArguments().getString("mode");
        String accessToken = getArguments().getString("accessToken");
        String IID = getArguments().getString("IID");
        String radius = getArguments().getString("radius");
        String daysFromNow = getArguments().getString("daysFromNow");
        switch (Objects.requireNonNull(mode)) {
            case "nearme":
                String longitude = getArguments().getString("longitude");
                String latitude = getArguments().getString("latitude");
                if (tagsToFilterBy == null || tagsToFilterBy.isEmpty()) {
                    // TODO Asynchronously load more events
                    VolleyRequestQueue requestQueue = VolleyRequestQueue.getInstance(getContext());

                    // TODO show user that 721 is updating events


                    EventCuratorAsyncTask eventCuratorAsyncTask = new EventCuratorAsyncTask(accessToken, IID, longitude, latitude, radius, daysFromNow);
                    eventCuratorAsyncTask.execute();

                }
                break;
            case "discover":
                String searchLocation = getArguments().getString("searchLocation");
                EventCuratorAsyncTask eventCuratorAsyncTask = new EventCuratorAsyncTask(accessToken, IID, radius, daysFromNow, searchLocation);
                eventCuratorAsyncTask.execute();
                break;
            case "applink":
                break;
            default:
                Toast.makeText(getContext(), "721 launched in invalid mode", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_card_swipe, container, false);

        AnalyticsHelper.logEvent(getContext(), AnalyticsHelper.TEST_RELEASE_ANALYTICS_EVENT, null);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_card_swipe);


        FloatingActionButton likeButton = root.findViewById(R.id.thumbupButton);
        FloatingActionButton dislikeButton = root.findViewById(R.id.thumbdownButton);
        FloatingActionButton shareEventButton = root.findViewById(R.id.shareEventButton);
        likeButton.setOnClickListener(this::likesTopCard);
        dislikeButton.setOnClickListener(this::dislikesTopCard);
        shareEventButton.setOnClickListener(view -> {
            try {
                Log.v("INDEX", String.valueOf(cardStackLayoutManager.getTopPosition()));

                int index = cardStackLayoutManager.getTopPosition();
                Card card = cardArrayList.get(index);
                if (card instanceof EventCard) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.putExtra(Intent.EXTRA_TEXT, "share721.appspot.com/event/" + ((EventCard) card).getEventSourceID());
                    i.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(i, null);
                    startActivity(shareIntent);
                } else if (card instanceof FeedbackCard) {
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.travel721"));
                    startActivity(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        FloatingActionButton filterButton = root.findViewById(R.id.filterButton);
        if (callingLoader instanceof LoadingNearMeFragment) {
            filterButton.setOnClickListener(view -> {
                FilterBottomSheetFragment filterBottomSheetFragment = FilterBottomSheetFragment.newInstance(callingLoader, tags);
                filterBottomSheetFragment.show(Objects.requireNonNull(getFragmentManager()),
                        "filter_sheet_fragment");

            });
        }
//        if (callingLoader instanceof LoadingDiscoverFragment) {
//            SelectLocationDiscoverFragment addPhotoBottomDialogFragment =
//                    SelectLocationDiscoverFragment.newInstance(R.id.fragmentContainer, Objects.requireNonNull(getArguments()).getString("accessToken"), getArguments().getString("IID"));
//            addPhotoBottomDialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),
//                    "discover_sheet_fragment");
//        }
        cardStackView = root.findViewById(R.id.card_stack_view);

        if (cardArrayList != null && cardArrayList.isEmpty())
            root.findViewById(R.id.background_textview).setVisibility(View.VISIBLE);

        initialise();

        String mode = Objects.requireNonNull(getArguments()).getString("mode");
        if ("applink".equals(Objects.requireNonNull(mode))) {
            TextView tv = root.findViewById(R.id.background_textview);
            tv.setText("To return to 721; click here");
            tv.setOnClickListener(v -> {
                Intent i = new Intent(getContext(), InitialLoadSplashActivity.class);
                startActivity(i);
                Objects.requireNonNull(getActivity()).finish();
            });
        }

        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        queue.getRequestQueue().cancelAll(CARD_SWIPE_REQUEST_TAG);
        queue.getRequestQueue().stop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        queue.getRequestQueue().cancelAll(CARD_SWIPE_REQUEST_TAG);
        queue.getRequestQueue().stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        queue.getRequestQueue().cancelAll(CARD_SWIPE_REQUEST_TAG);
        queue.getRequestQueue().stop();
    }

    void initialise() {
        // Create card stack manager
        cardStackLayoutManager = new CardStackLayoutManager(getContext(), this);
        cardStackLayoutManager.setStackFrom(StackFrom.Top);
        cardStackLayoutManager.setVisibleCount(2);
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        List<Direction> directions = new ArrayList<>();
        directions.add(Direction.Right);
        directions.add(Direction.Left);
        directions.add(Direction.Left);
        cardStackLayoutManager.setDirections(directions);
        queue = VolleyRequestQueue.getInstance(getContext());
        // Create card stack adapter
        cardStackAdapter = new CardStackAdapter(cardArrayList);

        cardStackView.setLayoutManager(cardStackLayoutManager);
        cardStackView.setAdapter(cardStackAdapter);
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {
        if (!snackShown) {
            snackShown = true;
            Snackbar.make(cardStackView, getResources().getString(R.string.tap_to_see_more_info), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCardSwiped(Direction direction) {
        // Removes TSnackBar
        if (cardStackLayoutManager.getChildCount() == 0) {
            TextView tv = root.findViewById(R.id.background_textview);
            tv.setVisibility(View.VISIBLE);
        }
        // Gets the Card index
        int index = cardStackLayoutManager.getTopPosition() - 1;
//        int index = cardStackLayoutManager.getTopPosition();
        if (Objects.requireNonNull(Objects.requireNonNull(getArguments()).getString("mode")).equals("applink")) {
            cardStackView.setVisibility(View.GONE);
            cardStackView.setVisibility(View.GONE);
        }

        if (cardArrayList.get(index) instanceof EventCard) {
            EventCard eventCard = (EventCard) cardArrayList.get(index);

            StringRequest stringRequest;
            String url = API_ROOT_URL + "eventProfiles?access_token=" + getArguments().getString("accessToken");
            // Each case makes a call to the Analytics API
            //vector drawable
            TSnackbar snackbar = TSnackbar
                    .make(root.findViewById(R.id.rootConstraintLayout), "", TSnackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.WHITE);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.WHITE);
            TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            textView.setPadding(0, 50, 0, 50);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            final Runnable runnable = () -> CacheDatabase.getInstance(getContext()).eventCardDao().delete(eventCard);
            switch (direction) {
                case Left:
                    snackbar.dismiss();
                    String[] negative_terms = getResources().getStringArray(R.array.negative_terms);
                    textView.setText(getRandom(negative_terms));
                    textView.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorAccent));
                    snackbar.show();
                    AnalyticsHelper.logEvent(getContext(), AnalyticsHelper.USER_SWIPED_LEFT, null);
                    // Dislikes
                    // Request a string response from the provided URL.
                    stringRequest = new StringRequest(Request.Method.POST, url,
                            response -> {
                                // Display the first 500 characters of the response string.
                                Log.v("SWIPE", "Successfully registered dislike on " + eventCard.getName());
                            }, error -> Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show()) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> map = new HashMap<>();
                            map.put("swipe", "false");
                            map.put("eventSourceId", eventCard.getEventSourceID());
                            map.put("profileId", Objects.requireNonNull(getArguments()).getString("IID"));
                            return map;
                        }
                    };
                    new Thread(runnable).start();
                    // Add the request to the RequestQueue.
                    stringRequest.setTag(CARD_SWIPE_REQUEST_TAG);
                    queue.addToRequestQueue(stringRequest);
                    break;
                case Right:
                    snackbar.dismiss();
                    String[] positive_terms = getResources().getStringArray(R.array.positive_terms);
                    textView.setText(getRandom(positive_terms));
                    textView.setTextColor(Color.rgb(0, 230, 118));
                    snackbar.show();
                    AnalyticsHelper.logEvent(getContext(), AnalyticsHelper.USER_SWIPED_RIGHT, null);

                    // Likes
                    // Request a string response from the provided URL.
                    stringRequest = new StringRequest(Request.Method.POST, url,
                            response -> {
                                // Display the first 500 characters of the response string.
                                Log.v("SWIPE", "Successfully registered like on " + eventCard.getName());
                            }, error -> Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show()) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> map = new HashMap<>();
                            map.put("swipe", "true");
                            map.put("eventSourceId", eventCard.getEventSourceID());
                            map.put("profileId", Objects.requireNonNull(getArguments()).getString("IID"));
                            return map;
                        }
                    };
                    new Thread(runnable).start();                    // Add the request to the RequestQueue.
                    stringRequest.setTag(CARD_SWIPE_REQUEST_TAG);
                    queue.addToRequestQueue(stringRequest);
                    break;
            }
        }
        if (cardArrayList.get(index) instanceof FeedbackCard) {
            switch (direction) {
                case Left:
                    AnalyticsHelper.logEvent(getContext(), USER_NEGATIVE_FEEDBACK, null);
                    break;
                case Right:
                    AnalyticsHelper.logEvent(getContext(), USER_POSITIVE_FEEDBACK, null);
                    break;
            }
        }
//        cardArrayList.remove(cardArrayList.get(index));
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }

    public void dislikesTopCard(View view) {
        cardStackLayoutManager.setSwipeAnimationSetting(new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(SLIDE_ANIMATION_DURATION)
                .setInterpolator(new AccelerateInterpolator())
                .build());
        cardStackView.swipe();

    }


    public void likesTopCard(View view) {

        cardStackLayoutManager.setSwipeAnimationSetting(new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(SLIDE_ANIMATION_DURATION)
                .setInterpolator(new AccelerateInterpolator())
                .build());
        cardStackView.swipe();
    }

    protected void updateCards() {
        cardStackAdapter.notifyDataSetChanged();
    }

    public class EventCuratorAsyncTask extends AsyncTask<Void, String, List<EventCard>> {
        private String accessToken;
        private String IID;
        private String longitude;
        private String latitude;
        private String radius;
        private String daysFromNow;
        private boolean discoverMode;
        private String searchLocation;

        /**
         * This constructor is for the 'Near Me' mode only
         *
         * @param accessToken the API access token
         * @param IID         the user's IID
         * @param longitude   user's longitude
         * @param latitude    user's latitude
         * @param radius      user's search radius
         * @param daysFromNow days from now to search
         */
        public EventCuratorAsyncTask(String accessToken, String IID, String longitude, String latitude, String radius, String daysFromNow) {
            this.accessToken = accessToken;
            this.IID = IID;
            this.longitude = longitude;
            this.latitude = latitude;
            this.radius = radius;
            this.daysFromNow = daysFromNow;
            discoverMode = false;
        }


        /**
         * This constructor is for 'Discover' mode only
         *
         * @param accessToken    the API access token
         * @param IID            the user's IID
         * @param radius         user's search radius
         * @param daysFromNow    days from now to search
         * @param searchLocation the location to discover
         */
        public EventCuratorAsyncTask(String accessToken, String IID, String radius, String daysFromNow, String searchLocation) {
            this.accessToken = accessToken;
            this.IID = IID;
            this.longitude = String.valueOf(0);
            this.latitude = String.valueOf(0);
            this.radius = radius;
            this.daysFromNow = daysFromNow;
            this.searchLocation = searchLocation;
            discoverMode = true;
        }

        @Override
        protected void onPostExecute(List<EventCard> eventCards) {
            super.onPostExecute(eventCards);
            if (!eventCards.isEmpty()) updateCards();
            cardStackAdapter.getEvents().add((int) (cardStackAdapter.getItemCount() * 0.4), new AdCard());
            cardStackAdapter.getEvents().add((int) (cardStackAdapter.getItemCount() * 0.6), new AdCard());
            updateCards();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.v("ECAST", values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dbAccessContext = CardSwipeFragment.this.getContext();
        }

        @Override
        protected List<EventCard> doInBackground(Void... voids) {
            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            String stringResponse;
            publishProgress("Updating events on server");

            ArrayList<EventCard> eventsFound = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();
            try {
                FormBody formBody;
                if (discoverMode) {
                    formBody = new FormBody.Builder()
                            .add("latitude", "0")
                            .add("longitude", "0")
                            .add("location", searchLocation)
                            .add("explore", "true")
                            .add("radius", String.valueOf(radius))
                            .add("daysFromNow", String.valueOf(daysFromNow))
                            .build();
                } else {
                    formBody = new FormBody.Builder()
                            .add("latitude", latitude)
                            .add("longitude", longitude)
                            .add("explore", "false")
                            .add("radius", String.valueOf(radius))
                            .add("daysFromNow", String.valueOf(daysFromNow))
                            .build();
                }
                okhttp3.Request updateEventsOnServer = new okhttp3.Request.Builder()
                        .url(API_ROOT_URL + "events/updateNew?access_token=" + accessToken)
                        .put(formBody)
                        .build();

                try (Response updateEventsOnServerResponse = client.newCall(updateEventsOnServer).execute()) {
                    stringResponse = Objects.requireNonNull(updateEventsOnServerResponse.body()).string();

                    publishProgress(stringResponse);
                    // We don't need to look at the response, just check it returned 200
                    if (updateEventsOnServerResponse.code() == HTTP_STATUS_OK) {

                        okhttp3.Request getEventsFromServerRequest = new okhttp3.Request.Builder()
                                .url(discoverMode ?
                                        API_ROOT_URL + "events/getWithinDistance?latitude=0&longitude=0&radius=" + radius + "&daysFromNow=" + daysFromNow + "&access_token=" + accessToken + "&explore=true&location=" + searchLocation :

                                        API_ROOT_URL + "events/getWithinDistance?latitude=" + latitude + "&longitude=" + longitude + "&radius=" + radius + "&daysFromNow=" + daysFromNow + "&access_token=" + accessToken + "&explore=false")
                                .build();

                        try (Response getEventsFromServerResponse = client.newCall(getEventsFromServerRequest).execute()) {
                            stringResponse = Objects.requireNonNull(getEventsFromServerResponse.body()).string();
                            publishProgress(stringResponse);
                            // Extract events from JSON response
                            JSONObject jo1 = new JSONObject(stringResponse);
                            JSONArray events = jo1.getJSONArray("getWithinDistance");

                            // Add events to temporary list
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                eventsFound.add(EventCard.unpackFromJson(event));

                            }
                            // Filter events already swiped through
                            okhttp3.Request getSwipedEventIDsRequest = new okhttp3.Request.Builder()
                                    .url(API_ROOT_URL + "eventProfiles?access_token=" + accessToken + "&filter=" + eventProfileAllSearchFilter(IID))
                                    .build();
                            try (Response getSwipedEventIDsResponse = client.newCall(getSwipedEventIDsRequest).execute()) {
                                stringResponse = Objects.requireNonNull(getSwipedEventIDsResponse.body()).string();
                                publishProgress(stringResponse);
                                final JSONArray eventProfileArray = new JSONArray(stringResponse);
                                JSONObject jsonObject;
                                ArrayList<String> alreadySwipedIDs = new ArrayList<>();
                                for (int i = 0; i < eventProfileArray.length(); i++) {
                                    jsonObject = eventProfileArray.getJSONObject(i);
                                    String eventID = jsonObject.getString("eventSourceId");
                                    alreadySwipedIDs.add(eventID);
                                }

                                ArrayList<EventCard> filteredCards = new ArrayList<>();
                                for (EventCard e : eventsFound) {
                                    if (!alreadySwipedIDs.contains(e.getEventSourceID()) && !cardStackAdapter.getEvents().contains(e)) {
                                        filteredCards.add(e);
                                    }
                                }

                                for (EventCard ec : filteredCards) {
                                    if (!discoverMode)
                                        CacheDatabase.getInstance(dbAccessContext).eventCardDao().insert(ec);
                                    cardStackAdapter.getEvents().add(ec);
                                    Log.v("ASYNC", "added card to stack");
                                }

                                return filteredCards;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }

}