package com.md.musculardistrophy.Message;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.DocumentChange;
import com.md.musculardistrophy.Adapter.MessageUserListAdapter;
import com.md.musculardistrophy.Model.userData;
import com.md.musculardistrophy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    RecyclerView accountList ;
    FirebaseFirestore firebaseFirestore ;
    FirebaseAuth auth ;
    List<userData> userDataList ;
    MessageUserListAdapter adapter ;
    SearchView searchView ;
    SwipeRefreshLayout refreshLayout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        accountList = findViewById(R.id.messages);
        searchView = findViewById(R.id.search);
        firebaseFirestore = FirebaseFirestore.getInstance() ;
        auth = FirebaseAuth.getInstance() ;

        userDataList = new ArrayList<>();
        accountList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageUserListAdapter(userDataList);
        accountList.setAdapter(adapter);

        refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userDataList.clear();
                firebaseFirestore.collection("user").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        for (DocumentChange doc : value.getDocumentChanges()){
                            if (!doc.getDocument().getId().toString().equals(auth.getCurrentUser().getUid())){
                                userData mUserData = doc.getDocument().toObject(userData.class);
                                userDataList.add(mUserData);
                                adapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
                refreshLayout.setRefreshing(false);
            }
        });

        firebaseFirestore.collection("user").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                for (DocumentChange doc : value.getDocumentChanges()){
                    if (!doc.getDocument().getId().toString().equals(auth.getCurrentUser().getUid())){
                        userData mUserData = doc.getDocument().toObject(userData.class);
                        userDataList.add(mUserData);
                        adapter.notifyDataSetChanged();
                    }

                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query query = firebaseFirestore.collection("user").orderBy("userName").startAt(newText).endAt(newText+"\uf9ff" );
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        userDataList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()){
                            if (!doc.getId().toString().equals(auth.getCurrentUser().getUid())){
                                userData mUserData = doc.toObject(userData.class);
                                userDataList.add(mUserData);
                                adapter.notifyDataSetChanged();
                            }

                        }

                    }
                });
                return false;
            }
        });
    }
}