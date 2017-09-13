package com.example.tiena.amsconnection.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.tiena.amsconnection.broadcast.AlarmReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by tiena on 25/08/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if(notification!=null)
        Log.d("notification",notification.toString());
        Map<String,String> data = remoteMessage.getData();
        Log.d("data",data.toString());
        String task_id = data.get("task_id");
        if(task_id==null)return;
        Log.d("task_id",task_id);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Intent intent1 = new Intent(MyFirebaseMessagingService.this, AlarmReceiver.class);
        intent1.putExtra("task_id",task_id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyFirebaseMessagingService.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }
}
