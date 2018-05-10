package com.ardeapps.menomesta.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.adapters.PrivateListAdapter;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetOlderMessagesHandler;
import com.ardeapps.menomesta.handlers.ObjectExistsHandler;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.Session;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.resources.PrivateMessagesResource;
import com.ardeapps.menomesta.resources.PrivateSessionsResource;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.IconView;

import java.util.ArrayList;

import static com.ardeapps.menomesta.objects.User.MALE;
import static com.ardeapps.menomesta.services.FirebaseDatabaseService.messageLimit;

public class PrivateFragment extends Fragment implements PrivateListAdapter.PrivateListAdapterListener {

    Listener mListener = null;
    ListView messageList;
    PrivateListAdapter adapter;
    LinearLayout menu_container;
    LinearLayout removeChat;
    User recipient;
    ArrayList<Comment> messages = new ArrayList<>();
    TextView titleText;
    IconView sendMessage;
    EditText messageText;
    String sessionId;
    boolean scrollToBottom = false;
    int olderMessagesSize = 0;
    AppRes appRes = (AppRes) AppRes.getContext();

    public void setListener(Listener l) {
        mListener = l;
    }

    public void updateMessageData() {
        if (messages.size() == 0) {
            menu_container.setVisibility(View.GONE);
        } else {
            menu_container.setVisibility(View.VISIBLE);

            adapter.setRecipient(recipient);
            adapter.setMessages(messages);
            adapter.setSessionId(sessionId);
            adapter.notifyDataSetChanged();

            messageList.post(new Runnable() {
                @Override
                public void run() {
                    if (scrollToBottom) {
                        messageList.setSelection(messages.size() - 1);
                        scrollToBottom = false;
                    } else {
                        if (messages.size() >= messageLimit && olderMessagesSize < messageLimit)
                            messageList.setSelection(olderMessagesSize);
                        else
                            messageList.setSelection(messageLimit);
                    }
                }
            });
        }
    }

    public void addNewMessage(String sessionId, Comment message) {
        if (sessionId != null && this.sessionId.equals(sessionId)) {
            messages.add(message);
            adapter.setMessages(messages);
            adapter.notifyDataSetChanged();
        }
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public void setMessages(ArrayList<Comment> messages) {
        this.messages = messages;
    }

    public void addOlderMessages(ArrayList<Comment> olderMessages) {
        messages.addAll(0, olderMessages);
        this.olderMessagesSize = olderMessages.size();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PrivateListAdapter(getActivity());
        adapter.setListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_private, container, false);
        messageList = (ListView) v.findViewById(R.id.messageList);
        titleText = (TextView) v.findViewById(R.id.title);
        sendMessage = (IconView) v.findViewById(R.id.sendMessage);
        messageText = (EditText) v.findViewById(R.id.messageText);
        removeChat = (LinearLayout) v.findViewById(R.id.removeChat);
        menu_container = (LinearLayout) v.findViewById(R.id.menu_container);

        messageList.setAdapter(adapter);

        messageText.setHorizontallyScrolling(false);
        messageText.setMaxLines(10);

        if (messages.size() == 0) {
            menu_container.setVisibility(View.GONE);
        } else menu_container.setVisibility(View.VISIBLE);

        String title = getString(R.string.private_message) + " ";
        if (recipient.gender.equals(MALE)) {
            title += getString(R.string.male) + " ";
        } else title += getString(R.string.female) + " ";
        title += StringUtils.getAgeText(recipient.birthday);
        titleText.setText(title);

        updateMessageData();
        messageList.post(new Runnable() {
            @Override
            public void run() {
                messageList.setSelection(adapter.getCount() - 1);
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = messageText.getText().toString();
                if (!StringUtils.isEmptyString(message)) {
                    PrivateMessagesResource.getInstance().sessionExists(sessionId, new ObjectExistsHandler() {
                        @Override
                        public void onObjectChecked(boolean sessionExists) {
                            //uusi sessio
                            if (StringUtils.isEmptyString(sessionId)) {
                                final Session session = new Session();
                                session.userId = recipient.userId;
                                session.startTime = System.currentTimeMillis();
                                PrivateSessionsResource.getInstance().addPrivateSession(AppRes.getUser().userId, session, new AddSuccessListener() {
                                    @Override
                                    public void onAddSuccess(String id) {
                                        session.sessionId = id;
                                        final Session recipientSession = new Session();
                                        recipientSession.sessionId = session.sessionId;
                                        recipientSession.userId = AppRes.getUser().userId;
                                        recipientSession.startTime = session.startTime;
                                        PrivateSessionsResource.getInstance().editPrivateSession(recipient.userId, session, new EditSuccessListener() {
                                            @Override
                                            public void onEditSuccess() {
                                                final Comment comment = new Comment();
                                                comment.userId = AppRes.getUser().userId;
                                                comment.time = System.currentTimeMillis();
                                                comment.message = message;
                                                PrivateMessagesResource.getInstance().addPrivateMessage(recipientSession.sessionId, comment, new AddSuccessListener() {
                                                    @Override
                                                    public void onAddSuccess(String id) {
                                                        comment.commentId = id;
                                                        sessionId = session.sessionId;
                                                        messages.add(comment);
                                                        scrollToBottom = true;
                                                        updateMessageData();
                                                        mListener.onSessionChanged();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            } else {
                                if (sessionExists) {
                                    final Comment comment = new Comment();
                                    comment.userId = AppRes.getUser().userId;
                                    comment.time = System.currentTimeMillis();
                                    comment.message = message;
                                    PrivateMessagesResource.getInstance().addPrivateMessage(sessionId, comment, new AddSuccessListener() {
                                        @Override
                                        public void onAddSuccess(String id) {
                                            comment.commentId = id;
                                            messages.add(comment);
                                            scrollToBottom = true;
                                            updateMessageData();
                                        }
                                    });
                                } else {
                                    InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.chat_removed_title), getString(R.string.chat_removed_desc));
                                    dialog.show(getFragmentManager(), "Keskustelu poistettu");
                                    mListener.onSessionChanged();
                                }
                            }
                            messageText.setText("");
                        }
                    });
                }
            }
        });

        removeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmationDialogFragment confirm_dialog = ConfirmationDialogFragment.newInstance(getString(R.string.confirmation_chat));
                confirm_dialog.show(getFragmentManager(), "poista keskustelu dialogi");
                confirm_dialog.setListener(new ConfirmationDialogFragment.ConfirmationDialogCloseListener() {
                    @Override
                    public void onDialogYesButtonClick() {
                        PrivateMessagesResource.getInstance().removePrivateMessages(sessionId, new EditSuccessListener() {
                            @Override
                            public void onEditSuccess() {
                                PrivateSessionsResource.getInstance().removePrivateSession(AppRes.getUser().userId, sessionId, new EditSuccessListener() {
                                    @Override
                                    public void onEditSuccess() {
                                        PrivateSessionsResource.getInstance().removePrivateSession(recipient.userId, sessionId, new EditSuccessListener() {
                                            @Override
                                            public void onEditSuccess() {
                                                mListener.onSessionChanged();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onLoadOlderMessages(String sessionId, String commentId) {
        PrivateMessagesResource.getInstance().getOlderPrivateMessages(sessionId, commentId, new GetOlderMessagesHandler() {
            @Override
            public void onOlderMessagesLoaded(ArrayList<Comment> olderMessages) {
                if (olderMessages.size() != 0) {
                    addOlderMessages(olderMessages);
                    updateMessageData();
                }
            }
        });
    }

    public interface Listener {
        void onSessionChanged();
    }
}
