package com.example.tiena.amsconnection.broadcast;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.activity.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by tiena on 24/08/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    String contentTitle="";
    String contentText="";
    String task_id;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d("my log","broadcast received");

        task_id=intent.getExtras().getString("task_id");
        if(task_id==null){
            Log.d("null task_id","fuck you");
            return;
        }


        long when = System.currentTimeMillis();
        final NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("fragment","noti");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final int mNotificationId=1000;


        final NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.ic_launcher)
                .setSound(alarmSound)
                .setAutoCancel(true).setWhen(when)
                .setContentIntent(resultPendingIntent);


        FirebaseDatabase.getInstance().getReference("tasks").child(task_id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        contentTitle=dataSnapshot.child("name").getValue(String.class);
                        contentText=dataSnapshot.child("deadline").getValue(String.class);
                        mNotifyBuilder.setContentTitle(contentTitle)
                                .setContentText(contentText);
                        Log.d("content",contentText+" "+contentTitle);
                        notificationManager.notify(mNotificationId, mNotifyBuilder.build());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );


    }
}
