package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetPrivateSessionsHandler;
import com.ardeapps.menomesta.handlers.GetUsersMapHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewSessionHandler;
import com.ardeapps.menomesta.objects.Session;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.services.FirebaseService;
import com.ardeapps.menomesta.utils.StringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Map;

import static com.ardeapps.menomesta.PrefRes.LAST_SESSION_ID;

/**
 * Created by Arttu on 19.1.2018.
 */

public class PrivateSessionsResource extends FirebaseService {
    private static PrivateSessionsResource instance;
    private static DatabaseReference database;

    public static PrivateSessionsResource getInstance() {
        if (instance == null) {
            instance = new PrivateSessionsResource();
        }
        database = getDatabase().child(PRIVATE_SESSIONS);
        return instance;
    }

    public void addPrivateSession(String userId, Session session, final AddSuccessListener handler) {
        session.sessionId = database.child(userId).push().getKey();
        addData(database.child(userId).child(session.sessionId), session, handler);
    }

    public void editPrivateSession(String userId, Session session, final EditSuccessListener handler) {
        editData(database.child(userId).child(session.sessionId), session, handler);
    }

    public void removePrivateSession(String userId, String sessionId, final EditSuccessListener handler) {
        editData(database.child(userId).child(sessionId), null, handler);
    }

    public void isNewPrivateSessions(final NewSessionHandler handler) {
        setChildAddListener(database.child(AppRes.getUser().userId).limitToLast(1), new GetDataSuccessListener() {
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

    public void getPrivateSessions(final GetPrivateSessionsHandler handler) {
        getData(database.child(AppRes.getUser().userId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                final ArrayList<String> userIds = new ArrayList<>();
                final ArrayList<Session> sessions = new ArrayList<>();
                for (DataSnapshot object : dataSnapshot.getChildren()) {
                    Session session = object.getValue(Session.class);
                    if (session != null) {
                        if (!userIds.contains(session.userId)) {
                            userIds.add(session.userId);
                        }
                        sessions.add(session);
                    }
                }
                if (userIds.size() > 0) {
                    UsersResource.getInstance().getUsers(userIds, new GetUsersMapHandler() {
                        @Override
                        public void onUsersMapLoaded(Map<String, User> usersMap) {
                            for (Session session : sessions) {
                                session.recipient = usersMap.get(session.userId);
                            }
                            handler.onPrivateSessionsLoaded(sessions);
                        }
                    });
                } else {
                    handler.onPrivateSessionsLoaded(sessions);
                }
            }
        });
    }
}
