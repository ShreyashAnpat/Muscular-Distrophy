package com.md.musculardistrophy.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.md.musculardistrophy.Adapter.CommentAdapter;
import com.md.musculardistrophy.Model.commentData;
import com.md.musculardistrophy.Notification.ApiService;
import com.md.musculardistrophy.Notification.Client;
import com.md.musculardistrophy.Notification.Data;
import com.md.musculardistrophy.Notification.NotificationSender;
import com.md.musculardistrophy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.ResponseBody;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class CommentActivity extends AppCompatActivity {

    CircleImageView postProfile , commentProfile;
    ImageView send ,postImage , lick , commentIcon , savePost , savedPost;
    TextView userName ,caption , lickCount, commentCount;
    EditText comment ;
    RecyclerView commentList ;
    String userID ,postID , userProfileID  , currentUserName , postTimeStamp , postUserName , postUserProfile , Token;
    FirebaseAuth auth ;
    CommentAdapter adapter ;
    FirebaseFirestore firebaseFirestore ;
    ProgressDialog progressDialog ;
    List<commentData> commentDataList ;
    String caption_txt ;
    String postImageUri  , postUserID;
    NestedScrollView scrollView ;
    LottieAnimationView licked ;
    String fcmUrl = "https://fcm.googleapis.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        postProfile = findViewById(R.id.profile);
        commentProfile = findViewById(R.id.circleImageView);
        comment = findViewById(R.id.commentText);
        commentIcon = findViewById(R.id.commentIcon);
        send = findViewById(R.id.imageView7);
        userName = findViewById(R.id.username);
        commentList = findViewById(R.id.CommentList);
        postImage = findViewById(R.id.postImage);
        caption = findViewById(R.id.captions);
        commentDataList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        postID = getIntent().getStringExtra("postID");
        licked = findViewById(R.id.licked);
        lick = findViewById(R.id.like);
        savePost = findViewById(R.id.savePosts);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        savedPost = findViewById(R.id.savedPosts);
        lickCount = findViewById(R.id.likeCount);
        scrollView = findViewById(R.id.scrollView);
        commentCount = findViewById(R.id.commentCounts);
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));


        firebaseFirestore.collection("post").document(postID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    postImageUri = value.getString("post");
                    caption_txt = value.getString("Caption");
                    postUserID = value.getString("UID");
                    postTimeStamp = value.getString("TimeStamp");
                    postUserName = value.getString("username");
                    postUserProfile = value.getString("Profile");
                    Picasso.get().load(postUserProfile).into(postProfile);
                    if (postImageUri.equals("")) {
                        postImage.setVisibility(View.GONE);
                    } else {
                        Picasso.get().load(value.getString("post")).into(postImage);
                    }
                    if (caption_txt.equals("")) {
                        caption.setVisibility(View.GONE);
                    } else {
                        caption.setText(caption_txt);
                        caption.setVisibility(View.VISIBLE);
                    }
                    userName.setText(value.getString("username"));
                    value.getReference().collection("lick").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            lickCount.setText(value.getDocuments().size() + " Likes");
                        }
                    });
                    value.getReference().collection("lick").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null) {
                                if (value.get("userName") != null) {
                                    licked.setVisibility(View.VISIBLE);
                                    licked.playAnimation();
                                    lick.setVisibility(View.INVISIBLE);
                                }

                            }
                        }
                    });
                    value.getReference().collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            commentCount.setText(value.getDocuments().size() + " Comments");
                        }
                    });
                }
                else {
                    scrollView.setVisibility(View.GONE);
                    Toast.makeText(CommentActivity.this, "This post is deleted ... ", Toast.LENGTH_LONG).show();
                }
            }
        });


        firebaseFirestore.collection("user").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userProfileID = value.getString("Profile");
                currentUserName = value.getString("userName");
                Picasso.get().load(userProfileID).into(commentProfile);

                value.getReference().collection("savePost").document(postID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.get("postID")!= null){
                            savePost.setVisibility(View.INVISIBLE);
                            savedPost.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });

        lick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String , Object> lickData = new HashMap<>();
                lickData.put("userID", userID);
                lickData.put("timeStamp", timeStamp);
                lickData.put("userName" ,currentUserName );
                firebaseFirestore.collection("post").document(postID).collection("lick").document(userID).set(lickData);

                licked.setVisibility(View.VISIBLE);
                licked.playAnimation();
                lick.setVisibility(View.INVISIBLE);
                firebaseFirestore.collection("Tokens").document(postUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!postUserID.equals(userID)){
                            if(postImageUri.equals("")){
                                Token = value.getString("token");
                                String extra = currentUserName + "like your post.";
                                String data = "Tab to see this post";
                                sendNotification(userProfileID ,Token, extra ,data  );
                                String message = " Like your post";
                                uploadLickNotificationData(postUserID , userProfileID, message);
                            }
                            else {
                                Token = value.getString("token");
                                String extra = currentUserName + " lick your post .";
                                String data = "Tab to see this post";
                                String message = " Like your post";
                                sendNotification(postImageUri ,Token, extra ,data  );
                                uploadLickNotificationData(postUserID,postImageUri, message);

                            }
                        }
                    }
                });
            }

        });

        licked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("post").whereEqualTo("UID",postUserID ).whereEqualTo("TimeStamp", postTimeStamp ).whereEqualTo("post",postImageUri).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                            doc.getReference().collection("lick").document(userID).delete();
                            Toast.makeText(CommentActivity.this, "Your post is deleted ... !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                licked.setVisibility(View.INVISIBLE);
                lick.setVisibility(View.VISIBLE);
            }
        });

        savePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePost.setVisibility(View.INVISIBLE);
                savedPost.setVisibility(View.VISIBLE);
                HashMap<String , Object> savePost = new HashMap<>();
                savePost.put("TimeStamp" , timeStamp);
                savePost.put("postID" , postID);

                firebaseFirestore.collection("user").document(userID).collection("savePost").document(postID).set(savePost);
            }
        });

        savedPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePost.setVisibility(View.VISIBLE);
                savedPost.setVisibility(View.GONE);
                firebaseFirestore.collection("user").document(userID).collection("savePost").document(postID).delete();

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
                    firebaseFirestore.collection("Tokens").document(postUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (postImageUri.equals("")){
                                Log.d(TAG, "onEvent: " + value.getString("token") );
                                String message = " Comment on your Post";
                                uploadLickNotificationData(postUserID,userProfileID , message);
                                sendNotification(userProfileID , value.getString("token") ,currentUserName+"comment On your post" ,"Tap to see this comment");
                            }
                            else {
                                String message = " Comment on your Post";
                                sendNotification(postImageUri,value.getString("token") ,currentUserName+"comment On your post" ,"Tap to see this comment");
                                uploadLickNotificationData(postUserID,postImageUri, message);
                            }

                        }
                    });

                }
                Log.d(TAG, "onClick: "+ postUserID);
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

    private void sendNotification(String s, String token, String title, String msg) {
        Data data = new Data(title,msg , s);
        Log.d(TAG, "ShowNotification: "+ msg);
        NotificationSender notificationSender = new NotificationSender(data,token);

        ApiService apiService = Client.getRetrofit(fcmUrl).create(ApiService.class);

        apiService.sendNotification(notificationSender).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void uploadLickNotificationData(String postUserID, String profile, String message) {
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        HashMap<String, Object> notification = new HashMap<>();
        notification.put("userID" , userID);
        notification.put("message" , message);
        notification.put("postImage" , profile);
        notification.put("postID" , postID);
        notification.put("TimeStamp" , timeStamp);
        firebaseFirestore.collection("user").document(postUserID).collection("notification").document().set(notification);
    }

}