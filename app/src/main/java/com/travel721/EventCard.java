package com.travel721;

public class EventCard {
    private String eventImgURL;
    private String eventName;
    private String eventLocation;

    private EventCard(){};

    public EventCard(String eventImgURL, String eventName, String eventLocation, String eventDate) {
        this.eventImgURL = eventImgURL;
        this.eventName = eventName;
        this.eventLocation = eventLocation;
        this.eventDate = eventDate;
    }

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


}