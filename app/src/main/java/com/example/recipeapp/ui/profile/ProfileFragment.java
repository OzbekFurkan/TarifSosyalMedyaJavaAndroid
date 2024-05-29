package com.example.recipeapp.ui.profile;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipeapp.MainActivity;
import com.example.recipeapp.R;
import com.example.recipeapp.databinding.FragmentProfileBinding;
import com.example.recipeapp.feature.Content;
import com.example.recipeapp.feature.ContentAdapter;
import com.example.recipeapp.feature.DbOperations;
import com.example.recipeapp.feature.ProfileCallBack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

public class ProfileFragment extends Fragment {

    //ui
    ImageView profile_imageview;
    TextView username;
    TextView about;
    TextView post_counter;
    RecyclerView p_contents_rec;

    //data


    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textProfile;
        profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitializeVariablesUI(view);
        HandleProfileUI();
    }

    private void InitializeVariablesUI(View view)
    {
        profile_imageview = (ImageView) view.findViewById(R.id.my_profile_image);
        username = (TextView) view.findViewById(R.id.my_profile_name);
        about = (TextView) view.findViewById(R.id.my_profile_about);
        post_counter = (TextView) view.findViewById(R.id.my_profile_post_amount);
        p_contents_rec = (RecyclerView) view.findViewById(R.id.my_contents_rec);
    }

    private void HandleProfileUI()
    {
        DbOperations dbOperations = new DbOperations();
        dbOperations.GetProfile(((MainActivity) getActivity()).user_id, new ProfileCallBack() {
            @Override
            public void onProfileDataReady(Uri img, String usr, String abt, long p_counter, ArrayList<Content> p_contents) {
                Picasso.get().load(img).resize(100,100).into(profile_imageview);
                username.setText(usr);
                about.setText(abt);
                post_counter.setText("posts: "+p_counter);
                SetPostedContentsAdapter(p_contents);
            }
        });
    }

    private void SetPostedContentsAdapter(ArrayList<Content> p_contents)
    {
        Log.d("ben", "content sayısı: "+p_contents.size());
        p_contents_rec.setLayoutManager(new LinearLayoutManager(getContext()));
        ContentAdapter contentAdapter = new ContentAdapter(getContext(), p_contents);
        p_contents_rec.setAdapter(contentAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}