package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetUserHandler;
import com.ardeapps.menomesta.handlers.GetUsersLookingForCompanyHandler;
import com.ardeapps.menomesta.handlers.IsUsersLookingForCompanyHandler;
import com.ardeapps.menomesta.handlers.ObjectExistsHandler;
import com.ardeapps.menomesta.objects.CompanyMessage;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.services.FirebaseDatabaseService;
import com.ardeapps.menomesta.utils.DateUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by Arttu on 19.1.2018.
 */

public class UsersLookingForCompanyResource extends FirebaseDatabaseService {
    private static UsersLookingForCompanyResource instance;
    private static DatabaseReference database;

    public static UsersLookingForCompanyResource getInstance() {
        if (instance == null) {
            instance = new UsersLookingForCompanyResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(USERS_LOOKING_FOR_COMPANY);
        return instance;
    }

    public void editUserLookingForCompany(CompanyMessage companyMessage, final EditSuccessListener handler) {
        editData(database.child(AppRes.getUser().userId), companyMessage, handler);
    }

    public void removeUserLookingForCompany(final EditSuccessListener handler) {
        editData(database.child(AppRes.getUser().userId), null, handler);
    }

    public void userIsLookingForCompany(final String userId, final ObjectExistsHandler handler) {
        getData(database.child(userId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    handler.onObjectChecked(true);
                } else handler.onObjectChecked(false);
            }
        });
    }

    public void getUsersLookingForCompany(final GetUsersLookingForCompanyHandler handler) {
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(final DataSnapshot dataSnapshot) {
                final ArrayList<CompanyMessage> usersLookingForCompany = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    final ArrayList<CompanyMessage> companyMessages = new ArrayList<>();
                    for (DataSnapshot object : dataSnapshot.getChildren()) {
                        final CompanyMessage companyMessage = object.getValue(CompanyMessage.class);
                        if (companyMessage != null) {
                            if (DateUtil.isAfterYesterday(companyMessage.time)) {
                                companyMessages.add(companyMessage);
                            }
                        }
                    }
                    final ArrayList<User> users = new ArrayList<>();
                    for (final CompanyMessage companyMessage : companyMessages) {
                        UsersResource.getInstance().getUser(companyMessage.userId, new GetUserHandler() {
                            @Override
                            public void onUserLoaded(User user) {
                                users.add(user);
                                companyMessage.user = user;
                                usersLookingForCompany.add(companyMessage);

                                if (companyMessages.size() == users.size()) {
                                    handler.onUsersLookingForCompanyLoaded(usersLookingForCompany);
                                }
                            }
                        });
                    }
                } else {
                    handler.onUsersLookingForCompanyLoaded(usersLookingForCompany);
                }
            }
        });
    }

    public void isUsersLookingForCompany(final IsUsersLookingForCompanyHandler handler) {
        setDataChangeListener(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean userFound = false;
                    for (DataSnapshot object : dataSnapshot.getChildren()) {
                        CompanyMessage companyMessage = object.getValue(CompanyMessage.class);
                        if (companyMessage != null) {
                            if (!companyMessage.userId.equals(AppRes.getUser().userId))
                                userFound = true;

                            if (!DateUtil.isAfterYesterday(companyMessage.time)) {
                                // Aseta ettei käyttäjä hae enää seuraa
                                // Tässä reviewId ja userId on sama
                                User user = AppRes.getUser().clone();
                                if (companyMessage.commentId.equals(user.userId)) {
                                    user.isLookingFor = false;
                                    AppRes.setUser(user);
                                    UsersResource.getInstance().editUser(user);
                                }
                                // Poista vanhat seuranhakijat
                                editData(database.child(companyMessage.commentId), null);
                            }
                        }
                    }
                    if (userFound) {
                        handler.onIsUsersLookingForCompanyChecked(true);
                    } else {
                        handler.onIsUsersLookingForCompanyChecked(false);
                    }
                } else handler.onIsUsersLookingForCompanyChecked(false);
            }
        });
    }
}
