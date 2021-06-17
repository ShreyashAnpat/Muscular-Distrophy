package com.example.musculardistrophy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musculardistrophy.Message.ChatActivity;
import com.example.musculardistrophy.Model.userData;
import com.example.musculardistrophy.R;
import com.example.musculardistrophy.ui.home.userProfile;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAccountAdapter extends RecyclerView.Adapter<UserAccountAdapter.ViewHolder> {
    List<userData> userDataList ;
    Context context;
    public UserAccountAdapter(List<userData> userDataList, Context context) {
        this.userDataList = userDataList ;
        this.context = context ;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_account,parent,false);
        return new UserAccountAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Picasso.get().load(userDataList.get(position).getProfile()).into(holder.profile);
        holder.userName.setText(userDataList.get(position).getUserName());
        holder.account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context , userProfile.class);
                intent.putExtra("userID", userDataList.get(position).getUserID());
                context.startActivity(intent);
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
            account = itemView.findViewById(R.id.click);

        }
    }
}
