package com.example.musculardistrophy.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musculardistrophy.Adapter.CommentAdapter;
import com.example.musculardistrophy.Model.commentData;
import com.example.musculardistrophy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    CircleImageView postProfile , commentProfile;
    ImageView send ,postImage;
    TextView userName ,caption;
    EditText comment ;
    RecyclerView commentList ;
    String userID ,postID , userProfileID  , currentUserName;
    FirebaseAuth auth ;
    CommentAdapter adapter ;
    FirebaseFirestore firebaseFirestore ;
    ProgressDialog progressDialog ;
    List<commentData> commentDataList ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        postProfile = findViewById(R.id.profile);
        commentProfile = findViewById(R.id.circleImageView);
        comment = findViewById(R.id.commentText);
        send = findViewById(R.id.imageView7);
        userName = findViewById(R.id.username);
        commentList = findViewById(R.id.CommentList);
        postImage = findViewById(R.id.postImage);
        caption = findViewById(R.id.captions);
        commentDataList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        postID = getIntent().getStringExtra("postID");


        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));


        firebaseFirestore.collection("post").document(postID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String postImageUri = value.getString("post") ;
                String caption_txt = value.getString("Caption");
                Picasso.get().load(value.getString("Profile")).into(postProfile);
                if (postImageUri.equals("")){
                    postImage.setVisibility(View.GONE);
                     caption.setPadding(50,150,40,20);
                }else {
                    Picasso.get().load(value.getString("post")).into(postImage);
                }
                if (caption_txt.equals("")){
                    caption.setVisibility(View.GONE);
                }else {
                    caption.setText(caption_txt);
                    caption.setVisibility(View.VISIBLE);
                }
                userName.setText(value.getString("username"));
            }
        });

        firebaseFirestore.collection("user").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userProfileID = value.getString("Profile");
                currentUserName = value.getString("userName");

                Picasso.get().load(userProfileID).into(commentProfile);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment_txt = comment.getText().toString();
                if (comment_txt.isEmpty()){
                    Snackbar.make(v, "Wire Some Comment" , Snackbar.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.setMessage("Uploading Comment");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    HashMap<String , Object> Comments = new HashMap<>();
                    Comments.put("userName" ,currentUserName);
                    Comments.put("Profile" , userProfileID);
                    Comments.put("userID" , userID);
                    Comments.put("Comment", comment_txt);
                    Comments.put("TimeStamp" ,timeStamp);
                    Comments.put("commentID" , userID+timeStamp);
                    firebaseFirestore.collection("post").document(postID).collection("Comments").document(userID+timeStamp).set(Comments).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.cancel();
                            comment.setText("");
                        }
                    });
                }
            }
        });

        commentList.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        adapter = new CommentAdapter(commentDataList , postID);
        commentList.setAdapter(adapter);

        firebaseFirestore.collection("post").document(postID).collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange doc : value.getDocumentChanges()){
                    commentData mCommentData = doc.getDocument().toObject(commentData.class);
                    commentDataList.add(mCommentData);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }
}