package com.ardeapps.menomesta.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.adapters.CompanyListAdapter;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetPrivateMessagesHandler;
import com.ardeapps.menomesta.handlers.GetPrivateSessionsHandler;
import com.ardeapps.menomesta.handlers.GetUsersLookingForCompanyHandler;
import com.ardeapps.menomesta.handlers.ObjectExistsHandler;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.CompanyMessage;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.objects.Session;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.resources.PrivateMessagesResource;
import com.ardeapps.menomesta.resources.PrivateSessionsResource;
import com.ardeapps.menomesta.resources.UsersLookingForCompanyResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.IconView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.ArrayList;
import java.util.Collections;

public class CompanyFragment extends Fragment implements AddCompanyDialogFragment.AddCompanyDialogCloseListener,
        GetUsersLookingForCompanyHandler, GetPrivateSessionsHandler, CompanyListAdapter.CompanyListAdapterListener {

    ListView companyList;
    SwipeRefreshLayout swipeRefresh;
    CompanyListAdapter adapter;
    ArrayList<CompanyMessage> usersLookingForCompany;
    ArrayList<CompanyMessage> sessionsAlreadyStarted;
    RelativeLayout company_info_container;
    LinearLayout myContainer;
    ImageView group_icon;
    ToggleButton companyButton;
    TextView titleText;
    ArrayList<Session> sessions;
    ArrayList<Session> validSessions;
    TextView infoText;
    TextView myCommentText;
    IconView edit_image;
    AddCompanyDialogFragment companyDialog;
    CompanyMessage myMessage;
    AppRes appRes = (AppRes) AppRes.getContext();
    CompoundButton.OnCheckedChangeListener beerButtonListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
            if (isChecked) {
                companyDialog = new AddCompanyDialogFragment();
                companyDialog.setCancelable(false);
                companyDialog.show(getFragmentManager(), "Lisää seuranhakuteksti");
                companyDialog.setListener(CompanyFragment.this);
            } else {
                Answers.getInstance().logCustom(new CustomEvent("Olut-nappia painettu")
                        .putCustomAttribute("Seuraa haettu", "ei"));

                final User userToSave = AppRes.getUser().clone();
                userToSave.isLookingFor = false;

                UsersLookingForCompanyResource.getInstance().removeUserLookingForCompany(new EditSuccessListener() {
                    @Override
                    public void onEditSuccess() {
                        appRes.setCompanyMessage(null);

                        UsersResource.getInstance().editUser(userToSave, new EditSuccessListener() {
                            @Override
                            public void onEditSuccess() {
                                AppRes.setUser(userToSave);
                                UsersResource.getInstance().updateUserKarma(KarmaPoints.BEER_BUTTON_PRESSED, false);
                                UsersLookingForCompanyResource.getInstance().getUsersLookingForCompany(CompanyFragment.this);
                            }
                        });
                    }
                });
            }
        }
    };

    public void update() {
        if (AppRes.getUser().isLookingFor) {
            // Poistetaan käyttäjä itse listasta
            for (CompanyMessage companyUser : usersLookingForCompany) {
                if (companyUser.userId.equals(AppRes.getUser().userId)) {
                    myMessage = companyUser;
                    appRes.setCompanyMessage(companyUser);

                    usersLookingForCompany.remove(companyUser);
                    break;
                }
            }
            // Näytetään esittelyteksti
            if (myContainer != null)
                myContainer.setVisibility(View.VISIBLE);
            if (myCommentText != null && myMessage != null)
                myCommentText.setText(myMessage.message);
        } else {
            // Piilotetaan esittelyteksti
            if (myContainer != null)
                myContainer.setVisibility(View.GONE);
        }

        if (sessions.size() == 0) {
            // Sessioita ei löydy eikä kukaan etsi seuraa
            if (usersLookingForCompany.size() == 0) {
                company_info_container.setVisibility(View.VISIBLE);
                String info = AppRes.getUser().isLookingFor ? getString(R.string.company_only_you) : getString(R.string.company_no_company);
                infoText.setText(info);
            } else {
                // Sessioita ei löydy mutta joku etsii seuraa
                company_info_container.setVisibility(View.GONE);
            }
        } else {
            // Sessioita löytyy mutta kukaan ei etsi seuraa
            if (usersLookingForCompany.size() == 0) {
                // Näytetään infoteksti
                company_info_container.setVisibility(View.VISIBLE);
                String info = (AppRes.getUser().isLookingFor ? getString(R.string.company_only_you) : getString(R.string.company_no_company)) + " " + getString(R.string.company_chats_below);
                infoText.setText(info);
                for (Session session : sessions) {
                    CompanyMessage comment = new CompanyMessage();
                    comment.time = session.startTime;
                    comment.commentId = "1";
                    comment.userId = session.recipient.userId;
                    comment.message = "";
                    usersLookingForCompany.add(comment);
                }
            } else {
                // Sessioita löytyy ja joku etsii seuraa
                // Piilotetaan infoteksti
                company_info_container.setVisibility(View.GONE);
                sessionsAlreadyStarted = new ArrayList<>();
                validSessions = new ArrayList<>(sessions);

                for (CompanyMessage userLookingForCompany : usersLookingForCompany) {
                    boolean sessionAlreadyStarted = false;

                    for (Session session : validSessions) {
                        if (session.recipient.userId.equals(userLookingForCompany.userId)) {
                            sessionAlreadyStarted = true;
                            userLookingForCompany.time = session.startTime;
                            validSessions.remove(session);
                            break;
                        }
                    }
                    if (sessionAlreadyStarted)
                        sessionsAlreadyStarted.add(userLookingForCompany);
                }
                // Poistetaan jo aloitetut sessiot hakijoista
                if (sessionsAlreadyStarted.size() > 0)
                    usersLookingForCompany.removeAll(sessionsAlreadyStarted);

                for (Session session : validSessions) {
                    CompanyMessage comment = new CompanyMessage();
                    comment.time = session.startTime;
                    comment.commentId = "1";
                    comment.userId = session.recipient.userId;
                    comment.message = "";
                    usersLookingForCompany.add(comment);
                }
                for (CompanyMessage sessionStarted : sessionsAlreadyStarted) {
                    sessionStarted.commentId = "2";
                    usersLookingForCompany.add(sessionStarted);
                }

                Collections.reverse(usersLookingForCompany);
            }
        }

        adapter.setSessions(sessions);
        adapter.setUsersLookingForCompany(usersLookingForCompany);
        adapter.notifyDataSetChanged();
    }

    public void setSessions(ArrayList<Session> sessions) {
        this.sessions = sessions;
    }

    public void setUsersLookingForCompany(ArrayList<CompanyMessage> usersLookingForCompany) {
        this.usersLookingForCompany = usersLookingForCompany;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new CompanyListAdapter(getActivity());
        adapter.setListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_company, container, false);
        companyList = (ListView) v.findViewById(R.id.companyList);
        company_info_container = (RelativeLayout) v.findViewById(R.id.company_info_container);
        infoText = (TextView) v.findViewById(R.id.infoText);
        group_icon = (ImageView) v.findViewById(R.id.group_icon);
        myContainer = (LinearLayout) v.findViewById(R.id.myContainer);
        myCommentText = (TextView) v.findViewById(R.id.myComment);
        edit_image = (IconView) v.findViewById(R.id.edit_image);
        companyButton = (ToggleButton) v.findViewById(R.id.companyButton);
        titleText = (TextView) v.findViewById(R.id.title);

        companyList.setAdapter(adapter);

        titleText.setText(getString(R.string.company_and_private));

        companyButton.setChecked(AppRes.getUser().isLookingFor);

        update();

        companyButton.setOnCheckedChangeListener(beerButtonListener);

        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                companyDialog = new AddCompanyDialogFragment();
                companyDialog.setCompanyText(myMessage.message);
                companyDialog.show(getFragmentManager(), "Muokkaa seuranhakutekstiä");
                companyDialog.setListener(CompanyFragment.this);
            }
        });

        swipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                UsersLookingForCompanyResource.getInstance().getUsersLookingForCompany(CompanyFragment.this);
            }
        });
        return v;
    }

    @Override
    public void onCompanyDialogTextAdded(String text, boolean create) {
        companyDialog.dismiss();

        if (create) {
            final User userToSave = AppRes.getUser().clone();
            userToSave.isLookingFor = true;

            final CompanyMessage companyMessage = new CompanyMessage();
            companyMessage.commentId = userToSave.userId;
            companyMessage.message = text;
            companyMessage.time = System.currentTimeMillis();
            companyMessage.userId = userToSave.userId;

            UsersLookingForCompanyResource.getInstance().editUserLookingForCompany(companyMessage, new EditSuccessListener() {
                @Override
                public void onEditSuccess() {
                    UsersResource.getInstance().editUser(userToSave, new EditSuccessListener() {
                        @Override
                        public void onEditSuccess() {
                            AppRes.setUser(userToSave);
                            appRes.setCompanyMessage(companyMessage);

                            UsersResource.getInstance().updateUserKarma(KarmaPoints.BEER_BUTTON_PRESSED, true);
                            UsersLookingForCompanyResource.getInstance().getUsersLookingForCompany(CompanyFragment.this);

                            Answers.getInstance().logCustom(new CustomEvent("Olut-nappia painettu")
                                    .putCustomAttribute("Seuraa haettu", "kyllä"));
                        }
                    });
                }
            });
        } else {
            myMessage.message = text;
            UsersLookingForCompanyResource.getInstance().editUserLookingForCompany(myMessage, new EditSuccessListener() {
                @Override
                public void onEditSuccess() {
                    appRes.setCompanyMessage(myMessage);
                }
            });
        }

        myCommentText.setText(text);
    }

    @Override
    public void onCompanyDialogCancel() {
        if (AppRes.getUser().isLookingFor) {
            companyButton.setOnCheckedChangeListener(null);
            companyButton.setChecked(false);
            companyButton.setOnCheckedChangeListener(beerButtonListener);
        } else {
            companyButton.setChecked(false);
        }
        companyDialog.dismiss();
    }

    @Override
    public void onUsersLookingForCompanyLoaded(ArrayList<CompanyMessage> usersLookingForCompany) {
        this.usersLookingForCompany = usersLookingForCompany;
        PrivateSessionsResource.getInstance().getPrivateSessions(this);
    }

    @Override
    public void onPrivateSessionsLoaded(ArrayList<Session> sessions) {
        this.sessions = sessions;
        update();
    }

    @Override
    public void onPrivateUserClick(final User recipient, final String sessionId) {
        if (!StringUtils.isEmptyString(sessionId)) {
            // Vanha keskustelu
            PrivateMessagesResource.getInstance().getPrivateMessages(sessionId, new GetPrivateMessagesHandler() {
                @Override
                public void onPrivateMessagesLoaded(ArrayList<Comment> messages) {
                    FragmentListeners.getInstance().getFragmentChangeListener().goToPrivateFragment(recipient, sessionId, messages);
                }
            });
        } else {
            // Uusi keskustelu
            UsersLookingForCompanyResource.getInstance().userIsLookingForCompany(recipient.userId, new ObjectExistsHandler() {
                @Override
                public void onObjectChecked(boolean objectExists) {
                    if (objectExists) {
                        FragmentListeners.getInstance().getFragmentChangeListener().goToPrivateFragment(recipient, sessionId, new ArrayList<Comment>());
                    } else {
                        InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.user_not_look_title), getString(R.string.user_not_look_desc));
                        dialog.show(getFragmentManager(), "Ei hae enää seuraa");
                        UsersLookingForCompanyResource.getInstance().getUsersLookingForCompany(CompanyFragment.this);
                    }
                }
            });
        }
    }
}
