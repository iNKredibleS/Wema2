package com.inkredibles.wema20;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateRakFragment extends Fragment {

    @BindView(R.id.createRakTxt) EditText createRakTxt;
    @BindView(R.id.createBtn) Button createBtn;

    private onItemSelectedListener listener;


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


    @OnClick(R.id.createBtn)
    protected void createRak() {
        listener.addRakToServer(createRakTxt.getText().toString());
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


