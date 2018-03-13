package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.Event;

import java.util.ArrayList;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetEventsHandler {
    void onEventsLoaded(ArrayList<Event> events);
}
