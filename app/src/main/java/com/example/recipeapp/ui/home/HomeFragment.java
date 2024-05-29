package com.example.recipeapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.FragmentHomeBinding;
import com.example.recipeapp.feature.Content;
import com.example.recipeapp.feature.ContentAdapter;
import com.example.recipeapp.feature.ContentCallback;
import com.example.recipeapp.feature.DbOperations;
import com.example.recipeapp.feature.User;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    //ui
    RecyclerView content_rec;

    //data
    ArrayList<Content> contents;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitializeVariablesUI(view);
        GetAllContents();
    }

    private void InitializeVariablesUI(View view)
    {
            content_rec = view.findViewById(R.id.con_rec);
            contents = new ArrayList<>();
    }

    private void GetAllContents()
    {
        DbOperations dbOperations = new DbOperations();
        dbOperations.GetAllContents(new ContentCallback() {
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
        content_rec.setLayoutManager(new LinearLayoutManager(getContext()));
        ContentAdapter contentAdapter = new ContentAdapter(getContext(), contents);
        content_rec.setAdapter(contentAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}