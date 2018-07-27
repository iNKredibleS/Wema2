    package com.inkredibles.wema20;

    import android.content.Context;
    import android.os.Bundle;
    import android.support.v4.app.Fragment;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import com.inkredibles.wema20.models.Post;
    import com.parse.ParseImageView;

    import butterknife.BindView;
    import butterknife.ButterKnife;

    /*The detail fragment shows more information about the post that has been selected from the recyclerview*/
    public class DetailFragment extends Fragment {
        //bind views
        @BindView(R.id.tvTitle) TextView tvtTitle;
        @BindView(R.id.tvMessage) TextView tvMessage;
        @BindView(R.id.ivPostImg)ParseImageView ivPostImage;



        private Post post;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            // Defines the xml file for the fragment
            return inflater.inflate(R.layout.fragment_detail, parent, false);
        }


        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            ButterKnife.bind(this,view );
            Bundle bundle = this.getArguments();
            if (bundle == null){
                Log.d("DetailFragment", "Detail is null");
                return;
            }

            post = bundle.getParcelable("post");
            tvtTitle.setText(post.getTitle());
            tvMessage.setText(post.getMessage());
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
