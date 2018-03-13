package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.Comment;

import java.util.ArrayList;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetLatestPrivateMessagesHandler {
    void onGetLatestPrivateMessagesLoaded(ArrayList<Comment> latestMessages);
}
