package com.inkredibles.wema20;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inkredibles.wema20.models.Post;
import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        public TextView tvMessage;
        public TextView tvNumClaps;
        public TextView tvLocation;
        public TextView tvDate;
        public ImageView ivShare;

        public  ViewHolder(View itemView){
            super(itemView);
            context = itemView.getContext();
            tvItemTitle = (TextView) itemView.findViewById(R.id.tvItemTitle);
            ivPostImageView = (ParseImageView) itemView.findViewById(R.id.ivPostImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.lLayout);
            cardView = (CardView) itemView.findViewById(R.id.cvCardview);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvNumClaps = (TextView) itemView.findViewById(R.id.tvNumClaps);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            ivShare = (ImageView) itemView.findViewById(R.id.ivShare);
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
            View postView = inflater.inflate(R.layout.item_post, parent, false);
            ViewHolder viewHolder = new ViewHolder(postView);
            return viewHolder;
        }else if (adapterMode.equals(context.getResources().getString(R.string.reflection_tab))){
            View postView = inflater.inflate(R.layout.item_detail, parent, false);
            ViewHolder viewHolder = new ViewHolder(postView);
            return viewHolder;
        }
        else  {
            View contactView = inflater.inflate(R.layout.item_archive, parent, false);
            ViewHolder viewHolder = new ViewHolder(contactView);
            return viewHolder;

        }
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final PostsAdapter.ViewHolder viewHolder, int position) {
        String adapterMode = Singleton.getInstance().getAdapterMode();
        // Get the post at the current position
        final Post post = mPosts.get(position);
        viewHolder.tvItemTitle.setText(post.getTitle());
        ParseFile file = post.getImage();
        if (file != null) {
            viewHolder.ivPostImageView.setParseFile(post.getImage());
            viewHolder.ivPostImageView.loadInBackground();
        }else{
            viewHolder.ivPostImageView.getLayoutParams().height = 0;
        }
        if(adapterMode.equals(context.getResources().getString(R.string.reflection_tab))){
            viewHolder.tvMessage.setText(post.getMessage());
            viewHolder.tvNumClaps.setText(Integer.toString(post.getNumClaps()));
            viewHolder.tvLocation.setText(post.getPlaceName());
            viewHolder.tvDate.setText(post.getRelativeTimeAgo());
            viewHolder.ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickShare(post, viewHolder.ivPostImageView);
                }
            });
        }
    }

    public void onClickShare(Post post, ParseImageView ivShare) {
        String message = post.getMessage() + " \n " + "\n" + " A reflection created on Wema with ❤️.";
        Intent shareIntent = new Intent();
        ParseFile image = post.getImage();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, post.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TITLE, post.getTitle());
        shareIntent.setType("image/*");
        if (image != null) {
            String url = post.getImage().getUrl();
            Bitmap bp = getBitmapFromView(ivShare);
            Uri bmpUri = getBitmapFromDrawable(bp);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        }
        //copy the message to a clipboard
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Reflection", message);
        clipboard.setPrimaryClip(clip);

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "Share Reflection..."));
    }
    //this method given a view returns a bitmap
    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas); //has background drawable, then draw it on the canvas
        else
            canvas.drawColor(Color.WHITE);    //does not have background drawable, then draw white background on the canvas
        view.draw(canvas);
        return returnedBitmap;
    }


    //Given a Bitmap, this method returns the URI which we can then use to send to the share intent
    public Uri getBitmapFromDrawable(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();

            // wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
            bmpUri = FileProvider.getUriForFile(context, "com.inkredibles.wema20", file);  // use this version for API >= 24

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
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
    // sets the viewholderlistener
    public void setViewHolderListener(ViewHolderListener viewHolderListener){
        this.viewHolderListener = viewHolderListener;
    }

}