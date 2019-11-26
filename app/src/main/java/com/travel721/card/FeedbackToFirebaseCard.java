package com.travel721.card;

import android.os.Parcel;
import android.os.Parcelable;

import com.travel721.analytics.AnalyticsHelper;

public class FeedbackToFirebaseCard extends Card implements Parcelable {
    private String question;
    public static final Creator<FeedbackToFirebaseCard> CREATOR = new Creator<FeedbackToFirebaseCard>() {
        @Override
        public FeedbackToFirebaseCard createFromParcel(Parcel in) {
            return new FeedbackToFirebaseCard(in);
        }

        @Override
        public FeedbackToFirebaseCard[] newArray(int size) {
            return new FeedbackToFirebaseCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(question);
    }

    protected FeedbackToFirebaseCard(Parcel in) {
        question = in.readString();
    }

    public FeedbackToFirebaseCard() {
        question = AnalyticsHelper.getRandomQuestion();
    }

    public String getQuestion() {
        return question;
    }
}
