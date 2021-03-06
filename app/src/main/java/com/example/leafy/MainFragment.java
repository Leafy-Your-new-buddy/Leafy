package com.example.leafy;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

    ImageView go_chat;
    TextView cardNews;
    public static TextView welcome;
    ImageButton go_set;
    Button btn_test;
    static TextView text;
 //   static TextView tv;
    TextView tv_weathertip;
    public static TextView water_feedback;

    public String humid;
    public String temp;

    private String uid;
    private DatabaseReference mDatabaseRef;  //????????? ??????????????????
    private FirebaseUser user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        View view=null;//Fragment??? ????????? View ????????? ????????? ????????????
        view= inflater.inflate(R.layout.fragment_main, null);
        //?????? ???????????? view?????? ?????? ?????? TextView??? ????????????
        text= (TextView)view.findViewById(R.id.tvReceiveData_main);
        water_feedback= (TextView)view.findViewById(R.id.water_Feedback);
        go_chat= (ImageView) view.findViewById(R.id.btn_chat);
        tv_weathertip = (TextView) view.findViewById(R.id.weather_tip);

        weatherState = (TextView)view.findViewById(R.id.weatherCondition);
        Temperature = (TextView)view.findViewById(R.id.temperature);
        mweatherIcon = (ImageView) view.findViewById(R.id.weatherIcon);

        welcome=(TextView)view.findViewById(R.id.tv_welcome);

        //????????????
        cardNews = (TextView)view.findViewById(R.id.btn_cardnews);
        cardNews.setOnClickListener(this);

        go_set=(ImageButton) view.findViewById(R.id.setIcon);

        context = container.getContext();   // toast ????????? ??????


        uid = user != null ? user.getUid() : null; // ???????????? ????????? ?????? uid ????????????
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("appname");

        //????????? ???????????? ????????? ?????????, ????????? ??????
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserAccount value =  snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                welcome.setText(value.getName() +"??? ???????????????!");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
         //   setTextViewValue("??????????????? ???????????? ?????????.");
            water_feedback.setText("??????????????? ???????????? ?????????.");
        }
        else {

            String strhumid= settingActivity.readMessage;
            //int humid=Integer.parseInt(strhumid.substring(0,2));

            if(strhumid==null){
              //  setTextViewValue("?????????????????? ????????? ??????????????????.");
                water_feedback.setText("?????????????????? ????????? ??????????????????.");
              //  water_feedback.setText("?????? ????????? ????????? ????????????. \n????????? ?????? ?????? ????????? ????????????!");
            }
            else{

                /*
                water_feedback.setText("(???????????? ?????? ?????????).");

                 */
            }

        }

        //?????? ??? ????????? ????????? ???????????? ??????
        btn_test=view.findViewById(R.id.test_button);
        btn_test.setOnClickListener(this);

        btn_test.setVisibility(View.GONE); //?????? ????????? ?????? ????????????..
        //?????? ?????? ????????? ?????? ??????????????? ??????
        go_chat.setOnClickListener(this);
        go_set.setOnClickListener(this);


        mDatabaseRef= FirebaseDatabase.getInstance().getReference("appname");
        user = FirebaseAuth.getInstance().getCurrentUser(); // ???????????? ????????? ?????? ????????????
        uid = user != null ? user.getUid() : null; // ???????????? ????????? ?????? uid ????????????

        //????????? View ????????? ??????
        return view;

        //return inflater.inflate(R.layout.fragment_main, container, false);
    }
    //TextView??? ????????? ???????????? ?????? ?????? ?????????


    public static void setTextViewValue(String str){
        String temp=text.getText().toString();
        String humid=str.substring(0,2);
        text.setText(humid);



        int h=Integer.parseInt(humid);
        if(h<=40) water_feedback.setText("?????? ???????????????.\n?????? ??? ??? ????????? ???????????? ?????? ?????????!");
        else if(h>40&&h<55) water_feedback.setText("?????? ????????? ????????? ????????????. \n????????? ?????? ?????? ????????? ????????????!");
        else if(h>=55) water_feedback.setText("?????? ????????? ????????????.\n???????????????!");
        else water_feedback.setText("????????? ???????????? ????????? ????????????.");

    }
    public static int getTextViewValue(String str){


        int res=0;
        String currentHumid=str.substring(0,2);
        String humidText= text.getText().toString();
        try{
            res=Integer.parseInt(currentHumid)-Integer.parseInt(humidText);
        }catch(Exception e){
            res=0;
        }finally {
            return res;
        }

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
            //    Toast.makeText(getActivity(), "?????? ????????? ????????????????????? ??????.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_chat:
                Intent intent = new Intent(getActivity(),chatActivity.class);
                startActivity(intent);
                break;
            case R.id.setIcon:
                Intent intent2 = new Intent(getActivity(),settingActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_cardnews:
                Intent intent3 = new Intent(getActivity(),CardNewsActivity.class);
                startActivity(intent3);
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
                if(name.checkwaterDate(getTime)){
                    name.addwaterDate(getTime);
                    mDatabaseRef.child("UserAccount").child(uid).setValue(name);
                    Toast.makeText(context,"????????????",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context,"??????",Toast.LENGTH_SHORT).show();
               }
                //Toast.makeText(context,,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

      //  Toast.makeText(context, "????????? ??????", Toast.LENGTH_SHORT).show();


    }


    // ???????????? ??????
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

        // ?????? ?????????
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
                Toast.makeText(context,"??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context,"???????????? ?????? ??????", Toast.LENGTH_SHORT).show();
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
             //   Toast.makeText(context,"??????????????? ??????????????????.",Toast.LENGTH_SHORT).show();
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
        //NameofCity.setText(weather.getMcity());
        weatherState.setText(weather.getmWeatherType());
        int resourceID=getResources().getIdentifier(weather.getMicon(),"drawable",getActivity().getPackageName());
        mweatherIcon.setImageResource(resourceID);

        // weather tip
        int tmp_t = weather.getTempforTip();
        String tmp_w = weather.getMicon();
        String tip="";
        if(tmp_t>35){
            tip="????????? ?????? ????????? ????\n????????? ?????? ????????? ???????????????!";
        }
        else if(tmp_t>10){
            if(tmp_w=="sunny"){
                tip="????????? ????????? ???? \n????????? ????????? ?????? ?????? ???! ????????? ????????????!";
            }
            else if(tmp_w=="thunderstorm1"||tmp_w=="lightrain"||tmp_w=="shower"||tmp_w=="thunderstorm2"){
                tip="?????? ?????? ????????? ???????????? ?????? ??? ?????????! \n????????? ???????????? ??? ?????????. ????";
            }
            else if(tmp_w=="snow1"||tmp_w=="snow2"){
                tip="?????? ????????? ????????? ???????????? ?????? ??? ?????????! \n????????? ???????????? ??? ?????????. ????";
            }
            else if(tmp_w=="cloudy"||tmp_w=="fog"||tmp_w=="overcast"){
                tip="????????? ?????? ???????????? ???? \n????????? ????????? ??? ??? ?????? ????????????!";
            }
            else{
                tip=tmp_w+" ??? ?????? ????????? ????????? ??? ????????????. \n????";
            }
        }
        else{
            tip="????????? ?????????! \n????????? ??????, ????????? ???????????????. ????";
        }

        tv_weathertip.setText(tip);

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