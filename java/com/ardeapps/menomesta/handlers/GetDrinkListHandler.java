package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.Drink;

import java.util.ArrayList;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetDrinkListHandler {
    void onDrinkListLoaded(ArrayList<Drink> drinks);
}
