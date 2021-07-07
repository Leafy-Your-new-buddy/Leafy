package com.example.leafy;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class CalendarFragment extends Fragment implements View.OnClickListener {


    public CalendarFragment() {
        // Required empty public constructor
    }

    Button btn_calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=null;//Fragment가 보여줄 View 객체를 참조할 참조변수
        view= inflater.inflate(R.layout.fragment_calendar, null);
        btn_calendar=view.findViewById(R.id.btn_calendar);
        btn_calendar.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_calendar:
                Intent intent = new Intent(getActivity(),CalendarActivity.class);
                startActivity(intent);
                break;
        }
    }
}