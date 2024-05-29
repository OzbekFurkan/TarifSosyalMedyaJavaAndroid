package com.example.recipeapp.feature;

import java.util.ArrayList;

public interface AddContentCallBack {
    public void onIngredientsRetrieved(ArrayList<Ingredient> ingredients);
    public void onAddContentCompleted();
}
