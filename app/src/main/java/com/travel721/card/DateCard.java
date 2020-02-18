package com.travel721.card;

import android.os.Parcel;
import android.os.Parcelable;

public class DateCard extends Card implements Parcelable {

    public static final Creator<DateCard> CREATOR = new Creator<DateCard>() {
        @Override
        public DateCard createFromParcel(Parcel in) {
            return new DateCard(in);
        }

        @Override
        public DateCard[] newArray(int size) {
            return new DateCard[size];
        }
    };

    protected DateCard(Parcel in) {

    }

    public DateCard() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
