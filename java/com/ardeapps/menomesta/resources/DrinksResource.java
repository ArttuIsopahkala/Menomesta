package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetDrinkListHandler;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.services.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by Arttu on 19.1.2018.
 */

public class DrinksResource extends FirebaseService {
    private static DrinksResource instance;
    private static DatabaseReference database;

    public static DrinksResource getInstance() {
        if (instance == null) {
            instance = new DrinksResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(DRINKS);
        return instance;
    }

    public void addDrink(String barId, Drink drink, final AddSuccessListener handler) {
        drink.drinkId = database.child(barId).push().getKey();
        addData(database.child(barId).child(drink.drinkId), drink, handler);
    }

    public void editDrink(String barId, Drink drink, final EditSuccessListener handler) {
        editData(database.child(barId).child(drink.drinkId), drink, handler);
    }

    public void getAllDrinks(final GetDrinkListHandler handler) {
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                ArrayList<Drink> drinks = new ArrayList<>();
                for (DataSnapshot bar : dataSnapshot.getChildren()) {
                    for (DataSnapshot object : bar.getChildren()) {
                        Drink drink = object.getValue(Drink.class);
                        drinks.add(drink);
                    }
                }
                handler.onDrinkListLoaded(drinks);
            }
        });
    }

    public void getDrinks(String barId, final GetDrinkListHandler handler) {
        getData(database.child(barId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                ArrayList<Drink> drinks = new ArrayList<>();
                for (DataSnapshot object : dataSnapshot.getChildren()) {
                    Drink drink = object.getValue(Drink.class);
                    drinks.add(drink);
                }
                handler.onDrinkListLoaded(drinks);
            }
        });
    }
}
