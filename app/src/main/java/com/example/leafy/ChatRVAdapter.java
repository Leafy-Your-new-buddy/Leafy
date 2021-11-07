package com.example.leafy;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatRVAdapter extends RecyclerView.Adapter {
    private ArrayList<ChatsModal> chatsModalArrayList;
    private Context context;


    public interface OnListItemSelectedInterface {
        void onItemSelected(View v, int position);
    }

    OnListItemSelectedInterface mListener;

    public ChatRVAdapter(ArrayList<ChatsModal> chatsModalArrayList, Context context,OnListItemSelectedInterface listener) {
        this.chatsModalArrayList = chatsModalArrayList;
        this.context = context;

        this.mListener = listener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_msg_rv_item,parent,false);
                return new UserViewHolder(view);

            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_msg_rv_item,parent,false);
                return new BotViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatsModal chatsModal = chatsModalArrayList.get(position);
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        switch (chatsModal.getSender()){
            case "user":
                ((UserViewHolder)holder).userTV.setText(chatsModal.getMessage());
                ((UserViewHolder)holder).user_time.setText(currentTime);
                break;
            case "bot":
                ((BotViewHolder)holder).botTV.setText(chatsModal.getMessage());
                ((BotViewHolder)holder).bot_time.setText(currentTime);
                String endChar=chatsModal.getMessage().substring(chatsModal.getMessage().length()-1);
                if(endChar.equals("?")){
                    ((BotViewHolder)holder).yesBtn.setEnabled(true);
                    ((BotViewHolder)holder).noBtn.setEnabled(true);
                    ((BotViewHolder)holder).yesBtn.setVisibility(View.VISIBLE);
                    ((BotViewHolder)holder).noBtn.setVisibility(View.VISIBLE);


                }
                else{
                    ((BotViewHolder)holder).yesBtn.setEnabled(false);
                    ((BotViewHolder)holder).noBtn.setEnabled(false);
                    ((BotViewHolder)holder).yesBtn.setVisibility(View.GONE); // 화면에 안보이게 한다.
                    ((BotViewHolder)holder).noBtn.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position){
        switch (chatsModalArrayList.get(position).getSender()){
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        return chatsModalArrayList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView userTV;
        TextView user_time;



        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userTV = itemView.findViewById(R.id.idTVUser);
            user_time = itemView.findViewById(R.id.userTime);
        }
    }

    public class BotViewHolder extends RecyclerView.ViewHolder{
        TextView botTV;
        TextView bot_time;
        Button yesBtn=itemView.findViewById(R.id.yesButton);
        Button noBtn=itemView.findViewById(R.id.noButton);

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            botTV = itemView.findViewById(R.id.idTVBot);
            bot_time = itemView.findViewById(R.id.botTime);

            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = 0; //예를 클릭
                    mListener.onItemSelected(v, position);

                }
            });
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = 1; //아니오를 클릭
                    mListener.onItemSelected(v, position);

                }
            });

        }



    }
}