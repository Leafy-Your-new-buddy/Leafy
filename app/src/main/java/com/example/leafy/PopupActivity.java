package com.example.leafy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.List;

public class PopupActivity extends Activity {



    public static Activity popActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String currentActivityName = getClass().getSimpleName().trim();
        Log.v("testerr",currentActivityName);




        //질문 리스트 16개
        String [] quesList={
                "Q. 오늘 하루중 기억에 남기고 싶은 것은?",
                "Q. 죽기 전에 꼭 하고 싶은 일이 있다면?",
                "Q. 나에게 주고 싶은 선물이 있다면?",
                "Q. 나의 신조는 무엇인가?",
                "Q.닮고 싶은 문학작품 속 주인공은?.",
                "Q. _____은 재미있다.",
                "Q. 여름이 좋은가, 겨울이 좋은가?",
                "Q. 내 묘비에 남기고 싶은 말은?",
                "Q. 현실에 안주하고 싶은가, 흥분되는 도전을 원하는가?",
                "Q. 자신의 몸에 대해 어떻게 생각하는가?",
                "Q. 내가 한 거짓말 중에 가장 큰 파장을 불러일으켰던 것은?",
                "Q. 최근에 기분 좋은 대화를 나눈 사람의 이름을 적어보자.",
                "Q. 지금 사랑하고 있는가?",
                "Q. 오늘 있었던 일 중에서 지우고 싶은 기억이 있다면?",
                "Q. 집이란 무엇이라고 생각하는가?",
                "Q. 오늘 하루 슬퍼할 일이 있었는가?",

        };
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);


        Intent intent;
        try {
            //데이터 가져오기
            intent = getIntent();
        }catch(Exception e){
            intent = getIntent();
            finish();
        }


    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
        // this.onStop();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

}
