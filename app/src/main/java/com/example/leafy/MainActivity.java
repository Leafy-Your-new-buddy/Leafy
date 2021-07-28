package com.example.leafy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.bottom_nav);
        navigationView.setOnNavigationItemSelectedListener(listener);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new MainFragment()).commit();
        navigationView.getMenu().getItem(1).setChecked(true);

        //세팅화면으로 이동
        ImageView iv = (ImageView) findViewById(R.id.setting);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), settingActivity.class);
                startActivity(intent);
                //wow
            }
        });
/*
        btn_camera = (Button)findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });
*/
        // CameraFragment fragcam;
        // fragcam.clickCam();






    }

    //하단 메뉴바.
    private BottomNavigationView.OnNavigationItemSelectedListener listener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected_fragment=null;
            switch (item.getItemId()){
                case R.id.calendar:
                    //TODO: calendar activity로 연결시키기
                    //selected_fragment=new CalendarFragment();
                    //break;
                case R.id.home:
                    selected_fragment=new MainFragment();
                    break;
                case R.id.camera:
                    selected_fragment=new CameraFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,selected_fragment).commit();

            return true;
        }
    };






}