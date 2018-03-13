package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetUserHandler;
import com.ardeapps.menomesta.handlers.GetUsersMapHandler;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.services.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class UsersResource extends FirebaseService {
    private static UsersResource instance;
    private static DatabaseReference database;

    public static UsersResource getInstance() {
        if (instance == null) {
            instance = new UsersResource();
        }
        database = getDatabase().child(USERS);
        return instance;
    }

    public void editUser(User user, final EditSuccessListener handler) {
        editData(database.child(user.userId), user, handler);
    }

    public void editUser(User user) {
        editData(database.child(user.userId), user);
    }

    public void removeUser(String userId, final EditSuccessListener handler) {
        editData(database.child(userId), null, handler);
    }

    public void updateUserKarma(final long points, final boolean add) {
        final User user = AppRes.getUser();
        getDataAnonymously(database.child(user.userId).child(KARMA), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                long karma = (long) dataSnapshot.getValue();
                long newKarma;
                if (add) {
                    newKarma = karma + points;
                } else {
                    if (points >= karma) {
                        // Karma menisi miinusmerkkiseksi, joten aseta nollaksi
                        newKarma = 0;
                    } else {
                        newKarma = karma - points;
                    }
                }
                user.karma = newKarma;
                AppRes.setUser(user);
                editData(database.child(AppRes.getUser().userId).child(KARMA), newKarma);
            }
        });
    }

    public void giveUserKarma(final String userId, final long points, final boolean add) {
        getDataAnonymously(database.child(userId).child(KARMA), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long karma = (long) dataSnapshot.getValue();
                    if (add) {
                        karma += points;
                    } else {
                        if (points >= karma) {
                            // Karma menisi miinusmerkkiseksi, joten aseta nollaksi
                            karma = 0;
                        } else {
                            karma -= points;
                        }
                    }
                    editData(database.child(userId).child(KARMA), karma);
                }
            }
        });
    }

    public void getUser(String userId, final GetUserHandler handler) {
        getData(database.child(userId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                handler.onUserLoaded(user);
            }
        });
    }

    public void getUsers(final ArrayList<String> userIds, final GetUsersMapHandler handler) {
        final Map<String, User> users = new HashMap<>();
        for (String userId : userIds) {
            getData(database.child(userId), new GetDataSuccessListener() {
                @Override
                public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        users.put(user.userId, user);
                        if (users.size() == userIds.size()) {
                            handler.onUsersMapLoaded(users);
                        }
                    }
                }
            });
        }
    }
}
