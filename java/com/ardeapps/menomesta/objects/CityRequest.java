package com.ardeapps.menomesta.objects;

/**
 * Created by Arttu on 20.9.2016.
 */
public class CityRequest {
    public String userId;
    public String requestId;
    public String city;

    public CityRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(CityRequest.class)
    }
}
