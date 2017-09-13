package com.example.tiena.amsconnection.viewholder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tiena.amsconnection.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import com.example.tiena.amsconnection.helperclass.CircleTransform;
import io.techery.properratingbar.ProperRatingBar;

/**
 * Created by tiena on 6/09/2017.
 */

public class RatingHolder extends RecyclerView.ViewHolder {
    ImageView user_avatar;
    TextView rating_comment,user_name;
    ProperRatingBar rating_score;

    public RatingHolder(View itemView){
        super(itemView);
        user_avatar = itemView.findViewById(R.id.user_avatar);
        rating_comment = itemView.findViewById(R.id.rating_comment);
        rating_score = itemView.findViewById(R.id.rating_score);
        user_name = itemView.findViewById(R.id.user_name);
    }


    public void setUserAvatar(String user_id){
        if(user_id==null){
            Bitmap default_avatar = BitmapFactory.decodeResource(itemView.getContext().getResources(),
                    R.drawable.default_avatar);
            user_avatar.setImageBitmap(new CircleTransform().transform(default_avatar));
            return;
        }
        FirebaseDatabase.getInstance().getReference("students/"+user_id+"/photo_url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photo_url=dataSnapshot.getValue(String.class);
                Picasso.with(itemView.getContext()).load(photo_url).transform(new CircleTransform()).into(user_avatar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setUserName(String user_id){
        if(user_id==null){
            user_name.setText("Anonymous");
            return;
        }
        FirebaseDatabase.getInstance().getReference("students/"+user_id+"/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.getValue(String.class);
                user_name.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setRatingScore(int score){
        rating_score.setRating(score);
    }

    public void setRatingComment(String comment){
        rating_comment.setText(comment);
    }
}
