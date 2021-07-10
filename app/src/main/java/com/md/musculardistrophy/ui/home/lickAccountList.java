package com.md.musculardistrophy.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.md.musculardistrophy.Adapter.likeUserAccountAdapter;
import com.md.musculardistrophy.Model.likeAccountData;
import com.md.musculardistrophy.R;

import java.util.ArrayList;
import java.util.List;

public class lickAccountList extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore ;
    FirebaseAuth auth ;
    String postId ;
    RecyclerView likeUser ;
    likeUserAccountAdapter  adapter ;
    List<likeAccountData> list ;
    LottieAnimationView placeholder ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lick_account_list);
        auth = FirebaseAuth.getInstance() ;
        firebaseFirestore = FirebaseFirestore.getInstance();
        postId = getIntent().getStringExtra("postID");
        likeUser = findViewById(R.id.likeUser);
        placeholder = findViewById(R.id.notification);
        list = new ArrayList<>();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        likeUser.setLayoutManager(new LinearLayoutManager(this));
        adapter = new likeUserAccountAdapter(list);
        likeUser.setAdapter(adapter);

        firebaseFirestore.collection("post").document(postId).collection("lick").orderBy("timeStamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (value.getDocuments().size()>0){
                    placeholder.setVisibility(View.GONE);
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()){
                        likeAccountData data = documentSnapshot.toObject(likeAccountData.class);
                        list.add(data);
                        adapter.notifyDataSetChanged();
                    }
                }
                else {
                    placeholder.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}