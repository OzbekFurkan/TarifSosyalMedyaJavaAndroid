package com.example.recipeapp.feature;

import android.util.Log;

import com.google.android.gms.tasks.Task;

import java.util.concurrent.ExecutionException;

public class Ingredient {
    String ing_id;
    public String ing_name;
    String price_link;

    //data from price_link will be assigned to these 2 variables
    String seller="";
    String price;

    //For Retrieving
    public Ingredient(String ing_id, String ing_name, String price_link) {
        this.ing_id = ing_id;
        this.ing_name = ing_name;
        this.price_link = price_link;
    }

    public Task<Void> SetLowestPriceAndSeller()
    {

        try {
            GetPriceAndSeller getPriceAndSeller = new GetPriceAndSeller();
            return getPriceAndSeller.execute(this).get();


        }catch (InterruptedException interruptedException)
        {
            interruptedException.printStackTrace();
        }catch (ExecutionException executionException)
        {
            executionException.printStackTrace();
        }
        return null;
    }
}
