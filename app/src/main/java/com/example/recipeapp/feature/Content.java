package com.example.recipeapp.feature;

import android.net.Uri;

import java.util.Date;
import java.util.List;

public class Content {
    String recipe_id;
    Date publish_date;
    Uri recipe_image;
    String recipe_name;
    String recipe_desc;
    List<Ingredient> ingredients;

    //For Adding Data
    public Content(Uri recipe_image, String recipe_name,
                   String recipe_desc, List<Ingredient> ingredients) {
        this.recipe_image = recipe_image;
        this.recipe_name = recipe_name;
        this.recipe_desc = recipe_desc;
        this.ingredients = ingredients;
    }

    //For Retrieving Data
    public Content(String recipe_id, Date publish_date, Uri recipe_image,
                   String recipe_name, String recipe_desc, List<Ingredient> ingredients) {
        this.recipe_id = recipe_id;
        this.publish_date = publish_date;
        this.recipe_image = recipe_image;
        this.recipe_name = recipe_name;
        this.recipe_desc = recipe_desc;
        this.ingredients = ingredients;
    }
}
