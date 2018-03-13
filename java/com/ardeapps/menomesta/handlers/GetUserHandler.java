package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.User;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetUserHandler {
    void onUserLoaded(User user);
}
