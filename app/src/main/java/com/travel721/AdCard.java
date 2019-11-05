package com.travel721;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Special type of card to represent Ads
 * @author Bhav
 */
public class AdCard extends Card implements Parcelable {

    public static final Creator<AdCard> CREATOR = new Creator<AdCard>() {
        @Override
        public AdCard createFromParcel(Parcel in) {
            return new AdCard(in);
        }

        @Override
        public AdCard[] newArray(int size) {
            return new AdCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    protected AdCard(Parcel in) {

    }

    public AdCard() {
    }
}
