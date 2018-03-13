package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetLatestPrivateMessagesHandler;
import com.ardeapps.menomesta.handlers.GetOlderMessagesHandler;
import com.ardeapps.menomesta.handlers.GetPrivateMessagesHandler;
import com.ardeapps.menomesta.handlers.ObjectExistsHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewPrivateMessageHandler;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.services.FirebaseService;
import com.ardeapps.menomesta.utils.StringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by Arttu on 19.1.2018.
 */

public class PrivateMessagesResource extends FirebaseService {
    private static PrivateMessagesResource instance;
    private static DatabaseReference database;

    public static PrivateMessagesResource getInstance() {
        if (instance == null) {
            instance = new PrivateMessagesResource();
        }
        database = getDatabase().child(PRIVATE_MESSAGES);
        return instance;
    }

    public void addPrivateMessage(String sessionId, Comment comment, final AddSuccessListener handler) {
        comment.commentId = database.child(sessionId).push().getKey();
        addData(database.child(sessionId).child(comment.commentId), comment, handler);
    }

    public void removePrivateMessages(String sessionId, final EditSuccessListener handler) {
        editData(database.child(sessionId), null, handler);
    }

    public void getPrivateMessages(final String sessionId, final GetPrivateMessagesHandler handler) {
        getData(database.child(sessionId).limitToLast(messageLimit), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                ArrayList<Comment> messages = new ArrayList<>();
                for (DataSnapshot comment : dataSnapshot.getChildren()) {
                    Comment object = comment.getValue(Comment.class);
                    messages.add(object);
                }
                handler.onPrivateMessagesLoaded(messages);
            }
        });
    }

    public void getOlderPrivateMessages(final String sessionId, final String lastCommentId, final GetOlderMessagesHandler handler) {
        Query query = database.child(sessionId).orderByKey().endAt(lastCommentId).limitToLast(messageLimit + 1);
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

    public void sessionExists(final String sessionId, final ObjectExistsHandler handler) {
        getData(database.child(sessionId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    handler.onObjectChecked(true);
                } else handler.onObjectChecked(false);
            }
        });
    }

    public void isNewPrivateMessages(final ArrayList<String> sessionIds, final NewPrivateMessageHandler handler) {
        for (final String sessionId : sessionIds) {
            setChildAddListener(database.child(sessionId).limitToLast(1), new GetDataSuccessListener() {
                @Override
                public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                    Comment privateMessage = dataSnapshot.getValue(Comment.class);
                    if (privateMessage != null) {
                        String lastCommentId = PrefRes.getString(sessionId);
                        // Eka kertaa tyhjä
                        if (!StringUtils.isEmptyString(lastCommentId) && !lastCommentId.equals(privateMessage.commentId) && !privateMessage.userId.equals(AppRes.getUser().userId)) {
                            handler.onNewPrivateMessage(sessionId, privateMessage);
                            PrefRes.putString(sessionId, privateMessage.commentId);
                            return;
                        }
                        PrefRes.putString(sessionId, privateMessage.commentId);
                    }
                }
            });
        }
    }

    public void getLatestPrivateMessages(final ArrayList<String> sessionIds, final GetLatestPrivateMessagesHandler handler) {
        final ArrayList<Comment> comments = new ArrayList<>();
        for (final String sessionId : sessionIds) {
            setDataChangeListener(database.child(sessionId).limitToLast(1), new GetDataSuccessListener() {
                @Override
                public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                    Comment privateMessage = dataSnapshot.getValue(Comment.class);
                    comments.add(privateMessage);
                    if (comments.size() == sessionIds.size()) {
                        handler.onGetLatestPrivateMessagesLoaded(comments);
                    }
                }
            });
        }
    }
}
