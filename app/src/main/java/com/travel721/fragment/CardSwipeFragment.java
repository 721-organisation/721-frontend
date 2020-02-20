package com.travel721.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.travel721.CardStackAdapter;
import com.travel721.PageViewModel;
import com.travel721.R;
import com.travel721.VolleyRequestQueue;
import com.travel721.activity.Email721TeamRedirectActivity;
import com.travel721.activity.InitialLoadSplashActivity;
import com.travel721.analytics.AnalyticsHelper;
import com.travel721.analytics.ReleaseAnalyticsEvent;
import com.travel721.analytics.ReleaseScreenNameAnalytic;
import com.travel721.card.AdCard;
import com.travel721.card.Card;
import com.travel721.card.CardComparator;
import com.travel721.card.ContactUsFeedbackCard;
import com.travel721.card.EventCard;
import com.travel721.card.FeedbackCard;
import com.travel721.card.FeedbackToFirebaseCard;
import com.travel721.card.InstagramFeedbackCard;
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
import java.util.Collections;
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
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.content.Context.MODE_PRIVATE;
import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.SLIDE_ANIMATION_DURATION;
import static com.travel721.Constants.eventProfileAllSearchFilter;
import static com.travel721.analytics.ReleaseAnalyticsEvent.DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION;
import static com.travel721.analytics.ReleaseAnalyticsEvent.DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION_NEGATIVE_RESPONSE;
import static com.travel721.analytics.ReleaseAnalyticsEvent.DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION_POSITIVE_RESPONSE;
import static com.travel721.analytics.ReleaseAnalyticsEvent.FILTER_CLICKED_IN_DISCOVER;
import static com.travel721.analytics.ReleaseAnalyticsEvent.FILTER_CLICKED_IN_NEAR_ME;
import static com.travel721.analytics.ReleaseAnalyticsEvent.FINDING_EXPERIENCES_YOU_LIKE_QUESTION;
import static com.travel721.analytics.ReleaseAnalyticsEvent.FINDING_EXPERIENCES_YOU_LIKE_QUESTION_NEGATIVE_RESPONSE;
import static com.travel721.analytics.ReleaseAnalyticsEvent.FINDING_EXPERIENCES_YOU_LIKE_QUESTION_POSITIVE_RESPONSE;
import static com.travel721.analytics.ReleaseAnalyticsEvent.HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION;
import static com.travel721.analytics.ReleaseAnalyticsEvent.HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION_NEGATIVE_RESPONSE;
import static com.travel721.analytics.ReleaseAnalyticsEvent.HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION_POSITIVE_RESPONSE;
import static com.travel721.analytics.ReleaseAnalyticsEvent.HAVING_FUN_QUESTION;
import static com.travel721.analytics.ReleaseAnalyticsEvent.HAVING_FUN_QUESTION_NEGATIVE_RESPONSE;
import static com.travel721.analytics.ReleaseAnalyticsEvent.HAVING_FUN_QUESTION_POSITIVE_RESPONSE;
import static com.travel721.analytics.ReleaseAnalyticsEvent.NEED_MORE_HELP_QUESTION;
import static com.travel721.analytics.ReleaseAnalyticsEvent.NEED_MORE_HELP_QUESTION_NEGATIVE_RESPONSE;
import static com.travel721.analytics.ReleaseAnalyticsEvent.NEED_MORE_HELP_QUESTION_POSITIVE_RESPONSE;
import static com.travel721.analytics.ReleaseAnalyticsEvent.TEST_RELEASE_ANALYTICS_EVENT;
import static com.travel721.analytics.ReleaseAnalyticsEvent.USER_NEGATIVE_FEEDBACK;
import static com.travel721.analytics.ReleaseAnalyticsEvent.USER_POSITIVE_FEEDBACK;
import static com.travel721.analytics.ReleaseAnalyticsEvent.USER_SWIPED_LEFT;
import static com.travel721.analytics.ReleaseAnalyticsEvent.USER_SWIPED_RIGHT;
import static com.yuyakaido.android.cardstackview.Direction.Left;
import static com.yuyakaido.android.cardstackview.Direction.Right;

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

    static void filterUsingTags(List<Card> cardsToFilter, List<String> tagsToFilterBy) {
        ArrayList<Card> cardsToRemove = new ArrayList<>();
        for (Card c : cardsToFilter) {
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
        cardsToFilter.removeAll(cardsToRemove);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<String> tagsToFilterBy = null;
        cardArrayList = Objects.requireNonNull(getArguments()).getParcelableArrayList("events");
        if (cardArrayList != null) {
            tagsToFilterBy = getArguments().getStringArrayList("tagsToFilterBy");

            filterUsingTags(cardArrayList, tagsToFilterBy);

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
        String minDays;
        switch (Objects.requireNonNull(mode)) {
            case "nearme":
                String longitude = getArguments().getString("longitude");
                String latitude = getArguments().getString("latitude");
//                if (tagsToFilterBy == null || tagsToFilterBy.isEmpty()) {
                // TODO Asynchronously load more events

                // TODO show user that 721 is updating events

                EventCuratorAsyncTask nearmeeventCuratorAsyncTask = new EventCuratorAsyncTask(tagsToFilterBy, accessToken, IID, longitude, latitude, radius, daysFromNow);
                nearmeeventCuratorAsyncTask.execute();

//                }
                break;
            case "discover":
                AnalyticsHelper.setScreenNameAnalytic(getContext(), Objects.requireNonNull(getActivity()), ReleaseScreenNameAnalytic.DISCOVER_LAUNCHED, SelectLocationDiscoverFragment.class.getName());
//                minDays = "0"; Removed from ECAST
                String searchLocation = getArguments().getString("searchLocation");

                EventCuratorAsyncTask discovereventCuratorAsyncTask = new EventCuratorAsyncTask(accessToken, IID, radius, daysFromNow, searchLocation);
                discovereventCuratorAsyncTask.execute();
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

        View root = inflater.inflate(R.layout.fragment_card_swipe, container, false);

        AnalyticsHelper.logEvent(getContext(), TEST_RELEASE_ANALYTICS_EVENT, null);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_card_swipe);


        FloatingActionButton likeButton = root.findViewById(R.id.thumbupButton);
        FloatingActionButton dislikeButton = root.findViewById(R.id.thumbdownButton);
        FloatingActionButton shareEventButton = root.findViewById(R.id.shareEventButton);
        likeButton.setOnClickListener(this::likesTopCard);
        dislikeButton.setOnClickListener(this::dislikesTopCard);
        shareEventButton.setOnClickListener(view -> {
            AnalyticsHelper.logEvent(getContext(), ReleaseAnalyticsEvent.SHARE_CLICKED, null);
            try {
                Log.v("INDEX", String.valueOf(cardStackLayoutManager.getTopPosition()));

                int index = cardStackLayoutManager.getTopPosition();
                Card card = cardArrayList.get(index);
                if (card instanceof EventCard) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.putExtra(Intent.EXTRA_TEXT, "share721.appspot.com/event/" + ((EventCard) card).getEventSourceID());
                    i.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(i, Objects.requireNonNull(getContext()).getString(R.string.share_721_experience, ((EventCard) card).getName()));
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
                AnalyticsHelper.logEvent(getContext(), FILTER_CLICKED_IN_NEAR_ME, null);
                FilterBottomSheetFragment filterBottomSheetFragment = FilterBottomSheetFragment.newInstance(callingLoader, tags);
                filterBottomSheetFragment.show(Objects.requireNonNull(getFragmentManager()),
                        "filter_sheet_fragment");

            });
        }
        if (callingLoader instanceof LoadingDiscoverFragment) {
            filterButton.setOnClickListener(view -> {
                AnalyticsHelper.logEvent(getContext(), FILTER_CLICKED_IN_DISCOVER, null);
                SelectLocationDiscoverFragment addPhotoBottomDialogFragment =
                        SelectLocationDiscoverFragment.newInstance(callingLoader, Objects.requireNonNull(getArguments()).getString("accessToken"), getArguments().getString("IID"), getArguments().getString("searchLocation"));
                addPhotoBottomDialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),
                        "discover_sheet_fragment");
            });
        }
        cardStackView = root.findViewById(R.id.card_stack_view);

        if (cardArrayList != null && cardArrayList.isEmpty())
            root.findViewById(R.id.background_textview).setVisibility(View.VISIBLE);

        initialise();

        String mode = Objects.requireNonNull(getArguments()).getString("mode");
        if ("applink".equals(Objects.requireNonNull(mode))) {
            TextView tv = root.findViewById(R.id.background_textview);
            tv.setText(getString(R.string._721_event_link_return_hint));
            tv.setOnClickListener(v -> {
                Intent i = new Intent(getContext(), InitialLoadSplashActivity.class);
                startActivity(i);
                Objects.requireNonNull(callingLoader.getActivity()).finish();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            // CSF Tutorial
            SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(getContext().getPackageName(), MODE_PRIVATE);
            if (sharedPreferences.getBoolean("firstcsfvisit", true)) {
                // sequence example
                ShowcaseConfig config = new ShowcaseConfig();
                Log.v("COLOR", String.valueOf(Color.argb(255, 254, 96, 96)));
                config.setMaskColor(Color.rgb(254, 96, 96));
                config.setDelay(100); // half second between each showcase view
                Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.open_sans_bold);
                config.setDismissTextStyle(typeface);
                MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "TutorialNearMe");
                sequence.setConfig(config);

                int positions = 0;
                sequence.addSequenceItem(
                        new MaterialShowcaseView.Builder(getActivity())
                                .setTarget(getView().findViewById(R.id.card_stack_view))
                                .setSkipText("Skip")
                                .setShapePadding(-500)
                                .setDismissText(getString(R.string.click_to_continue))
                                .setContentText("Swipe right to save to My 721. Swipe left to dismiss")
                                .setMaskColour(Color.argb(200, 254, 96, 96))
                                .build()
                );
                positions++;

                sequence.addSequenceItem(
                        new MaterialShowcaseView.Builder(getActivity())
                                .setSkipText("Skip")
                                .setTarget(getView().findViewById(R.id.thumbupButton))
                                .setDismissText(getString(R.string.click_to_continue))
                                .setContentText("If you don't like swiping, feel free to use the buttons here")
                                .setMaskColour(Color.argb(200, 254, 96, 96))

                                .build());
                positions++;

                sequence.addSequenceItem(
                        new MaterialShowcaseView.Builder(getActivity())
                                .setSkipText("Skip")
                                .setTarget(getView().findViewById(R.id.filterButton))
                                .setDismissText(getString(R.string.click_to_continue))
                                .setContentText("Filter and change curation settings using this button")
                                .setMaskColour(Color.argb(200, 254, 96, 96))
                                .build());
                positions++;


                sequence.addSequenceItem(new MaterialShowcaseView.Builder(getActivity())
                        .setSkipText("Skip")
                        .setTarget(getView().findViewById(R.id.shareEventButton))
                        .setDismissText(getString(R.string.click_to_continue))
                        .setContentText("Share experiences on 721 with this button")
                        .setMaskColour(Color.argb(200, 254, 96, 96))
                        .build());
                positions++;

                sequence.addSequenceItem(
                        new MaterialShowcaseView.Builder(getActivity())
                                .setSkipText("Skip")
                                .setTarget(((TabLayout) getActivity().findViewById(R.id.tabLayout)).getTabAt(2).view)
                                .setDismissText(getString(R.string.click_to_continue))
                                .setContentText(getString((R.string.discover_tutorial_hint)))
                                .setMaskColour(Color.argb(200, 254, 96, 96))
                                .build());
                positions++;

                sequence.addSequenceItem(
                        new MaterialShowcaseView.Builder(getActivity())
                                .setSkipText("Skip")
                                .setTarget(((TabLayout) getActivity().findViewById(R.id.tabLayout)).getTabAt(0).view)
                                .setDismissText("Got It!")
                                .setContentText(getString(R.string.my_721_tutorial_hint))
                                .setMaskColour(Color.argb(200, 254, 96, 96))
                                .build());
                positions++;


                int numberOfPositions = positions;
                sequence.setOnItemDismissedListener((itemView, position) -> {
                    if (position == numberOfPositions) {
                        // Unset first run
                        sharedPreferences.edit().putBoolean("firstcsfvisit", false).apply();
                    }

                });
                sequence.start();

            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        queue.getRequestQueue().start();

    }

    void initialise() {
        // Create card stack manager
        cardStackLayoutManager = new CardStackLayoutManager(getContext(), this);
        cardStackLayoutManager.setStackFrom(StackFrom.Top);
        cardStackLayoutManager.setVisibleCount(2);
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        List<Direction> directions = new ArrayList<>();
        directions.add(Direction.Right);
        directions.add(Left);
        cardStackLayoutManager.setDirections(directions);
        queue = VolleyRequestQueue.getInstance(getContext());
        queue.getRequestQueue().start();
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
            TextView tv = getView().findViewById(R.id.background_textview);
            tv.setVisibility(View.VISIBLE);
        }
        // Gets the Card index
        int index = cardStackLayoutManager.getTopPosition() - 1;
//        int index = cardStackLayoutManager.getTopPosition();
        if (Objects.requireNonNull(Objects.requireNonNull(getArguments()).getString("mode")).equals("applink")) {
            cardStackView.setVisibility(View.GONE);
        }

        if (cardArrayList.get(index) instanceof EventCard) {
            EventCard eventCard = (EventCard) cardArrayList.get(index);

            StringRequest stringRequest;
            String url = API_ROOT_URL + "eventProfiles?access_token=" + getArguments().getString("accessToken");
            // Each case makes a call to the Analytics API
            //vector drawable
            TSnackbar snackbar = TSnackbar
                    .make(getView().findViewById(R.id.rootConstraintLayout), "", TSnackbar.LENGTH_SHORT);
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
                    Log.d("CSF", "onCardSwiped: swiped left");
                    snackbar.dismiss();
                    String[] negative_terms = getResources().getStringArray(R.array.negative_terms);
                    textView.setText(getRandom(negative_terms));
                    textView.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.secondaryColor));

                    AnalyticsHelper.logEvent(getContext(), USER_SWIPED_LEFT, null);
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
                    Log.d("CSF", "onCardSwiped: swiped right");
                    snackbar.dismiss();
                    String[] positive_terms = getResources().getStringArray(R.array.positive_terms);
                    textView.setText(getRandom(positive_terms));
                    textView.setTextColor(Color.rgb(0, 230, 118));
                    snackbar.show();
                    AnalyticsHelper.logEvent(getContext(), USER_SWIPED_RIGHT, null);

                    // Likes
                    // Request a string response from the provided URL.
                    stringRequest = new StringRequest(Request.Method.POST, url,
                            response -> {
                                // Display the first 500 characters of the response string.
                                Log.v("SWIPE", "Successfully registered like on " + eventCard.getName());
                            }, error -> {
                        error.printStackTrace();
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }) {
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
        if (cardArrayList.get(index) instanceof InstagramFeedbackCard) {
            if (direction == Left) {
                String url = "https://www.instagram.com/721app/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                Objects.requireNonNull(getContext()).startActivity(i);
            }
            SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(getContext().getPackageName(), MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("firstinstacard", false).apply();
        }
        if (cardArrayList.get(index) instanceof ContactUsFeedbackCard && direction == Direction.Right) {
            Intent i = new Intent(getContext(), Email721TeamRedirectActivity.class);
            Objects.requireNonNull(getContext()).startActivity(i);
        }
        if (cardArrayList.get(index) instanceof FeedbackToFirebaseCard) {
            String question = ((FeedbackToFirebaseCard) cardArrayList.get(index)).getQuestion();
            switch (question) {
                case HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION:
                    if (direction == Left)
                        AnalyticsHelper.logEvent(getContext(), HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION_NEGATIVE_RESPONSE, null);
                    if (direction == Right)
                        AnalyticsHelper.logEvent(getContext(), HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION_POSITIVE_RESPONSE, null);
                    break;
                case DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION:
                    if (direction == Left)
                        AnalyticsHelper.logEvent(getContext(), DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION_NEGATIVE_RESPONSE, null);
                    if (direction == Right)
                        AnalyticsHelper.logEvent(getContext(), DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION_POSITIVE_RESPONSE, null);
                    break;
                case HAVING_FUN_QUESTION:
                    if (direction == Left)
                        AnalyticsHelper.logEvent(getContext(), HAVING_FUN_QUESTION_NEGATIVE_RESPONSE, null);
                    if (direction == Right)
                        AnalyticsHelper.logEvent(getContext(), HAVING_FUN_QUESTION_POSITIVE_RESPONSE, null);
                    break;
                case FINDING_EXPERIENCES_YOU_LIKE_QUESTION:
                    if (direction == Left)
                        AnalyticsHelper.logEvent(getContext(), FINDING_EXPERIENCES_YOU_LIKE_QUESTION_NEGATIVE_RESPONSE, null);
                    if (direction == Right)
                        AnalyticsHelper.logEvent(getContext(), FINDING_EXPERIENCES_YOU_LIKE_QUESTION_POSITIVE_RESPONSE, null);
                    break;
                case NEED_MORE_HELP_QUESTION:
                    if (direction == Left)
                        AnalyticsHelper.logEvent(getContext(), NEED_MORE_HELP_QUESTION_NEGATIVE_RESPONSE, null);
                    if (direction == Right)
                        AnalyticsHelper.logEvent(getContext(), NEED_MORE_HELP_QUESTION_POSITIVE_RESPONSE, null);
            }
            Toast.makeText(getContext(), "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
        }
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
                .setDirection(Left)
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


    public class EventCuratorAsyncTask extends AsyncTask<Void, String, List<EventCard>> {
        private String accessToken;
        private String IID;
        private String longitude;
        private String latitude;
        private String radius;
        private String daysFromNow;
        private boolean discoverMode;
        private String searchLocation;
        private List<String> tags;

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
        public EventCuratorAsyncTask(@Nullable List<String> tags, String accessToken, String IID, String longitude, String latitude, String radius, String daysFromNow) {
            this.accessToken = accessToken;
            this.IID = IID;
            this.longitude = longitude;
            this.tags = tags;
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
            try {
                super.onPostExecute(eventCards);

                // Check if there are no results and display message accordingly
                final View.OnClickListener performFilterButtonClick = view -> Objects.requireNonNull(getView()).findViewById(R.id.filterButton).performClick();
                if ((eventCards != null && eventCards.isEmpty()) && (cardArrayList != null && cardArrayList.isEmpty())) {
                    if (discoverMode) {
                        TextView bgtv = Objects.requireNonNull(getView()).findViewById(R.id.background_textview);
                        bgtv.setText(getResources().getString(R.string.discover_unavailable));
                        bgtv.setOnClickListener(view -> {
                            SelectLocationDiscoverFragment addPhotoBottomDialogFragment =
                                    SelectLocationDiscoverFragment.newInstance(callingLoader, accessToken, IID);
                            addPhotoBottomDialogFragment.show(Objects.requireNonNull(getFragmentManager()),
                                    "discover_sheet_fragment");
                        });
                        getView().findViewById(R.id.card_stack_view).setVisibility(View.GONE);
                    } else {
                        TextView bgtv = Objects.requireNonNull(getView()).findViewById(R.id.background_textview);
                        bgtv.setText(getResources().getString(R.string.near_me_unavailable));
                        bgtv.setOnClickListener(performFilterButtonClick);
                        Objects.requireNonNull(getView()).findViewById(R.id.card_stack_view).setVisibility(View.GONE);

                    }
                } else {
                    if (getView() != null) {
                        TextView bgtv = Objects.requireNonNull(getView()).findViewById(R.id.background_textview);
                        bgtv.setText(getResources().getString(R.string.end_of_stack));
                        bgtv.setOnClickListener(performFilterButtonClick);
                    }
                }
                // Add two AdCards and display to screen
                ArrayList<Card> combinedList = new ArrayList<>();
                combinedList.addAll(cardStackAdapter.getEvents());
                combinedList.addAll(Objects.requireNonNull(eventCards));
                combinedList.add((int) (combinedList.size() * 0.4), new AdCard());
                combinedList.add((int) (combinedList.size() * 0.8), new AdCard());
                combinedList.add((int) (combinedList.size() * 0.51), new FeedbackToFirebaseCard());
                SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(getContext().getPackageName(), MODE_PRIVATE);
                if (sharedPreferences.getBoolean("firstinstacard", true))
                    combinedList.add((int) (combinedList.size() * 0.2), new InstagramFeedbackCard());
                cardArrayList = combinedList;


                Collections.sort(cardArrayList, new CardComparator<>());

                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CardDiffCallback(cardStackAdapter.getEvents(), combinedList));

                // Implements filtering networked results
                if (tags == null || tags.isEmpty()) {
                    cardStackAdapter.setEvents(cardArrayList);
                } else {
                    filterUsingTags(combinedList, tags);
                    cardStackAdapter.setEvents(combinedList);
                }

                diffResult.dispatchUpdatesTo(cardStackAdapter);

            } catch (NullPointerException e) {
                Log.e("NPE", "onPostExecute: tried to update UI but failed with nullpointerexception");
            }
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
                            .add("minDays", "0")
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
                                        API_ROOT_URL + "events/getWithinDistance?latitude=0&longitude=0&radius=" + radius + "&daysFromNow=" + daysFromNow + "&access_token=" + accessToken + "&explore=true&location=" + searchLocation + "&minDays=0" :

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

    public static double getDistanceFromLatLongs(double lat1, double lat2, double lon1,
                                                 double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}