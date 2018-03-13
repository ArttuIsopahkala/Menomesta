package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.Session;

import java.util.ArrayList;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetPrivateSessionsHandler {
    void onPrivateSessionsLoaded(ArrayList<Session> sessions);
}
