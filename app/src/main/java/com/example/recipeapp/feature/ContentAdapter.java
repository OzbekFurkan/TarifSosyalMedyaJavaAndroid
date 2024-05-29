package com.example.recipeapp.feature;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> implements Filterable {

    Context context;
    User user;
    ArrayList<Content> contents;

    //detail popup
    Dialog mDialog;
    ImageButton btn_close;
    ImageView pop_image;
    TextView pop_name;
    TextView pop_desc;
    RecyclerView pop_lw;

    public ContentAdapter(Context context, ArrayList<Content> contents)
    {
        this.context = context;
        this.contents = contents;
        mDialog = new Dialog(context);
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_content, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position)
    {
        GetUserByContentId(contents.get(position).recipe_id, holder, position);
        AddFavorites(holder, position);
        ShowDetails(holder, position);
    }

    private void GetUserByContentId(String content_id, ContentViewHolder holder, int position)
    {
        DbOperations dbOperations = new DbOperations();
        dbOperations.GetUserByContentId(content_id, new ContentCallback() {
            @Override
            public void onContentRetrieved(ArrayList<Content> contents) {

            }

            @Override
            public void onUserRetrieved(User user) {
                SetValuesOnUI(holder, position, user);
            }
        });
    }

    private void SetValuesOnUI(ContentViewHolder holder, int position, User user)
    {
        this.user=user;
        Picasso.get().load(user.imageData).resize(30,30).into(holder.con_profile_image);
        holder.con_profile_name.setText(user.username);
        holder.con_date.setText(contents.get(position).publish_date.toString());
        Picasso.get().load(contents.get(position).recipe_image)
                .resize(200,200).into(holder.con_image);
        holder.con_name.setText(contents.get(position).recipe_name);
        holder.con_desc.setText(contents.get(position).recipe_desc);
    }

    private void AddFavorites(ContentViewHolder holder, int pos)
    {
        holder.add_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbOperations dbOperations = new DbOperations();
                dbOperations.AddFavorites(contents.get(pos).recipe_id);
            }
        });
    }

    private void ShowDetails(ContentViewHolder holder, int pos)
    {
        holder.show_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popup settings
                mDialog.setContentView(R.layout.content_detail_popup);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(mDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                mDialog.show();
                mDialog.getWindow().setAttributes(lp);
                //popup ui items assigned
                btn_close = (ImageButton) mDialog.findViewById(R.id.back_btn);
                pop_image = (ImageView) mDialog.findViewById(R.id.d_image);
                pop_name = (TextView) mDialog.findViewById(R.id.d_name);
                pop_desc = (TextView) mDialog.findViewById(R.id.d_desc);
                pop_lw = (RecyclerView) mDialog.findViewById(R.id.lw_ing_price);
                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                //popup ui values assigned
                Picasso.get().load(contents.get(pos).recipe_image).into(pop_image);
                pop_name.setText(contents.get(pos).recipe_name);
                pop_desc.setText(contents.get(pos).recipe_desc);
                List<Task<Void>> tasks = new ArrayList<>();
                for(Ingredient ing:contents.get(pos).ingredients)
                {
                    Task<Void> task = ing.SetLowestPriceAndSeller();
                    tasks.add(task);
                }

                Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> objects) {
                        pop_lw.setLayoutManager(new LinearLayoutManager(context));
                        IngredientListAdapter ingredientAdapter = new IngredientListAdapter(context, (ArrayList<Ingredient>) contents.get(pos).ingredients);
                        pop_lw.setAdapter(ingredientAdapter);
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount()
    {
        return contents.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if(constraint==null || constraint.length()==0)
                {
                    filterResults.values = contents;
                    filterResults.count = contents.size();
                }
                else
                {
                    String searchStr = constraint.toString().toLowerCase();
                    ArrayList<Content> filtered_contents = new ArrayList<>();
                    for(Content content:contents)
                    {
                        if(content.recipe_name.toLowerCase().contains(searchStr) ||
                                content.recipe_desc.toLowerCase().contains(searchStr))
                        {
                            filtered_contents.add(content);
                        }
                    }
                    filterResults.values = filtered_contents;
                    filterResults.count = filtered_contents.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contents = (ArrayList<Content>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder
    {
        ImageView con_profile_image;
        TextView con_profile_name;
        TextView con_date;
        ImageView con_image;
        TextView con_name;
        TextView con_desc;
        ImageButton add_favorites;
        ImageButton show_detail;
        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            con_profile_image = (ImageView) itemView.findViewById(R.id.content_profile_image);
            con_profile_name = (TextView) itemView.findViewById(R.id.content_profile_name);
            con_date = (TextView) itemView.findViewById(R.id.content_publish_date);
            con_image = (ImageView) itemView.findViewById(R.id.content_image);
            con_name = (TextView) itemView.findViewById(R.id.content_name);
            con_desc = (TextView) itemView.findViewById(R.id.content_desc);
            add_favorites = (ImageButton) itemView.findViewById(R.id.btn_add_favorites);
            show_detail = (ImageButton) itemView.findViewById(R.id.btn_show_detail);
        }
    }

}
