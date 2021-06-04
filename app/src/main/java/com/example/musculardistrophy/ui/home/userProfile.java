package com.example.musculardistrophy.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.musculardistrophy.Adapter.UserPostAdapter;
import com.example.musculardistrophy.Model.postData;
import com.example.musculardistrophy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class userProfile extends AppCompatActivity {

    CircleImageView profile ;
    TextView userName , totalUserPost , totalUsers , address ;
    RecyclerView userPost ;
    FirebaseAuth auth  ;
    FirebaseFirestore firebaseFirestore ;
    String currentUserID , userId ;
    List<postData> postData ;
    UserPostAdapter postAdapters ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profile = findViewById(R.id.circleImageView3);
        userName = findViewById(R.id.username);
        totalUserPost = findViewById(R.id.postCount);
        totalUsers = findViewById(R.id.userCount);
        address = findViewById(R.id.location);
        userPost = findViewById(R.id.userPost);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        userId = getIntent().getStringExtra("userID");

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        firebaseFirestore.collection("user").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Picasso.get().load(value.getString("Profile")).into(profile);
                userName.setText(value.getString("userName"));
                address.setText(value.getString("location"));
            }
        });

        firebaseFirestore.collection("user").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                totalUsers.setText( String.valueOf(value.getDocuments().size()));
            }
        });

        postData = new ArrayList<>();
        userPost.setLayoutManager(new GridLayoutManager(this,2));
        postAdapters = new UserPostAdapter(postData);
        userPost.setAdapter(postAdapters);

        firebaseFirestore.collection("post").whereEqualTo("UID" , userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                totalUserPost.setText(String.valueOf(value.getDocuments().size()));
                if (value != null){
                    for (DocumentChange doc : value.getDocumentChanges()){
                        postData mPostData = doc.getDocument().toObject(postData.class);
                        postData.add(mPostData);
                        postAdapters.notifyDataSetChanged();
                    }
                }
            }
        });




    }
}