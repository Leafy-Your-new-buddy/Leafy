package com.example.leafy;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class CameraFragment extends Fragment implements View.OnClickListener {


    public CameraFragment() {
        // Required empty public constructor
    }

    Button btn_camera;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=null;//Fragment가 보여줄 View 객체를 참조할 참조변수
        view= inflater.inflate(R.layout.fragment_camera, null);
        btn_camera=view.findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:
                Intent intent = new Intent(getActivity(),CameraActivity.class);
                startActivity(intent);
                break;
        }
    }
}