package com.md.musculardistrophy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.md.musculardistrophy.Model.postData;
import com.md.musculardistrophy.R;
import com.md.musculardistrophy.ui.profile.userPostList;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.ViewHolder> {
    List<postData> postAdapters ;
    Context context ;

    public UserPostAdapter(List<postData> postData) {
        this.postAdapters = postData ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post,parent,false);
        context = parent.getContext();
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (postAdapters.get(position).getPost().equals("")){
            holder.caption.setVisibility(View.VISIBLE);
            holder.caption.setText(postAdapters.get(position).getCaption());
        }
        else {
            Picasso.get().load(postAdapters.get(position).getPost()).into(holder.postImage);
            holder.caption.setVisibility(View.GONE);
        }

        holder.userPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, userPostList.class);
                intent.putExtra("position", String.valueOf(position) );
                intent.putExtra("UID" , postAdapters.get(position).getUID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postAdapters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage ;
        TextView caption ;
        ConstraintLayout userPost ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage  = itemView.findViewById(R.id.postImage);
            caption = itemView.findViewById(R.id.caption);
            userPost = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
