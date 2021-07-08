package com.example.leafy;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainFragment extends Fragment implements View.OnClickListener{

    final String APP_ID = "ac5471e3caa6df5bb40fbe111f57c735";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    final long MIN_TIME = 5000; // 5sec
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;
    private Context context;

    //String Location_Provider = LocationManager.GPS_PROVIDER;
    //String Location_Provider = LocationManager.NETWORK_PROVIDER;
    TextView NameofCity, weatherState, Temperature;
    ImageView mweatherIcon;
    RelativeLayout mCityFinder;

    LocationManager mLocationManager;
    LocationListener mLocationListner;

    public MainFragment() {
        // Required empty public constructor

    }

    Button btn_test;
    static TextView text;
    TextView water_feedback;
    String uid;

    private DatabaseReference mDatabaseRef;  //실시간 데이터베이스
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        View view=null;//Fragment가 보여줄 View 객체를 참조할 참조변수
        view= inflater.inflate(R.layout.fragment_main, null);
        //위에 만들어진 view객체 안에 있는 TextView를 찾아오기
        text= (TextView)view.findViewById(R.id.tvReceiveData_main);
        water_feedback= (TextView)view.findViewById(R.id.water_Feedback);


        weatherState = (TextView)view.findViewById(R.id.weatherCondition);
        Temperature = (TextView)view.findViewById(R.id.temperature);
        mweatherIcon = (ImageView) view.findViewById(R.id.weatherIcon);
        NameofCity = (TextView)view.findViewById(R.id.cityName);

        context = container.getContext();   // toast 사용에 필요

        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
         //   setTextViewValue("블루투스를 활성화해 주세요.");
            water_feedback.setText("블루투스를 활성화해 주세요.");
        }
        else {
            String humid= settingActivity.readMessage;
            if(humid==null){
              //  setTextViewValue("수분센서와의 연결을 확인해주세요.");
                water_feedback.setText("수분센서와의 연결을 확인해주세요.");
            }
            else{
                setTextViewValue(humid);
                water_feedback.setText("(수분량에 따른 피드백)");

            }

        }

        //일단 이 버튼을 누르면 물줬다고 인식
        btn_test=view.findViewById(R.id.test_button);
        btn_test.setOnClickListener(this);

        mDatabaseRef= FirebaseDatabase.getInstance().getReference("appname");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        uid = user != null ? user.getUid() : null; // 로그인한 유저의 고유 uid 가져오기

        //생성된 View 객체를 리턴
        return view;

        //return inflater.inflate(R.layout.fragment_main, container, false);
    }
    //TextView의 글씨를 변경하기 위해 만든 메소드


    public static void setTextViewValue(String str){
        text.setText(str); //전달 받은 문자열로 TextView의 글씨를 변경
    }


    public static MainFragment newInstance( ) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        //String a=settingActivity.readMessage;
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.test_button:
                //Intent intent = new Intent(getActivity(),CameraActivity.class);
                //startActivity(intent);
                watering();
                Toast.makeText(getActivity(), "현재 날짜를 파이어베이스에 저장.", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    public void watering(){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = simpleDate.format(mDate);



        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserAccount name =  snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                name.addwaterDate(getTime);
                mDatabaseRef.child("UserAccount").child(uid).setValue(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    // 여기부터 날씨
    @Override
    public void onResume() {
        super.onResume();
        Intent mIntent = getActivity().getIntent();
        String city = mIntent.getStringExtra("City");
        if (city != null) {
            getWeatherForNewCity(city);
        } else {
            getWeatherForCurrentLocation();
        }

    }

    private void getWeatherForNewCity(String city) {
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);
        letsdoSomeNetworking(params);

    }

    private void getWeatherForCurrentLocation() {

        mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // 로그 확인용
        Log.d("MainFrag", "isGEPEnabled: "+isGPSEnabled);
        Log.d("MainFrag", "isNetworkEnabled: "+isGPSEnabled);

        mLocationListner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                RequestParams params = new RequestParams();
                params.put("lat", Latitude);
                params.put("lon", Longitude);
                params.put("appid", APP_ID);
                letsdoSomeNetworking(params);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //not able to get location
                Toast.makeText(context,"위치정보를 받아올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        };


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        //mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListner);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListner);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListner);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode==REQUEST_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(context,"위치정보 받기 완료", Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            }
            else
            {
                //user denied the permission
            }
        }


    }

    private  void letsdoSomeNetworking(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                Toast.makeText(context,"위치정보를 받아왔습니다.",Toast.LENGTH_SHORT).show();
                weatherData weatherD=weatherData.fromJson(response);
                updateUI(weatherD);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });

    }

    private  void updateUI(weatherData weather){


        Temperature.setText(weather.getmTemperature());
        NameofCity.setText(weather.getMcity());
        weatherState.setText(weather.getmWeatherType());
        int resourceID=getResources().getIdentifier(weather.getMicon(),"drawable",getActivity().getPackageName());
        mweatherIcon.setImageResource(resourceID);


    }

    @Override
    public void onPause() {
        super.onPause();
        if(mLocationManager!=null)
        {
            mLocationManager.removeUpdates(mLocationListner);
        }
    }
}