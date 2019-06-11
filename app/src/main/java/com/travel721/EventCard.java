package com.travel721;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventCard {
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

    public Date getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(Date formattedDate) {
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
    private Date formattedDate;
    private String time;
    private String minimumAge;
    private String price;
    private Bitmap imgBitmap = null;

    public EventCard(String name, String skiddleID, String venueName, String venueLat, String venueLong, String imgURL, String eventHyperLink, String date, String time, String minimumAge, String price) {
        this.name = name;
        this.skiddleID = skiddleID;
        this.venueName = venueName;
        this.location = new LatLng(Double.valueOf(venueLat),Double.valueOf(venueLong));
        this.imgURL = imgURL;
        this.eventHyperLink = eventHyperLink;
        this.date = date;
        this.time = time;
        this.minimumAge = minimumAge;
        this.price = price;
        new DownloadFileFromURL(new IOnFileDownloadedListener() {
            @Override
            public void onFileDownloaded(Bitmap bmp) {
                imgBitmap = bmp;
            }
        });
        try {
            this.formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}