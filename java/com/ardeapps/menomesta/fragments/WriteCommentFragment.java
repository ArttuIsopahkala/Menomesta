package com.ardeapps.menomesta.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.IconView;

import java.util.ArrayList;

public class WriteCommentFragment extends Fragment {

    Listener mListener = null;
    IconView stop_writing;
    TextView send_comment;
    EditText message;

    public WriteCommentFragment() {
        // Required empty public constructor
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_comment, container, false);
        send_comment = (TextView) v.findViewById(R.id.send_comment);
        message = (EditText) v.findViewById(R.id.message);
        stop_writing = (IconView) v.findViewById(R.id.stop_writing);

        Helper.showKeyBoard();
        message.requestFocus();
        message.setHorizontallyScrolling(false);
        message.setMaxLines(10);
        message.setText("");

        stop_writing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = message.getText().toString().trim();
                if (!StringUtils.isEmptyString(messageText)) {
                    final Comment comment = new Comment();
                    comment.userId = AppRes.getUser().userId;
                    comment.message = messageText;
                    comment.time = System.currentTimeMillis();
                    comment.replySize = 0;
                    comment.usersVoted = new ArrayList<>();
                    comment.usersReported = new ArrayList<>();
                    mListener.onSendClicked(comment);
                    message.setText("");
                    getActivity().onBackPressed();
                }
            }
        });

        return v;
    }

    public interface Listener {
        void onSendClicked(Comment comment);
    }
}
