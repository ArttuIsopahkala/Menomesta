package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

/**
 * Created by Arttu on 15.9.2016.
 */
public class BarDetails {
    public String barId;
    public int ageLimit;
    public int ageLimitSaturday;
    public int entrancePrice;
    public boolean jacketIncluded;
    public long ageLimitUpdated;
    public long ageLimitSaturdayUpdated;
    public long entranceUpdated;

    public BarDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(BarDetails.class)
    }

    @Exclude
    public BarDetails clone() {
        BarDetails clone = new BarDetails();
        clone.barId = this.barId;
        clone.ageLimit = this.ageLimit;
        clone.ageLimitSaturday = this.ageLimitSaturday;
        clone.entrancePrice = this.entrancePrice;
        clone.jacketIncluded = this.jacketIncluded;
        clone.ageLimitUpdated = this.ageLimitUpdated;
        clone.ageLimitSaturdayUpdated = this.ageLimitSaturdayUpdated;
        clone.entranceUpdated = this.entranceUpdated;
        return clone;
    }

}
