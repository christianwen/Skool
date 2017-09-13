package com.example.tiena.amsconnection.activity;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.fragment.RatingDialogFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.foldingcell.FoldingCell;

import io.techery.properratingbar.ProperRatingBar;
import io.techery.properratingbar.RatingListener;
import com.example.tiena.amsconnection.viewholder.RatingHolder;

public class TeacherProfileActivity extends Activity {
    String TEACHER_ID;
    RecyclerView ratingList;
    FirebaseRecyclerAdapter mAdapter;
    String teacher_classes;
    DatabaseReference dbRef;
    double average_rating;
    int total_ratings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        final FoldingCell fc = findViewById(R.id.folding_cell_about);
        // attach click listener to folding cell
        fc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.toggle(false);
            }
        });

        final TextView teacherName=findViewById(R.id.teacher_name);
        final TextView teacherSubject=findViewById(R.id.teacher_subject);
        final ImageView teacherImage=findViewById(R.id.teacher_avatar);
        final TextView ratingStatus=findViewById(R.id.rating_status);

        ratingList = findViewById(R.id.rating_list);
        ratingList.setLayoutManager(new LinearLayoutManager(this));

        TEACHER_ID = getIntent().getExtras().getString("teacher_id",null);

        final ProperRatingBar ratingBar=findViewById(R.id.rating_score);
        ratingBar.setListener(new RatingListener() {
            @Override
            public void onRatePicked(ProperRatingBar properRatingBar) {
                RatingDialogFragment fragment = new RatingDialogFragment();
                Bundle args = new Bundle();
                args.putInt("rating_score",ratingBar.getRating());
                args.putString("teacher_id",TEACHER_ID);

                fragment.setArguments(args);
                fragment.show(getFragmentManager(),"rating_dialog_fragment");
            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference("teachers/"+TEACHER_ID);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView teacher_phone=findViewById(R.id.teacher_phone);
                TextView teacher_email=findViewById(R.id.teacher_email);
                final TextView teacher_classes=findViewById(R.id.teacher_classes);
                teacher_phone.setText(dataSnapshot.child("info").child("phone").getValue(String.class));
                teacher_email.setText(dataSnapshot.child("info").child("email").getValue(String.class));

                for(DataSnapshot snapshot : dataSnapshot.child("info/classes").getChildren()){
                    String class_id=snapshot.getKey();
                    FirebaseDatabase.getInstance().getReference("classes/"+class_id+"/name")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    teacher_classes.setText(teacher_classes.getText()+dataSnapshot.getValue(String.class)+" ");
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                }

                teacherName.setText(dataSnapshot.child("info").child("name").getValue(String.class));
                String subject_str="";
                for(DataSnapshot snapshot : dataSnapshot.child("info/subjects").getChildren()){
                    subject_str+=snapshot.getValue(String.class)+" ";
                }

                teacherSubject.setText(subject_str);


                if(dataSnapshot.child("image_url").getValue()==null){
                    Bitmap default_avatar = BitmapFactory.decodeResource(getResources(),
                            R.drawable.default_avatar);
                    teacherImage.setImageBitmap(default_avatar);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("average_rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                average_rating=dataSnapshot.getValue(double.class);
                average_rating = (double)Math.round(average_rating * 10d) / 10d;
                String rating_status="";
                rating_status+= average_rating + " stars out of 6 in "+total_ratings+" reviews";
                ratingStatus.setText(rating_status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("total_ratings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                total_ratings =  dataSnapshot.getValue(int.class);
                String rating_status="";
                rating_status+= average_rating + " stars out of 6 in "+total_ratings+" reviews";
                ratingStatus.setText(rating_status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        setRecyclerAdapter();

    }

    public void setRecyclerAdapter(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("teachers/"+TEACHER_ID+"/ratings");
        final DatabaseReference refToRatings = FirebaseDatabase.getInstance().getReference("ratings");
        mAdapter = new FirebaseRecyclerAdapter<Boolean, RatingHolder>(
                Boolean.class,
                R.layout.rating,
                RatingHolder.class,
                ref
        ) {
            @Override
            protected void populateViewHolder(final RatingHolder holder, Boolean value, int position) {
                String key = mAdapter.getRef(position).getKey();
                refToRatings.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        holder.setRatingScore(dataSnapshot.child("score").getValue(int.class));
                        holder.setRatingComment(dataSnapshot.child("comment").getValue(String.class));
                        if(!dataSnapshot.child("state").getValue(Boolean.class)){
                            holder.setUserName(null);
                            holder.setUserAvatar(null);
                        }else {
                            String user_id = dataSnapshot.child("from").getValue(String.class);
                            holder.setUserAvatar(user_id);
                            holder.setUserName(user_id);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };


        ratingList.setAdapter(mAdapter);
    }
}
