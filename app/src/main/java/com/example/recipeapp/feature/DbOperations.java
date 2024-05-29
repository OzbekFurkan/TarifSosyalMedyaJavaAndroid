package com.example.recipeapp.feature;

import android.net.Uri;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.DocumentTransform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DbOperations {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference sf;

    public DbOperations()
    {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sf = FirebaseStorage.getInstance().getReference();
    }

//auth -> image -> firestore
    public void create_db_auth(String email, String password, User user)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User registered successfully
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Set user id
                            String userId = firebaseUser.getUid();

                            //store image in firebase storage
                            String image_name = "user_images/"+ UUID.randomUUID()+".jpg";
                            sf.child(image_name).putFile(user.imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    StorageReference newReference = FirebaseStorage.getInstance().getReference(image_name);
                                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String url = uri.toString();
                                            HashMap<String, Object> userData = new HashMap<>();
                                            userData.put("username", user.username);
                                            userData.put("about", user.about);
                                            userData.put("usr_image", url);
                                            userData.put("post_counter", user.post_counter);
                                            userData.put("posted_contents", user.posted_contents);
                                            userData.put("favorite_contents", user.favorite_contents);
                                            db.collection("Users").document(userId).set(userData);
                                        }
                                    });
                                }
                            });
                        }
                    } else {
                        // Registration failed
                        Log.w("ben", "Error creating user", task.getException());
                    }
                });
    }

    public void LoginCheck(String email, String password, LoginCallBack callback)
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if(firebaseUser != null)
                {
                    callback.onLoginSuccess(firebaseUser.getUid());
                }
                else
                {

                }
            }
            else
            {

            }
        });


    }

    public void GetProfile(String user_id, ProfileCallBack profileCallBack)
    {
        try {
            db.collection("Users").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String, Object> profile_data = documentSnapshot.getData();
                    String username = (String) profile_data.get("username");
                    String about = (String) profile_data.get("about");
                    String user_image = (String) profile_data.get("usr_image");
                    long post_counter = (long) profile_data.get("post_counter");
                    ArrayList<String> posted_contents = (ArrayList<String>) profile_data.get("posted_contents");
                    if(posted_contents==null || posted_contents.isEmpty())
                        return;

                    ArrayList<Content> contents = new ArrayList<>();
                    Log.d("ben", "c_id: "+posted_contents);
                    db.collection("Contents").whereIn(FieldPath.documentId(),posted_contents).get().
                            addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                            {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                                    Log.d("ben", "dönüş sayısı: "+queryDocumentSnapshots.size());
                                    for(DocumentSnapshot ds:queryDocumentSnapshots)
                                    {
                                        Task<QuerySnapshot> task;
                                        String content_id = ds.getId();
                                        Date netDate = ds.getTimestamp("recipe_date").toDate();
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
                                        simpleDateFormat.format(netDate);
                                        Uri img = Uri.parse((String) ds.get("recipe_image"));
                                        String con_name = (String) ds.get("recipe_name");
                                        String con_desc = (String) ds.get("recipe_desc");
                                        ArrayList<String> ing_ids = (ArrayList<String>) ds.get("ingredients");
                                        if(ing_ids==null || ing_ids.isEmpty())
                                            return;

                                        ArrayList<Ingredient> ingredients = new ArrayList<>();

                                        task=db.collection("ingredients").whereIn("ing_id",ing_ids).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots1) {
                                                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots1)
                                                {
                                                    String ing_id = documentSnapshot.getId();
                                                    String ing_name = (String) documentSnapshot.get("ing_name");
                                                    String price_link = (String) documentSnapshot.get("price_link");
                                                    Ingredient ingredient = new Ingredient(ing_id, ing_name, price_link);
                                                    ingredients.add(ingredient);
                                                }
                                                Content content = new Content(content_id, netDate, img, con_name, con_desc, ingredients);
                                                contents.add(content);
                                            }
                                        });
                                        tasks.add(task);
                                    }
                                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                                        @Override
                                        public void onSuccess(List<Object> objects) {
                                            profileCallBack.onProfileDataReady(Uri.parse(user_image), username, about, post_counter, contents);
                                        }
                                    });
                                }
                            });

                }
            });
        }catch (IllegalArgumentException e)
        {

        }

    }

    public void GetAllIngredients(AddContentCallBack addContentCallBack)
    {
       db.collection("ingredients").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
           @Override
           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
               ArrayList<Ingredient> ingredients = new ArrayList<>();
                for(DocumentSnapshot ds:queryDocumentSnapshots)
                {
                    ingredients.add(new Ingredient(ds.getId(),
                            (String) ds.get("ing_name"), (String) ds.get("price_link")));
                }
                addContentCallBack.onIngredientsRetrieved(ingredients);
           }
       });
    }

    //image -> content firestore -> user firestore(posted_contents)
    public void AddContent(Content content, AddContentCallBack addContentCallBack)
    {
        String image_name = "content_images/"+UUID.randomUUID()+".jpg";
        sf.child(image_name).putFile(content.recipe_image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference newReference = FirebaseStorage.getInstance().getReference(image_name);
                newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap<String, Object> content_data = new HashMap<>();
                        content_data.put("recipe_name", content.recipe_name);
                        content_data.put("recipe_desc", content.recipe_desc);
                        content_data.put("recipe_image", uri);
                        content_data.put("recipe_date", FieldValue.serverTimestamp());
                        ArrayList<String> ing_ids = new ArrayList<>();
                        for(Ingredient ing:content.ingredients)
                        {
                            ing_ids.add(ing.ing_id);
                        }
                        content_data.put("ingredients", ing_ids);
                        DocumentReference dr = db.collection("Contents").document();
                        dr.set(content_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                db.collection("Users").document(mAuth.getUid()).
                                        update("posted_contents", FieldValue.arrayUnion(dr.getId())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                addContentCallBack.onAddContentCompleted();
                                            }
                                        });
                                //post counter will be incremented by one
                            }
                        });
                    }
                });
            }
        });

    }

    public void GetAllContents(ContentCallback contentCallback)
    {
        db.collection("Contents").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Content> contents = new ArrayList<>();
                List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                Task<QuerySnapshot> task;
                for(DocumentSnapshot ds:queryDocumentSnapshots)
                {
                    Map<String, Object> content_data = ds.getData();
                    String con_id = (String) ds.getId();
                    Date netDate = ds.getTimestamp("recipe_date").toDate();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
                    simpleDateFormat.format(netDate);
                    Uri img = Uri.parse((String) content_data.get("recipe_image"));
                    String con_name = (String) content_data.get("recipe_name");
                    String con_desc = (String) content_data.get("recipe_desc");
                    ArrayList<String> ing_ids = (ArrayList<String>) content_data.get("ingredients");
                    if(ing_ids==null || ing_ids.isEmpty())
                        return;

                    ArrayList<Ingredient> ingredients = new ArrayList<>();

                    task = db.collection("ingredients").whereIn(FieldPath.documentId(),ing_ids).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            for(DocumentSnapshot documentSnapshot:querySnapshot)
                            {
                                String ing_id = documentSnapshot.getId();
                                String ing_name = (String) documentSnapshot.get("ing_name");
                                String price_link = (String) documentSnapshot.get("price_link");
                                Ingredient ingredient = new Ingredient(ing_id, ing_name, price_link);
                                ingredients.add(ingredient);
                            }
                            Content content = new Content(con_id, netDate, img, con_name, con_desc, ingredients);
                            contents.add(content);
                        }
                    });
                    tasks.add(task);


                }
                Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> objects) {
                        contentCallback.onContentRetrieved(contents);
                    }
                });

            }
        });
    }

    public void GetUserByContentId(String content_id, ContentCallback contentCallback)
    {
        db.collection("Users").whereArrayContains("posted_contents", content_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot ds = queryDocumentSnapshots.getDocuments().get(0);
                Map<String, Object> user_data = ds.getData();

                String user_id = ds.getId();
                String username = (String) user_data.get("username");
                String about = (String) user_data.get("about");
                Uri img = Uri.parse((String) user_data.get("usr_image"));
                long post_counter = (long) user_data.get("post_counter");
                ArrayList<String> posted_contents = (ArrayList<String>) user_data.get("posted_contents");
                ArrayList<String> favorite_contents = (ArrayList<String>) user_data.get("favorite_contents");

                User user = new User(user_id, username, about, img, post_counter, posted_contents, favorite_contents);

                contentCallback.onUserRetrieved(user);
            }
        });
    }

    public void AddFavorites(String content_id)
    {
        db.collection("Users").document(mAuth.getUid()).
                update("favorite_contents", FieldValue.arrayUnion(content_id)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
    }

    public void GetFavoriteContents(ContentCallback contentCallback)
    {
        try {
            db.collection("Users").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String, Object> profile_data = documentSnapshot.getData();
                    ArrayList<String> fav_contents = (ArrayList<String>) profile_data.get("favorite_contents");


                    ArrayList<Content> contents = new ArrayList<>();
                    db.collection("Contents").whereIn(FieldPath.documentId(),fav_contents).get().
                            addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                            {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                                    for(DocumentSnapshot ds:queryDocumentSnapshots)
                                    {
                                        Task<QuerySnapshot> task;
                                        String content_id = ds.getId();
                                        Date netDate = ds.getTimestamp("recipe_date").toDate();
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
                                        simpleDateFormat.format(netDate);
                                        Uri img = Uri.parse((String) ds.get("recipe_image"));
                                        String con_name = (String) ds.get("recipe_name");
                                        String con_desc = (String) ds.get("recipe_desc");
                                        ArrayList<String> ing_ids = (ArrayList<String>) ds.get("ingredients");

                                        ArrayList<Ingredient> ingredients = new ArrayList<>();

                                        task=db.collection("ingredients").whereIn("ing_id",ing_ids).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots1) {
                                                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots1)
                                                {
                                                    String ing_id = documentSnapshot.getId();
                                                    String ing_name = (String) documentSnapshot.get("ing_name");
                                                    String price_link = (String) documentSnapshot.get("price_link");
                                                    Ingredient ingredient = new Ingredient(ing_id, ing_name, price_link);
                                                    ingredients.add(ingredient);
                                                }
                                                Content content = new Content(content_id, netDate, img, con_name, con_desc, ingredients);
                                                contents.add(content);
                                            }
                                        });
                                        tasks.add(task);
                                    }
                                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                                        @Override
                                        public void onSuccess(List<Object> objects) {
                                            contentCallback.onContentRetrieved(contents);
                                        }
                                    });
                                }
                            });

                }
            });
        }catch (IllegalArgumentException e)
        {

        }

    }

}
