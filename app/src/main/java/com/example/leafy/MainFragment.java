package com.example.leafy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainFragment extends Fragment {



    public MainFragment() {
        // Required empty public constructor

    }

    static TextView text;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view=null;//Fragment가 보여줄 View 객체를 참조할 참조변수
        view= inflater.inflate(R.layout.fragment_main, null);
        //위에 만들어진 view객체 안에 있는 TextView를 찾아오기
        text= (TextView)view.findViewById(R.id.tvReceiveData_main);
        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            setTextViewValue("블루투스를 활성화해 주세요.");
        }
        else {
            String humid= settingActivity.readMessage;
            if(humid==null){
                setTextViewValue("수분센서와의 연결을 확인해주세요.");
            }
            else{
                setTextViewValue(humid);
            }


        }

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







}