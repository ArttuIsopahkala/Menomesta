package com.ardeapps.menomesta.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.CommentHolder;
import com.ardeapps.menomesta.views.IconView;

import java.util.ArrayList;

/**
 * Created by Arttu on 15.9.2016.
 */
public class ReplyListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private ReplyListAdapterListener mListener = null;
    private ArrayList<Comment> replies = new ArrayList<>();
    private Context context;

    public ReplyListAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListener(ReplyListAdapterListener l) {
        mListener = l;
    }

    public void setReplies(ArrayList<Comment> replies) {
        this.replies = replies;
    }

    @Override
    public int getCount() {
        return replies.size();
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
            cv = inflater.inflate(R.layout.list_item_comment, null);
        }
        holder.commentContainer = (LinearLayout) cv.findViewById(R.id.commentContainer);
        holder.replyContainer = (LinearLayout) cv.findViewById(R.id.replyContainer);
        holder.likesText = (TextView) cv.findViewById(R.id.likesText);
        holder.like_icon = (IconView) cv.findViewById(R.id.likeIcon);
        holder.report_icon = (LinearLayout) cv.findViewById(R.id.reportIcon);
        holder.comment = (TextView) cv.findViewById(R.id.comment);
        holder.time = (TextView) cv.findViewById(R.id.time);
        holder.replies = (TextView) cv.findViewById(R.id.replies);
        holder.replies_icon = (IconView) cv.findViewById(R.id.replies_icon);
        holder.divider_line = cv.findViewById(R.id.divider_line);

        final Comment reply = replies.get(position);

        holder.commentContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.color_secondary));
        if(position == 0) {
            holder.divider_line.setVisibility(View.VISIBLE);
        } else holder.divider_line.setVisibility(View.INVISIBLE);

        holder.replyContainer.setVisibility(View.INVISIBLE);

        if(reply.usersVoted != null)
            holder.likesText.setText(String.valueOf(reply.usersVoted.size()));
        else
            holder.likesText.setText("0");

        holder.comment.setText(reply.message);
        holder.time.setText(StringUtils.getCommentTimeText(reply.time));

        if (reply.usersVoted != null && reply.usersVoted.contains(AppRes.getUser().userId)) {
            holder.like_icon.setAlpha(0.2f);
            holder.like_icon.setOnClickListener(null);
        } else {
            holder.like_icon.setAlpha(1f);
            holder.like_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position == 0) {
                        mListener.onLikeCommentClick(reply);
                    } else {
                        mListener.onLikeReplyClick(reply);
                    }
                }
            });
        }

        holder.report_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = (FragmentActivity)(context);
                FragmentManager fm = activity.getSupportFragmentManager();

                if (reply.usersReported != null && reply.usersReported.contains(AppRes.getUser().userId)) {
                    InfoDialogFragment reportDialog = InfoDialogFragment.newInstance(context.getString(R.string.not_participated_title), context.getString(R.string.confirmation_report_error_desc_comment));
                    reportDialog.show(fm, "olet jo raportoinut tästä");
                } else {
                    ConfirmationDialogFragment confirm_dialog = ConfirmationDialogFragment.newInstance(context.getString(R.string.confirmation_report_comment));

                    confirm_dialog.show(fm, "flägää dialogi");
                    confirm_dialog.setListener(new ConfirmationDialogFragment.ConfirmationDialogCloseListener() {
                        @Override
                        public void onDialogYesButtonClick() {
                            if (position == 0) {
                                mListener.onReportCommentClick(reply);
                            } else {
                                mListener.onReportReplyClick(reply);
                            }
                        }
                    });
                }
            }
        });

        return cv;
    }

    public interface ReplyListAdapterListener {
        void onLikeCommentClick(Comment comment);

        void onReportCommentClick(Comment comment);

        void onLikeReplyClick(Comment reply);

        void onReportReplyClick(Comment reply);
    }

}
