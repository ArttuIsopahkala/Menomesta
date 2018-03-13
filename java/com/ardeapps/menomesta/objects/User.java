package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

/**
 * Created by Arttu on 16.9.2016.
 */

public class User {

    final public static String MALE = "male";
    final public static String FEMALE = "female";

    public String userId;
    public String city;
    public String gender;
    public long birthday;
    public long lastLoginTime;
    public long creationTime;
    public long karma;
    public boolean isLookingFor;
    public boolean premium;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    @Exclude
    public User clone() {
        User clone = new User();
        clone.userId = this.userId;
        clone.city = this.city;
        clone.birthday = this.birthday;
        clone.gender = this.gender;
        clone.isLookingFor = this.isLookingFor;
        clone.lastLoginTime = this.lastLoginTime;
        clone.creationTime = this.creationTime;
        clone.karma = this.karma;
        clone.premium = this.premium;
        return clone;
    }

    @Exclude
    public boolean isMale() {
        return this.gender.equals(MALE);
    }
}
