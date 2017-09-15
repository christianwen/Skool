package com.example.tiena.amsconnection.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.viewholder.TeacherHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TeacherListActivity extends AppCompatActivity {
    String CLASS_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);
        CLASS_ID = getIntent().getExtras().getString("class_id");
        setAdapter();
    }

   FirebaseRecyclerAdapter mAdapter ;

    private void setAdapter(){
        RecyclerView teachers = findViewById(R.id.teachers_recycler);
        teachers.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabase mDb = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDb.getReference("classes/"+CLASS_ID+"/teachers");

        mAdapter = new FirebaseRecyclerAdapter<Boolean,TeacherHolder>(
                Boolean.class,
                R.layout.teacher,
                TeacherHolder.class,
                ref
        ) {
            @Override
            protected void populateViewHolder(TeacherHolder holder, Boolean boo, int position) {
                String key = mAdapter.getRef(position).getKey();
                holder.init(key);
                Log.d("key",key);
            }
        };

        teachers.setAdapter(mAdapter);
    }
}
