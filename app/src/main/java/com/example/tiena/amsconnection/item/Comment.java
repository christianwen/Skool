package com.example.tiena.amsconnection.item;

/**
 * Created by tiena on 12/09/2017.
 */

public class Comment {
    public String user_id;
    public String task_id;
    public String content;
    public Long time_created;

    public Comment(){

    }

    public Comment(String user_id,String task_id,String content,Long time_created){
        this.user_id = user_id;
        this.task_id = task_id;
        this.content = content;
        this.time_created = time_created;
    }
}
