package com.inkredibles.wema20;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseRole;

import java.util.ArrayList;

/*
The group adapter binds the array of user's groups to the inflated views of the recycler view. When
the view holder is clicked the group or role at that position is sent to the CurrentGroup Fragment
which is essentially a group details page.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private ArrayList<ParseRole> mGroups;
    private Context context;
    private onItemSelectedListener listener;

    public GroupAdapter(ArrayList <ParseRole> groups){
        mGroups = groups;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView groupImage;
        public TextView groupName;


        public ViewHolder(View itemView) {
            super(itemView);

            groupImage = (ImageView) itemView.findViewById(R.id.imageViewGroupIcon);
            groupName = (TextView) itemView.findViewById(R.id.groupName);

            itemView.setOnClickListener(this);

        }

        //obtain the current group/role and launch CurrentGroupFragment
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ParseRole currentRole = mGroups.get(position);
                if(currentRole != null){
                    if (context instanceof onItemSelectedListener) {
                        listener = (onItemSelectedListener) context;
                        listener.toCurrentGroup(currentRole);
                    } else {
                        throw new ClassCastException(context.toString()
                                + " must implement OnItemSelectedListener");
                    }
                }else {
                    Log.i("Group Adapter", "new role is null for some reason");
                }

            }

        }
    }

    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View userView = inflater.inflate(R.layout.item_group, parent, false);
        GroupAdapter.ViewHolder viewHolder = new GroupAdapter.ViewHolder(userView);
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {
        ParseRole role = mGroups.get(position);
        holder.groupName.setText(role.getName());

        //possibly upload a group picture if we have one
    }

    @Override
    public int getItemCount() { return mGroups.size(); }


}
