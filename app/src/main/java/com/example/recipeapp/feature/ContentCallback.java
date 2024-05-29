package com.example.recipeapp.feature;

import java.util.ArrayList;

public interface ContentCallback
{
    public void onContentRetrieved(ArrayList<Content> contents);
    public void onUserRetrieved(User user);
}
