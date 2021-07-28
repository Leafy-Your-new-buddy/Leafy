package com.example.leafy;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener;
import org.naishadhparmar.zcustomcalendar.Property;

import java.util.Calendar;
import java.util.HashMap;

public class CalendarActivity extends AppCompatActivity {
    //initialize variables
    CustomCalendar customCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //변수초기화
        customCalendar = findViewById(R.id.custom_calendar);

        //해시맵 초기화
        HashMap<Object, Property> descHashMap = new HashMap<>();
        //default property 초기화
        Property defaultProperty = new Property();
        //default resource 초기화
        defaultProperty.layoutResource = R.layout.default_view;
        //초기화 & 변수 선언
        defaultProperty.dateTextViewResource = R.id.text_view;
        //put object and property
        descHashMap.put("default", defaultProperty);

        //For current date 파란색
        Property currentProperty = new Property();
        currentProperty.layoutResource = R.layout.current_view;
        currentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("current", currentProperty);

        //For present date 초록색
        Property presentProperty = new Property();
        presentProperty.layoutResource = R.layout.present_view;
        presentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("present", presentProperty);

        //For absent 빨간색
        Property absentProperty = new Property();
        absentProperty.layoutResource = R.layout.absent_view;
        absentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("absent", absentProperty);

        //Set desc hash map on custom calendar
        customCalendar.setMapDescToProp(descHashMap);

        //date hash map 초기화
        HashMap<Integer, Object> dateHashMap = new HashMap<>();
        //캘린더 초기화
        Calendar calendar = Calendar.getInstance();
        //Put values
        dateHashMap.put(calendar.get(Calendar.DAY_OF_MONTH), "current");
        //색변경하는 부분
        dateHashMap.put(1, "present");
        dateHashMap.put(2, "absent");
        dateHashMap.put(3, "present");
        dateHashMap.put(4, "absent");
        dateHashMap.put(20, "present");
        dateHashMap.put(30, "absent");

        //set date
        customCalendar.setDate(calendar, dateHashMap);

        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
                String sDate = selectedDate.get(Calendar.DAY_OF_MONTH)
                        + "/" + (selectedDate.get(Calendar.MONTH)+1)
                        + "/" + selectedDate.get(Calendar.YEAR);
                //Display date in toast
                Toast.makeText(getApplicationContext(), sDate, Toast.LENGTH_LONG).show();
            }
        });






    }
}
