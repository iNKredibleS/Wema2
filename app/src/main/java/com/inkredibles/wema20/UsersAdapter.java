package com.inkredibles.wema20;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<ParseUser> mUsers; //why is this neccessary?
    private Context context;

    MainActivity main = new MainActivity();

    public UsersAdapter(List<ParseUser> allUsers) {
        mUsers = allUsers;
    }


    public class ViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivUserProfile;
        public TextView tvUser;

        public ViewHolder(View itemView) {
            super(itemView);

            ivUserProfile = (ImageView) itemView.findViewById(R.id.ivUserProfile);
            tvUser = (TextView) itemView.findViewById(R.id.tvUser);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View userView = inflater.inflate(R.layout.item_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user = mUsers.get(position);
        holder.tvUser.setText(user.getUsername());
    }

    @Override
    public int getItemCount() { return mUsers.size(); }
}
