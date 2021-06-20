package com.example.musculardistrophy.Message;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musculardistrophy.Adapter.MessageAdapter;
import com.example.musculardistrophy.Model.messageData;
import com.example.musculardistrophy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    ImageView back , options ;
    CircleImageView profile ,camera ,send ;
    TextView userName , note ;
    EditText message ;
    RecyclerView messageList ;
    FirebaseFirestore firebaseFirestore ;
    FirebaseAuth auth ;
    String currentUserId , receiverId;
    MessageAdapter adapter ;
    List<messageData> messageDataList ;
    ConstraintLayout block;
    MaterialCardView inbox ;
    Button sendRequest ;
    Uri imageUri ;
    StorageReference Folder , ImageName;
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        back =findViewById(R.id.back);
        options = findViewById(R.id.options);
        profile = findViewById(R.id.profile);
        camera = findViewById(R.id.camera);
        send = findViewById(R.id.send);
        userName = findViewById(R.id.userName);
        message =findViewById(R.id.txt_message);
        messageList = findViewById(R.id.messageList);
        receiverId = getIntent().getStringExtra("userID");
        firebaseFirestore = FirebaseFirestore.getInstance() ;
        auth= FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        messageDataList = new ArrayList<>();
        block = findViewById(R.id.request);
        inbox = findViewById(R.id.materialCardView8);
        sendRequest = findViewById(R.id.sendRequest);
        note = findViewById(R.id.note);

        progressDialog = new ProgressDialog(this );
        progressDialog.setCanceledOnTouchOutside(false);

        messageList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messageDataList);
        messageList.setAdapter(adapter);

        firebaseFirestore.collection("user").document(currentUserId).collection("message").document(receiverId)
                .collection(receiverId).orderBy("timeStamp" , Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
               if (!value.isEmpty()) {
                   for (DocumentChange doc : value.getDocumentChanges()) {
                       if (doc.getType() == DocumentChange.Type.ADDED) {
                           messageData mMessageData = doc.getDocument().toObject(messageData.class);
                           messageDataList.add(mMessageData);
                           adapter.notifyDataSetChanged();
                           messageList.smoothScrollToPosition(messageDataList.size());
                       }
//                       messageList.smoothScrollToPosition(messageDataList.size());

                   }
               }
            }
        });

        firebaseFirestore.collection("user").document(currentUserId).collection("message").document(receiverId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (value.exists()){

                        if (value.get("blockAccount").equals("true") && value.get("blockedUserID").equals(currentUserId)){
                            block.setVisibility(View.VISIBLE);
                            messageList.setVisibility(View.GONE);
                            inbox.setVisibility(View.GONE);
                        }
                        else if (value.get("blockAccount").equals("true") && value.get("blockedUserID").equals(receiverId)){
                            block.setVisibility(View.VISIBLE);
                            messageList.setVisibility(View.GONE);
                            inbox.setVisibility(View.GONE);
                            sendRequest.setVisibility(View.GONE);
                            options.setVisibility(View.GONE);
                            note.setText("You are blocked..!");
                        }
                        else {
                            messageList.setVisibility(View.VISIBLE);
                            block.setVisibility(View.GONE);
                            inbox.setVisibility(View.VISIBLE);
                            options.setVisibility(View.VISIBLE);
                            sendRequest.setVisibility(View.VISIBLE);
                            note.setText("");
                        }

                }
            }
        });

        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String , Object> blockAccount = new HashMap<>();
                blockAccount.put("blockAccount", "false");
                blockAccount.put("blockedUserID", currentUserId);
                firebaseFirestore.collection("user").document(currentUserId).collection("message").document(receiverId).set(blockAccount);
                firebaseFirestore.collection("user").document(receiverId).collection("message").document(currentUserId).set(blockAccount);
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog =  new BottomSheetDialog(ChatActivity.this);
                bottomSheetDialog.setContentView(R.layout.chat_option_bottomsheet);
                bottomSheetDialog.getDismissWithAnimation();
                bottomSheetDialog.show();



                ConstraintLayout blockAccount = bottomSheetDialog.findViewById(R.id.blockAccount);

                blockAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String , Object> blockAccount = new HashMap<>();
                        blockAccount.put("blockAccount", "true");
                        blockAccount.put("blockedUserID", currentUserId);

                        firebaseFirestore.collection("user").document(currentUserId).collection("message").document(receiverId).set(blockAccount);
                        firebaseFirestore.collection("user").document(receiverId).collection("message").document(currentUserId).set(blockAccount);
                        bottomSheetDialog.cancel();

                    }
                });

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.getText().toString().equals("")){
                    progressDialog.setMessage("Sending Message ....");
                    progressDialog.show();
                    String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                    HashMap<String , Object> messageData = new HashMap<>();
                    messageData.put("senderID" , currentUserId);
                    messageData.put("message" , message.getText().toString());
                    messageData.put("timeStamp" , timeStamp);
                    messageData.put("seen" , "1");
                    messageData.put("date" , currentDate);
                    messageData.put("time" , currentTime);
                    messageData.put("type" , "text");

                    firebaseFirestore.collection("user").document(currentUserId).collection("message").document(receiverId).collection(receiverId).document().set(messageData);
                    firebaseFirestore.collection("user").document(receiverId).collection("message").document(currentUserId).collection(currentUserId).document().set(messageData);
                    message.setText("");
                    progressDialog.cancel();
                }
                else {
                    Toast.makeText(ChatActivity.this, "Write a message...", Toast.LENGTH_SHORT).show();
                }

            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firebaseFirestore.collection("user").document(receiverId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                Picasso.get().load(value.get("Profile").toString()).into(profile);
                userName.setText(value.get("userName").toString());
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });
    }

    private void selectImage() {

        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                sendMessage(imageUri);
            }
        }
    }

    private void sendMessage(Uri imageUri) {
        progressDialog.setMessage("Sending image ... ");
        progressDialog.show();
        Folder = FirebaseStorage.getInstance().getReference().child("Message");
        ImageName = Folder.child(auth.getCurrentUser().getUid() + imageUri.getLastPathSegment());
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        ImageName.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                        HashMap<String , Object> MessageData = new HashMap<>();
                        MessageData.put("senderID" , currentUserId);
                        MessageData.put("message" , uri.toString());
                        MessageData.put("timeStamp" , timeStamp);
                        MessageData.put("seen" , "1");
                        MessageData.put("date" , currentDate);
                        MessageData.put("time" , currentTime);
                        MessageData.put("type" , "Image");

                        firebaseFirestore.collection("user").document(currentUserId).collection("message").document(receiverId).collection(receiverId).document().set(MessageData);
                        firebaseFirestore.collection("user").document(receiverId).collection("message").document(currentUserId).collection(currentUserId).document().set(MessageData);
                        progressDialog.cancel();
                    }
                });
            }
        });
    }

}