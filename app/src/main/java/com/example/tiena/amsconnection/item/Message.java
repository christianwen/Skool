package com.example.tiena.amsconnection.item;

import android.util.Log;

/**
 * Created by tiena on 17/09/2017.
 */

public class Message {
    public String from;
    public String content;
    public Long time_created;

    public Message(){
    }

    public Message(String content, String from, Long time_created){
        this.content = content;
        this.from = from;
        this.time_created = time_created;
    }
}
