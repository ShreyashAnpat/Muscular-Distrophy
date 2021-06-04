package com.example.musculardistrophy.ui.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.musculardistrophy.Adapter.postAdapter;
import com.example.musculardistrophy.Model.postData;
import com.example.musculardistrophy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class userPostList extends AppCompatActivity {
    RecyclerView userPost ;
    FirebaseAuth auth ;
    String userID ;
    List<postData> PostData ;
    FirebaseFirestore firebaseFirestore ;
    postAdapter adapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post_list);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        userPost = findViewById(R.id.userPost);
        auth = FirebaseAuth.getInstance();
        userID = getIntent().getStringExtra("UID");
        firebaseFirestore = FirebaseFirestore.getInstance();
        PostData = new ArrayList<>();

        userPost.setLayoutManager(new LinearLayoutManager(this));
        adapter = new postAdapter(PostData) ;
        userPost.setAdapter(adapter);

        firebaseFirestore.collection("post").whereEqualTo("UID", userID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    for (DocumentChange doc : value.getDocumentChanges()){
                        postData mPostData = doc.getDocument().toObject(postData.class);
                        PostData.add(mPostData);
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        });

        int position = Integer.parseInt(getIntent().getStringExtra("position"));
//        Toast.makeText(this, position, Toast.LENGTH_SHORT).show();
        userPost.scrollToPosition(position);

    }
}