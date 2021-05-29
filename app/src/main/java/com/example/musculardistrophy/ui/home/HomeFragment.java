package com.example.musculardistrophy.ui.home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musculardistrophy.Adapter.postAdapter;
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

public class HomeFragment extends Fragment {

    CircleImageView profile ;
    ImageView message ;
    SearchView searchView ;
    FirebaseAuth auth ;
    String userID ;
    List<postData> PostData ;
    postAdapter  adapter ;
    RecyclerView postList;
    FirebaseFirestore firebaseFirestore ;
    ProgressDialog progressDialog ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        profile = root.findViewById(R.id.profile);
        message = root.findViewById(R.id.message);
        postList = root.findViewById(R.id.postList);
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        PostData = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Loading data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        postList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new postAdapter(PostData) ;
        postList.setAdapter(adapter);

        firebaseFirestore.collection("post").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    for (DocumentChange doc : value.getDocumentChanges()){
                            postData mPostData = doc.getDocument().toObject(postData.class);
                            PostData.add(mPostData);
                            adapter.notifyDataSetChanged();
                    }
                    progressDialog.cancel();
                }
            }
        });

        firebaseFirestore.collection("user").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Picasso.get().load(value.getString("Profile")).into(profile);
            }
        });

        return root;
    }
}