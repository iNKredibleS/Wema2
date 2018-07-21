package com.inkredibles.wema20;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inkredibles.wema20.models.Post;
import com.parse.ParseImageView;

import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private List<Post>mPosts;
    private Context context;

    //static public boolean archive;
    MainActivity main = new MainActivity();

    // Pass in the contact array into the constructor
    public PostsAdapter(List<Post> posts) {
        mPosts = posts;
    }


    public class ViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tvMessage;
        public ParseImageView ivPostImageView;
        public TextView tvItemTitle;
        public TextView tvUsername;

        public  ViewHolder(View itemView){
            super(itemView);
            //tvMessage = (TextView) itemView.findViewById()
            //tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            //TODO: use title and not message
            tvItemTitle = (TextView) itemView.findViewById(R.id.tvItemTitle);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            ivPostImageView = (ParseImageView) itemView.findViewById(R.id.ivPostImage);
            //tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("PostsAdapter","Clicked view");
            Intent intent = new Intent(context,MainActivity.class );
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                Post selectedPost = mPosts.get(position);
                //for parcels to be defined, remember to add the parcel dependencies in the build.gradle file
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(selectedPost));
                context.startActivity(intent);
            }

        }
    }

    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        if(main.getArchiveBool()){
            // Inflate the custom layout
            //View contactView = inflater.inflate(R.layout.item_archive, parent, false);
            // Return a new holder instance
           // ViewHolder viewHolder = new ViewHolder(contactView);
           //return viewHolder;
            return null;
        } else {
            // Inflate the custom layout
            View postView = inflater.inflate(R.layout.item_post, parent, false);
            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(postView);
            return viewHolder;

        }

    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PostsAdapter.ViewHolder viewHolder, int position) {
        // Get the post at the current position
        Post post = mPosts.get(position);
        viewHolder.tvMessage.setText(post.getMessage()); //TODO: bind the title and not the message
        viewHolder.tvItemTitle.setText(post.getTitle());
        //ParseFile file = post.getImage();
        viewHolder.ivPostImageView.setParseFile(post.getImage());
        viewHolder.ivPostImageView.loadInBackground();
        if(main.getArchiveBool()) viewHolder.tvUsername.setText(post.getUser().getUsername());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    //clean all the elements in the recycler
    public void clear(){
        mPosts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> posts){
        mPosts.addAll(posts);
        notifyDataSetChanged();
    }

}
