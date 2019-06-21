package com.travel721;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventCard implements Parcelable, Serializable {
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
    private String name;
    private String eventSourceID;
    private String venueName;
    private LatLng location;
    private String imgURL;
    private String eventHyperLink;
    private String formattedDate;
    private String time;
    private String minimumAge;
    private String price;
    private Bitmap imgBitmap = null;
    private String description;

    protected EventCard(Parcel in) {
        name = in.readString();
        eventSourceID = in.readString();
        venueName = in.readString();
        String lat = in.readString();
        String lon = in.readString();
        location = new LatLng(Double.valueOf(lat), Double.valueOf(lon));
        imgURL = in.readString();
        eventHyperLink = in.readString();
        formattedDate = in.readString();
        time = in.readString();
        minimumAge = in.readString();
        price = in.readString();
        description = in.readString();
        sourceTag = in.readString();
    }


    public EventCard() {
    }

    public static EventCard unpackFromJson(JSONObject jo) throws JSONException {
        Log.v("json", jo.toString());
        // Guaranteed Field
        EventCard eventCard = new EventCard();
        eventCard.setName(checkHasAndReturnData(jo, "name"));
        eventCard.setEventSourceID(checkHasAndReturnData(jo, "eventSourceId"));
        eventCard.setVenueName(checkHasAndReturnData(jo, "venueName"));
        eventCard.setLocation(checkHasAndReturnData(jo, "venueLat"), checkHasAndReturnData(jo, "venueLong"));
        eventCard.setImgURL(checkHasAndReturnData(jo, "image"));
        eventCard.setEventHyperLink(checkHasAndReturnData(jo, "link"));
        eventCard.setFormattedDate(checkHasAndReturnData(jo, "date"));
        eventCard.setTime(checkHasAndReturnData(jo, "time"));
        eventCard.setMinimumAge(checkHasAndReturnData(jo, "minAge"));
        eventCard.setPrice(checkHasAndReturnData(jo, "price"));
        eventCard.setDescription(checkHasAndReturnData(jo, "description"));
        eventCard.setSourceTag(checkHasAndReturnData(jo, "eventSourceTag"));
        return eventCard;
    }

    private static String checkHasAndReturnData(JSONObject jo, String prop) throws JSONException {
        if (jo.has(prop)) {
            return jo.getString(prop);
        } else {
            return "No data provided for " + prop;
        }

    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(eventSourceID);
        parcel.writeString(venueName);
        parcel.writeString(String.valueOf(location.latitude));
        parcel.writeString(String.valueOf(location.longitude));
        parcel.writeString(imgURL);
        parcel.writeString(eventHyperLink);
        parcel.writeString(formattedDate);
        parcel.writeString(time);
        parcel.writeString(minimumAge);
        parcel.writeString(price);
        parcel.writeString(description);
        parcel.writeString(sourceTag);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventSourceID() {
        return eventSourceID;
    }

    public void setEventSourceID(String eventSourceID) {
        this.eventSourceID = eventSourceID;
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

    public void setLocation(String lat, String lon) {
        if (lat.equals("") || lon.equals("")) setLocation(new LatLng(0, 0));
        this.setLocation(new LatLng(Float.valueOf(lat), Float.valueOf(lon)));
    }

    public String getEventHyperLink() {
        return eventHyperLink;
    }

    public void setEventHyperLink(String eventHyperLink) {
        this.eventHyperLink = eventHyperLink;
    }

    public String getDayOfWeek() {
        try {
            Date realDateOfEvent = new SimpleDateFormat("yyyy-MM-dd").parse(formattedDate);
            Date realCurrentDate = new Date();
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(realCurrentDate);

            if (formattedDate.equals(currentDate)) {
                return "Today";
            } else {
                return new SimpleDateFormat("EEEE").format(realDateOfEvent);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
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

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String s) {
        this.imgURL = s;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String sourceTag;

    public void setSourceTag(String sourceTag) {
        this.sourceTag = sourceTag;
    }

    public String getSourceTag() {
        return sourceTag;
    }

    public double getLocationLatitude() {
        return this.location.latitude;
    }

    public double getLocationLongitude() {
        return this.location.longitude;
    }
}