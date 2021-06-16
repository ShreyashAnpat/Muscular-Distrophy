package com.example.musculardistrophy.Message;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musculardistrophy.Adapter.MessageAdapter;
import com.example.musculardistrophy.Model.messageData;
import com.example.musculardistrophy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

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
    TextView userName ;
    EditText message ;
    RecyclerView messageList ;
    FirebaseFirestore firebaseFirestore ;
    FirebaseAuth auth ;
    String currentUserId , receiverId;
    MessageAdapter adapter ;
    List<messageData> messageDataList ;
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

        messageList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messageDataList);
        messageList.setAdapter(adapter);

        firebaseFirestore.collection("user").document(currentUserId).collection("message")
                .document(receiverId).collection(receiverId).orderBy("timeStamp" , Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                for (DocumentChange doc : value.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        messageData mMessageData = doc.getDocument().toObject(messageData.class);
                        messageDataList.add(mMessageData);
                        adapter.notifyDataSetChanged();
                        messageList.smoothScrollToPosition(messageDataList.size());
                    }
                    messageList.smoothScrollToPosition(messageDataList.size());
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                firebaseFirestore.collection("user").document(currentUserId).collection("message").document(receiverId).collection(receiverId).document().set(messageData);
                firebaseFirestore.collection("user").document(receiverId).collection("message").document(currentUserId).collection(currentUserId).document().set(messageData);
                message.setText("");


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


    }
}