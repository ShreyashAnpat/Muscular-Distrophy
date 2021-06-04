package com.example.musculardistrophy.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.musculardistrophy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class editProfile extends AppCompatActivity {
    EditText userName , address , phoneNumber ;
    FirebaseFirestore  firebaseFirestore ;
    StorageReference Folder , ImageName ;
    CircleImageView profile ;
    ProgressDialog progressDialog ;
    Button submit ;
    CardView editProfile ;
    FirebaseAuth auth ;
    Uri imageUri ;
    String userID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_profile);
        userName = findViewById(R.id.userName);
        address = findViewById(R.id.Address);
        phoneNumber = findViewById(R.id.PhoneNumber);
        submit = findViewById(R.id.submit);
        profile = findViewById(R.id.circleImageView4);
        editProfile = findViewById(R.id.cardView2);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance() ;
        userID = auth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Profile");
        progressDialog.setCanceledOnTouchOutside(false);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        firebaseFirestore.collection("user").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userName.setText(value.getString("userName"));
                address.setText(value.getString("location"));
                phoneNumber.setText(value.getString("phoneNumber"));
                Picasso.get().load(value.getString("Profile")).into(profile);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
 
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                HashMap<String , Object> updateProfile = new HashMap<>();
                updateProfile.put("location" , address.getText().toString());
                updateProfile.put("phoneNumber", phoneNumber.getText().toString());
                updateProfile.put("userName" , userName.getText().toString());

                firebaseFirestore.collection("user").document(userID).update(updateProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                   progressDialog.dismiss();
                        onBackPressed();
                    }
                });

                HashMap<String , Object> updatePost = new HashMap<>();
                updatePost.put("username" , userName.getText().toString());
                firebaseFirestore.collection("post").whereEqualTo("UID" , userID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentSnapshot doc : value.getDocuments()){
                            doc.getReference().update(updatePost);
                        }

                    }
                });

                HashMap<String , Object> updateComment = new HashMap<>();
                updateComment.put("userName" , userName.getText().toString());
                firebaseFirestore.collection("post").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentSnapshot doc : value.getDocuments()){

                            doc.getReference().collection("Comments")
                                    .whereEqualTo("userID", userID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    for (DocumentSnapshot documentSnapshot : value.getDocuments()){
                                        documentSnapshot.getReference().update(updateComment);
//                                        Toast.makeText(editProfile.this, doc.getId(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });

    }
    private void selectImage() {

        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start( this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                profile.setImageURI(imageUri);
                updateProfile(imageUri);
            }
        }
    }

    private void updateProfile(Uri imageUri) {
        progressDialog.show();
        Folder = FirebaseStorage.getInstance().getReference().child("userProfile");
        ImageName = Folder.child(auth.getCurrentUser().getUid() + imageUri.getLastPathSegment());
        ImageName.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        HashMap<String , Object> updateProfiles = new HashMap<>();
                        updateProfiles.put("Profile", uri.toString());

                        firebaseFirestore.collection("user").document(userID).update(updateProfiles).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                onBackPressed();
                            }
                        });

                        firebaseFirestore.collection("post").whereEqualTo("UID",userID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                for (DocumentSnapshot doc : value.getDocuments()){
                                    doc.getReference().update(updateProfiles);
                                }
                            }
                        });

                        firebaseFirestore.collection("post").document().collection("Comments")
                                .whereEqualTo("userID", userID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                for (DocumentSnapshot doc : value.getDocuments()){
                                    doc.getReference().update(updateProfiles);
                                }
                            }
                        });

                        firebaseFirestore.collection("post").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                for (DocumentSnapshot doc : value.getDocuments()){
                                    doc.getReference().collection("Comments")
                                            .whereEqualTo("userID", userID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            for (DocumentSnapshot documentSnapshot : value.getDocuments()){
                                                documentSnapshot.getReference().update(updateProfiles);
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    }
                });

            }
        });
    }

}