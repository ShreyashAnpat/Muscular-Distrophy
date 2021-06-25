package com.md.musculardistrophy.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.md.musculardistrophy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPostActivity extends AppCompatActivity {

    CircleImageView profile ;
    TextView userName  , post  ;
    String userID ;
    ImageView camera  ,postImage;
    FirebaseFirestore firebaseFirestore;
    String profileUri  , userNameString ,isText ="";
    EditText caption ;
    FirebaseAuth auth ;
    ProgressDialog pd ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_post);
        profile  = findViewById(R.id.circleImageView2);
        userName = findViewById(R.id.username);
        camera = findViewById(R.id.camera);
        postImage = findViewById(R.id.postImage);
        caption = findViewById(R.id.captions);
        post = findViewById(R.id.post);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth  = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        pd   =  new ProgressDialog(this);
        pd.setMessage("Uploading Post ...");
        pd.setCanceledOnTouchOutside(false);

        firebaseFirestore.collection("user").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                Picasso.get().load(value.get("Profile").toString());
                userName.setText(value.get("userName").toString());
            }
        });

        firebaseFirestore.collection("post").document(getIntent().getStringExtra("postId")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (!value.get("post").equals("")){
                    Picasso.get().load(value.get("post").toString()).into(postImage);
                    postImage.setVisibility(View.VISIBLE);
                }
                caption.setText(value.get("Caption").toString());
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                HashMap<String , Object> editPost = new HashMap<>();
                editPost.put("Caption", caption.getText().toString());
                firebaseFirestore.collection("post").document(getIntent().getStringExtra("postId")).update(editPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.cancel();
                        onBackPressed();
                    }
                });
            }
        });


    }
}