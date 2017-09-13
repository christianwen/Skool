package com.example.tiena.amsconnection.activity;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aigestudio.wheelpicker.WheelPicker;
import com.example.tiena.amsconnection.service.MyFirebaseInstanceIDService;
import com.example.tiena.amsconnection.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.List;


public class RequestInfoActivity extends AppCompatActivity {
    DatabaseReference mDb;
    List<String> classesData=new ArrayList<>();
    ArrayList<String> classesKey=new ArrayList<>();
    List<String> schoolsData=new ArrayList<>();
    ArrayList<String> schoolsKey=new ArrayList<>();
    WheelPicker wheelPickerClass;
    WheelPicker wheelPickerSchool;
    Button btnNextClass,btnNextSchool;

    int count=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_info);
        mDb=FirebaseDatabase.getInstance().getReference();
        //alterDB();
        btnNextClass=findViewById(R.id.button_next_class);
        btnNextSchool=findViewById(R.id.button_next_school);

        wheelPickerClass=findViewById(R.id.wheel_picker_class);
        wheelPickerSchool=findViewById(R.id.wheel_picker_school);
        loadSchoolsData();


        wheelPickerClass.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {

            }
        });

        btnNextClass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null) {
                    String user_name = user.getDisplayName();
                    String user_class = classesKey.get(wheelPickerClass.getCurrentItemPosition());
                    String user_id = user.getUid();

                    mDb.child("students").child(user_id).setValue(new StudentEntry(user_name, user_class));
                    new MyFirebaseInstanceIDService().onTokenRefresh();
                    Intent intent=new Intent(RequestInfoActivity.this,MainActivity.class);
                    intent.putExtra("class_id",user_class);
                    startActivity(intent);
                }
            }
        });

        btnNextSchool.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String user_school_id = schoolsKey.get(wheelPickerSchool.getCurrentItemPosition());

                loadClassesData(user_school_id);
                findViewById(R.id.school_picker_layout).setVisibility(View.GONE);
                findViewById(R.id.class_picker_layout).setVisibility(View.VISIBLE);


            }
        });

    }

    private void loadSchoolsData(){
        mDb.child("school_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final String school_data = snapshot.getValue(String.class);

                    schoolsData.add(school_data);

                    String key=snapshot.getKey();
                    schoolsKey.add(key);
                    /*mDb.child("classes/"+key+"/member_num").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            count++;
                            if(dataSnapshot.getValue()!=null) {
                                int member_num=dataSnapshot.getValue(int.class);
                                String suffix = member_num==1?"mem":"mems";
                                classesData.set(count,class_data + " (" + member_num + " "+suffix+")");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/
                }
                wheelPickerSchool.setData(schoolsData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadClassesData(String school_id){
        mDb.child("schools/"+school_id+"/classes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final String class_data = snapshot.getValue(String.class);

                    classesData.add(class_data);

                    String key=snapshot.getKey();
                    classesKey.add(key);
                    mDb.child("classes/"+key+"/member_num").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            count++;
                            if(dataSnapshot.getValue()!=null) {
                                int member_num=dataSnapshot.getValue(int.class);
                                String suffix = member_num==1?"mem":"mems";
                                classesData.set(count,class_data + " (" + member_num + " "+suffix+")");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if(classesData.size()==0){
                    wheelPickerClass.setVisibleItemCount(2);
                    classesData.add("No class");
                    classesData.add("No class");

                }
                wheelPickerClass.setData(classesData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private class StudentEntry{
        public String name;
        public String class_id;
        public StudentEntry(){

        };
        public StudentEntry(String name,String class_id){
            this.name=name;
            this.class_id=class_id;
        }
    }

    void alterDB(){
        mDb.child("classes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
