package com.example.leafy;

import java.util.ArrayList;
import java.util.List;

public class UserAccount {

    private String idToken; //Firebase Uid (고유 토큰 정보)
    private String emailId;
    private String password;

    private String name;


    //List waterDate=new ArrayList();
    public List<String> waterDate;
    public List<Diary> diaryList;



    //파이어베이스 특징-빈 생성자를 만들어줘야 함
    public UserAccount(){

    }
    public UserAccount(String idToken, String emailId,String password){
        this.idToken = idToken;
        this.emailId = emailId;
        this.password = password;

    }

    public String getIdToken(){
        return idToken;
    }
    public void setIdToken(String idToken){
        this.idToken=idToken;
    }
    public String getEmailId(){
        return emailId;
    }
    public void setEmailId(String emailId){
        this.emailId=emailId;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password=password;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public boolean checkwaterDate(String newDate){
        String date;
        if( waterDate.isEmpty() || waterDate.size()==1){
            date= "2000-01-01 00:00:00";
        }
        else date= waterDate.get(waterDate.size()-1);

        date=date.substring(0,10);
        String tempDate=newDate.substring(0,10);
        if(date.equals(tempDate)) return false;
        else return true;

    }

    public void addwaterDate(String date){
        waterDate.add(date);
    }
    public void setwaterDate(){
        waterDate=new ArrayList();
        waterDate.add("first");
    }
    public String getwaterDate(int idx){
        if(waterDate.get(idx).equals("first")){
            return "0000-00-00 00";
        }
        else{
            String date=waterDate.get(idx).substring(0,10);
            return date;
        }

    }

    public void addDiary(Diary d){

        diaryList.add(d);
    }
    public void setDiaryList(){
        diaryList=new ArrayList();
        Diary d=new Diary("0000-00-00 00","오늘은 리피를 시작한 날이야.","000000000000000000000");
        diaryList.add(d);
    }

    public int getwaterDateSize(){
        return waterDate.size();
    }
    public List<String> getWaterDateList(){
        return this.waterDate;
    }

    public int getDiarySize(){
        return diaryList.size();
    }
    //i를 주면 그 인덱스의 기록한 날을 가져오는 함수
    public String getDiaryDate(int idx){
        String date=diaryList.get(idx).getDate().substring(0,10);
        return date;
    }

}
