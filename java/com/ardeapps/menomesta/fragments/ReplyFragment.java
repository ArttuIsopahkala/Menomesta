package com.ardeapps.menomesta.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.adapters.ReplyListAdapter;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetCityCommentHandler;
import com.ardeapps.menomesta.handlers.GetRepliesHandler;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.objects.ReportCounts;
import com.ardeapps.menomesta.resources.CommentsResource;
import com.ardeapps.menomesta.resources.RepliesResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.services.FragmentListeners;

import java.util.ArrayList;

public class ReplyFragment extends Fragment implements ReplyListAdapter.ReplyListAdapterListener {

    ListView replyList;
    SwipeRefreshLayout swipeRefresh;
    Button addComment;
    ReplyListAdapter adapter;
    Comment comment;
    ArrayList<Comment> replies = new ArrayList<>();
    TextView titleText;
    View menu_line;
    AppRes appRes = (AppRes) AppRes.getContext();

    public void update() {
        adapter = new ReplyListAdapter(getActivity());
        adapter.setListener(this);
        adapter.setReplies(replies);
        replyList.setAdapter(adapter);
        replyList.post(new Runnable() {
            @Override
            public void run() {
                replyList.smoothScrollToPosition(adapter.getCount() - 1);
            }
        });
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void setReplies(ArrayList<Comment> replies) {
        this.replies = replies;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reply, container, false);
        replyList = (ListView) v.findViewById(R.id.replyList);
        titleText = (TextView) v.findViewById(R.id.title);
        addComment = (Button) v.findViewById(R.id.showBarInfo);
        menu_line = v.findViewById(R.id.menu_line);

        menu_line.setVisibility(View.GONE);
        titleText.setText("");
        update();

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToWriteCommentFragment(new WriteCommentFragment.Listener() {
                    @Override
                    public void onSendClicked(final Comment reply) {
                        // Kommenttiin uusi vastaus. Ladataan tuore vastauskoko ja lisätään yhdellä.
                        CommentsResource.getInstance().getComment(comment.commentId, new GetCityCommentHandler() {
                            @Override
                            public void onCityCommentLoaded(final Comment cityComment) {
                                cityComment.replySize++;
                                CommentsResource.getInstance().editComment(cityComment, new EditSuccessListener() {
                                    @Override
                                    public void onEditSuccess() {
                                        appRes.setCityComment(cityComment.commentId, cityComment);
                                        RepliesResource.getInstance().addReply(comment.commentId, reply, new AddSuccessListener() {
                                            @Override
                                            public void onAddSuccess(String id) {
                                                reply.commentId = id;
                                                replies = Comment.setComment(replies, reply.commentId, reply);
                                                update();
                                                FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshChatFragment();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        UsersResource.getInstance().updateUserKarma(KarmaPoints.COMMENTED_REPLY, true);
                    }
                });
            }
        });
        swipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                RepliesResource.getInstance().getReplies(comment.commentId, new GetRepliesHandler() {
                    @Override
                    public void onRepliesLoaded(ArrayList<Comment> replies) {
                        setReplies(replies);
                        update();
                    }
                });
            }
        });
        return v;
    }

    @Override
    public void onLikeCommentClick(Comment comment) {
        final Comment commentToSave = comment.clone();
        commentToSave.usersVoted.add(AppRes.getUser().userId);
        CommentsResource.getInstance().editComment(commentToSave, new EditSuccessListener() {
            @Override
            public void onEditSuccess() {
                appRes.setCityComment(commentToSave.commentId, commentToSave);

                replies = Comment.setComment(replies, commentToSave.commentId, commentToSave);
                update();

                FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshChatFragment();
                UsersResource.getInstance().giveUserKarma(commentToSave.userId, KarmaPoints.COMMENT_LIKED, true);
            }
        });
    }

    @Override
    public void onReportCommentClick(final Comment comment) {
        final Comment commentToSave = comment.clone();
        commentToSave.usersReported.add(AppRes.getUser().userId);
        // Tallenna raportointi tai poista kommentti ja vastaukset
        if (commentToSave.usersReported.size() >= ReportCounts.REPORTS_TO_DELETE_COMMENT) {
            CommentsResource.getInstance().removeComment(commentToSave.commentId, new EditSuccessListener() {
                @Override
                public void onEditSuccess() {
                    appRes.setCityComment(commentToSave.commentId, null);
                    RepliesResource.getInstance().removeAllReplies(commentToSave.commentId, new EditSuccessListener() {
                        @Override
                        public void onEditSuccess() {
                            FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshChatFragment();
                            getActivity().onBackPressed();
                        }
                    });
                }
            });
        } else {
            CommentsResource.getInstance().editComment(commentToSave, new EditSuccessListener() {
                @Override
                public void onEditSuccess() {
                    appRes.setCityComment(commentToSave.commentId, commentToSave);

                    // Replyt ei muutu, vain kommentti muuttuu.
                    update();
                    FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshChatFragment();
                }
            });
        }
        UsersResource.getInstance().giveUserKarma(commentToSave.userId, KarmaPoints.COMMENT_REPORTED, false);
    }

    @Override
    public void onLikeReplyClick(Comment reply) {
        final Comment replyToSave = reply.clone();
        replyToSave.usersVoted.add(AppRes.getUser().userId);
        RepliesResource.getInstance().editReply(comment.commentId, replyToSave, new EditSuccessListener() {
            @Override
            public void onEditSuccess() {
                replies = Comment.setComment(replies, replyToSave.commentId, replyToSave);
                update();

                UsersResource.getInstance().giveUserKarma(replyToSave.userId, KarmaPoints.COMMENT_LIKED, true);
            }
        });
    }

    @Override
    public void onReportReplyClick(Comment reply) {
        final Comment replyToSave = reply.clone();
        replyToSave.usersReported.add(AppRes.getUser().userId);
        if (replyToSave.usersReported.size() >= ReportCounts.REPORTS_TO_DELETE_REPLY) {
            RepliesResource.getInstance().removeReply(comment.commentId, replyToSave.commentId, new EditSuccessListener() {
                @Override
                public void onEditSuccess() {
                    replies = Comment.setComment(replies, replyToSave.commentId, null);
                    CommentsResource.getInstance().getComment(comment.commentId, new GetCityCommentHandler() {
                        @Override
                        public void onCityCommentLoaded(final Comment cityComment) {
                            cityComment.replySize--;
                            CommentsResource.getInstance().editComment(cityComment, new EditSuccessListener() {
                                @Override
                                public void onEditSuccess() {
                                    appRes.setCityComment(cityComment.commentId, cityComment);
                                    update();
                                    FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshChatFragment();
                                }
                            });
                        }
                    });
                }
            });
        } else {
            RepliesResource.getInstance().editReply(comment.commentId, replyToSave, new EditSuccessListener() {
                @Override
                public void onEditSuccess() {
                    replies = Comment.setComment(replies, replyToSave.commentId, replyToSave);
                    update();
                }
            });
        }
        UsersResource.getInstance().giveUserKarma(comment.userId, KarmaPoints.COMMENT_REPORTED, false);
    }
}
