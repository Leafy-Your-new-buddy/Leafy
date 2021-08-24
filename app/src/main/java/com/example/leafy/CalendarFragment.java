package com.example.leafy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener, View.OnClickListener{


    TextView monthYearText;
    RecyclerView calendarRecyclerView;
    Context context;


    public CalendarFragment() {
        // Required empty public constructor

    }

    Button btn_calendar;
    View calCell;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=null;
        view= inflater.inflate(R.layout.fragment_calendar, null);


        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        calCell=view.findViewById(R.id.parentView);
        monthYearText = view.findViewById(R.id.monthYearTV);
        CalendarUtils.selectedDate = LocalDate.now();
        CalendarUtils.firstLoad=true;

        context = container.getContext();

        setMonthView();

        Button prev_btn=view.findViewById(R.id.previousmonth_btn);
        prev_btn.setOnClickListener(this);
        Button next_btn=view.findViewById(R.id.nextmonth_btn);
        next_btn.setOnClickListener(this);




        return view;
    }
    CalendarAdapter calendarAdapter;
    RecyclerView.LayoutManager layoutManager;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView()
    {

        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);

        calendarAdapter = new CalendarAdapter(daysInMonth, this);
        if(calendarRecyclerView==null){
            Toast.makeText(context,"null",Toast.LENGTH_SHORT).show();
        }

        //수정
        layoutManager = new GridLayoutManager(context, 7);


        RecyclerView.ItemAnimator animator = calendarRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        calendarAdapter.setHasStableIds(true); //깜빡임 없도록
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);


    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<LocalDate> daysInMonthArray(LocalDate date)
    {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
                daysInMonthArray.add(null);
            else
                daysInMonthArray.add(LocalDate.of(CalendarUtils.selectedDate.getYear(),CalendarUtils.selectedDate.getMonth(),i - dayOfWeek));
        }
        return  daysInMonthArray;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }


    LocalDate tempDate;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date)
    {
        if(date != null)
        {
            CalendarUtils.selectedDate = date; //yyyy-MM-dd 형식
            CalendarUtils.firstLoad=false;
            String testdate = "2021-07-10";



            //이걸 쓰면 물준날은 갱신 x 클릭한 날짜 테두리만 갱신할 수 있다. (깜빡임 해결!!)
            calendarAdapter.notifyDataSetChanged();

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previousmonth_btn:
                previousMonthAction(v);
                break;
            case R.id.nextmonth_btn:
                nextMonthAction(v);
                break;

        }
    }
}