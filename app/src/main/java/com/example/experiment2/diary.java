package com.example.experiment2;

public class diary {
    private String title;
    private String date;
    private int id;

    public diary(int id,String title,String date){
        this.title=title;
        this.id=id;
        this.date=date;
    }
    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getTitle(){
        return title;
    }
    public String getDate(){
        return date;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setDate(String date){
        this.date = date;
    }
}
