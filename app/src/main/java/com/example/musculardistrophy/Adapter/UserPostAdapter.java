package com.example.musculardistrophy.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musculardistrophy.Model.postData;
import com.example.musculardistrophy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.ContentValues.TAG;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.ViewHolder> {
    List<postData> postAdapters ;

    public UserPostAdapter(List<postData> postData) {
        this.postAdapters = postData ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post,parent,false);
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
    }

    @Override
    public int getItemCount() {
        return postAdapters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage ;
        TextView caption ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage  = itemView.findViewById(R.id.postImage);
            caption = itemView.findViewById(R.id.caption);
        }
    }
}
