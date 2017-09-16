package com.example.tiena.amsconnection.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.tiena.amsconnection.R;

public class ChatActivity extends AppCompatActivity {
    String ROOM_ID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ROOM_ID = getIntent().getExtras().getString("room_id");
    }
}
