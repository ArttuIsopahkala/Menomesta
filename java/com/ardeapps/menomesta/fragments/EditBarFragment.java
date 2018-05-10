package com.ardeapps.menomesta.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.FbRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.adapters.DrinkListAdapter;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.BarDetails;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.resources.BarDetailsResource;
import com.ardeapps.menomesta.resources.BarsResource;
import com.ardeapps.menomesta.resources.DrinksResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.DrinkHolder;
import com.ardeapps.menomesta.views.IconView;

import java.util.ArrayList;
import java.util.Collections;

public class EditBarFragment extends Fragment implements AddDrinkDialogFragment.AddDrinkDialogCloseListener, DrinkListAdapter.DrinkListListener,
        AddValueDialogFragment.AddValueDialogCloseListener {

    Listener mListener = null;
    ArrayList<Drink> drinks;
    Bar bar;

    RelativeLayout detailsContent;
    ListView drinkListView;
    TextView addDrink;
    TextView titleText;
    TextView ageValueFriday;
    TextView ageValueSaturday;
    TextView ageUpdatedFriday;
    TextView ageUpdatedSaturday;
    TextView entranceValue;
    TextView entranceUpdated;
    AddDrinkDialogFragment drinkDialog;
    IconView edit_icon_age_friday;
    IconView edit_icon_age_saturday;
    IconView edit_icon_entrance;
    AddValueDialogFragment valueDialog;
    BarDetails barDetails;
    LinearLayout detailsListHeader;
    CheckBox food_checkBox;
    DrinkHolder beerHolder;
    DrinkHolder longDrinkHolder;
    View beerItem;
    View longDrinkItem;
    boolean isFacebookBar;
    AppRes appRes = (AppRes) AppRes.getContext();

    public void setListener(Listener l) {
        mListener = l;
    }

    public void setDrinks(ArrayList<Drink> drinks) {
        this.drinks = drinks;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public void setBarDetails(BarDetails barDetails) {
        this.barDetails = barDetails;
    }

    public void setIsFacebookBar(boolean isFacebookBar) {
        this.isFacebookBar = isFacebookBar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_bar, container, false);

        detailsContent = (RelativeLayout) v.findViewById(R.id.detailsContent);
        beerItem = v.findViewById(R.id.beerItem);
        beerHolder = new DrinkHolder();
        beerHolder.nameText = (TextView) beerItem.findViewById(R.id.name);
        beerHolder.sizeText = (TextView) beerItem.findViewById(R.id.size);
        beerHolder.priceText = (TextView) beerItem.findViewById(R.id.price);
        beerHolder.updatedText = (TextView) beerItem.findViewById(R.id.updated);
        beerHolder.edit_icon = (IconView) beerItem.findViewById(R.id.edit_icon);

        longDrinkItem = v.findViewById(R.id.longDrinkItem);
        longDrinkHolder = new DrinkHolder();
        longDrinkHolder.nameText = (TextView) longDrinkItem.findViewById(R.id.name);
        longDrinkHolder.sizeText = (TextView) longDrinkItem.findViewById(R.id.size);
        longDrinkHolder.priceText = (TextView) longDrinkItem.findViewById(R.id.price);
        longDrinkHolder.updatedText = (TextView) longDrinkItem.findViewById(R.id.updated);
        longDrinkHolder.edit_icon = (IconView) longDrinkItem.findViewById(R.id.edit_icon);

        drinkListView = (ListView) v.findViewById(R.id.drinksList);
        titleText = (TextView) v.findViewById(R.id.title);
        addDrink = (TextView) v.findViewById(R.id.addDrink);
        edit_icon_age_friday = (IconView) v.findViewById(R.id.edit_icon_age_friday);
        edit_icon_age_saturday = (IconView) v.findViewById(R.id.edit_icon_age_saturday);
        edit_icon_entrance = (IconView) v.findViewById(R.id.edit_icon_entrance);
        ageValueFriday = (TextView) v.findViewById(R.id.ageValueFriday);
        ageValueSaturday = (TextView) v.findViewById(R.id.ageValueSaturday);
        ageUpdatedFriday = (TextView) v.findViewById(R.id.ageUpdatedFriday);
        ageUpdatedSaturday = (TextView) v.findViewById(R.id.ageUpdatedSaturday);
        entranceValue = (TextView) v.findViewById(R.id.entranceValue);
        entranceUpdated = (TextView) v.findViewById(R.id.entranceUpdated);
        detailsListHeader = (LinearLayout) v.findViewById(R.id.detailsListHeader);
        food_checkBox = (CheckBox) v.findViewById(R.id.food_checkBox);

        titleText.setText(FbRes.getBarDetail(bar.barId) != null ? FbRes.getBarDetail(bar.barId).name : bar.name);
        food_checkBox.setChecked(bar.isFoodPlace);

        if(isFacebookBar) {
            detailsContent.setVisibility(View.GONE);
        } else {
            detailsContent.setVisibility(View.VISIBLE);
            updateBarDetails();
            edit_icon_age_friday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    valueDialog = new AddValueDialogFragment();
                    valueDialog.setBarDetails(barDetails);
                    valueDialog.setEditType(AddValueDialogFragment.EditType.AGE_FRIDAY);
                    valueDialog.show(getFragmentManager(), "Arvon muokkaus");
                    valueDialog.setListener(EditBarFragment.this);
                }
            });

            edit_icon_age_saturday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    valueDialog = new AddValueDialogFragment();
                    valueDialog.setBarDetails(barDetails);
                    valueDialog.setEditType(AddValueDialogFragment.EditType.AGE_SATURDAY);
                    valueDialog.show(getFragmentManager(), "Arvon muokkaus");
                    valueDialog.setListener(EditBarFragment.this);
                }
            });

            edit_icon_entrance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    valueDialog = new AddValueDialogFragment();
                    valueDialog.setBarDetails(barDetails);
                    valueDialog.setEditType(AddValueDialogFragment.EditType.ENTRANCE);
                    valueDialog.show(getFragmentManager(), "Arvon muokkaus");
                    valueDialog.setListener(EditBarFragment.this);
                }
            });
            food_checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Bar barToSave = bar.clone();
                    barToSave.isFoodPlace = ((CheckBox) v).isChecked();

                    BarsResource.getInstance().editBar(barToSave, new EditSuccessListener() {
                        @Override
                        public void onEditSuccess() {
                            appRes.setBar(barToSave.barId, barToSave);
                            bar = barToSave;

                            updateBarDetails();
                            mListener.onBarUpdated(barToSave);
                        }
                    });
                }
            });
        }

        updateDrinks();
        addDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drinkDialog = new AddDrinkDialogFragment();
                drinkDialog.setDrink(null);
                drinkDialog.show(getFragmentManager(), "Juoman muokkaus");
                drinkDialog.setListener(EditBarFragment.this);
            }
        });

        return v;
    }

    public void updateBarDetails() {
        detailsListHeader.setVisibility(View.GONE);
        if (barDetails != null) {
            if (barDetails.ageLimitUpdated != 0) {
                ageValueFriday.setVisibility(View.VISIBLE);
                ageUpdatedFriday.setVisibility(View.VISIBLE);
                detailsListHeader.setVisibility(View.VISIBLE);
                ageValueFriday.setText(getString(R.string.age, String.valueOf(barDetails.ageLimit)));
                ageUpdatedFriday.setText(StringUtils.getDateText(barDetails.ageLimitUpdated));
            } else {
                ageValueFriday.setVisibility(View.INVISIBLE);
                ageUpdatedFriday.setVisibility(View.INVISIBLE);
            }

            if (barDetails.ageLimitSaturdayUpdated != 0) {
                ageValueSaturday.setVisibility(View.VISIBLE);
                ageUpdatedSaturday.setVisibility(View.VISIBLE);
                detailsListHeader.setVisibility(View.VISIBLE);
                ageValueSaturday.setText(getString(R.string.age, String.valueOf(barDetails.ageLimitSaturday)));
                ageUpdatedSaturday.setText(StringUtils.getDateText(barDetails.ageLimitSaturdayUpdated));
            } else {
                ageValueSaturday.setVisibility(View.INVISIBLE);
                ageUpdatedSaturday.setVisibility(View.INVISIBLE);
            }

            if (barDetails.entranceUpdated != 0) {
                entranceValue.setVisibility(View.VISIBLE);
                entranceUpdated.setVisibility(View.VISIBLE);
                detailsListHeader.setVisibility(View.VISIBLE);
                entranceValue.setText(getString(R.string.price, String.valueOf(barDetails.entrancePrice)));
                entranceUpdated.setText(StringUtils.getDateText(barDetails.entranceUpdated));
            } else {
                entranceValue.setVisibility(View.INVISIBLE);
                entranceUpdated.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void updateDrinks() {
        boolean beerFound = false;
        boolean longFound = false;
        for (Drink drink : drinks) {
            if (!beerFound || !longFound) {
                if (drink.name.equals(getString(R.string.prices_beer))) {
                    beerFound = true;
                }
                if (drink.name.equals(getString(R.string.prices_long))) {
                    longFound = true;
                }
            } else break;
        }

        if(!beerFound) {
            beerItem.setVisibility(View.VISIBLE);
            beerHolder.nameText.setText(getString(R.string.prices_beer));
            beerHolder.sizeText.setText("");
            beerHolder.priceText.setText("");
            beerHolder.updatedText.setText("");
            beerHolder.edit_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Drink beer = new Drink();
                    beer.name = getString(R.string.prices_beer);
                    onEditDrinkClick(beer);
                }
            });
        } else {
            beerItem.setVisibility(View.GONE);
        }

        if(!longFound) {
            longDrinkItem.setVisibility(View.VISIBLE);
            longDrinkHolder.nameText.setText(getString(R.string.prices_long));
            longDrinkHolder.sizeText.setText("");
            longDrinkHolder.priceText.setText("");
            longDrinkHolder.updatedText.setText("");
            longDrinkHolder.edit_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Drink longDrink = new Drink();
                    longDrink.name = getString(R.string.prices_long);
                    onEditDrinkClick(longDrink);
                }
            });
        } else {
            longDrinkItem.setVisibility(View.GONE);
        }

        Collections.sort(drinks, Drink.customDrinkComparator);
        DrinkListAdapter drinkAdapter = new DrinkListAdapter(getActivity());
        drinkAdapter.setBar(bar);
        drinkAdapter.setDrinks(drinks);
        drinkAdapter.setListener(this);
        drinkListView.setAdapter(drinkAdapter);
    }

    @Override
    public void onDrinkUpdated(final Drink drink) {
        DrinksResource.getInstance().editDrink(bar.barId, drink, new EditSuccessListener() {
            @Override
            public void onEditSuccess() {
                drinks = Drink.setDrink(drinks, drink.drinkId, drink);
                updateDrinks();

                mListener.onDrinksUpdated(drinks, isFacebookBar);
                UsersResource.getInstance().updateUserKarma(KarmaPoints.DRINK_UPDATED, true);
            }
        });
    }

    @Override
    public void onDrinkAdded(final Drink drink) {
        drink.barId = bar.barId;
        // Uusi juoma
        for (Drink object : drinks) {
            if (object.name.equals(drink.name)) {
                InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.prices_error_title), getString(R.string.prices_error_exists));
                dialog.show(getFragmentManager(), "Juoma löytyy jo");
                return;
            }
        }
        DrinksResource.getInstance().addDrink(bar.barId, drink, new AddSuccessListener() {
            @Override
            public void onAddSuccess(String id) {
                drink.drinkId = id;
                drinks = Drink.setDrink(drinks, drink.drinkId, drink);

                updateDrinks();

                mListener.onDrinksUpdated(drinks, isFacebookBar);
                UsersResource.getInstance().updateUserKarma(KarmaPoints.DRINK_ADDED, true);
            }
        });
    }

    @Override
    public void onDrinkDialogCancel() {
        drinkDialog.dismiss();
    }

    @Override
    public void onEditDrinkClick(Drink drink) {
        drinkDialog = new AddDrinkDialogFragment();
        drinkDialog.setDrink(drink);
        drinkDialog.show(getFragmentManager(), "Juoman muokkaus");
        drinkDialog.setListener(EditBarFragment.this);
    }

    @Override
    public void onValueUpdated(final BarDetails barDetails) {
        valueDialog.dismiss();
        barDetails.barId = bar.barId;

        BarDetailsResource.getInstance().editBarDetails(bar.barId, barDetails, new EditSuccessListener() {
            @Override
            public void onEditSuccess() {
                setBarDetails(barDetails);
                mListener.onBarDetailsUpdated(barDetails);

                UsersResource.getInstance().updateUserKarma(KarmaPoints.DETAILS_UPDATED, true);
            }
        });
    }

    @Override
    public void onValueDialogCancel() {
        valueDialog.dismiss();
    }

    public interface Listener {
        void onDrinksUpdated(ArrayList<Drink> drinks, boolean isFacebookBar);

        void onBarDetailsUpdated(BarDetails barDetails);

        void onBarUpdated(Bar bar);
    }
}
