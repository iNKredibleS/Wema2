package com.inkredibles.wema20;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseRole;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CurrentGroupFragment extends Fragment {

    @BindView(R.id.tvGroupName) TextView tvGroupName;

    private onItemSelectedListener listener;
    private ParseRole currentRole;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_group, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        //get the newly created group from args
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentRole = bundle.getParcelable("newRole");
            tvGroupName.setText(currentRole.getName());
        }else {
            Toast.makeText(getContext(), "bundle not gotten", Toast.LENGTH_SHORT);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //get the newly created group from args
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentRole = bundle.getParcelable("newRole");
            tvGroupName.setText(currentRole.getName());
        }else {
            Toast.makeText(getContext(), "bundle not gotten", Toast.LENGTH_SHORT);
        }
    }

    @OnClick(R.id.btnCreateGroupPost)
    protected void createGroupPost() {
     //   listener.setIsGroup(true);
        listener.fromCurrentGrouptoCreatePost(currentRole);


    }

    @OnClick(R.id.btnCreateGroupRak)
    protected void createGroupRak(){
        listener.fromCurrentGrouptoCreateRak(currentRole);

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onItemSelectedListener) {
            listener = (onItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnItemSelectedListener");
        }
    }


}
