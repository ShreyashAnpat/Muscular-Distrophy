package com.example.musculardistrophy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.musculardistrophy.Model.postData;
import com.example.musculardistrophy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class postAdapter extends RecyclerView.Adapter<postAdapter.ViewHolder> {
    List<postData> postData;
    Context context ;
    FirebaseFirestore firebaseFirestore ;
    FirebaseAuth auth ;
    String userID  , uid ,TimeStamp ,post , userName , postID;
    public postAdapter(List<postData> postData) {
        this.postData = postData ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout,parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid() ;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        uid = postData.get(position).getUID();
        post = postData.get(position).getPost();
        TimeStamp = postData.get(position).getTimeStamp();
        userName = postData.get(position).getUsername();
        postID = postData.get(position).getPostID();

        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        holder.lick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, post, Toast.LENGTH_SHORT).show();
                HashMap<String , Object> lickData = new HashMap<>();
                lickData.put("userID", userID);
                lickData.put("timeStamp", timeStamp);
                lickData.put("userName" ,userName );
                Toast.makeText(context, postID, Toast.LENGTH_SHORT).show();
                firebaseFirestore.collection("post").document(postID).collection("lick").document(userID).set(lickData);

                holder.licked.setVisibility(View.VISIBLE);
                holder.licked.playAnimation();
                holder.lick.setVisibility(View.INVISIBLE);

            }
        });


        holder.username.setText(postData.get(position).getUsername());
        Picasso.get().load(postData.get(position).getProfile()).into(holder.profile);
        Picasso.get().load(post).into(holder.postImage);



        firebaseFirestore.collection("post").whereEqualTo("UID",uid ).whereEqualTo("TimeStamp", TimeStamp ).whereEqualTo("post",post).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){

                    doc.getReference().collection("lick").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            holder.lickCount.setText(value.getDocuments().size() + " Licks");
                        }
                    });

                    doc.getReference().collection("lick").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null){
                                if (value.get("userName")!= null){
                                    holder.licked.setVisibility(View.VISIBLE);
                                    holder.licked.playAnimation();
                                    holder.lick.setVisibility(View.INVISIBLE);
                                }

                            }
                        }
                    });


                }
            }
        });




        holder.licked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("post").whereEqualTo("UID",uid ).whereEqualTo("TimeStamp", TimeStamp ).whereEqualTo("post",post).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                            doc.getReference().collection("lick").document(userID).delete();
                        }
                    }
                });

                holder.licked.setVisibility(View.INVISIBLE);
                holder.lick.setVisibility(View.VISIBLE);
            }
        });

        holder.savePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.savePost.setVisibility(View.INVISIBLE);
                holder.savedPost.setVisibility(View.VISIBLE);
            }
        });

        holder.savedPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.savePost.setVisibility(View.VISIBLE);
                holder.savedPost.setVisibility(View.GONE);
            }
        });


    }


    @Override
    public int getItemCount() {
        return postData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView savePost , lick , savedPost  ;
        LottieAnimationView licked ;
        TextView username , lickCount , commentCount ;
        CircleImageView profile ;
        ImageView postImage ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile = itemView.findViewById(R.id.profile);
            postImage = itemView.findViewById(R.id.postImage);
            savePost = itemView.findViewById(R.id.savePost);
            lick = itemView.findViewById(R.id.imageView6);
            licked = itemView.findViewById(R.id.lick);
            savedPost = itemView.findViewById(R.id.savedPost);
            lickCount = itemView.findViewById(R.id.textView3);
            commentCount = itemView.findViewById(R.id.commentCount);
        }

    }
}
