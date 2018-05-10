package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetRepliesHandler;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by Arttu on 19.1.2018.
 */

public class RepliesResource extends FirebaseDatabaseService {
    private static RepliesResource instance;
    private static DatabaseReference database;

    public static RepliesResource getInstance() {
        if (instance == null) {
            instance = new RepliesResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(REPLIES);
        return instance;
    }

    public void addReply(String commentId, Comment reply, final AddSuccessListener handler) {
        reply.commentId = database.child(commentId).push().getKey();
        addData(database.child(commentId).child(reply.commentId), reply, handler);
    }

    public void editReply(String commentId, Comment reply, final EditSuccessListener handler) {
        editData(database.child(commentId).child(reply.commentId), reply, handler);
    }

    public void removeAllReplies(String commentId, final EditSuccessListener handler) {
        editData(database.child(commentId).child(commentId), null, handler);
    }

    public void removeReply(String commentId, String replyId, final EditSuccessListener handler) {
        editData(database.child(commentId).child(replyId), null, handler);
    }

    public void getReplies(String commentId, final GetRepliesHandler handler) {
        getData(database.child(commentId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                ArrayList<Comment> replies = new ArrayList<>();
                for (DataSnapshot reply : dataSnapshot.getChildren()) {
                    Comment object = reply.getValue(Comment.class);
                    replies.add(object);
                }
                handler.onRepliesLoaded(replies);
            }
        });
    }
}
