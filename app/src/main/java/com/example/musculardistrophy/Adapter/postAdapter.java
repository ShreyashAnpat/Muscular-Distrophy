package com.example.musculardistrophy.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.musculardistrophy.ui.home.CommentActivity;
import com.example.musculardistrophy.ui.home.userProfile;
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
    String userID  , uid ,TimeStamp ,post , userName , postID , captions;
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
        captions = postData.get(position).getCaption();

        if (postData.get(position).getCaption().equals("")){
           holder.captions.setVisibility(View.GONE);
        }
        else {
            holder.captions.setText(postData.get(position).getCaption());
            holder.captions.setVisibility(View.VISIBLE);

        }


        if (postData.get(position).getPost().equals("")){
            holder.postImage.setVisibility(View.GONE);
            holder.captions.setPadding(50,150,40,20);
        }
        else {
            Picasso.get().load(post).into(holder.postImage);
        }

        firebaseFirestore.collection("user").document(userID).collection("savePost").document(postData.get(position).getPostID()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                postID = postData.get(position).getPostID();
                if (value.get("postID")!= null){
                    holder.savePost.setVisibility(View.INVISIBLE);
                    holder.savedPost.setVisibility(View.VISIBLE);
                    postID = postData.get(position).getPostID();

                }
            }
        });

        firebaseFirestore.collection("post").document(postID).collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                holder.commentCount.setText(value.getDocuments().size()+" Comments");
            }
        });
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        holder.username.setText(postData.get(position).getUsername());
        Picasso.get().load(postData.get(position).getProfile()).into(holder.profile);





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

        holder.lick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = postData.get(position).getUsername();
                postID = postData.get(position).getPostID();

                HashMap<String , Object> lickData = new HashMap<>();
                lickData.put("userID", userID);
                lickData.put("timeStamp", timeStamp);
                lickData.put("userName" ,userName );
//                Toast.makeText(context, postData.get(position).getPostID(), Toast.LENGTH_SHORT).show();
                firebaseFirestore.collection("post").document(postData.get(position).getPostID()).collection("lick").document(userID).set(lickData);

                holder.licked.setVisibility(View.VISIBLE);
                holder.licked.playAnimation();
                holder.lick.setVisibility(View.INVISIBLE);

            }
        });



        holder.licked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uid = postData.get(position).getUID();
                post = postData.get(position).getPost();
                TimeStamp = postData.get(position).getTimeStamp();
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
                postID = postData.get(position).getPostID();

                HashMap<String , Object> savePost = new HashMap<>();
                savePost.put("TimeStamp" , timeStamp);
                savePost.put("postID" , postID);

                firebaseFirestore.collection("user").document(userID).collection("savePost").document(postID).set(savePost);
            }
        });

        holder.savedPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postID = postData.get(position).getPostID();
                holder.savePost.setVisibility(View.VISIBLE);
                holder.savedPost.setVisibility(View.GONE);

                firebaseFirestore.collection("user").document(userID).collection("savePost").document(postID).delete();

            }
        });

        holder.commentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , CommentActivity.class);
                intent.putExtra("postID" , postData.get(position).getPostID());
                context.startActivity(intent);
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , CommentActivity.class);
                intent.putExtra("postID" , postData.get(position).getPostID());
                context.startActivity(intent);
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , userProfile.class);
                intent.putExtra("userID", postData.get(position).getUID());
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return postData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView savePost , lick , savedPost  ,comment;
        LottieAnimationView licked ;
        TextView username , lickCount , commentCount  , captions;
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
            comment = itemView.findViewById(R.id.comment);
            captions = itemView.findViewById(R.id.captions);
        }

    }
}
