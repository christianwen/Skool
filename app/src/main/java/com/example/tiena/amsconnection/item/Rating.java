package com.example.tiena.amsconnection.item;

/**
 * Created by tiena on 6/09/2017.
 */

public class Rating {
    public int score;
    public String comment;
    public String from;
    public String to;
    public boolean state;

    public Rating(){

    }

    public Rating(int score,String comment,String from,String to, boolean state){
        this.score=score;
        this.comment=comment;
        this.from=from;
        this.to=to;
        this.state=state;
    }
}
