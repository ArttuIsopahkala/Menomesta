package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetBarCommentsHandler;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by Arttu on 19.1.2018.
 */

public class BarCommentsResource extends FirebaseDatabaseService {
    private static BarCommentsResource instance;
    private static DatabaseReference database;

    public static BarCommentsResource getInstance() {
        if (instance == null) {
            instance = new BarCommentsResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(BAR_COMMENTS);
        return instance;
    }

    public void addBarComment(String barId, Comment comment, final AddSuccessListener handler) {
        comment.commentId = database.child(barId).push().getKey();
        addData(database.child(barId).child(comment.commentId), comment, handler);
    }

    public void editBarComment(String barId, Comment comment, final EditSuccessListener handler) {
        editData(database.child(barId).child(comment.commentId), comment, handler);
    }

    public void removeBarComment(String barId, String commentId, final EditSuccessListener handler) {
        editData(database.child(barId).child(commentId), null, handler);
    }

    public void getBarComments(String barId, final GetBarCommentsHandler handler) {
        Query query = database.child(barId).limitToLast(barCommentLimit);
        getData(query, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                ArrayList<Comment> barComments = new ArrayList<>();
                for (DataSnapshot barComment : dataSnapshot.getChildren()) {
                    Comment object = barComment.getValue(Comment.class);
                    barComments.add(object);
                }
                handler.onBarCommentsLoaded(barComments);
            }
        });
    }
}
