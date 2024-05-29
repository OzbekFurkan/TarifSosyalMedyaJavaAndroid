package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.recipeapp.databinding.ActivityLogRegBinding;
import com.example.recipeapp.ui.register.fragment_register;

public class LogRegActivity extends AppCompatActivity {
    private ActivityLogRegBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogRegBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
    public void GoToRegister(View view)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, new fragment_register());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void GoToLogin(View view)
    {
        popStackToLogin();
    }
    public void popStackToLogin()
    {
        getSupportFragmentManager().popBackStack();
    }
}