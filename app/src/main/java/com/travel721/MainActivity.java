package com.travel721;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private Context context;
    private ArrayList eventArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        context = MainActivity.this;

        parentView = (RelativeLayout) findViewById(R.id.main_layoutview);

        windowwidth = getWindowManager().getDefaultDisplay().getWidth();

        screenCenter = windowwidth / 2;

        eventArrayList = new ArrayList<>();

        getArrayData();

    }
}
