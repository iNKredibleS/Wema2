package com.inkredibles.wema20;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inkredibles.wema20.models.Post;
import com.parse.ParseImageView;

import java.util.List;

/*
    The posts adapter converts the post at a specific position in the posts or archives list
    into an item view row in the recycler view. Using the boolean archive defined in the main activity
    this Posts adapter can be used to convert the archived posts and the public feed posts by creating
    different viewholders for the two different uses.
 */

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private List<Post>mPosts;
    private Context context;
    private ViewHolderListener  viewHolderListener;
    // private onItemSelectedListener listener;

    //static public boolean archive;
    MainActivity main = new MainActivity();

    // Pass in the contact array into the constructor
    public PostsAdapter(List<Post> posts) {
        mPosts = posts;
    }


    public class ViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ParseImageView ivPostImageView;
        public TextView tvItemTitle;
        public TextView tvUsername;

        public  ViewHolder(View itemView){
            super(itemView);
            tvItemTitle = (TextView) itemView.findViewById(R.id.tvItemTitle);
            ivPostImageView = (ParseImageView) itemView.findViewById(R.id.ivPostImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("PostsAdapter","Clicked view");
           // Intent intent = new Intent(context,MainActivity.class );
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                Post selectedPost = mPosts.get(position);
                //for parcels to be defined, remember to add the parcel dependencies in the build.gradle file
                //intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(selectedPost));
               // context.startActivity(intent);
                if (viewHolderListener != null) viewHolderListener.onViewHolderClicked(selectedPost, ivPostImageView);
            }


        }
    }

    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if(main.getArchiveBool()){
            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.item_archive, parent, false);
            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(contactView);
           return viewHolder;
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

    public interface ViewHolderListener{
        public void onViewHolderClicked(Post post,ParseImageView parseImageView);
    }
//
    public void setViewHolderListener(ViewHolderListener viewHolderListener){
        this.viewHolderListener = viewHolderListener;
    }

}
