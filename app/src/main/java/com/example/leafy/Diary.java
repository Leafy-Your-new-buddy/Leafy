package com.example.leafy;

public class Diary {
    public String date;
    public String image;
    public String diary;
    public Diary(){

    }
    public Diary(String t, String d,String i){
        this.date=t;
        this.diary=d;
        this.image=i;
    }

    public String getDate(){
        return this.date;
    }
    public String getDiary(){
        return this.diary;
    }
    public String getImage(){
        return this.image;
    }
}
