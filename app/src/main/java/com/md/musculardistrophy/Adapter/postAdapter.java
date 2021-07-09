package com.md.musculardistrophy.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.md.musculardistrophy.Model.postData;
import com.md.musculardistrophy.Notification.ApiService;
import com.md.musculardistrophy.Notification.Client;
import com.md.musculardistrophy.Notification.Data;
import com.md.musculardistrophy.Notification.NotificationSender;

import com.md.musculardistrophy.ui.home.CommentActivity;
import com.md.musculardistrophy.ui.home.EditPostActivity;
import com.md.musculardistrophy.ui.home.lickAccountList;
import com.md.musculardistrophy.ui.home.userProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.ResponseBody;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import com.md.musculardistrophy.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class postAdapter extends RecyclerView.Adapter<postAdapter.ViewHolder> {
    List<postData> postData;
    Context context ;
    FirebaseFirestore firebaseFirestore ;
    FirebaseAuth auth ;

    String fcmUrl = "https://fcm.googleapis.com/",Token;
    String userID  , uid ,TimeStamp ,post , userName , postID , captions , currentUserName , CurrentProfile;
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

        if (post.isEmpty()){
            holder.postImage.setVisibility(View.GONE);
            holder.imageLoader.setVisibility(View.GONE);
        }
        else {
            holder.imageLoader.setVisibility(View.VISIBLE);
            Picasso.get().load(post).into(holder.postImage);
            holder.postImage.setVisibility(View.VISIBLE);
        }

        if (postData.get(position).getCaption().equals("")){
           holder.captions.setVisibility(View.GONE);
        }
        else {
            holder.captions.setText(postData.get(position).getCaption());
            holder.captions.setVisibility(View.VISIBLE);

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
                            holder.lickCount.setText(value.getDocuments().size() + " Likes");
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


        holder.lickCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(context , lickAccountList.class);
                    intent.putExtra("postID", postData.get(position).getPostID());
                    context.startActivity(intent);

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
                firebaseFirestore.collection("post").document(postData.get(position).getPostID()).collection("lick").document(userID).set(lickData);

                holder.licked.setVisibility(View.VISIBLE);
                holder.licked.playAnimation();
                holder.lick.setVisibility(View.INVISIBLE);

                firebaseFirestore.collection("user").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        currentUserName = value.getString("userName");
                        CurrentProfile  = value.getString("Profile");
                    }
                });
                firebaseFirestore.collection("Tokens").document(postData.get(position).getUID()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!postData.get(position).getUID().equals(userID)){
                            if(postData.get(position).getPost().equals("")){
                                Token = value.getString("token");
                                String extra = currentUserName + "lick your post.";
                                String data = "Tab to see this post";
                                sendNotification(CurrentProfile ,Token, extra ,data  );
                                uploadLickNotificationData(postData.get(position).getUID() ,postData.get(position).getProfile());
                            }
                            else {
                                Token = value.getString("token");
                                String extra = currentUserName + " lick your post .";
                                String data = "Tab to see this post";
                                sendNotification(postData.get(position).getPost() ,Token, extra ,data  );
                                uploadLickNotificationData(postData.get(position).getUID(),postData.get(position).getPost());

                            }
                        }
                    }
                });
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
                if (!postData.get(position).getUID().equals(auth.getCurrentUser().getUid())){
                    Intent intent = new Intent(context , userProfile.class);
                    intent.putExtra("userID", postData.get(position).getUID());
                    context.startActivity(intent);
                }
                else {
                    Activity activity = (Activity) context;
                    NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                    navController.navigate(R.id.navigation_profile);
                }

            }
        });

        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog =  new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(R.layout.post_bottom_sheet);
                bottomSheetDialog.getDismissWithAnimation();
                bottomSheetDialog.show();

                ConstraintLayout editPost, deletePost , reportPost , savePost ;

                editPost = bottomSheetDialog.findViewById(R.id.editPost);
                deletePost = bottomSheetDialog.findViewById(R.id.deletePost);
                reportPost = bottomSheetDialog.findViewById(R.id.reportPost);
                savePost = bottomSheetDialog.findViewById(R.id.savePost);


                if (userID.equals(postData.get(position).getUID())){
                    editPost.setVisibility(View.VISIBLE);
                    deletePost.setVisibility(View.VISIBLE);
                    reportPost.setVisibility(View.GONE);
                }

                reportPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.cancel();
                    }
                });

                savePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.savePost.setVisibility(View.INVISIBLE);
                        holder.savedPost.setVisibility(View.VISIBLE);
                        postID = postData.get(position).getPostID();

                        HashMap<String , Object> savePost = new HashMap<>();
                        savePost.put("TimeStamp" , timeStamp);
                        savePost.put("postID" , postID);

                        firebaseFirestore.collection("user").document(userID).collection("savePost").document(postID).set(savePost);
                        bottomSheetDialog.cancel();
                    }
                });

                editPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context , EditPostActivity.class);
                        intent.putExtra("postId", postData.get(position).getPostID());
                        context.startActivity(intent);
                        bottomSheetDialog.cancel();
                    }
                });

                deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseFirestore.collection("post").document(postData.get(position).getPostID()).delete();
                        firebaseFirestore.collection("user").document(userID).collection("savePost").document(postData.get(position).getPostID()).delete();
                        bottomSheetDialog.cancel();
                    }
                });


            }
        });

    }



    @Override
    public int getItemCount() {
        return postData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView savePost , lick , savedPost  ,comment , option;
        LottieAnimationView licked ;
        TextView username , lickCount , commentCount  , captions;
        CircleImageView profile ;
        ImageView postImage ;
        LottieAnimationView imageLoader ;
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
            option = itemView.findViewById(R.id.option);
            imageLoader = itemView.findViewById(R.id.imageLoader);
        }

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

    private void uploadLickNotificationData(String postUserID, String profile) {
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        HashMap<String, Object> notification = new HashMap<>();
        notification.put("userID" , userID);
        notification.put("message" , "Liked Your post");
        notification.put("postImage" , profile);
        notification.put("postID" , postID);
        notification.put("TimeStamp" ,timeStamp );
        firebaseFirestore.collection("user").document(postUserID).collection("notification").document().set(notification);
    }

}
