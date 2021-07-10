package com.md.musculardistrophy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.md.musculardistrophy.Message.ChatActivity;
import com.md.musculardistrophy.Model.userData;
import com.md.musculardistrophy.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageUserListAdapter extends RecyclerView.Adapter<MessageUserListAdapter.ViewHolder> {
    List<userData> userDataList ;
    Context context ;
    FirebaseFirestore firebaseFirestore ;
    FirebaseAuth auth ;
    String currentUserID;
    int smscount ;
    public MessageUserListAdapter(List<userData> userDataList) {
        this.userDataList =userDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_account_list,parent,false);
        context = parent.getContext() ;
        firebaseFirestore  = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance() ;
        currentUserID = auth.getCurrentUser().getUid();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Picasso.get().load(userDataList.get(position).getProfile()).into(holder.profile);
        holder.userName.setText(userDataList.get(position).getUserName());
        holder.account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("user").document(currentUserID).collection("message").document(userDataList.get(position).getUserID()).collection(userDataList.get(position).getUserID())
                        .whereEqualTo("seen", "1").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        for (DocumentSnapshot doc : value.getDocuments()){
                            if (!currentUserID.equals(doc.get("senderID").toString())){
                                HashMap<String , Object > seen  = new HashMap<>();
                                seen.put("seen", "0");
                                doc .getReference().update(seen);
                            }

                        }

                    }
                });

                Intent intent = new Intent(context , ChatActivity.class);
                intent.putExtra("userID" , userDataList.get(position).getUserID());
                context.startActivity(intent);

            }
        });

        firebaseFirestore.collection("user").document(currentUserID).collection("message").document(userDataList.get(position).getUserID()).collection(userDataList.get(position).getUserID())
                .whereEqualTo("seen", "1").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
//                String count = String.valueOf(value.getDocuments().size());
//                holder.count.setText(count);

                for (DocumentSnapshot documentSnapshot : value.getDocuments()){
                    if (!currentUserID.equals(documentSnapshot.get("senderID"))){
                        smscount++;
                        holder.count.setText(String.valueOf(smscount));
                    }
                }
                if (String.valueOf(smscount).equals("0")){
                   holder.count.setVisibility(View.GONE);
               }
               else {
                   holder.count.setVisibility(View.VISIBLE);
               }
                smscount = 0;
//

            }
        });

    }

    @Override
    public int getItemCount() {
        return userDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile;
        TextView userName , count ;
        ConstraintLayout account ;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile);
            userName = itemView.findViewById(R.id.userName);
            count = itemView.findViewById(R.id.count);
            account = itemView.findViewById(R.id.account);
        }
    }
}
