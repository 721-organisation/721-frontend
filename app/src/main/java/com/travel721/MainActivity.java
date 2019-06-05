package com.travel721;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int windowwidth;
    int screenCenter;
    int x_cord, y_cord, x, y;
    int Likes = 0;
    public RelativeLayout parentView;
    private Context context;
    private ArrayList<EventCard> eventArrayList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        parentView = (RelativeLayout) findViewById(R.id.main_layoutview);
        windowwidth = getWindowManager().getDefaultDisplay().getWidth();
        screenCenter = windowwidth / 2;
        eventArrayList = new ArrayList<>();
        getArrayData();
        for (int i = 0; i < eventArrayList.size(); i++) {
            LayoutInflater inflate =
                    (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View containerView = inflate.inflate(R.layout.layout, null);
            RelativeLayout relativeLayoutContainer = containerView.findViewById(R.id.relative_container);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            containerView.setLayoutParams(layoutParams);
            containerView.setTag(i);

            // Unpack event data
            TextView eventTitle = containerView.findViewById(R.id.eventTitle);
            eventTitle.setText(eventArrayList.get(i).getEventName());

            TextView eventLine1 = (TextView) containerView.findViewById(R.id.eventLine1);
            eventLine1.setText(eventArrayList.get(i).getEventImgURL());
            //new DownloadImageTask((ImageView) findViewById(R.id.eventImage))
            ////      .execute(eventArrayList.get(i).getEventImgURL());
            final int finalI = i;
            Thread thread = new Thread(){
                public void run(){
                    try {
                        URL newurl = new URL(eventArrayList.get(finalI).getEventImgURL());
                        Bitmap mIcon_val;

                        mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                        ((ImageView) findViewById(R.id.eventImage)).setImageBitmap(mIcon_val);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();



            relativeLayoutContainer.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    x_cord = (int) event.getRawX();
                    y_cord = (int) event.getRawY();

                    containerView.setX(0);
                    containerView.setY(0);

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            x = (int) event.getX();
                            y = (int) event.getY();


                            Log.v("On touch", x + " " + y);
                            break;
                        case MotionEvent.ACTION_MOVE:

                            x_cord = (int) event.getRawX();
                            // smoother animation.
                            y_cord = (int) event.getRawY();

                            containerView.setX(x_cord - x);
                            containerView.setY(y_cord - y);


                            if (x_cord >= screenCenter) {
                                containerView.setRotation((float) ((x_cord - screenCenter) * (Math.PI / 32)));
                                if (x_cord > (screenCenter + (screenCenter / 2))) {
                                    if (x_cord > (windowwidth - (screenCenter / 4))) {
                                        Likes = 2;
                                    } else {
                                        Likes = 0;
                                    }
                                } else {
                                    Likes = 0;
                                }
                            } else {
                                // rotate image while moving
                                containerView.setRotation((float) ((x_cord - screenCenter) * (Math.PI / 32)));
                                if (x_cord < (screenCenter / 2)) {
                                    if (x_cord < screenCenter / 4) {
                                        Likes = 1;
                                    } else {
                                        Likes = 0;
                                    }
                                } else {
                                    Likes = 0;
                                }
                            }

                            break;
                        case MotionEvent.ACTION_UP:

                            x_cord = (int) event.getRawX();
                            y_cord = (int) event.getRawY();

                            Log.e("X Point", "" + x_cord + " , Y " + y_cord);

                            if (Likes == 0) {
                                Toast.makeText(context, "NOTHING", Toast.LENGTH_SHORT).show();
                                Log.e("Event_Status :-> ", "Nothing");
                                containerView.setX(0);
                                containerView.setY(0);
                                containerView.setRotation(0);
                            } else if (Likes == 1) {
                                Toast.makeText(context, "UNLIKE", Toast.LENGTH_SHORT).show();
                                Log.e("Event_Status :-> ", "UNLIKE");
                                parentView.removeView(containerView);
                            } else if (Likes == 2) {
                                Toast.makeText(context, "LIKED", Toast.LENGTH_SHORT).show();
                                Log.e("Event_Status :-> ", "Liked");
                                parentView.removeView(containerView);
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            parentView.addView(containerView);
        }
    }

    private void getArrayData() {
        eventArrayList.add(new EventCard().setEventNameBuilder("SoulJam").setPhotoBuilder("https://www.plantnet.com.au/wp-content/uploads/plantnet-category-blueberries.jpg"));
        eventArrayList.add(new EventCard().setEventNameBuilder("SummerSocial").setPhotoBuilder("https://www.plantnet.com.au/wp-content/uploads/plantnet-category-blueberries.jpg"));
    }
}

