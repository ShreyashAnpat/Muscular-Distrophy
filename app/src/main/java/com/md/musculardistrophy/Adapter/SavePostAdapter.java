package com.md.musculardistrophy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.md.musculardistrophy.ui.profile.savePostList;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.ContentValues.TAG;

public class SavePostAdapter extends RecyclerView.Adapter<SavePostAdapter.ViewHolder> {
    List<postData> postAdapters ;
    Context context ;

    public SavePostAdapter(List<postData> postData) {
        this.postAdapters = postData ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post,parent,false);
        context = parent.getContext() ;
        return  new SavePostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+ postAdapters.get(position).getPost());

        if (postAdapters.get(position).getPost().equals("")){
            holder.caption.setVisibility(View.VISIBLE);
            holder.caption.setText(postAdapters.get(position).getCaption());
        }
        else {
            Picasso.get().load(postAdapters.get(position).getPost()).into(holder.postImage);
            holder.caption.setVisibility(View.GONE);
        }

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, savePostList.class);
                intent.putExtra("position",String.valueOf(position));
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
        ConstraintLayout constraintLayout ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage  = itemView.findViewById(R.id.postImage);
            caption = itemView.findViewById(R.id.caption);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
