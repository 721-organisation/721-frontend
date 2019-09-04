package com.travel721;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class TutorialSlideFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tutorial_slide, container, false);
        ImageView imageView = rootView.findViewById(R.id.tutorialSlideImage);
        int drawable;
        switch (position) {
            case 0:
                drawable = R.drawable.ready_to_explore;
                break;
            case 1:
                drawable = R.drawable.discover;
                break;
            default:
                drawable = R.drawable.tut_go;

        }


        imageView.setImageDrawable(ContextCompat.getDrawable(rootView.getContext(), drawable));
        return rootView;
    }

    private int position;

    TutorialSlideFragment(int i) {
        position = i;
    }

}
