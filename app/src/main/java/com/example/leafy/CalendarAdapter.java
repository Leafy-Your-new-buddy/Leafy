package com.example.leafy;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.example.leafy.R.color.water;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;

    public CalendarViewHolder holder;

    @Override
    public long getItemId(int position) {
        return position;
    }
    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener)
    {
        this.days = days;
        this.onItemListener = onItemListener;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(days.size() > 15) //month view
        {
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        }

        else // week view
            layoutParams.height = (int) parent.getHeight();
        holder=new CalendarViewHolder(view, onItemListener, days);


        return holder;
    }

    boolean recordCheck;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {

        final LocalDate date = days.get(position);

        if(date == null)
            holder.dayOfMonth.setText("");
        else
        {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if(date.equals(CalendarUtils.selectedDate)){

                holder.parentView.setBackgroundResource(R.drawable.edge_cal); //?????? ?????? ????????? ????????????..
                if(holder.calText1.getText().equals("????????? ???")||holder.calText2.getText().equals("????????? ???"))
                    recordCheck=true;
                else
                    recordCheck=false;
            }
            else{ //???????????? ??????????????? ????????? ?????????
                holder.parentView.setBackgroundResource(0);
            }

            showWaterDate(holder,date); //?????????,???????????? ??????



        }

    }



    //???????????????????????? ????????? ?????? ????????????
    public void showWaterDate(@NonNull CalendarViewHolder holder,LocalDate date){
        /* -----------------------------------------??????????????????---------------------------------------*/
        DatabaseReference mDatabaseRef= FirebaseDatabase.getInstance().getReference("appname");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // ???????????? ????????? ?????? ????????????
        String uid = user != null ? user.getUid() : null; // ???????????? ????????? ?????? uid ????????????



        mDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserAccount value =  snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);


                for(int i=0;i<value.getwaterDateSize();i++){
                    String water=value.getwaterDate(i);

                    if(water.equals(date.toString())){

                        if(!holder.calText1.getText().equals("????????? ???")){
                            holder.calText1.setBackgroundColor(Color.parseColor("#D4F4FA"));
                            holder.calText1.setText(" ??? ??? ??? ");
                        }
                        else {
                            holder.calText2.setBackgroundColor(Color.parseColor("#D4F4FA"));
                            holder.calText2.setText(" ??? ??? ??? ");
                        }
                    }
                }

                for(int i=0;i<value.getDiarySize();i++){
                    String water=value.getDiaryDate(i);
                    if(water.equals(date.toString())){

                        if(!holder.calText1.getText().equals(" ??? ??? ??? ")){
                            holder.calText1.setBackgroundColor(Color.parseColor("#FAECC5"));
                            holder.calText1.setText("????????? ???");
                        }
                        else {
                            holder.calText2.setBackgroundColor(Color.parseColor("#FAECC5"));
                            holder.calText2.setText("????????? ???");
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /* ----------------------------------------??????????????????----------------------------------------------*/
    }

    public boolean checkRecordDate(){
            return this.recordCheck;

    }
    public CalendarViewHolder getHolder(){
        return this.holder;
    }



    @Override
    public int getItemCount()
    {
        return days.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, LocalDate date);

    }
}
