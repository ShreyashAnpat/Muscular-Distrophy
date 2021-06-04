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

import com.example.musculardistrophy.Model.notificationData;
import com.example.musculardistrophy.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    List<notificationData> notificationDataList ;
    FirebaseFirestore firebaseFirestore ;
    public NotificationAdapter(List<notificationData> list) {
        this.notificationDataList = list ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout,parent,false);
        firebaseFirestore  = FirebaseFirestore.getInstance();
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(notificationDataList.get(position).getPostImage()).into(holder.notificationImage);
        firebaseFirestore.collection("user").document(notificationDataList.get(position).getUserID()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Picasso.get().load(value.getString("Profile")).into(holder.profile);
                String message = "<b>" +  value.getString("userName") + "</b>" + notificationDataList.get(position).getMessage();
                holder.message.setText(Html.fromHtml(message));
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile ;
        TextView message ;
        ImageView notificationImage ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile);
            message = itemView.findViewById(R.id.message);
            notificationImage = itemView.findViewById(R.id.notificationImage);
        }
    }
}
