package com.md.musculardistrophy.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.md.musculardistrophy.Adapter.NotificationAdapter;
import com.md.musculardistrophy.Model.notificationData;
import com.md.musculardistrophy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class NotificationsFragment extends Fragment {
    RecyclerView notificationList ;
    List<notificationData> list ;
    NotificationAdapter adapter ;
    FirebaseFirestore firebaseFirestore ;
    FirebaseAuth auth ;
    LottieAnimationView notification;
    TextView notificationText ;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        notificationList = root.findViewById(R.id.notifications);
        firebaseFirestore = FirebaseFirestore.getInstance() ;
        notification = root.findViewById(R.id.lottieAnimationView3);
        notificationText = root.findViewById(R.id.notification_hint);
        auth = FirebaseAuth.getInstance() ;
        list = new ArrayList<>() ;

        notificationList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(list);
        notificationList.setAdapter(adapter);


        firebaseFirestore.collection("user").document(auth.getCurrentUser().getUid()).collection("notification").orderBy("TimeStamp" , Query.Direction.DESCENDING)
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

                Log.d(TAG, "onEvent: "+list.size());
                if (list.size() == 0 ){
                    notification.setVisibility(View.VISIBLE);
                    notificationText.setVisibility(View.VISIBLE);
                    notificationList.setVisibility(View.GONE);
                }
                else {
                    notificationText.setVisibility(View.GONE);
                    notification.setVisibility(View.GONE);
                    notificationList.setVisibility(View.VISIBLE);
                }

            }
        });


        return root;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(getView());;
                navController.navigate(R.id.navigation_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }
}