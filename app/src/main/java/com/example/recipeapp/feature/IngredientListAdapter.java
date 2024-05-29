package com.example.recipeapp.feature;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;

import java.util.ArrayList;
import java.util.List;

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.IngredientViewHolder> {

    Context context;
    ArrayList<Ingredient> ingredients;
    public IngredientListAdapter(Context context, ArrayList<Ingredient> ingredients)
    {
        this.context=context;
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngredientListAdapter.IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_ingredient_lw_row, parent, false);
        return new IngredientListAdapter.IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientListAdapter.IngredientViewHolder holder, int position) {
        holder.ing_name.setText(ingredients.get(position).ing_name);
        holder.ing_price.setText(ingredients.get(position).price);
        holder.ing_seller.setText(ingredients.get(position).seller);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder{
        TextView ing_name;
        TextView ing_price;
        TextView ing_seller;
        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ing_name = itemView.findViewById(R.id.d_ing_name);
            ing_price = itemView.findViewById(R.id.d_ing_price);
            ing_seller = itemView.findViewById(R.id.d_ing_seller);
        }
    }

/*
    List<Ingredient> ingredients;
    public IngredientListAdapter(@NonNull Context context, List<Ingredient> ingredients) {
        super(context, R.layout.detail_ingredient_lw_row, ingredients);
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Ingredient ingredient = getItem(position);
        if(convertView!=null) {
            convertView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.detail_ingredient_lw_row, parent, false);

            TextView ing_name = convertView.findViewById(R.id.d_ing_name);
            TextView ing_price = convertView.findViewById(R.id.d_ing_price);
            TextView ing_seller = convertView.findViewById(R.id.d_ing_seller);

            ing_name.setText(ingredient.ing_name);
            ing_price.setText(ingredient.price);
            ing_seller.setText(ingredient.seller);
        }
        return convertView;
    }*/
}
