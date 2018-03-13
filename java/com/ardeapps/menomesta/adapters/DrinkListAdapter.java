package com.ardeapps.menomesta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.DrinkHolder;
import com.ardeapps.menomesta.views.IconView;

import java.util.ArrayList;

/**
 * Created by Arttu on 15.9.2016.
 */
public class DrinkListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    DrinkListListener mListener = null;
    ArrayList<Drink> drinks;
    Bar bar;
    Context context;

    public DrinkListAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListener(DrinkListListener l) {
        mListener = l;
    }

    public void setDrinks(ArrayList<Drink> drinks) {
        this.drinks = drinks;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    @Override
    public int getCount() {
        return drinks.size();
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
        final DrinkHolder holder = new DrinkHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.drink_list_item, null);
        }
        holder.nameText = (TextView) convertView.findViewById(R.id.name);
        holder.sizeText = (TextView) convertView.findViewById(R.id.size);
        holder.priceText = (TextView) convertView.findViewById(R.id.price);
        holder.updatedText = (TextView) convertView.findViewById(R.id.updated);
        holder.edit_icon = (IconView) convertView.findViewById(R.id.edit_icon);

        final Drink drink = drinks.get(position);

        holder.nameText.setText(drink.name);
        holder.sizeText.setText(context.getString(R.string.size, String.valueOf(drink.size)));
        holder.priceText.setText(context.getString(R.string.price, String.valueOf(drink.price)));

        if (drink.updateTime == 0) {
            holder.updatedText.setVisibility(View.INVISIBLE);
        } else {
            holder.updatedText.setVisibility(View.VISIBLE);
            holder.updatedText.setText(StringUtils.getDateText(drink.updateTime));
        }

        holder.edit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEditDrinkClick(drink);
            }
        });

        return convertView;
    }

    public interface DrinkListListener {
        void onEditDrinkClick(Drink drink);
    }
}
