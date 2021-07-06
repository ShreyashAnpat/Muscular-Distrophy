package com.md.musculardistrophy.ui.profile;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.md.musculardistrophy.Adapter.SavePostAdapter;
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

public class savedPost extends Fragment {
    RecyclerView savePost ;
    FirebaseAuth auth ;
    List<postData> postData ;
    SavePostAdapter postAdapters ;
    FirebaseFirestore firebaseFirestore ;
    String userID ;
    LinearLayout placeholder ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_saved_post, container, false);
        savePost = view.findViewById(R.id.savedPost);
        placeholder = view.findViewById(R.id.placeholder);
        firebaseFirestore = FirebaseFirestore.getInstance() ;
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        postData = new ArrayList<>();

        savePost.setLayoutManager(new GridLayoutManager(getContext(),2));
        postAdapters = new SavePostAdapter(postData);
        savePost.setAdapter(postAdapters);

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
                                    postData.add(mPostData);
                                    postAdapters.notifyDataSetChanged();
                                }
                            }
                        }
                    });

                }
                if (value.getDocuments().size()==0){
                    placeholder.setVisibility(View.VISIBLE);
                    savePost.setVisibility(View.GONE);
                }
                else {
                    placeholder.setVisibility(View.GONE);
                    savePost.setVisibility(View.VISIBLE);
                }
            }
        });

        return  view ;
    }
}