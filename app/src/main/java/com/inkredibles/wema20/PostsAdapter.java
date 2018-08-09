package com.inkredibles.wema20;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inkredibles.wema20.models.Post;
import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.util.ArrayList;
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
    private  LinearLayout linearLayout;

    MainActivity main = new MainActivity();

    // Pass in the contact array into the constructor
    public PostsAdapter(List<Post> posts) {
        mPosts =  posts;
    }

    public class ViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ParseImageView ivPostImageView;
        public TextView tvItemTitle;
        public TextView tvUsername;
        public CardView cardView;

        public  ViewHolder(View itemView){
            super(itemView);
            context = itemView.getContext();
            tvItemTitle = (TextView) itemView.findViewById(R.id.tvItemTitle);
            ivPostImageView = (ParseImageView) itemView.findViewById(R.id.ivPostImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.lLayout);
            cardView = (CardView) itemView.findViewById(R.id.cvCardview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Intent intent = new Intent(context,MainActivity.class );
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                Post selectedPost = mPosts.get(position);
                ArrayList<Post> arrayList = new ArrayList<>();
                arrayList.addAll(mPosts);
                if (viewHolderListener != null)  viewHolderListener.onViewHolderClicked(selectedPost, ivPostImageView, "transition"+position, position,  arrayList, tvItemTitle, "titleTransition"+position, cardView, "cardTransition"+position);

            }


        }
    }

    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        String adapterMode = Singleton.getInstance().getAdapterMode();

        if(adapterMode.equals(context.getResources().getString(R.string.feed_mode))){ //for the normal feed
            // Inflate the custom layout
            View postView = inflater.inflate(R.layout.item_post, parent, false);
            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(postView);
            return viewHolder;
        }else if (adapterMode.equals(context.getResources().getString(R.string.reflection_tab))){
            View postView = inflater.inflate(R.layout.item_detail, parent, false);
            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(postView);
            return viewHolder;
        }
        else  { //rak
            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.item_archive, parent, false);
            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(contactView);
            return viewHolder;

        }
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PostsAdapter.ViewHolder viewHolder, int position) {
        String adapterMode = Singleton.getInstance().getAdapterMode();
        // Get the post at the current position
        Post post = mPosts.get(position);
        viewHolder.tvItemTitle.setText(post.getTitle());
        ParseFile file = post.getImage();
        if (file != null) {
            viewHolder.ivPostImageView.setParseFile(post.getImage());
            viewHolder.ivPostImageView.loadInBackground();
        }else{
            viewHolder.ivPostImageView.getLayoutParams().height = 0;
        }
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
        public void onViewHolderClicked(Post post, ParseImageView parseImageView, String transitionName, int position, ArrayList<Post>posts, TextView title, String titleTransition, CardView cardView, String cardTransition);
    }
    //
    public void setViewHolderListener(ViewHolderListener viewHolderListener){
        this.viewHolderListener = viewHolderListener;
    }

}