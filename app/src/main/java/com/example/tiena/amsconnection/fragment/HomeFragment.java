package com.example.tiena.amsconnection.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.activity.SignInActivity;
import com.example.tiena.amsconnection.activity.TeacherProfileActivity;
import com.example.tiena.amsconnection.activity.TimetableActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.tiena.amsconnection.helperclass.CircleTransform;

/**
 * Created by tiena on 22/08/2017.
 */

public  class HomeFragment extends Fragment{
    FirebaseDatabase mDb= FirebaseDatabase.getInstance();

    String[] WEEK_DAYS={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Monday","Sunday","Monday"};
    FirebaseUser user;


    ExpandingList expandingList;
    String class_id,user_id;
    ExpandingItem item_timetable,item_teachers;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View homeFragmentLayout=inflater.inflate(R.layout.home_fragment, container, false);
        ImageView userAvatar=homeFragmentLayout.findViewById(R.id.user_avatar);
        TextView userName=homeFragmentLayout.findViewById(R.id.user_name);
        expandingList =  homeFragmentLayout.findViewById(R.id.expanding_list_main);
        class_id=getArguments().getString("class_id",null);
        Log.d("class_id",class_id);

        user= FirebaseAuth.getInstance().getCurrentUser();
        user_id=user.getUid();

        Uri photoUri=user.getPhotoUrl();
        Log.d("photo_url",photoUri.toString());
        Picasso.with(getActivity().getApplicationContext()).load(photoUri).transform(new CircleTransform()).into(userAvatar);
        userName.setText(user.getDisplayName());

        setTimetableView();


        setTeachersView();



        return homeFragmentLayout;


    }

    String getTomorrowWeekDay(String currentWeekDay){
        String result=null;
        for(int i=0;i<8;i++){
            if(WEEK_DAYS[i].equals(currentWeekDay)){
                result=WEEK_DAYS[i+1];
                break;
            }
        }
        return result;

    }

    void setTimetableView(){
        String weekDay=new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date());
        Log.d("Weekday",weekDay);
        String tomorrowWeekDay=getTomorrowWeekDay(weekDay);

        Log.d("tomorrow",tomorrowWeekDay);
        Log.d("class id",class_id);

        item_timetable = expandingList.createNewItem(R.layout.expanding_layout);
        item_timetable.setIndicatorColorRes(R.color.TimetableColor);
        item_timetable.setIndicatorIconRes(R.drawable.timetable_icon);
        String str="Timetable ";
        if(tomorrowWeekDay.equals("Monday")){
            str+= "(Next Monday)";
        }
        else str+="(Tomorrow)";
        ((TextView)item_timetable.findViewById(R.id.title)).setText(str);
        item_timetable.collapse();
        mDb.getReference("timetable/"+class_id+"/"+tomorrowWeekDay.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count=0;

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    View subItem=item_timetable.createSubItem();
                    TextView textView=subItem.findViewById(R.id.sub_title);
                    textView.setText(snapshot.getValue(String.class));
                    count++;
                }

                int[] layout_id={com.diegodobelo.expandingview.R.layout.layout_template};
                item_timetable.createSubItems(layout_id);
                View fullTimetable=item_timetable.getSubItemView(count);
                Button seeTimetableBtn=fullTimetable.findViewById(R.id.see_timetable_button);

                seeTimetableBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getActivity(),TimetableActivity.class);
                        intent.putExtra("class_id",class_id);
                        startActivity(intent);

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    void setTeachersView(){
        item_teachers=expandingList.createNewItem(R.layout.expanding_layout);
        item_teachers.setIndicatorColorRes(R.color.TeachersColor);
        item_teachers.setIndicatorIconRes(R.drawable.teacher_icon);
        ((TextView) item_teachers.findViewById(R.id.title)).setText("Teachers");
        item_teachers.collapse();
        mDb.getReference("classes/"+class_id+"/teachers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final String key = snapshot.getKey();
                    mDb.getReference("teachers/"+key+"/info/name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            View subItem = item_teachers.createSubItem();
                            TextView textView=subItem.findViewById(R.id.sub_title);
                            textView.setText(dataSnapshot.getValue(String.class));
                            subItem.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent=new Intent(getActivity(),TeacherProfileActivity.class);
                                    intent.putExtra("teacher_id",key);
                                    startActivity(intent);

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}