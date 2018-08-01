    package com.inkredibles.wema20;

    import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inkredibles.wema20.models.Post;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.SaveCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

    /*The detail fragment shows more information about the post that has been selected from the recyclerview*/
    public class DetailFragment extends Fragment {
        //bind views
        @BindView(R.id.tvTitle) TextView tvtTitle;
        @BindView(R.id.tvMessage) TextView tvMessage;
        @BindView(R.id.ivPostImg)ParseImageView ivPostImage;
        @BindView(R.id.tvLocation) TextView tvLocation;
        @BindView(R.id.cvCardview) CardView cardView;
        @BindView(R.id.tvDate) TextView tvDate;
        @BindView(R.id.tvClap) TextView tvClap;
        @BindView(R.id.tvNumClaps) TextView tvNumClaps;
        @BindView(R.id.ivLocation) ParseImageView ivLocation;



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
            ivPostImage.setParseFile(post.getImage());
            ivPostImage.loadInBackground();
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
        @OnClick(R.id.tvClap)
        public void onClickClap(){
            post.clapForPost();
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    int numClaps = post.getNumClaps();
                    tvNumClaps.setText(numClaps+" claps");


                }
            });
        }




    }
