package com.example.recipeapp.ui.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.FragmentSearchBinding;
import com.example.recipeapp.feature.Content;
import com.example.recipeapp.feature.ContentAdapter;
import com.example.recipeapp.feature.ContentCallback;
import com.example.recipeapp.feature.DbOperations;
import com.example.recipeapp.feature.User;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    //ui
    SearchView searchView;
    RecyclerView recyclerView;

    //data
    ArrayList<Content> contents;
    ContentAdapter contentAdapter;

    private FragmentSearchBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSearch;
        searchViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitializeVariablesUI(view);
        GetAllContents();
        SearchContent();
    }

    private void InitializeVariablesUI(View view)
    {
        searchView = view.findViewById(R.id.search_content);
        recyclerView = view.findViewById(R.id.search_content_rec);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contentAdapter = new ContentAdapter(getContext(), contents);
        recyclerView.setAdapter(contentAdapter);

    }

    private void SearchContent()
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(contentAdapter!=null)
                    contentAdapter.getFilter().filter(newText);

                return false;
            }
        });
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}