package com.example.leafy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class chatActivity extends AppCompatActivity {

    Button go_Main_btn;
    private RecyclerView chatsRV;
    private EditText userMsgET;
    private FloatingActionButton sendMsgFAB;
    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    private ArrayList<ChatsModal> chatsModalArrayList;
    private ChatRVAdapter chatRVAdapter;

    private TextView bot_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //메인으로 돌아가는 버튼(임시)
        go_Main_btn=(Button)findViewById(R.id.chatToMain);
        go_Main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });

        chatsRV = findViewById(R.id.idRVChats);
        userMsgET = findViewById(R.id.idETMessage);
        sendMsgFAB = findViewById(R.id.idFABSend);
        chatsModalArrayList = new ArrayList<>();
        chatRVAdapter = new ChatRVAdapter(chatsModalArrayList,this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatsRV.setLayoutManager(manager);
        chatsRV.setAdapter(chatRVAdapter);

        sendMsgFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userMsgET.getText().toString().isEmpty()){
                    Toast.makeText(chatActivity.this,"Please enter you msg", Toast.LENGTH_SHORT).show();
                    return;
                }
                getResponse(userMsgET.getText().toString());
                userMsgET.setText("");
            }
        });
    }

    private void getResponse(String msg){
        chatsModalArrayList.add(new ChatsModal(msg, USER_KEY));
        String url="http://~~~~~/?msg="+msg;
        String BASE_URL="http://~~~~~/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        Call<MsgModal> call = retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModal>() {
            @Override
            public void onResponse(Call<MsgModal> call, Response<MsgModal> response) {
                if(response.isSuccessful()){
                    MsgModal modal = response.body();
                    chatsModalArrayList.add(new ChatsModal(modal.getAnswer(),BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                }
                else{
                    Log.v("logTest","실패");
                    if(response.errorBody()!=null){
                        try {
                            Log.v("logTest","" + response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();

                            Log.v("logTest",e.toString());

                        }
                    }

                }

            }

            @Override
            public void onFailure(Call<MsgModal> call, Throwable t) {
                chatsModalArrayList.add(new ChatsModal("failure",BOT_KEY));
                chatRVAdapter.notifyDataSetChanged();
                Log.v("logTest",t.getMessage());

            }
        });

    }
}