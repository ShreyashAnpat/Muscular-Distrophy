package com.md.musculardistrophy.ui.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.md.musculardistrophy.Adapter.SearchAccountAdapter;
import com.md.musculardistrophy.Adapter.postAdapter;
import com.md.musculardistrophy.Message.MessageActivity;
import com.md.musculardistrophy.Model.postData;
import com.md.musculardistrophy.Model.userData;
import com.md.musculardistrophy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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
    SearchAccountAdapter searchAccountAdapter ;

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


        accountList = root.findViewById(R.id.accountLists);
        searchView = root.findViewById(R.id.search);
        userDataList = new ArrayList<>();

        accountList.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAccountAdapter = new SearchAccountAdapter(userDataList);
        accountList.setAdapter(searchAccountAdapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
                public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()){
                    postList.setVisibility(View.INVISIBLE);
                    accountList.setVisibility(View.VISIBLE);
                    Query query = firebaseFirestore.collection("user").orderBy("userName").startAt(newText).endAt(newText+"\uf9ff" );
                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()){
                                if (!documentSnapshot.getString("userID").equals(userID)){
                                    userData mUserData = documentSnapshot.toObject(userData.class);
                                    userDataList.add(mUserData);
                                    searchAccountAdapter.notifyDataSetChanged();
                                }

                            }
                        }
                    });
                }
                else {
                    postList.setVisibility(View.VISIBLE);
                    accountList.setVisibility(View.GONE);
                    userDataList.clear();
                }
                return false;
            }
        });



        return root;
    }

    private void loadData() {
        refreshLayout.setRefreshing(false);
        PostData = new ArrayList<>();
        postList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new postAdapter(PostData) ;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.icon)
                        .setTitle("Alert")
                        .setMessage("Are you want to close this app?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                                getActivity().moveTaskToBack(true);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
