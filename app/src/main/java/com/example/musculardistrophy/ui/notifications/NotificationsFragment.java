package com.example.musculardistrophy.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musculardistrophy.Adapter.NotificationAdapter;
import com.example.musculardistrophy.Model.notificationData;
import com.example.musculardistrophy.Model.postData;
import com.example.musculardistrophy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationsFragment extends Fragment {
    RecyclerView notificationList ;
    List<notificationData> list ;
    NotificationAdapter adapter ;
    FirebaseFirestore firebaseFirestore ;
    FirebaseAuth auth ;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        notificationList = root.findViewById(R.id.notifications);
        firebaseFirestore = FirebaseFirestore.getInstance() ;
        auth = FirebaseAuth.getInstance() ;
        list = new ArrayList<>() ;

        notificationList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(list);
        notificationList.setAdapter(adapter);


        firebaseFirestore.collection("user").document(auth.getCurrentUser().getUid()).collection("notification").orderBy("TimeStamp" , Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.equals(null)){
                    for (DocumentChange doc : value.getDocumentChanges()){
                        notificationData mPostData = doc.getDocument().toObject(notificationData.class);
                         list.add(mPostData);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return root;
    }
}