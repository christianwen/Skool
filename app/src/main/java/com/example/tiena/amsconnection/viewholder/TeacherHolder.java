package com.example.tiena.amsconnection.viewholder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.activity.TeacherProfileActivity;
import com.example.tiena.amsconnection.helperclass.CircleTransform;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Created by tiena on 15/09/2017.
 */

public class TeacherHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private TextView mUserName, mRatingScore, mSubjects;
    private ImageView mUserAvatar;
    private String user_id;

    public TeacherHolder(View itemView){
        super(itemView);
        mUserName = itemView.findViewById(R.id.user_name);
        mRatingScore = itemView.findViewById(R.id.rating_score);
        mUserAvatar = itemView.findViewById(R.id.user_avatar);
        mSubjects = itemView.findViewById(R.id.teacher_subject);
        itemView.setOnClickListener(this);
    }

    public void init(String user_id){
        this.user_id = user_id;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("teachers/"+user_id);
        ref.child("info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserName.setText(dataSnapshot.child("name").getValue(String.class));
                if(dataSnapshot.child("photo_url").getValue()==null){
                    Picasso.with(itemView.getContext()).load(R.drawable.default_avatar).transform(new CircleTransform()).into(mUserAvatar);
                }
                String subjects_str = "";
                for(DataSnapshot subject : dataSnapshot.child("subjects").getChildren()){
                    subjects_str += subject.getValue(String.class) + " ";
                }
                mSubjects.setText(subjects_str);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.child("average_rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double average_rating = dataSnapshot.getValue(double.class);
                average_rating = (double)Math.round(average_rating * 10d) / 10d;
                mRatingScore.setText(average_rating+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(itemView.getContext(), TeacherProfileActivity.class);
        intent.putExtra("teacher_id",user_id);
        itemView.getContext().startActivity(intent);

    }
}
