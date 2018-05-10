package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.FacebookBarDetails;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetFacebookBarDetailsHandler {
    // barId, facebookBarDetails
    void onFacebookBarDetailsLoaded(ArrayList<String> unsupportedBars, Map<String, FacebookBarDetails> facebookBarDetails);
}
