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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener, View.OnClickListener{

    String fname = null; //텍스트 저장할 파일
    String str = null;
    CalendarView calendarView;
    Button btn_record, //기록하기 버튼
            btn_watering, //물주기 버튼
            btn_store; //저장버튼

    View title_record; //오늘의 기록
    TextView contextEditText; //기록 적는 칸
    String uid;



    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef;  //실시간 데이터베이스

    public CalendarFragment() {
        // Required empty public constructor
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("appname");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        uid = user != null ? user.getUid() : null; // 로그인한 유저의 고유 uid 가져오기

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
        String name=intent.getStringExtra("userName"); */
        Intent intent = new Intent();
        final String userID=intent.getStringExtra("userID");

        //우선 이름 받아올게 없으니까 보류
        //날짜선택
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                if(contextEditText.getText() != null){
                    startActivity(intent);
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
    //파이어베이스에 연결
    public void  checkDay(int cYear,int cMonth,int cDay,String userID){
        fname=""+userID+cYear+"-"+(cMonth+1)+""+"-"+cDay+".txt";//저장할 파일 이름설정
        //FileInputStream fis=null;//FileStream fis 변수


        try{
            /*fis= getActivity().openFileInput(fname);

            byte[] fileData=new byte[fis.available()];
            fis.read(fileData);
            fis.close();*/

            //str=new String(fileData);

            contextEditText.setVisibility(View.INVISIBLE);
            title_record.setVisibility(View.INVISIBLE);


            btn_record.setVisibility(View.VISIBLE);
            btn_watering.setVisibility(View.VISIBLE);
            btn_store.setVisibility(View.INVISIBLE);

            //기록하기 버튼
            btn_record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveDiary(fname);
                    contextEditText.setVisibility(View.VISIBLE);
                    title_record.setVisibility(View.VISIBLE);
                    btn_record.setVisibility(View.INVISIBLE);
                    btn_watering.setVisibility(View.INVISIBLE);
                    btn_store.setVisibility(View.VISIBLE); //저장하기 버튼만 띄우기

                    contextEditText.setText(str);

                    saveDiary(fname);

                }

            });
            //물주기버튼
            btn_watering.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //contextEditText.setVisibility(View.VISIBLE);
                    //title_record.setVisibility(View.VISIBLE);
                    btn_record.setVisibility(View.VISIBLE);
                    btn_watering.setVisibility(View.VISIBLE);
                    btn_store.setVisibility(View.INVISIBLE);

                    //TODO: 달력 일자 파란색으로 색칠하기

                }
            });
            btn_store.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    contextEditText.setText(str);
                    //달력 첫 화면으로 넘어가기
                    btn_record.setVisibility(View.VISIBLE);
                    btn_watering.setVisibility(View.VISIBLE);
                    btn_store.setVisibility(View.INVISIBLE);
                    contextEditText.setVisibility(View.VISIBLE);
                    title_record.setVisibility(View.VISIBLE);
                    contextEditText.setText(str);

                    if(contextEditText.getText()==null){
                        contextEditText.setVisibility(View.VISIBLE);
                        btn_watering.setVisibility(View.VISIBLE);
                        btn_record.setVisibility(View.VISIBLE);
                        btn_store.setVisibility(View.INVISIBLE);
                        title_record.setVisibility(View.VISIBLE);
                    }

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
    /*@SuppressLint("WrongConstant")
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
    }*/
    @SuppressLint("WrongConstant")
    public void saveDiary(String readDay){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = simpleDate.format(mDate);

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount name = snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                if (readDay != null) {
                    String content = contextEditText.getText().toString();
                    //기록내용 firebase에 저장
                    mDatabaseRef.child("UserAccount").child(uid).child("Diary").setValue(content);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


        /*FileOutputStream fos=null;

        try{
            fos=getActivity().openFileOutput(readDay,Context.MODE_NO_LOCALIZED_COLLATORS);
            String content=contextEditText.getText().toString();
            fos.write((content).getBytes());
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }*/


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