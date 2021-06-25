package com.md.musculardistrophy.ui.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.md.musculardistrophy.Adapter.postAdapter;
import com.md.musculardistrophy.Model.postData;
import com.md.musculardistrophy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class savePostList extends AppCompatActivity {
    RecyclerView userPost ;
    FirebaseAuth auth ;
    String userID ;
    List<postData> PostData ;
    FirebaseFirestore firebaseFirestore ;
    postAdapter adapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_post_list);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        userPost = findViewById(R.id.userPost);
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        PostData = new ArrayList<>();

        userPost.setLayoutManager(new LinearLayoutManager(this));
        adapter = new postAdapter(PostData) ;
        userPost.setAdapter(adapter);

        firebaseFirestore.collection("user").document(userID).collection("savePost").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot documentSnapshot : value.getDocuments()){
                    firebaseFirestore.collection("post").whereEqualTo("postID" , documentSnapshot.getString("postID")).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                }
            }
        });

    }

}