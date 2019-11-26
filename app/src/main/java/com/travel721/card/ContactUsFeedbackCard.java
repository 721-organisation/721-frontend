package com.travel721.card;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactUsFeedbackCard extends Card implements Parcelable {

    public static final Creator<ContactUsFeedbackCard> CREATOR = new Creator<ContactUsFeedbackCard>() {
        @Override
        public ContactUsFeedbackCard createFromParcel(Parcel in) {
            return new ContactUsFeedbackCard(in);
        }

        @Override
        public ContactUsFeedbackCard[] newArray(int size) {
            return new ContactUsFeedbackCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    protected ContactUsFeedbackCard(Parcel in) {

    }

    public ContactUsFeedbackCard() {
    }
}
