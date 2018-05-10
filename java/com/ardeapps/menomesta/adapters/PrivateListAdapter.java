package com.ardeapps.menomesta.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.IconView;

import java.util.ArrayList;

import static com.ardeapps.menomesta.services.FirebaseDatabaseService.messageLimit;

/**
 * Created by Arttu on 15.9.2016.
 */
public class PrivateListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public PrivateListAdapterListener mListener;
    int olderMessagesCalled = 0;
    private ArrayList<Comment> messages = new ArrayList<>();
    private String lastCommentId;
    private String sessionId;
    private Context context;
    private User recipient;
    public PrivateListAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListener(PrivateListAdapterListener mListener) {
        this.mListener = mListener;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public void setMessages(ArrayList<Comment> messages) {
        this.messages = messages;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public int getCount() {
        return messages.size();
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
        final Holder holder = new Holder();
        if (cv == null) {
            cv = inflater.inflate(R.layout.list_item_private_message, null);
        }

        holder.ageText = (TextView) cv.findViewById(R.id.ageText);
        holder.sex_icon = (IconView) cv.findViewById(R.id.sex_icon);
        holder.comment = (TextView) cv.findViewById(R.id.comment);
        holder.time = (TextView) cv.findViewById(R.id.time);
        holder.commentContainer = (LinearLayout) cv.findViewById(R.id.commentContainer);
        if (messages.size() > messageLimit - 1 && position == 0 && !messages.get(0).commentId.equals(lastCommentId)) {
            olderMessagesCalled++;
            if(olderMessagesCalled % 2 != 0) {
                mListener.onLoadOlderMessages(sessionId, messages.get(0).commentId);
                lastCommentId = messages.get(0).commentId;
            }
        }

        final Comment message = messages.get(position);
        holder.commentContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.color_secondary));
        if (message.userId.equals(AppRes.getUser().userId)) {
            holder.sex_icon.setText(AppRes.getUser().isMale() ? R.string.icon_male : R.string.icon_female);
            holder.sex_icon.setTextColor(ContextCompat.getColor(AppRes.getContext(), AppRes.getUser().isMale() ? R.color.color_male : R.color.color_female));
            holder.ageText.setText(R.string.private_me);
            holder.ageText.setTextColor(ContextCompat.getColor(context, R.color.color_line));
        } else {
            holder.sex_icon.setText(recipient.isMale() ? R.string.icon_male : R.string.icon_female);
            holder.sex_icon.setTextColor(ContextCompat.getColor(AppRes.getContext(), recipient.isMale() ? R.color.color_male : R.color.color_female));
            holder.ageText.setText(StringUtils.getAgeText(recipient.birthday));
            holder.ageText.setTextColor(ContextCompat.getColor(context, R.color.color_text_light));
        }

        holder.comment.setText(message.message);
        holder.time.setText(StringUtils.getCommentTimeText(message.time));

        return cv;
    }

    public interface PrivateListAdapterListener {
        void onLoadOlderMessages(String sessionId, String commentId);
    }

    public class Holder {
        TextView ageText;
        TextView comment;
        TextView time;
        IconView sex_icon;
        LinearLayout commentContainer;
    }

}
