package com.example.musculardistrophy.Message;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.musculardistrophy.Adapter.MessageUserListAdapter;
import com.example.musculardistrophy.Model.userData;
import com.example.musculardistrophy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    RecyclerView accountList ;
    FirebaseFirestore firebaseFirestore ;
    FirebaseAuth auth ;
    List<userData> userDataList ;
    MessageUserListAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        accountList = findViewById(R.id.messages);
        firebaseFirestore = FirebaseFirestore.getInstance() ;
        auth = FirebaseAuth.getInstance() ;

        userDataList = new ArrayList<>();
        accountList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageUserListAdapter(userDataList);
        accountList.setAdapter(adapter);

        firebaseFirestore.collection("user").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot doc : value.getDocuments()){
                    if (!doc.getId().toString().equals(auth.getCurrentUser().getUid())){
                        userData mUserData = doc.toObject(userData.class);
                        userDataList.add(mUserData);
                        adapter.notifyDataSetChanged();
                    }

                }
            }
        });
    }
}