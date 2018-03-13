package com.ardeapps.menomesta.handlers.serviceHandlers;

import com.ardeapps.menomesta.objects.Comment;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface NewPrivateMessageHandler {
    void onNewPrivateMessage(String sessionId, Comment privateMessage);
}
