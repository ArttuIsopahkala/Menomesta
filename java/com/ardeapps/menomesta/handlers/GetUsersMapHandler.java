package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.User;

import java.util.Map;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetUsersMapHandler {
    void onUsersMapLoaded(Map<String, User> usersMap);
}
