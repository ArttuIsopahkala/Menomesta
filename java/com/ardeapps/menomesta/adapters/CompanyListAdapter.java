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
import com.ardeapps.menomesta.objects.CompanyMessage;
import com.ardeapps.menomesta.objects.Session;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.IconView;

import java.util.ArrayList;

public class CompanyListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public CompanyListAdapterListener mListener;
    AppRes appRes = (AppRes) AppRes.getContext();
    private ArrayList<CompanyMessage> usersLookingForCompany = new ArrayList<>();
    private ArrayList<Session> sessions = new ArrayList<>();

    public CompanyListAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListener(CompanyListAdapterListener mListener) {
        this.mListener = mListener;
    }

    public void setSessions(ArrayList<Session> sessions) {
        this.sessions = sessions;
    }

    public void setUsersLookingForCompany(ArrayList<CompanyMessage> usersLookingForCompany) {
        this.usersLookingForCompany = usersLookingForCompany;
    }

    @Override
    public int getCount() {
        return usersLookingForCompany.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_company, null);
        }
        holder.ageText = (TextView) convertView.findViewById(R.id.ageText);
        holder.sex_icon = (IconView) convertView.findViewById(R.id.sex_icon);
        holder.comment = (TextView) convertView.findViewById(R.id.comment);
        holder.arrow_image = (IconView) convertView.findViewById(R.id.arrow_image);
        holder.start_time_text = (TextView) convertView.findViewById(R.id.start_time_text);
        holder.start_time_container = (LinearLayout) convertView.findViewById(R.id.start_time_container);

        final CompanyMessage userLookingForCompany = usersLookingForCompany.get(position);
        if (userLookingForCompany.commentId.equals("1") || userLookingForCompany.commentId.equals("2")) {
            holder.start_time_container.setVisibility(View.VISIBLE);
            holder.start_time_text.setText(StringUtils.getDateText(userLookingForCompany.time));
        } else holder.start_time_container.setVisibility(View.GONE);

        holder.ageText.setText(StringUtils.getAgeText(userLookingForCompany.user.birthday));
        if (userLookingForCompany.user.isMale()) {
            holder.sex_icon.setText(R.string.icon_male);
            holder.sex_icon.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_male));
        } else {
            holder.sex_icon.setText(R.string.icon_female);
            holder.sex_icon.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_female));
        }

        holder.comment.setText(userLookingForCompany.message);

        switch (userLookingForCompany.commentId) {
            case "1":
                holder.arrow_image.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_star));
                break;
            case "2":
                holder.arrow_image.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_company_circle));
                break;
            default:
                holder.arrow_image.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_text_light));
                break;
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sessionId = "";
                if (userLookingForCompany.commentId.equals("1") || userLookingForCompany.commentId.equals("2")) {
                    for (Session session : sessions) {
                        if (session.recipient.userId.equals(userLookingForCompany.userId)) {
                            sessionId = session.sessionId;
                            break;
                        }
                    }
                }

                mListener.onPrivateUserClick(userLookingForCompany.user, sessionId);
            }
        });

        return convertView;
    }

    public interface CompanyListAdapterListener {
        void onPrivateUserClick(User user, String sessionId);
    }

    public class Holder {
        TextView ageText;
        TextView comment;
        IconView sex_icon;
        IconView arrow_image;
        TextView start_time_text;
        LinearLayout start_time_container;
    }
}
