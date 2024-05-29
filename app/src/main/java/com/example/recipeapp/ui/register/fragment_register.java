package com.example.recipeapp.ui.register;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.recipeapp.LogRegActivity;
import com.example.recipeapp.R;
import com.example.recipeapp.feature.DbOperations;
import com.example.recipeapp.feature.User;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;


public class fragment_register extends Fragment {

    EditText reg_usr;
    EditText reg_pass;
    EditText reg_mail;
    EditText reg_about;
    ImageButton reg_img;
    Bitmap selectedImage;
    Uri imageData;
    Button reg_btn;

    private ActivityResultLauncher<String> activityResultLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncher2;

    public fragment_register() {
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
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitializeVariableUI(view);
        SetLauncher(view);
        SelectImageOnGallery();
        RegisterApp();
    }

    private void InitializeVariableUI(View view)
    {
        reg_usr = (EditText) view.findViewById(R.id.reg_usr);
        reg_mail = (EditText) view.findViewById(R.id.reg_mail);
        reg_pass = (EditText) view.findViewById(R.id.reg_pass);
        reg_about = (EditText) view.findViewById(R.id.reg_about);
        reg_img = (ImageButton) view.findViewById(R.id.reg_image);
        reg_btn = (Button) view.findViewById(R.id.reg_btn);
    }

    public void SetLauncher(View view)
    {
        activityResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK)
                {
                    Intent intentFromResult = result.getData();
                    if(intentFromResult!=null)
                    {
                        imageData = intentFromResult.getData();
                        try {
                            if(Build.VERSION.SDK_INT >= 28)
                            {
                                ImageDecoder.Source source = ImageDecoder.createSource(view.getContext().getContentResolver(), imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                reg_img.setImageBitmap(selectedImage);
                            }
                            else
                            {
                                selectedImage = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), imageData);
                                reg_img.setImageBitmap(selectedImage);
                            }

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result)
                {
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher2.launch(intentToGallery);
                }
                else
                {
                    Toast.makeText(view.getContext(), "Permission needed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void SelectImageOnGallery()
    {
        reg_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)
                {
                    if(shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES))
                    {
                        Snackbar.make(v, "Permission needed for gallery!", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Ask for permission
                                activityResultLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                            }
                        }).show();
                    }
                    else
                    {
                        //Ask for permission
                        activityResultLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                    }
                }
                else
                {
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher2.launch(intentToGallery);
                    //Go to gallery
                }
            }
        });
    }

    public void RegisterApp()
    {
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(reg_usr.getText().toString(),
                        reg_about.getText().toString(), imageData, 0);
                DbOperations dbOperations = new DbOperations();
                dbOperations.create_db_auth(reg_mail.getText().toString(),
                        reg_pass.getText().toString(), user);
                ((LogRegActivity) getActivity()).popStackToLogin();
            }
        });
    }


}