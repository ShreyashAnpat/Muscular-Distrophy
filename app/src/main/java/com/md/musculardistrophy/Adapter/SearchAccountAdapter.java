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

import com.md.musculardistrophy.Model.userData;

import com.md.musculardistrophy.ui.home.userProfile;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.md.musculardistrophy.R;

public class SearchAccountAdapter extends RecyclerView.Adapter<SearchAccountAdapter.ViewHolder> {
    List<userData> userDataList ;
    Context context ;
    public SearchAccountAdapter(List<userData> userDataList) {
        this.userDataList =  userDataList ;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_account_list,parent,false);
        context = parent.getContext() ;
        return new  SearchAccountAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Picasso.get().load(userDataList.get(position).getProfile()).into(holder.profile);
        holder.userName.setText(userDataList.get(position).getUserName());
        holder.account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , userProfile.class) ;
                intent.putExtra("userID" , userDataList.get(position).getUserID());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
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

            account.setClickable(true);
        }
    }
}
