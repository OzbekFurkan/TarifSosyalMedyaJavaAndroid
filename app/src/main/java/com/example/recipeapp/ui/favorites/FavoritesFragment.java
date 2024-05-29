package com.example.recipeapp.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.FragmentFavoritesBinding;
import com.example.recipeapp.feature.Content;
import com.example.recipeapp.feature.ContentAdapter;
import com.example.recipeapp.feature.ContentCallback;
import com.example.recipeapp.feature.DbOperations;
import com.example.recipeapp.feature.User;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    //ui
    RecyclerView fav_con_rec;

    //data
    ArrayList<Content> contents;

    private FragmentFavoritesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FavoritesViewModel favoritesViewModel =
                new ViewModelProvider(this).get(FavoritesViewModel.class);

        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        favoritesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitializeVariablesUI(view);
        GetFavoriteContents();
    }

    private void InitializeVariablesUI(View view)
    {
        fav_con_rec = view.findViewById(R.id.favorite_content_rec);
        contents = new ArrayList<>();
    }

    private void GetFavoriteContents()
    {
        DbOperations dbOperations = new DbOperations();
        dbOperations.GetFavoriteContents(new ContentCallback() {
            @Override
            public void onContentRetrieved(ArrayList<Content> contents) {
                SetRecyclerviewAdapter(contents);
            }

            @Override
            public void onUserRetrieved(User user) {

            }
        });
    }

    private void SetRecyclerviewAdapter(ArrayList<Content> contents)
    {
        this.contents = contents;
        fav_con_rec.setLayoutManager(new LinearLayoutManager(getContext()));
        ContentAdapter contentAdapter = new ContentAdapter(getContext(), contents);
        fav_con_rec.setAdapter(contentAdapter);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}