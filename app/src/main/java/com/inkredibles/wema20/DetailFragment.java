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
    import android.widget.Toast;

    import com.inkredibles.wema20.models.Post;
    import com.parse.ParseImageView;

    import java.util.ArrayList;

    import butterknife.BindView;
    import butterknife.ButterKnife;

    /*The detail fragment shows more information about the post that has been selected from the recyclerview*/
    public class DetailFragment extends Fragment {
        //bind views
        @BindView(R.id.tvTitle) TextView tvtTitle;
        @BindView(R.id.tvMessage) TextView tvMessage;
        @BindView(R.id.ivPostImg)ParseImageView ivPostImage;
        @BindView(R.id.tvLocation) TextView tvLocation;
        @BindView(R.id.cvCardview) CardView cardView;



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
            }

            post = bundle.getParcelable("post");
            allPosts = bundle.getParcelableArrayList("all_posts");
            currPosition  = bundle.getInt("position");

            tvtTitle.setText(post.getTitle());
            String location = post.getPlaceName();
            if (location != null || location != ""){
                tvLocation.setText(location);
            }else{
                tvLocation.setText("No Location");
            }
            tvMessage.setText(post.getMessage());
            ivPostImage.setParseFile(post.getImage());
            ivPostImage.loadInBackground();
//            ivPostImage.setOnTouchListener(new onSw);

            cardView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
                @Override
                public void onSwipeDown() {
                    Toast.makeText(getContext(), "Down", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSwipeLeft() {
                    Toast.makeText(getContext(), "Left", Toast.LENGTH_SHORT).show();
                    nextElement(true);
                }

                @Override
                public void onSwipeUp() {
                    Toast.makeText(getContext(), "Up", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSwipeRight() {
                    Toast.makeText(getContext(), "Right", Toast.LENGTH_SHORT).show();
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


    }
