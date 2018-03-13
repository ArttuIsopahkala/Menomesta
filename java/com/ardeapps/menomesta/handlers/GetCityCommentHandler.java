package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.Comment;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetCityCommentHandler {
    void onCityCommentLoaded(Comment cityComment);
}
