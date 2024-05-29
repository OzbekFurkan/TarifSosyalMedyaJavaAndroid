package com.example.recipeapp.feature;

import android.net.Uri;

import java.util.ArrayList;

public interface ProfileCallBack {
    public void onProfileDataReady(Uri img, String usr, String abt, long p_counter, ArrayList<Content> p_contents);
}
