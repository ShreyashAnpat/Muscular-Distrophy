package com.md.musculardistrophy.Adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.md.musculardistrophy.Model.messageData;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.md.musculardistrophy.R;

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
        if (messageDataList.get(position).getType().equals("text")){
            holder.message.setText(messageDataList.get(position).getMessage());
            holder.messageImage.setVisibility(View.GONE);
            holder.message.setVisibility(View.VISIBLE);
            holder.messageImageLoader.setVisibility(View.GONE);
            holder.imageCard.setVisibility(View.GONE);

            if (messageDataList.get(position).getSenderID().equals(auth.getCurrentUser().getUid())){
                holder.linearLayoutCompat.setGravity(Gravity.RIGHT);
                holder.message.setBackgroundResource(R.drawable.message_sender_background);
                holder.message.setTextColor(Color.BLACK);
            }
            else {
                holder.linearLayoutCompat.setGravity(Gravity.LEFT);
                holder.message.setBackgroundResource(R.drawable.message_reciver_background);
                holder.message.setTextColor(Color.WHITE);
            }
        }
        else {
            Picasso.get().load(messageDataList.get(position).getMessage()).into(holder.messageImage);
            holder.messageImage.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.GONE);
            holder.messageImageLoader.setVisibility(View.VISIBLE);
            holder.imageCard.setVisibility(View.VISIBLE);
            if (messageDataList.get(position).getSenderID().equals(auth.getCurrentUser().getUid())){
                holder.linearLayoutCompat.setGravity(Gravity.RIGHT);
            }
            else {
                holder.linearLayoutCompat.setGravity(Gravity.LEFT);
            }
        }

    }

    @Override
    public int getItemCount() {
        return messageDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message ;
        LinearLayoutCompat linearLayoutCompat ;
        ImageView messageImage ;
        LottieAnimationView messageImageLoader ;
        CardView imageCard ;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            linearLayoutCompat = itemView.findViewById(R.id.layout);
            messageImage = itemView.findViewById(R.id.messageImage);
            messageImageLoader = itemView.findViewById(R.id.messageImageLoader);
            imageCard = itemView.findViewById(R.id.imageCard);
        }
    }
}
