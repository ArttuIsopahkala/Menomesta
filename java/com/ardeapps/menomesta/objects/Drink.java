package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Arttu on 8.1.2017.
 */
public class Drink {
    @Exclude
    public static Comparator<Drink> customDrinkComparator = new Comparator<Drink>() {
        @Override
        public int compare(final Drink entry1, final Drink entry2) {
            final double price1 = entry1.price;
            final double price2 = entry2.price;
            // jos vain olut ja lonkero ja kumpaakaan ei ole muutettu
            if (price1 == 0 && price2 == 0)
                return 0;

            if (price1 == price2) {
                if (entry1.size == entry2.size)
                    return entry1.barId.compareTo(entry2.barId);
                else
                    return entry1.size > entry2.size ? -1 : 1;
            }
            return price1 < price2 ? -1 : 1;
        }
    };
    public String drinkId;
    public String barId;
    public String name;
    public double size;
    public double price;
    public long updateTime;

    public Drink() {
        // Default constructor required for calls to DataSnapshot.getValue(Drink.class)
    }

    @Exclude
    public static ArrayList<Drink> setDrink(ArrayList<Drink> drinks, String drinkId, Drink drink) {
        if (drinks == null)
            drinks = new ArrayList<>();

        boolean exists = false;
        for (int index = 0; index < drinks.size(); index++) {
            if (drinks.get(index).drinkId.equals(drinkId)) {
                exists = true;
                if (drink == null) {
                    drinks.remove(index);
                } else {
                    drinks.set(index, drink);
                }
                break;
            }
        }
        if (!exists && drink != null) {
            drinks.add(drink);
        }
        return drinks;
    }

    @Exclude
    public Drink clone() {
        Drink drink = new Drink();
        drink.drinkId = this.drinkId;
        drink.barId = this.barId;
        drink.name = this.name;
        drink.size = this.size;
        drink.price = this.price;
        drink.updateTime = this.updateTime;
        return drink;
    }
}
