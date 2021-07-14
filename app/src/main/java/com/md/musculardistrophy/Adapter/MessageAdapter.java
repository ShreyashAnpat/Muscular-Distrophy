package com.md.musculardistrophy.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.md.musculardistrophy.Message.ChatActivity;
import com.md.musculardistrophy.Model.messageData;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.md.musculardistrophy.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    List<messageData> messageDataList ;
    FirebaseAuth auth ;
    Context context ;
    String userID ;
    FirebaseFirestore firebaseFirestore ;

    public MessageAdapter(List<messageData> messageDataList) {
        this.messageDataList = messageDataList ;
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false);
        context = parent.getContext();
        firebaseFirestore  = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
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


//        holder.linearLayoutCompat.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (messageDataList.get(position).getSenderID().equals(auth.getCurrentUser().getUid())){
//                    new AlertDialog.Builder(context)
//                            .setMessage("Delete Message !")
//                            .setNegativeButton("Delete for Me ", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    firebaseFirestore.collection("user").document(userID).collection("message")
//                                            .document(messageDataList.get(position).getSenderID()).collection(messageDataList.get(position).getSenderID())
//                                            .document(messageDataList.get(position).getDate()+messageDataList.get(position).getTime()).delete();
//                                }
//                            })
//                            .setPositiveButton("Delete for Everyone ", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Toast.makeText(context, "Delete from Me", Toast.LENGTH_SHORT).show();
//                                }
//                            })
//                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
//                                }
//                            }).show();
//
//
//                }
//                else {
//                    new AlertDialog.Builder(context)
//                            .setMessage("Cancel")
//                            .setPositiveButton("Cancel ", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            })
//                            .setNeutralButton("Delete for Me", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                   firebaseFirestore.collection("user").document(userID).collection("message")
//                                           .document(messageDataList.get(position).getSenderID()).collection(messageDataList.get(position).getSenderID())
//                                           .document(messageDataList.get(position).getDate()+messageDataList.get(position).getTime()).delete();
//                                }
//                            }).show();
//                }
//
//                return false;
//            }
//        });

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
