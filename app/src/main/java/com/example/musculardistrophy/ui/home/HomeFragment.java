package com.example.musculardistrophy.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musculardistrophy.Adapter.UserAccountAdapter;
import com.example.musculardistrophy.Adapter.postAdapter;
import com.example.musculardistrophy.Message.MessageActivity;
import com.example.musculardistrophy.Model.postData;
import com.example.musculardistrophy.Model.userData;
import com.example.musculardistrophy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.StructuredQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    CircleImageView profile ;
    ImageView message ;
    androidx.appcompat.widget.SearchView searchView ;
    FirebaseAuth auth ;
    String userID ;
    List<postData> PostData ;
    postAdapter  adapter ;
    SwipeRefreshLayout refreshLayout ;
    RecyclerView postList , accountList;
    FirebaseFirestore firebaseFirestore ;
    ProgressDialog progressDialog ;
    List<userData> userDataList ;
    UserAccountAdapter accountAdapter ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        profile = root.findViewById(R.id.profile);
        message = root.findViewById(R.id.message);
        postList = root.findViewById(R.id.postList);
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getContext());
        refreshLayout = root.findViewById(R.id.refresh);
        progressDialog.setMessage("Loading data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        searchView = root.findViewById(R.id.search);
        accountList = root.findViewById(R.id.accountList);
        userDataList = new ArrayList<>();

        accountList.setLayoutManager(new LinearLayoutManager(getContext()));
        accountAdapter = new UserAccountAdapter(userDataList , root.getContext());
        accountList.setAdapter(accountAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    postList.setVisibility(View.GONE);
                    accountList.setVisibility(View.VISIBLE);

                    Query query = firebaseFirestore.collection("user").orderBy("userName").startAt(newText).endAt(newText+"\uf9ff" );
                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                           if (!value.isEmpty()){
                               for (DocumentSnapshot doc : value.getDocuments()){

                                   userData mUserData = doc.toObject(userData.class);
                                   userDataList.add(mUserData);
                                   accountAdapter.notifyDataSetChanged();
                               }

                           }
                        }
                    });
                }
                else {
                    postList.setVisibility(View.VISIBLE);
                    accountList.setVisibility(View.GONE);
                    userDataList.clear();
                    accountAdapter.notifyDataSetChanged();
                }

                return false;
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext() , MessageActivity.class));
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        firebaseFirestore.collection("user").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Picasso.get().load(value.getString("Profile")).into(profile);
            }
        });


        loadData();

        return root;
    }

    private void loadData() {
        refreshLayout.setRefreshing(false);
        PostData = new ArrayList<>();
        postList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new postAdapter(PostData) ;
        adapter.setHasStableIds(false);
        postList.setAdapter(adapter);
        firebaseFirestore.collection("post").orderBy("TimeStamp" , Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    PostData.clear();
                    for (DocumentSnapshot  doc : value.getDocuments()){
                            postData mPostData = doc.toObject(postData.class);
                            PostData.add(mPostData);
                            adapter.notifyDataSetChanged();
                    }
                    progressDialog.cancel();
                }
            }
        });
        refreshLayout.setRefreshing(false);
    }
}