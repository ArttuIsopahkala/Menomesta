package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.Bar;

import java.util.Map;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetBarsHandler {
    void onBarsLoaded(Map<String, Bar> bars);
}
