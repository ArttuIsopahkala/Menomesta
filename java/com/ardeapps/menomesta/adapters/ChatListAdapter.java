package com.ardeapps.menomesta.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.fragments.ConfirmationDialogFragment;
import com.ardeapps.menomesta.fragments.InfoDialogFragment;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.CommentHolder;
import com.ardeapps.menomesta.views.IconView;

import java.util.ArrayList;

import static com.ardeapps.menomesta.services.FirebaseService.messageLimit;

/**
 * Created by Arttu on 15.9.2016.
 */
public class ChatListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public ChatListAdapterListener mListener = null;
    ArrayList<Comment> comments = new ArrayList<>();
    String lastCommentId;
    AppRes appRes;
    Context context;
    public ChatListAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListener(ChatListAdapterListener l) {
        mListener = l;
    }

    public void refreshData() {
        appRes = (AppRes) AppRes.getContext();
        comments = appRes.getCityComments();
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View cv, ViewGroup parent) {
        final CommentHolder holder = new CommentHolder();
        if (cv == null) {
            cv = inflater.inflate(R.layout.comment_list_item, null);
        }
        holder.commentContainer = (LinearLayout) cv.findViewById(R.id.commentContainer);
        holder.replyContainer = (LinearLayout) cv.findViewById(R.id.replyContainer);
        holder.likesText = (TextView) cv.findViewById(R.id.likesText);
        holder.like_icon = (IconView) cv.findViewById(R.id.like_icon);
        holder.report_icon = (LinearLayout) cv.findViewById(R.id.report_icon);
        holder.comment = (TextView) cv.findViewById(R.id.comment);
        holder.time = (TextView) cv.findViewById(R.id.time);
        holder.replies = (TextView) cv.findViewById(R.id.replies);
        holder.replies_icon = (IconView) cv.findViewById(R.id.replies_icon);

        if (comments.size() > messageLimit - 1 && position == comments.size() - 3 && !comments.get(comments.size() - 1).commentId.equals(lastCommentId)) {
            String commentId = comments.get(comments.size() - 1).commentId;
            mListener.onLoadOlderComments(commentId);
            lastCommentId = comments.get(comments.size() - 1).commentId;
        }

        final Comment comment = comments.get(position);

        holder.replyContainer.setVisibility(comment.replySize == 0 ? View.INVISIBLE : View.VISIBLE);
        holder.replies.setText(String.valueOf(comment.replySize));

        if(comment.usersVoted != null)
            holder.likesText.setText(String.valueOf(comment.usersVoted.size()));
        else
            holder.likesText.setText("0");

        holder.comment.setText(comment.message);
        holder.time.setText(StringUtils.getCommentTimeText(comment.time));

        if (comment.usersVoted != null && comment.usersVoted.contains(AppRes.getUser().userId)) {
            holder.like_icon.setAlpha(0.2f);
            holder.like_icon.setOnClickListener(null);
        } else {
            holder.like_icon.setAlpha(1f);
            holder.like_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onLikeCommentClick(comment);
                }
            });
        }

        holder.commentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToReplyFragment(comment);
            }
        });

        holder.report_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = (FragmentActivity)(context);
                FragmentManager fm = activity.getSupportFragmentManager();

                if (comment.usersReported != null && comment.usersReported.contains(AppRes.getUser().userId)) {
                    InfoDialogFragment reportDialog = InfoDialogFragment.newInstance(context.getString(R.string.not_participated_title), context.getString(R.string.confirmation_report_error_desc_comment));
                    reportDialog.show(fm, "olet jo raportoinut tästä");
                } else {
                    ConfirmationDialogFragment confirm_dialog = ConfirmationDialogFragment.newInstance(context.getString(R.string.confirmation_report_comment));
                    confirm_dialog.show(fm, "flägää dialogi");
                    confirm_dialog.setListener(new ConfirmationDialogFragment.ConfirmationDialogCloseListener() {
                        @Override
                        public void onDialogYesButtonClick() {
                            mListener.onReportCommentClick(comment);
                        }
                    });
                }
            }
        });

        return cv;
    }

    public interface ChatListAdapterListener {
        void onLikeCommentClick(Comment comment);

        void onReportCommentClick(Comment comment);

        void onLoadOlderComments(String commentId);
    }
}
