package com.ardeapps.menomesta.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ardeapps.menomesta.R;

import java.util.ArrayList;

/**
 * Created by Arttu on 6.2.2017.
 */
public class FilterSpinnerAdapter extends ArrayAdapter<String> {

    private int mSelectedIndex = 0;


    public FilterSpinnerAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
    }

    public void setSelection(int position) {
        mSelectedIndex =  position;
        notifyDataSetChanged();
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // this part is needed for hiding the original view
        View view = super.getView(position, convertView, parent);
        view.setVisibility(View.GONE);

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View itemView =  super.getDropDownView(position, convertView, parent);

        if (position == mSelectedIndex) {
            itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_line));
        } else {
            itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_menu));
        }

        return itemView;
    }
}
