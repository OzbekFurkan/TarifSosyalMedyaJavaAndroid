package com.example.recipeapp.ui.add_content;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.FragmentAddContentBinding;
import com.example.recipeapp.feature.AddContentCallBack;
import com.example.recipeapp.feature.Content;
import com.example.recipeapp.feature.DbOperations;
import com.example.recipeapp.feature.Ingredient;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.UUID;

public class AddContentFragment extends Fragment implements AdapterView.OnItemSelectedListener, AddContentCallBack {
    //ui
    ImageButton select_image;
    EditText rec_name;
    EditText rec_desc;
    Spinner ing_spin;
    Button add_ing;
    ListView list_ing;
    Button add_rec;
    ImageButton btn_mail_pop;
    Dialog mDialog;

    //data
    Bitmap selectedImage;
    Uri imageData;
    ArrayList<Ingredient> ingredients;
    ArrayList<String> ingredient_names;
    int selected_ing_pos;
    ArrayList<Ingredient> selected_ingredients;
    private ActivityResultLauncher<String> activityResultLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncher2;

    private FragmentAddContentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddContentViewModel addContentViewModel =
                new ViewModelProvider(this).get(AddContentViewModel.class);

        binding = FragmentAddContentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        addContentViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitializeVariablesUI(view);
        SetLauncher(view);
        SelectImageOnGallery();
        GetAllIngredientsForSpinner();
        AddIngredients();
        AddContent();
        OpenMailPopup();
    }

    private void InitializeVariablesUI(View view)
    {
        select_image = (ImageButton) view.findViewById(R.id.btn_select_image);
        rec_name = (EditText) view.findViewById(R.id.r_name);
        rec_desc = (EditText) view.findViewById(R.id.r_desc);
        ing_spin = (Spinner) view.findViewById(R.id.spinner_ingredients);
        add_ing = (Button) view.findViewById(R.id.btn_add_ing);
        list_ing = (ListView) view.findViewById(R.id.lw_ingredients);
        add_rec = (Button) view.findViewById(R.id.add_recipe_btn);
        btn_mail_pop = (ImageButton) view.findViewById(R.id.btn_mail_pop);
        mDialog = new Dialog(view.getContext());
        ingredient_names = new ArrayList<>();
        ingredients = new ArrayList<>();
        selected_ingredients = new ArrayList<>();
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
                                select_image.setImageBitmap(selectedImage);
                            }
                            else
                            {
                                selectedImage = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), imageData);
                                select_image.setImageBitmap(selectedImage);
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
        select_image.setOnClickListener(new View.OnClickListener() {
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

    private void GetAllIngredientsForSpinner()
    {
        DbOperations dbOperations = new DbOperations();
        dbOperations.GetAllIngredients(this);
    }

    private void SetSpinner(ArrayList<Ingredient> ingredients)
    {
        this.ingredients = ingredients;

        for(Ingredient ing:ingredients)
        {
            ingredient_names.add(ing.ing_name);
        }
        ing_spin.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_spinner_item, ingredient_names);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ing_spin.setAdapter(arrayAdapter);
    }

    private void AddIngredients()
    {
        add_ing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ben", "size: "+ingredients.size());
                selected_ingredients.add(ingredients.get(selected_ing_pos));
                SetIngredientsListview();
            }
        });
    }

    private void SetIngredientsListview()
    {
        ArrayList<String> s_i_n = new ArrayList<>();
        for(Ingredient ing:selected_ingredients)
        {
            s_i_n.add(ing.ing_name);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1, s_i_n);
        list_ing.setAdapter(arrayAdapter);
        list_ing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_ingredients.remove(position);
                SetIngredientsListview();
            }
        });
    }

    private void OpenMailPopup()
    {
        btn_mail_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setContentView(R.layout.absent_ingredient_mail_popup);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDialog.show();
                SendMailInPopup();
                CloseMailPopup();
            }
        });
    }

    private void SendMailInPopup()
    {
        Button btn_send_mail = mDialog.findViewById(R.id.btn_send_mail);
        EditText abs_ing_field = mDialog.findViewById(R.id.absent_ing_field);
        btn_send_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
            }
        });
    }

    private void CloseMailPopup()
    {
        ImageButton btn_close_mail_pop = (ImageButton) mDialog.findViewById(R.id.btn_close_mail_pop);
        btn_close_mail_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    private void AddContent()
    {
        add_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbOperations dbOperations = new DbOperations();
                Content content = new Content(imageData, rec_name.getText().toString(),
                        rec_desc.getText().toString(), selected_ingredients);
                dbOperations.AddContent(content, AddContentFragment.this);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selected_ing_pos=position;
        Log.d("ben", "pos: "+position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onIngredientsRetrieved(ArrayList<Ingredient> ingredients) {
        SetSpinner(ingredients);
    }

    @Override
    public void onAddContentCompleted() {
        Toast.makeText(getContext(), "Content Added!", Toast.LENGTH_LONG);
    }
}