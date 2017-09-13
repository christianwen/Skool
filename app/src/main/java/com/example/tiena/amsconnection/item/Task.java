package com.example.tiena.amsconnection.item;

/**
 * Created by tiena on 1/09/2017.
 */

public class Task {
    public String user_id;
    public String class_id;
    public String title;
    public String content;
    public String deadline;
    public long time_created;
    public Task(){

    }
    public Task(String user_id, String class_id, String title, String content, String deadline, long time_created){
        this.user_id=user_id;
        this.class_id=class_id;
        this.title=title;
        this.content=content;
        this.deadline=deadline;
        this.time_created = time_created;
    }


}
