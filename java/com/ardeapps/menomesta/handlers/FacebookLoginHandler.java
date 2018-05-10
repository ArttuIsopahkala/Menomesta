package com.ardeapps.menomesta.handlers;

/**
 * Created by Arttu on 24.3.2018.
 */

public interface FacebookLoginHandler {
    void onLoginSuccess(String gender, String birthday);
    void onLoginFailed();
}
