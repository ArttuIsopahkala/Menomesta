package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetCityCommentHandler;
import com.ardeapps.menomesta.handlers.GetCityCommentsHandler;
import com.ardeapps.menomesta.handlers.GetOlderMessagesHandler;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.services.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by Arttu on 19.1.2018.
 */

public class CommentsResource extends FirebaseService {
    private static CommentsResource instance;
    private static DatabaseReference database;

    public static CommentsResource getInstance() {
        if (instance == null) {
            instance = new CommentsResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(COMMENTS);
        return instance;
    }

    public void addComment(Comment comment, final AddSuccessListener handler) {
        comment.commentId = database.push().getKey();
        addData(database.child(comment.commentId), comment, handler);
    }

    public void editComment(Comment comment, final EditSuccessListener handler) {
        editData(database.child(comment.commentId), comment, handler);
    }

    public void removeComment(String commentId, final EditSuccessListener handler) {
        editData(database.child(commentId), null, handler);
    }

    public void getComment(String commentId, final GetCityCommentHandler handler) {
        getData(database.child(commentId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                handler.onCityCommentLoaded(comment);
            }
        });
    }

    public void getComments(final GetCityCommentsHandler handler) {
        Query query = database.orderByKey().limitToLast(messageLimit);
        getData(query, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                ArrayList<Comment> cityComments = new ArrayList<>();
                for (DataSnapshot object : dataSnapshot.getChildren()) {
                    Comment comment = object.getValue(Comment.class);
                    cityComments.add(comment);
                }
                handler.onCityCommentsLoaded(cityComments);
            }
        });
    }

    public void getOlderComments(String lastCommentId, final GetOlderMessagesHandler handler) {
        Query query = database.orderByKey().endAt(lastCommentId).limitToLast(messageLimit + 1);
        getData(query, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                ArrayList<Comment> olderMessages = new ArrayList<>();
                for (DataSnapshot comment : dataSnapshot.getChildren()) {
                    Comment object = comment.getValue(Comment.class);
                    if (olderMessages.size() < dataSnapshot.getChildrenCount() - 1) {
                        olderMessages.add(object);
                    }
                }
                handler.onOlderMessagesLoaded(olderMessages);
            }
        });
    }
}
