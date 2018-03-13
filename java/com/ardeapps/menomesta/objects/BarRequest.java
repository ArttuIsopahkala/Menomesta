package com.ardeapps.menomesta.objects;

/**
 * Created by Arttu on 20.9.2016.
 */
public class BarRequest {
    public String userId;
    public String requestId;
    public String name;
    public String city;
    public String address;
    public boolean nightClub;
    public boolean isFoodPlace;

    public BarRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(BarRequest.class)
    }
}
