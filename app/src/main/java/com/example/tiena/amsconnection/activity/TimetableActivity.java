package com.example.tiena.amsconnection.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.example.tiena.amsconnection.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimetableActivity extends Activity {
    final int[] DEFAULT_ICONS={R.drawable.mon_icon,R.drawable.tue_icon,R.drawable.wed_icon,R.drawable.thu_icon,R.drawable.fri_icon,R.drawable.sat_icon};
    final int[] DEFAULT_COLORS={R.color.Monday,R.color.Tuesday,R.color.Wednesday,R.color.Thursday,R.color.Friday,R.color.Saturday};
    final String[] DEFAULT_NAMES={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    final int SCHOOL_DAY_NUM=6;
    String class_id;
    ExpandingList expandingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        class_id=getIntent().getExtras().getString("class_id",null);

        String weekDay=new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date());
        String tomorrowWeekDay=getTomorrowWeekDay(weekDay);
        String suffix="";
        if(tomorrowWeekDay.equals("Monday")){
            suffix+= " (Next Monday)";
        }
        else suffix+=" (Tomorrow)";

        expandingList =  findViewById(R.id.expanding_list_main);
        for(int i=0;i<SCHOOL_DAY_NUM;i++){
            final ExpandingItem item=expandingList.createNewItem(R.layout.expanding_layout);

            item.setIndicatorIconRes(DEFAULT_ICONS[i]);
            item.setIndicatorColorRes(DEFAULT_COLORS[i]);
            final boolean flag = DEFAULT_NAMES[i].equals(tomorrowWeekDay);
            String text = flag ? DEFAULT_NAMES[i] + suffix : DEFAULT_NAMES[i] ;
            ((TextView) item.findViewById(R.id.title)).setText(text);

            FirebaseDatabase.getInstance().getReference("timetable/"+class_id+"/"+DEFAULT_NAMES[i].toLowerCase())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                View subItem=item.createSubItem();
                                ((TextView) subItem.findViewById(R.id.sub_title)).setText(snapshot.getValue(String.class));
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            if(!flag)item.collapse();


        }


    }

    String getTomorrowWeekDay(String currentWeekDay){
        String result=null;
        String[] WEEK_DAYS = getResources().getStringArray(R.array.week_days);
        for(int i=0;i<8;i++){
            if(WEEK_DAYS[i].equals(currentWeekDay)){
                result=WEEK_DAYS[i+1];
                break;
            }
        }
        return result;

    }
}
