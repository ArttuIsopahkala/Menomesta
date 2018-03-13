package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

/**
 * Created by Arttu on 15.9.2016.
 */
public class Bar {
    public String barId;
    public String city;
    public String name;
    public String address;
    public boolean nightClub;
    public double latitude;
    public double longitude;
    public boolean isFoodPlace;

    public Bar() {
        // Default constructor required for calls to DataSnapshot.getValue(Bar.class)
    }

    public Bar(String name, String address, boolean nightClub) {
        this.name = name;
        this.address = address;
        this.nightClub = nightClub;
    }

    @Exclude
    public Bar clone() {
        Bar clone = new Bar();
        clone.barId = this.barId;
        clone.city = this.city;
        clone.name = this.name;
        clone.address = this.address;
        clone.nightClub = this.nightClub;
        clone.latitude = this.latitude;
        clone.longitude = this.longitude;
        clone.isFoodPlace = this.isFoodPlace;
        return clone;
    }
}
