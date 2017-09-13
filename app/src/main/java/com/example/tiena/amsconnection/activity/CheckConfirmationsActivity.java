package com.example.tiena.amsconnection.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.tiena.amsconnection.R;

public class CheckConfirmationsActivity extends AppCompatActivity {

    String TASK_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_confirmations);
        //Use this id to access confirmations in /task/{task_id}
        TASK_ID = getIntent().getExtras().getString("task_id");
    }
}
