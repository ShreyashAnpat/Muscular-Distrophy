package com.example.musculardistrophy.Adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musculardistrophy.Model.messageData;
import com.example.musculardistrophy.R;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    List<messageData> messageDataList ;
    FirebaseAuth auth ;

    public MessageAdapter(List<messageData> messageDataList) {
        this.messageDataList = messageDataList ;
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.message.setText(messageDataList.get(position).getMessage());
        if (messageDataList.get(position).getSenderID().equals(auth.getCurrentUser().getUid())){
            holder.linearLayoutCompat.setGravity(Gravity.RIGHT);
            holder.message.setBackgroundResource(R.drawable.message_sender_background);
        }
        else {
            holder.linearLayoutCompat.setGravity(Gravity.LEFT);
            holder.message.setBackgroundResource(R.drawable.message_reciver_background);
            holder.message.setTextColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return messageDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message ;
        LinearLayoutCompat linearLayoutCompat ;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            linearLayoutCompat = itemView.findViewById(R.id.layout);
        }
    }
}
