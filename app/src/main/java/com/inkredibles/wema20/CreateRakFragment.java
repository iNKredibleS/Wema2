package com.inkredibles.wema20;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inkredibles.wema20.models.Rak;
import com.parse.ParseException;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateRakFragment extends Fragment {

    @BindView(R.id.createRakTxt) EditText createRakTxt;
    @BindView(R.id.createBtn) Button createBtn;

    private onItemSelectedListener listener;
    //Bundle bundle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_rak, container, false);

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

    }

    @Override
    public void onResume() {
        super.onResume();
        createRakTxt.setText("");
    }

    @OnClick(R.id.createBtn)
    protected void createRak() {
        Bundle bundle = this.getArguments();
        if(bundle != null && bundle.getBoolean("isGroup")){
            //you can make this it's own function
            ParseRole currentRole = bundle.getParcelable("currentRole");
            Rak groupRak = new Rak();
            groupRak.setTitle(createRakTxt.getText().toString());
            groupRak.setUser(ParseUser.getCurrentUser());
            groupRak.setRole(currentRole);

            groupRak.saveInBackground(
                    new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("CreateRakFragment", "create grouprak success");
                                Toast.makeText(getActivity(), "Group Rak Created", Toast.LENGTH_SHORT).show();
                                createRakTxt.setText("");
                                listener.toCurrentGroup();

                            } else {
                                e.printStackTrace();
                            }
                        }
                    });


        }else{
            listener.addRakToServer(createRakTxt.getText().toString());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
       // bundle = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(this.getArguments() != null){this.getArguments().clear(); }
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


