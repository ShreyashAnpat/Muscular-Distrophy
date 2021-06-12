package com.example.musculardistrophy.Adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.musculardistrophy.Model.commentData;
import com.example.musculardistrophy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    List<commentData> commentDataList;
    FirebaseAuth auth ;
    FirebaseFirestore firebaseFirestore ;
    String userID , postID;

    public CommentAdapter(List<commentData> commentDataList, String postID) {
        this.commentDataList = commentDataList ;
        this.postID = postID ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout,parent,false);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userName = commentDataList.get(position).getUserName();
        String commentText = commentDataList.get(position).getComment();
        String profile = commentDataList.get(position).getProfile();
        String comment = "<b>" + userName +"</b>"+"  "+commentText;

        Picasso.get().load(profile).into(holder.profile);
        holder.Comment.setText(Html.fromHtml(comment));
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        firebaseFirestore.collection("post").document(postID).collection("Comments")
                .document(commentDataList.get(position).getCommentID()).collection("Lick")
                .document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.get("UserID")!= null){
                    holder.lick.setVisibility(View.INVISIBLE);
                    holder.licked.setVisibility(View.VISIBLE);
                    holder.licked.playAnimation();
                }
            }
        });

        holder.lick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.lick.setVisibility(View.INVISIBLE);
                holder.licked.setVisibility(View.VISIBLE);
                holder.licked.playAnimation();

                HashMap<String , Object> lickComment = new HashMap<>();
                lickComment.put("TimeStamp",timeStamp );
                lickComment.put("UserID", userID);
                firebaseFirestore.collection("post").document(postID).collection("Comments")
                        .document(commentDataList.get(position).getCommentID()).collection("Lick").document(userID).set(lickComment);
            }
        });

        holder.licked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.lick.setVisibility(View.VISIBLE);
                holder.licked.setVisibility(View.INVISIBLE);
                firebaseFirestore.collection("post").document(postID).collection("Comments").document(userID).delete();
                firebaseFirestore.collection("post").document(postID).collection("Comments")
                        .document(commentDataList.get(position).getCommentID()).collection("Lick").document(userID).delete();
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile ;
        TextView Comment ;
        LottieAnimationView licked ;
        ImageView lick ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile);
            Comment = itemView.findViewById(R.id.commentText);
            lick = itemView.findViewById(R.id.lick);
            licked = itemView.findViewById(R.id.licked);
        }
    }

}
