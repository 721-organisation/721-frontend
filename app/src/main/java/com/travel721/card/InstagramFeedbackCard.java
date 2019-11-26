package com.travel721.card;

import android.os.Parcel;
import android.os.Parcelable;

public class InstagramFeedbackCard extends Card implements Parcelable {

    public static final Creator<InstagramFeedbackCard> CREATOR = new Creator<InstagramFeedbackCard>() {
        @Override
        public InstagramFeedbackCard createFromParcel(Parcel in) {
            return new InstagramFeedbackCard(in);
        }

        @Override
        public InstagramFeedbackCard[] newArray(int size) {
            return new InstagramFeedbackCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    protected InstagramFeedbackCard(Parcel in) {

    }

    public InstagramFeedbackCard() {
    }
}
