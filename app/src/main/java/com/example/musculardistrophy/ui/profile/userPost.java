package com.example.musculardistrophy.ui.profile;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.musculardistrophy.Adapter.UserPostAdapter;
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

public class userPost extends Fragment {
    RecyclerView userPostList ;
    UserPostAdapter postAdapters ;
    List<postData> postData ;
    FirebaseFirestore firebaseFirestore ;
    FirebaseAuth auth ;
    String userID ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_post, container, false);
        userPostList = view.findViewById(R.id.userPostList);

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance() ;
        userID = auth.getCurrentUser().getUid() ;
        postData = new ArrayList<>();

        userPostList.setLayoutManager(new GridLayoutManager(getContext(),2));
        postAdapters = new UserPostAdapter(postData);
        userPostList.setAdapter(postAdapters);

        firebaseFirestore.collection("post").whereEqualTo("UID" , userID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    for (DocumentChange doc : value.getDocumentChanges()){
                        postData mPostData = doc.getDocument().toObject(postData.class);
                        postData.add(mPostData);
                        postAdapters.notifyDataSetChanged();
                    }
                }
            }
        });


        return view ;
    }
}