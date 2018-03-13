package com.ardeapps.menomesta.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by Arttu on 29.11.2015.
 */
public class AddDrinkDialogFragment extends DialogFragment {

    AddDrinkDialogCloseListener mListener = null;
    EditText nameText;
    EditText sizeText;
    EditText priceText;
    TextView title;
    TextView sizeTitle;
    TextView priceTitle;
    Button ready_button;
    Button cancel_button;
    Drink drink;

    public void setListener(AddDrinkDialogCloseListener l) {
        mListener = l;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_drink_dialog, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        nameText = (EditText) v.findViewById(R.id.nameText);
        sizeText = (EditText) v.findViewById(R.id.sizeText);
        priceText = (EditText) v.findViewById(R.id.priceText);
        title = (TextView) v.findViewById(R.id.title);
        sizeTitle = (TextView) v.findViewById(R.id.sizeTitle);
        priceTitle = (TextView) v.findViewById(R.id.priceTitle);
        ready_button = (Button) v.findViewById(R.id.btn_ready);
        cancel_button = (Button) v.findViewById(R.id.btn_cancel);

        sizeTitle.setText(getString(R.string.prices_size) + "(L)");
        priceTitle.setText(getString(R.string.prices_price) + "(€)");

        if (drink != null) {
            title.setText(getString(R.string.prices_edit));
            nameText.setText(drink.name);
            if (drink.price == 0 && drink.size == 0) {
                sizeText.setText("");
                priceText.setText("");
            } else {
                sizeText.setText(drink.size + "");
                priceText.setText(drink.price + "");
            }
            if (drink.name.equals(getString(R.string.prices_beer)) || drink.name.equals(getString(R.string.prices_long))) {
                nameText.setEnabled(false);
            } else nameText.setEnabled(true);
        } else {
            title.setText(getString(R.string.prices_add));
            nameText.setText("");
            sizeText.setText("");
            priceText.setText("");
            nameText.requestFocus();
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        ready_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                double size;
                double price;
                try {
                    size = Double.parseDouble(sizeText.getText().toString());
                } catch (NumberFormatException ex) {
                    showValueError(getString(R.string.prices_error_size));
                    return;
                }
                if (size == 0 || size > 5) {
                    showValueError(getString(R.string.prices_error_size));
                    return;
                }
                try {
                    price = Double.parseDouble(priceText.getText().toString());
                } catch (NumberFormatException ex) {
                    showValueError(getString(R.string.prices_error_price));
                    return;
                }
                if (price == 0 || price > 100) {
                    showValueError(getString(R.string.prices_error_price));
                    return;
                }
                if (StringUtils.isEmptyString(name)) {
                    showValueError(getString(R.string.prices_error_name));
                    return;
                }

                Drink drinkToSave;
                if (drink != null) {
                    drinkToSave = drink.clone();
                } else {
                    drinkToSave = new Drink();
                }

                DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.getDefault());
                decimalSymbol.setDecimalSeparator('.');
                DecimalFormat df = new DecimalFormat("#.#", decimalSymbol);

                drinkToSave.name = name;
                drinkToSave.size = Double.valueOf(df.format(size));
                drinkToSave.price = Double.valueOf(df.format(price));
                drinkToSave.updateTime = System.currentTimeMillis();

                Helper.hideKeyBoard(nameText);
                dismiss();
                if (drink != null && drink.updateTime > 0) {
                    mListener.onDrinkUpdated(drinkToSave);
                } else {
                    mListener.onDrinkAdded(drinkToSave);
                }
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.hideKeyBoard(nameText);
                mListener.onDrinkDialogCancel();
            }
        });

        return v;
    }

    private void showValueError(String errorText) {
        InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.prices_error_title), errorText);
        dialog.show(getFragmentManager(), "Virheellinen arvo");
    }

    public interface AddDrinkDialogCloseListener {
        void onDrinkUpdated(Drink drink);

        void onDrinkAdded(Drink drink);

        void onDrinkDialogCancel();
    }
}
