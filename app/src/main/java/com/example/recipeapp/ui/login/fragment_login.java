package com.example.recipeapp.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.recipeapp.MainActivity;
import com.example.recipeapp.R;
import com.example.recipeapp.feature.DbOperations;
import com.example.recipeapp.feature.LoginCallBack;

public class fragment_login extends Fragment {

    EditText email;
    EditText pass;
    Button btn_log;

    public fragment_login() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitializeVariablesUI(view);
        LoginPressed();
    }

    private void InitializeVariablesUI(View view)
    {
        email = (EditText) view.findViewById(R.id.log_mail);
        pass = (EditText) view.findViewById(R.id.log_pass);
        btn_log = (Button) view.findViewById(R.id.btn_login);
    }

    private void LoginPressed()
    {
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbOperations dbOperations = new DbOperations();
                dbOperations.LoginCheck(email.getText().toString(), pass.getText().toString(),
                        new LoginCallBack() {
                            @Override
                            public void onLoginSuccess(String userId) {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.putExtra("user_id", userId);
                                startActivity(intent);
                            }
                        });

            }
        });
    }


}