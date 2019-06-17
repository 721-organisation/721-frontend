package com.travel721;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class EventCard implements Parcelable, Serializable {
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(skiddleID);
        parcel.writeString(venueName);
        parcel.writeString(String.valueOf(location.latitude));
        parcel.writeString(String.valueOf(location.longitude));
        parcel.writeString(imgURL);
        parcel.writeString(eventHyperLink);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(minimumAge);
        parcel.writeString(price);

    }

    protected EventCard(Parcel in) {
        name = in.readString();
        skiddleID = in.readString();
        venueName = in.readString();
        String lat = in.readString();
        String lon = in.readString();
        location = new LatLng(Double.valueOf(lat), Double.valueOf(lon));
        imgURL = in.readString();
        eventHyperLink = in.readString();
        date = in.readString();
        time = in.readString();
        minimumAge = in.readString();
        price = in.readString();
        this.formattedDate = date;
    }

    public static final Creator<EventCard> CREATOR = new Creator<EventCard>() {
        @Override
        public EventCard createFromParcel(Parcel in) {
            return new EventCard(in);
        }

        @Override
        public EventCard[] newArray(int size) {
            return new EventCard[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkiddleID() {
        return skiddleID;
    }

    public void setSkiddleID(String skiddleID) {
        this.skiddleID = skiddleID;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getEventHyperLink() {
        return eventHyperLink;
    }

    public void setEventHyperLink(String eventHyperLink) {
        this.eventHyperLink = eventHyperLink;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(String minimumAge) {
        this.minimumAge = minimumAge;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    private String name;
    private String skiddleID;
    private String venueName;
    private LatLng location;

    public String getImgURL() {
        return imgURL;
    }

    private String imgURL;
    private String eventHyperLink;
    private String date;
    private String formattedDate;
    private String time;
    private String minimumAge;
    private String price;
    private Bitmap imgBitmap = null;

    public EventCard(String name, String skiddleID, String venueName, String venueLat, String venueLong, String imgURL, String eventHyperLink, String date, String time, String minimumAge, String price) {
        this.name = name;
        this.skiddleID = skiddleID;
        this.venueName = venueName;
        this.location = new LatLng(Double.valueOf(venueLat), Double.valueOf(venueLong));
        this.imgURL = imgURL;
        this.eventHyperLink = eventHyperLink;
        this.date = date;
        this.time = time;
        this.minimumAge = minimumAge;
        this.price = price;
//        new DownloadFileFromURL(new IOnFileDownloadedListener() {
//            @Override
//            public void onFileDownloaded(Bitmap bmp) {
//                imgBitmap = bmp;
//            }
//        });
        this.formattedDate = date;

    }

    public static EventCard unpackFromJson(JSONObject jo) throws JSONException {
        return new EventCard(jo.getString("name"),
                jo.getString("skiddleId"),
                jo.getString("venueName"),
                jo.getString("venueLat"),
                jo.getString("venueLong"),
                jo.getString("image"),
                jo.getString("link"),
                jo.getString("date"),
                jo.getString("time"),
                jo.getString("minAge"),
                jo.getString("price"));
    }


    @Override
    public int describeContents() {
        return 0;
    }


}