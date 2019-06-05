package com.travel721;

import android.provider.ContactsContract;

public class EventCard {
    private String eventImgURL;
    private String eventName;
    private String eventLocation;

    public String getEventImgURL() {
        return eventImgURL;
    }

    public void setEventImgURL(String eventImgURL) {
        this.eventImgURL = eventImgURL;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
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

    public EventCard setEventNameBuilder(String eventName){
        this.eventLocation = eventName;
        return this;
    }
    public EventCard setPhotoBuilder(String ImgURL){
        this.eventImgURL = ImgURL;
        return this;

    }
}