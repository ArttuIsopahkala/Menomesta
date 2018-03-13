package com.ardeapps.menomesta.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.ardeapps.menomesta.MainActivity;
import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.serviceHandlers.BarRequestHandledHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.CityRequestHandledHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewCommentHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewEventHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewNotificationHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewReplyHandler;
import com.ardeapps.menomesta.objects.BarRequest;
import com.ardeapps.menomesta.objects.CityRequest;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.resources.ServiceResource;
import com.ardeapps.menomesta.utils.StringUtils;

import java.util.ArrayList;

import static com.ardeapps.menomesta.PrefRes.CITY;
import static com.ardeapps.menomesta.PrefRes.COMMENT_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.COMPANY_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.EVENT_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.IS_APP_VISIBLE;
import static com.ardeapps.menomesta.PrefRes.PRIVATE_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.REPLY_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.SESSION_IDS;
import static com.ardeapps.menomesta.PrefRes.USER_COMMENT_IDS;
import static com.ardeapps.menomesta.PrefRes.VIBRATE_NOTIFICATIONS;

/**
 * Created by Arttu on 20.10.2015.
 * Notification Service
 */
public class NotificationService extends Service {
    public static final String RESTART_SERVICE = "com.ardeapps.menomesta.RESTART_SERVICE";

    ArrayList<String> sessionIds;
    ArrayList<String> userCommentIds;
    boolean privateNotifications;
    boolean replyNotifications;
    boolean commentNotifications;
    boolean eventNotifications;
    boolean companyNotifications;
    boolean vibrate;

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent(RESTART_SERVICE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        privateNotifications = PrefRes.getBoolean(PRIVATE_NOTIFICATIONS);
        replyNotifications = PrefRes.getBoolean(REPLY_NOTIFICATIONS);
        commentNotifications = PrefRes.getBoolean(COMMENT_NOTIFICATIONS);
        eventNotifications = PrefRes.getBoolean(EVENT_NOTIFICATIONS);
        companyNotifications = PrefRes.getBoolean(COMPANY_NOTIFICATIONS);
        vibrate = PrefRes.getBoolean(VIBRATE_NOTIFICATIONS);
        sessionIds = new ArrayList<>(PrefRes.getStringSet(SESSION_IDS));
        userCommentIds = new ArrayList<>(PrefRes.getStringSet(USER_COMMENT_IDS));

        if (!StringUtils.isEmptyString(PrefRes.getUser().userId) && !StringUtils.isEmptyString(PrefRes.getString(CITY))) {
            ServiceResource.getInstance().isBarRequestHandled(new BarRequestHandledHandler() {
                @Override
                public void onBarRequestHandled(BarRequest barRequest) {
                    showNotification(getString(R.string.request1bar) + " " + barRequest.name + " " + getString(R.string.request2), 4, "");
                }
            });

            ServiceResource.getInstance().isCityRequestHandled(new CityRequestHandledHandler() {
                @Override
                public void onCityRequestHandled(CityRequest cityRequest) {
                    showNotification(getString(R.string.request1city) + " " + cityRequest.city + " " + getString(R.string.request2), 5, "");
                }
            });

            ServiceResource.getInstance().isNewNotifications(new NewNotificationHandler() {
                @Override
                public void onNewNotification(String message) {
                    showNotification(message, 7, message);
                }
            });

            if (replyNotifications) {
                ServiceResource.getInstance().isNewReplies(userCommentIds, new NewReplyHandler() {
                    @Override
                    public void onNewPrivateMessage(String userCommentId, Comment reply) {
                        showNotification(getString(R.string.new_reply), 1, userCommentId);
                    }
                });
            }

            if (commentNotifications) {
                ServiceResource.getInstance().isNewComments(new NewCommentHandler() {
                    @Override
                    public void onNewComment(Comment comment) {
                        showNotification(getString(R.string.new_message), 3, "");
                    }
                });
            }

            if (eventNotifications) {
                ServiceResource.getInstance().isNewEvents(new NewEventHandler() {
                    @Override
                    public void onNewEvent(Event event) {
                        showNotification(getString(R.string.new_event), 8, "");
                    }
                });
            }

            // TODO Seuranhaku-toimintoa ei ole vielä toteutettu
            /*if (privateNotifications) {
                ServiceResource.getInstance().isNewPrivateMessages(sessionIds, new NewPrivateMessageHandler() {
                    @Override
                    public void onNewPrivateMessage(final String sessionId, final Comment privateMessage) {
                        UsersResource.getInstance().getUser(privateMessage.userId, new GetUserHandler() {
                            @Override
                            public void onUserLoaded(User user) {
                                String title = "";
                                if (user.isMale()) {
                                    title += getString(R.string.male) + " ";
                                } else title += getString(R.string.female) + " ";
                                title += StringUtils.getAgeText(user.birthday) + ": ";
                                title += privateMessage.message;

                                showNotification(title, 6, sessionId);
                            }
                        });
                    }
                });
            }*/

            // TODO Seuranhaku-toimintoa ei ole vielä toteutettu
            /*if (privateNotifications) {
                ServiceResource.getInstance().isNewSessions(new NewSessionHandler() {
                    @Override
                    public void onNewSession(Session session) {
                        String title = getString(R.string.new_private_message) + " ";
                        if (session.recipient.isMale()) {
                            title += getString(R.string.male) + " ";
                        } else title += getString(R.string.female) + " ";
                        title += StringUtils.getAgeText(session.recipient.birthday);
                        showNotification(title, 2, session.sessionId);
                    }
                });
            }*/

            // TODO Seuranhaku-toimintoa ei ole vielä toteutettu
            /*if (companyNotifications) {
                ServiceResource.getInstance().isNewUsersLookingForCompany(new NewUserLookingForCompanyHandler() {
                    @Override
                    public void onNewUserLookingForCompany(CompanyMessage companyMessage) {
                        showNotification(getString(R.string.notification_new_company), 9, "");
                    }
                });
            }*/
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // don't bind service and activity together
        return null;
    }

    public void showNotification(String content, int option, String extra) {
        if (!PrefRes.getBoolean(IS_APP_VISIBLE)) {
            String contentTitle = getString(R.string.app_name);

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationIntent.putExtra("notificationOptionExtra", option);
            notificationIntent.putExtra("notificationExtra", extra);

            PendingIntent mainPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(contentTitle)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setContentIntent(mainPendingIntent);

            if (vibrate)
                mBuilder.setVibrate(new long[]{1000, 500});

            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(option, mBuilder.build());
        }
    }
}

