package com.md.musculardistrophy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.md.musculardistrophy.Message.ChatActivity;
import com.md.musculardistrophy.Model.userData;
import com.md.musculardistrophy.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageUserListAdapter extends RecyclerView.Adapter<MessageUserListAdapter.ViewHolder> {
    List<userData> userDataList ;
    Context context ;
    FirebaseFirestore firebaseFirestore ;
    FirebaseAuth auth ;
    String currentUserName ;

    public MessageUserListAdapter(List<userData> userDataList) {
        this.userDataList =userDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_account_list,parent,false);
        context = parent.getContext() ;
        firebaseFirestore  = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance() ;
        currentUserName = auth.getCurrentUser().getUid();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Picasso.get().load(userDataList.get(position).getProfile()).into(holder.profile);
        holder.userName.setText(userDataList.get(position).getUserName());
        holder.account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , ChatActivity.class);
                intent.putExtra("userID" , userDataList.get(position).getUserID());
                context.startActivity(intent);
            }
        });

//        firebaseFirestore.collection("user").document(currentUserName).collection(userDataList.get(position).getUserID()).

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
