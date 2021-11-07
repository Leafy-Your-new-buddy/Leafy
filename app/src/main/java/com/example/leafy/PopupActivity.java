package com.example.leafy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PopupActivity extends Activity {

    private DatabaseReference mDatabaseRef;  //실시간 데이터베이스
    private FirebaseUser user;
    private StorageReference mStorageRef; //파이어베이스 스토리지
    private String uid;

    public String clicked_date;
    ImageView iv_image;
    TextView txtText1;
    TextView txtText2;
    Button leftBtn;
    Button rightBtn;

    public int idx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        txtText1 = (TextView)findViewById(R.id.txtText1);
        txtText2 = (TextView)findViewById(R.id.txtText3);
        iv_image = findViewById(R.id.fb_image);

        leftBtn =findViewById(R.id.left_btn);
        rightBtn=findViewById(R.id.right_btn);

        idx=0;
        Intent intent;
        try {
            //데이터 가져오기
            intent = getIntent();
        }catch(Exception e){
            intent = getIntent();
            finish();
        }
        //이 날짜는 YYYY-MM-DD 형태
        clicked_date = intent.getStringExtra("data");

        mDatabaseRef= FirebaseDatabase.getInstance().getReference("appname");
        user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        uid = user != null ? user.getUid() : null; // 로그인한 유저의 고유 uid 가져오기
        mStorageRef = FirebaseStorage.getInstance().getReference(); //스토리지

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount value =  snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                List<Diary> diaryList;
                diaryList=value.getcertainDiaryList(clicked_date);
                txtText1.setText((idx+1)+"/"+diaryList.size());

                if(diaryList.size()==1){
                    rightBtn.setEnabled(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setRecordUI();
        leftBtn.setEnabled(false);
        leftBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                 idx--;

                 if(idx==0){
                     leftBtn.setEnabled(false);
                 }
                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserAccount value =  snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                        List<Diary> diaryList;
                        diaryList=value.getcertainDiaryList(clicked_date);
                        if(idx<diaryList.size()-1){
                            rightBtn.setEnabled(true);
                        }
                        txtText1.setText((idx+1)+"/"+diaryList.size());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                setRecordUI();



            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                idx++;
                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserAccount value =  snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                        List<Diary> diaryList;
                        diaryList=value.getcertainDiaryList(clicked_date);
                        if(idx==diaryList.size()-1){
                            rightBtn.setEnabled(false);
                        }
                        txtText1.setText((idx+1)+"/"+diaryList.size());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if(idx>0){
                    leftBtn.setEnabled(true);
                }
                setRecordUI();
            }
        });


    }
    Bitmap bitmap;
    public void setRecordUI(){
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount value =  snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                List<Diary> diaryList;
                diaryList=value.getcertainDiaryList(clicked_date);
                String myurl=diaryList.get(idx).image;
                Glide.with(getApplicationContext()).load(myurl).into(iv_image);
                txtText2.setText(diaryList.get(idx).diary);


            }





            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
