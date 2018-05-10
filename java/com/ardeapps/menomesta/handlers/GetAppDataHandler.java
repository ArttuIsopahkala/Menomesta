package com.ardeapps.menomesta.handlers;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetAppDataHandler {
    void onAppDataLoaded(String facebookAppToken, String currentAppVersion);
}
