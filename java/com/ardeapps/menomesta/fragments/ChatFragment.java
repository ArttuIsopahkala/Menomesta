package com.ardeapps.menomesta.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.adapters.ChatListAdapter;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetCityCommentsHandler;
import com.ardeapps.menomesta.handlers.GetOlderMessagesHandler;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.objects.ReportCounts;
import com.ardeapps.menomesta.resources.CommentsResource;
import com.ardeapps.menomesta.resources.RepliesResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import static com.ardeapps.menomesta.PrefRes.USER_COMMENT_IDS;

public class ChatFragment extends Fragment implements ChatListAdapter.ChatListAdapterListener {

    ListView chatList;
    SwipeRefreshLayout swipeRefresh;
    ImageView addComment;
    TextView no_comments;
    ChatListAdapter adapter;
    ArrayList<Comment> comments = new ArrayList<>();
    AppRes appRes = (AppRes) AppRes.getContext();

    public void update() {
        if (comments.size() == 0) {
            no_comments.setVisibility(View.VISIBLE);
        } else {
            no_comments.setVisibility(View.GONE);
        }

        adapter.refreshData();
        adapter.notifyDataSetChanged();
    }

    public void refreshData() {
        comments = appRes.getCityComments();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ChatListAdapter(getActivity());
        adapter.setListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        chatList = (ListView) v.findViewById(R.id.chatList);
        addComment = (ImageView) v.findViewById(R.id.showBarInfo);
        no_comments = (TextView) v.findViewById(R.id.no_comments);

        chatList.setAdapter(adapter);

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.hasUserSentCommentInMinute(comments)) {
                    InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.error_comment_title), getString(R.string.error_comment_desc));
                    dialog.show(getFragmentManager(), "kommentti lähetetty liian nopeasti");
                } else {
                    FragmentListeners.getInstance().getFragmentChangeListener().goToWriteCommentFragment(new WriteCommentFragment.Listener() {
                        @Override
                        public void onSendClicked(final Comment comment) {
                            CommentsResource.getInstance().addComment(comment, new AddSuccessListener() {
                                @Override
                                public void onAddSuccess(String id) {
                                    comment.commentId = id;
                                    appRes.setCityComment(comment.commentId, comment);
                                    refreshData();
                                    update();

                                    // Käynnistä service
                                    Set<String> userCommentIdsSet = PrefRes.getStringSet(USER_COMMENT_IDS);
                                    userCommentIdsSet.add(comment.commentId);
                                    PrefRes.putStringSet(USER_COMMENT_IDS, userCommentIdsSet);
                                    Helper.startNotificationService();
                                }
                            });

                            UsersResource.getInstance().updateUserKarma(KarmaPoints.COMMENTED_CITY, true);
                        }
                    });
                }
            }
        });
        swipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                CommentsResource.getInstance().getComments(new GetCityCommentsHandler() {
                    @Override
                    public void onCityCommentsLoaded(ArrayList<Comment> cityComments) {
                        Collections.reverse(cityComments);
                        appRes.setCityComments(cityComments);
                        refreshData();
                        update();
                    }
                });
            }
        });
        return v;
    }

    @Override
    public void onLikeCommentClick(final Comment comment) {
        final Comment commentToSave = comment.clone();
        commentToSave.usersVoted.add(AppRes.getUser().userId);
        CommentsResource.getInstance().editComment(commentToSave, new EditSuccessListener() {
            @Override
            public void onEditSuccess() {
                appRes.setCityComment(commentToSave.commentId, commentToSave);
                refreshData();
                update();
            }
        });
    }

    @Override
    public void onReportCommentClick(final Comment comment) {
        final Comment commentToSave = comment.clone();
        commentToSave.usersReported.add(AppRes.getUser().userId);
        if (commentToSave.usersReported.size() >= ReportCounts.REPORTS_TO_DELETE_COMMENT) {
            CommentsResource.getInstance().removeComment(commentToSave.commentId, new EditSuccessListener() {
                @Override
                public void onEditSuccess() {
                    RepliesResource.getInstance().removeAllReplies(commentToSave.commentId, new EditSuccessListener() {
                        @Override
                        public void onEditSuccess() {
                            appRes.setCityComment(commentToSave.commentId, null);
                            refreshData();
                            update();
                        }
                    });
                }
            });
        } else {
            CommentsResource.getInstance().editComment(commentToSave, new EditSuccessListener() {
                @Override
                public void onEditSuccess() {
                    appRes.setCityComment(commentToSave.commentId, commentToSave);
                    refreshData();
                    update();
                }
            });
        }
    }

    @Override
    public void onLoadOlderComments(String commentId) {
        CommentsResource.getInstance().getOlderComments(commentId, new GetOlderMessagesHandler() {
            @Override
            public void onOlderMessagesLoaded(ArrayList<Comment> olderMessages) {
                Collections.reverse(olderMessages);
                ArrayList<Comment> comments = appRes.getCityComments();
                comments.addAll(olderMessages);
                appRes.setCityComments(comments);
                refreshData();
                update();
            }
        });
    }
}
