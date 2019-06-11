package com.travel721;

import com.google.android.gms.maps.model.LatLng;

public class EventCard {
    private String eventImgURL;
    private String eventName;
    private LatLng eventLocation;

    private EventCard(){}

    public EventCard(String eventImgURL, String eventName, double lat, double lng, String eventDate) {
        this.eventImgURL = eventImgURL;
        this.eventName = eventName;
        this.eventLocation = new LatLng(lat,lng);
        this.eventDate = eventDate;
    }

    public String getEventImgURL() {
        return eventImgURL;
    }

    public void setEventImgURL(String eventImgURL) {
        this.eventImgURL = eventImgURL;
    }

    public LatLng getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(float lat, float lng) {
        this.eventLocation = new LatLng(lat,lng);
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    private String eventDate;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }


}