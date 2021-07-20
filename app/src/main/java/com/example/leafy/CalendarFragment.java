package com.example.leafy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener, View.OnClickListener{

    String fname = null; //텍스트 저장할 파일
    String str = null;
    CalendarView calendarView;
    Button btn_record, //기록하기 버튼
            btn_watering, //물주기 버튼
            btn_store; //저장버튼

    View title_record; //오늘의 기록
    TextView contextEditText; //기록 적는 칸

    public CalendarFragment() {
        // Required empty public constructor
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=null;
        //캘린더 fragment 띄우기
        view= inflater.inflate(R.layout.fragment_calendar, null);
        calendarView = view.findViewById(R.id.calendarView);
        contextEditText = view.findViewById(R.id.contextEditText); //기록내용
        title_record = view.findViewById(R.id.title_record);

        //버튼
        btn_watering=view.findViewById(R.id.btn_watering);
        btn_watering.setOnClickListener(this);
        btn_record=view.findViewById(R.id.btn_record);
        btn_record.setOnClickListener(this);
        btn_store=view.findViewById(R.id.btn_store);
        btn_store.setOnClickListener(this);

        //로그인 및 회원가입 액티비티에서 이름을 받아옴
        /* --TODO: 로그인 기록에서 회원정보를 갖고와서 해당 회원에 기록 저장하기
        Intent intent = new Intent();
        String name=intent.getStringExtra("userName");  */
        Intent intent = new Intent();
        final String userID=intent.getStringExtra("userID");

        //우선 이름 받아올게 없으니까 보류
        //날짜선택
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if(contextEditText.getText() != null){
                    title_record.setVisibility(View.VISIBLE);
                    contextEditText.setVisibility(View.VISIBLE);
                }
                btn_watering.setVisibility(View.VISIBLE);
                btn_record.setVisibility(View.VISIBLE);
                btn_store.setVisibility(View.INVISIBLE);
                checkDay(year, month, dayOfMonth, userID);
            }
        });

        return view;
    }

    //기록내용 파일저장
    public void  checkDay(int cYear,int cMonth,int cDay,String userID){
        fname=""+userID+cYear+"-"+(cMonth+1)+""+"-"+cDay+".txt";//저장할 파일 이름설정
        FileInputStream fis=null;//FileStream fis 변수

        try{
            fis= getActivity().openFileInput(fname);

            byte[] fileData=new byte[fis.available()];
            fis.read(fileData);
            fis.close();

            str=new String(fileData);

            contextEditText.setVisibility(View.INVISIBLE);
            title_record.setVisibility(View.INVISIBLE);


            btn_record.setVisibility(View.VISIBLE);
            btn_watering.setVisibility(View.VISIBLE);
            btn_store.setVisibility(View.INVISIBLE);

            //기록하기 버튼
            btn_record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contextEditText.setVisibility(View.VISIBLE);
                    title_record.setVisibility(View.VISIBLE);
                    btn_record.setVisibility(View.INVISIBLE);
                    btn_watering.setVisibility(View.VISIBLE);
                    btn_store.setVisibility(View.INVISIBLE);

                    contextEditText.setText(str);

                }

            });
            btn_watering.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //contextEditText.setVisibility(View.VISIBLE);
                    //title_record.setVisibility(View.VISIBLE);
                    btn_record.setVisibility(View.INVISIBLE);
                    btn_watering.setVisibility(View.VISIBLE);
                    btn_store.setVisibility(View.INVISIBLE);
                    //달력 일자 파란색으로 색칠하기

                }
            });
            if(contextEditText.getText()==null){
                contextEditText.setVisibility(View.VISIBLE);
                btn_watering.setVisibility(View.VISIBLE);
                btn_record.setVisibility(View.INVISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void removeDiary(String readDay){
        FileOutputStream fos=null;

        try{
            fos=getActivity().openFileOutput(readDay,Context.MODE_NO_LOCALIZED_COLLATORS);
            String content="";
            fos.write((content).getBytes());
            fos.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void saveDiary(String readDay){
        FileOutputStream fos=null;

        try{
            fos=getActivity().openFileOutput(readDay,Context.MODE_NO_LOCALIZED_COLLATORS);
            String content=contextEditText.getText().toString();
            fos.write((content).getBytes());
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(int position, String dayText) {
        if(!dayText.equals(""))
        {
            /*todo: 날짜 클릭하면 색 변하는 것 적용하기 */

            //   Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
}
    /*@RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView()
    {

        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        if(calendarRecyclerView==null){
            Toast.makeText(context,"null",Toast.LENGTH_SHORT).show();
        }

        //수정
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 7);

        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String> daysInMonthArray(LocalDate date)
    {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            {
                daysInMonthArray.add("");
            }
            else
            {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
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
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction(View view)
    {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, String dayText) {
        if(!dayText.equals(""))
        {

           /*todo: 날짜 클릭하면 색 변하는 것 적용하기 */
    /*
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            //   Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        }
    }
*/
    /*public void weeklyAction(View view){
        startActivity(new Intent(this, WeekViewActivity.class));
    }*/

/*
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
    */