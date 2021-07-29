package com.example.leafy;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public final TextView dayOfMonth;
    private final CalendarAdapter.OnItemListener onItemListener;
    private final ArrayList<LocalDate> days;
    public final View parentView;
    public final TextView calText1;
    public final TextView calText2;
    public boolean clicked;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days)
    {
        super(itemView);
        parentView = itemView.findViewById(R.id.parentView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        calText1=itemView.findViewById(R.id.calText1);
        calText2=itemView.findViewById(R.id.calText2);
        clicked=false;
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
        this.days=days;
        //clearLine();



    }

    @Override
    public void onClick(View view)
    {
        onItemListener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition()));

    }

}