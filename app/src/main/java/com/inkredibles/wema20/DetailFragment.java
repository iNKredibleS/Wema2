    package com.inkredibles.wema20;

    import android.annotation.SuppressLint;
    import android.content.ClipData;
    import android.content.ClipboardManager;
    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.Canvas;
    import android.graphics.Color;
    import android.graphics.drawable.Drawable;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;
    import android.os.Environment;
    import android.support.v4.app.Fragment;
    import android.support.v4.content.FileProvider;
    import android.support.v7.widget.CardView;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;

    import com.inkredibles.wema20.models.Post;
    import com.parse.ParseException;
    import com.parse.ParseFile;
    import com.parse.ParseImageView;
    import com.parse.SaveCallback;

    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.util.ArrayList;

    import butterknife.BindView;
    import butterknife.ButterKnife;
    import butterknife.OnClick;

    /*The detail fragment shows more information about the post that has been selected from the recyclerview*/
    public class DetailFragment extends Fragment {
        //bind views
        @BindView(R.id.tvItemTitle) TextView tvtTitle;
        @BindView(R.id.tvMessage) TextView tvMessage;
        @BindView(R.id.ivPostImage)ParseImageView ivPostImage;
        @BindView(R.id.tvLocation) TextView tvLocation;
        @BindView(R.id.cvCardview) CardView cardView;
        @BindView(R.id.tvDate) TextView tvDate;
        @BindView(R.id.tvClap) TextView tvClap;
        @BindView(R.id.tvNumClaps) TextView tvNumClaps;
        @BindView(R.id.ivLocation) ParseImageView ivLocation;
        @BindView(R.id.ivShare) ImageView ivShare;



        private Post post;
        private ArrayList<Post> allPosts;
        private int currPosition;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            // Defines the xml file for the fragment
            return inflater.inflate(R.layout.fragment_detail, parent, false);
        }



        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            ButterKnife.bind(this,view );
            Bundle bundle = this.getArguments();
            if (bundle == null){
                Log.d("DetailFragment", "Detail is null");
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivPostImage.setTransitionName(bundle.getString("transitionName"));
                tvtTitle.setTransitionName(bundle.getString("titleTransition"));
                cardView.setTransitionName(bundle.getString("cardTransition"));

            }
            post = bundle.getParcelable("post");
            allPosts = bundle.getParcelableArrayList("all_posts");
            currPosition  = bundle.getInt("position");

            tvtTitle.setText(post.getTitle());
            String location = post.getPlaceName();
            if (location != null && location != ""){
                tvLocation.setText(location);
            }else{
                tvLocation.setText("No Location");
            }
            tvMessage.setText(post.getMessage());
            tvDate.setText(post.getRelativeTimeAgo());
            tvNumClaps.setText(Integer.toString(post.getNumClaps()));
            ParseFile file = post.getImage();
            if (file != null) {
                ivPostImage.setParseFile(post.getImage());
                ivPostImage.loadInBackground();
            }else{
                ivPostImage.getLayoutParams().height = 0;
            }
            int color = getResources().getColor(R.color.md_blue_200);
            ivLocation.setColorFilter(color);
            cardView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
                @Override
                public void onSwipeDown() {
                    //do something when the user swipes down
                }

                @Override
                public void onSwipeLeft() {
                    nextElement(true);
                }

                @Override
                public void onSwipeUp() {
                    //do somthing when the user swipes up
                }

                @Override
                public void onSwipeRight() {
                    nextElement(false);
                }
            });
        }


        //Transitions to the next element. It gets executed when the user swipes on an item in the detail page.
        private void nextElement(boolean isLeft){
            if (isLeft) currPosition++;
            else{ currPosition --;}
            if (currPosition < 0 || currPosition >= allPosts.size()) return;
            post = allPosts.get(currPosition);
            tvMessage.setText(post.getMessage());
            tvtTitle.setText(post.getTitle());
            ivPostImage.setParseFile(post.getImage());
            ivPostImage.loadInBackground();
        }


        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (context instanceof onItemSelectedListener) {
                //listener = (onItemSelectedListener) context;
            } else {
                throw new ClassCastException(context.toString()
                        + " must implement OnItemSelectedListener");
            }
        }

        //click handler for the clap
        @OnClick(R.id.tvClap)
        public void onClickClap(){
            post.clapForPost();
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    int numClaps = post.getNumClaps();
                    tvNumClaps.setText(Integer.toString(numClaps));


                }
            });
        }


        //click handler for the share button.
        @OnClick(R.id.ivShare)
        public void onClickShare(){
            String message = post.getMessage() +" \n "+"\n"+" A reflection created on Wema with ❤️.";
            Intent shareIntent = new Intent();
            ParseFile image = post.getImage();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, post.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TITLE, post.getTitle());
            shareIntent.setType("image/*");
            if (image != null){
                String url = post.getImage().getUrl();
                Bitmap bp = getBitmapFromView(ivPostImage);
                Uri bmpUri = getBitmapFromDrawable(bp);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            }
            //copy the message to a clipboard
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Reflection", message);
            clipboard.setPrimaryClip(clip);

            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Reflection..."));
        }

        //this method given a view returns a bitmap
        public static Bitmap getBitmapFromView(View view) {
            //Define a bitmap with the same size as the view
            Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
            //Bind a canvas to it
            Canvas canvas = new Canvas(returnedBitmap);
            //Get the view's background
            Drawable bgDrawable =view.getBackground();
            if (bgDrawable!=null) bgDrawable.draw(canvas); //has background drawable, then draw it on the canvas
            else canvas.drawColor(Color.WHITE);    //does not have background drawable, then draw white background on the canvas
            // draw the view on the canvas
            view.draw(canvas);
            return returnedBitmap;
        }



        //Given a Bitmap, this method returns the URI which we can then use to send to the share intent
        public Uri getBitmapFromDrawable(Bitmap bmp){
            Uri bmpUri = null;
            try {
                File file =  new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();

                // wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
                bmpUri = FileProvider.getUriForFile(getContext(), "com.inkredibles.wema20", file);  // use this version for API >= 24

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }

    }
