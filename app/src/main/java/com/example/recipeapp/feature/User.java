package com.example.recipeapp.feature;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class User {
    String usr_id;
    String username;
    String about;
    Uri imageData;
    long post_counter;
    ArrayList<String> posted_contents;
    ArrayList<String> favorite_contents;

    //for retrieving data
    public User(String usr_id, String username, String about, Uri usr_image,
                long post_counter, ArrayList<String> posted_contents, ArrayList<String> favorite_contents)
    {
        this.usr_id = usr_id;
        this.imageData = usr_image;
        this.username = username;
        this.about = about;
        this.post_counter = post_counter;
        this.posted_contents = posted_contents;
        this.favorite_contents = favorite_contents;
    }
    //for putting data
    public User(String username, String about, Uri usr_image_bit, long post_counter)
    {
        this.imageData= usr_image_bit;
        this.username = username;
        this.about = about;
        this.post_counter = post_counter;
        this.posted_contents = new ArrayList<>();
        this.favorite_contents = new ArrayList<>();
    }

}