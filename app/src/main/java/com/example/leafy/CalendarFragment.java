package com.example.leafy;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CalendarFragment extends Fragment implements View.OnClickListener{

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.activity_calendar, container, false);
        inflate.findViewById(R.id.calendarRecyclerView);
        //btn_camera.setOnClickListener(this);
        return inflate;
        //return inflater.inflate(R.layout.activity_calendar, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendarRecyclerView:
                Intent intent = new Intent(getActivity(),CalendarActivity.class);
                startActivity(intent);
                break;

        }
    }
}