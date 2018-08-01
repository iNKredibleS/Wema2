package com.inkredibles.wema20;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inkredibles.wema20.models.Rak;
import com.inkredibles.wema20.models.User;
import com.parse.ParseException;

import java.util.List;

public class RakAdapter extends RecyclerView.Adapter<RakAdapter.ViewHolder> {

    private List<Rak> mRaks;
    private Context context;

    public RakAdapter(List<Rak> groupRaks){
        mRaks = groupRaks;
    }


    @Override
    public RakAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_rak, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //fields that I want in my view holder
        public TextView tvGroupRakCreator;
        public TextView tvGroupRak;


        //constructor for the viewholder find views by id's
        public ViewHolder(View itemView){
            super(itemView);
            tvGroupRakCreator = itemView.findViewById(R.id.tvGroupRakCreator);
            tvGroupRak = itemView.findViewById(R.id.tvGroupRak);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rak rak = mRaks.get(position);
        holder.tvGroupRak.setText(rak.getTitle());
        try {
            User user = (User) rak.fetchIfNeeded().getParseUser("current_user");
            holder.tvGroupRakCreator.setText(user.fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mRaks.size();
    }

    public void addAll(List<Rak> raks){
        mRaks.addAll(raks);
        notifyDataSetChanged();
    }
    public void clear(){
        mRaks.clear();
        notifyDataSetChanged();
    }
}
