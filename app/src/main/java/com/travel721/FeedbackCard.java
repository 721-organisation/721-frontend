package com.travel721;

import android.os.Parcel;
import android.os.Parcelable;

public class FeedbackCard extends Card implements Parcelable {

    public static final Creator<FeedbackCard> CREATOR = new Creator<FeedbackCard>() {
        @Override
        public FeedbackCard createFromParcel(Parcel in) {
            return new FeedbackCard(in);
        }

        @Override
        public FeedbackCard[] newArray(int size) {
            return new FeedbackCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    protected FeedbackCard(Parcel in) {

    }
    public FeedbackCard(){}
}
