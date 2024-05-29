package com.example.recipeapp.ui.add_content;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddContentViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AddContentViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Add Content");
    }

    public LiveData<String> getText() {
        return mText;
    }
}