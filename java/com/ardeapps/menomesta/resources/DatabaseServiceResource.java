package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.handlers.serviceHandlers.BarRequestHandledHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.CityRequestHandledHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewCommentHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewEventHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewNotificationHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewPrivateMessageHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewReplyHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewSessionHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewUserLookingForCompanyHandler;
import com.ardeapps.menomesta.objects.BarRequest;
import com.ardeapps.menomesta.objects.CityRequest;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.CompanyMessage;
import com.ardeapps.menomesta.objects.Session;
import com.ardeapps.menomesta.services.FirebaseDatabaseService;
import com.ardeapps.menomesta.utils.StringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static com.ardeapps.menomesta.PrefRes.CITY;
import static com.ardeapps.menomesta.PrefRes.LAST_COMMENT_ID;
import static com.ardeapps.menomesta.PrefRes.LAST_NOTIFICATION;
import static com.ardeapps.menomesta.PrefRes.LAST_SESSION_ID;
import static com.ardeapps.menomesta.PrefRes.LAST_USER_LOOKING_FOR_USER_ID;

/**
 * Created by Arttu on 19.1.2018.
 * NotificationService varten, joten käytetään prefs
 */

public class DatabaseServiceResource extends FirebaseDatabaseService {
    private static DatabaseServiceResource instance;
    private static DatabaseReference database;

    public static DatabaseServiceResource getInstance() {
        if (instance == null) {
            instance = new DatabaseServiceResource();
        }
        database = getDatabase();
        return instance;
    }

    public void isNewComments(final NewCommentHandler handler) {
        setChildAddListener(database.child(CITIES).child(PrefRes.getString(CITY)).child(COMMENTS).limitToLast(1), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                if (comment != null) {
                    String lastCommentId = PrefRes.getString(LAST_COMMENT_ID);
                    if (!comment.userId.equals(PrefRes.getUser().userId) && !comment.commentId.equals(lastCommentId)) {
                        handler.onNewComment(comment);
                    }

                    PrefRes.putString(LAST_COMMENT_ID, comment.commentId);
                }
            }
        });
    }

    public void isNewEvents(final NewEventHandler handler) {
        // TODO toteuta tämä tulevaisuudessa
        /*setChildAddListener(database.child(CITIES).child(PrefRes.getString(CITY)).child(EVENTS).limitToLast(1), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                if (event != null) {
                    String lastEventId = PrefRes.getString(LAST_EVENT_ID);
                    if (!event.userId.equals(PrefRes.getUser().userId) && !event.eventId.equals(lastEventId)) {
                        handler.onNewEvent(event);
                    }

                    PrefRes.putString(LAST_EVENT_ID, event.eventId);
                }
            }
        });*/
    }

    public void isNewNotifications(final NewNotificationHandler handler) {
        setChildAddListener(database.child(NOTIFICATIONS), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getKey().equals(PrefRes.getUser().userId)) {
                    if (dataSnapshot.getValue() != null) {
                        String lastNotification = PrefRes.getString(LAST_NOTIFICATION);

                        String message = dataSnapshot.getValue().toString();
                        if (StringUtils.isEmptyString(lastNotification) || !message.equals(lastNotification)) {
                            handler.onNewNotification(message);
                        }

                        PrefRes.putString(LAST_NOTIFICATION, message);
                    }
                }
            }
        });
    }

    public void isNewSessions(final NewSessionHandler handler) {
        setChildAddListener(database.child(PRIVATE_SESSIONS).child(PrefRes.getUser().userId).limitToLast(1), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                Session session = dataSnapshot.getValue(Session.class);
                if (session != null) {
                    String lastSessionId = PrefRes.getString(LAST_SESSION_ID);

                    if (!StringUtils.isEmptyString(lastSessionId) && !lastSessionId.equals(session.sessionId)) {
                        handler.onNewSession(session);
                    }

                    PrefRes.putString(LAST_SESSION_ID, session.sessionId);
                }
            }
        });
    }

    public void isNewUsersLookingForCompany(final NewUserLookingForCompanyHandler handler) {
        setChildAddListener(database.child(CITIES).child(PrefRes.getString(CITY)).child(USERS_LOOKING_FOR_COMPANY).limitToLast(1), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                CompanyMessage companyMessage = dataSnapshot.getValue(CompanyMessage.class);
                if (companyMessage != null) {
                    String lastUserLookingForUserId = PrefRes.getString(LAST_USER_LOOKING_FOR_USER_ID);
                    // Eka kertaa tyhjä. reviewId = userId
                    if (!companyMessage.commentId.equals(PrefRes.getUser().userId) && !lastUserLookingForUserId.equals(companyMessage.commentId)) {
                        handler.onNewUserLookingForCompany(companyMessage);
                    }

                    PrefRes.putString(LAST_USER_LOOKING_FOR_USER_ID, companyMessage.commentId);
                }
            }
        });
    }

    public void isBarRequestHandled(final BarRequestHandledHandler handler) {
        setChildRemoveListener(database.child(BAR_REQUESTS).child(PrefRes.getUser().userId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                BarRequest barRequest = dataSnapshot.getValue(BarRequest.class);
                if (barRequest != null) {
                    handler.onBarRequestHandled(barRequest);
                }
            }
        });
    }

    public void isCityRequestHandled(final CityRequestHandledHandler handler) {
        setChildRemoveListener(database.child(CITY_REQUESTS).child(PrefRes.getUser().userId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                CityRequest cityRequest = dataSnapshot.getValue(CityRequest.class);
                if (cityRequest != null) {
                    handler.onCityRequestHandled(cityRequest);
                }
            }
        });
    }

    public void isNewPrivateMessages(ArrayList<String> sessionIds, final NewPrivateMessageHandler handler) {
        for (final String sessionId : sessionIds) {
            setChildAddListener(database.child(PRIVATE_MESSAGES).child(sessionId).limitToLast(1), new GetDataSuccessListener() {
                @Override
                public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                    Comment privateMessage = dataSnapshot.getValue(Comment.class);
                    if (privateMessage != null) {
                        String lastCommentId = PrefRes.getString(sessionId);

                        if (!StringUtils.isEmptyString(PrefRes.getUser().userId) && !lastCommentId.equals(privateMessage.commentId)) {
                            handler.onNewPrivateMessage(sessionId, privateMessage);
                        }

                        PrefRes.putString(sessionId, privateMessage.commentId);
                    }
                }
            });
        }
    }

    public void isNewReplies(ArrayList<String> userCommentIds, final NewReplyHandler handler) {
        for (final String userCommentId : userCommentIds) {
            setChildAddListener(database.child(CITIES).child(PrefRes.getString(CITY)).child(REPLIES).child(userCommentId).limitToLast(1), new GetDataSuccessListener() {
                @Override
                public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                    Comment reply = dataSnapshot.getValue(Comment.class);
                    if (reply != null) {
                        String lastReplyId = PrefRes.getString(userCommentId);
                        if (!reply.userId.equals(PrefRes.getUser().userId) && !lastReplyId.equals(reply.commentId)) {
                            handler.onNewPrivateMessage(userCommentId, reply);
                        }

                        PrefRes.putString(userCommentId, reply.commentId);
                    }
                }
            });
        }
    }
}
