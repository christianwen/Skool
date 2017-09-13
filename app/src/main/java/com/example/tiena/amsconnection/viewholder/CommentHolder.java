package com.example.tiena.amsconnection.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.helperclass.CircleTransform;
import com.example.tiena.amsconnection.item.Comment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * Created by tiena on 12/09/2017.
 */

public class CommentHolder extends RecyclerView.ViewHolder {
    private TextView mUserName, mContent, mTimeCreated;

    private ImageView mUserAvatar;

    public CommentHolder(View itemView){
        super(itemView);
        mUserName = itemView.findViewById(R.id.user_name);
        mUserAvatar = itemView.findViewById(R.id.user_avatar);
        mContent = itemView.findViewById(R.id.content);
        mTimeCreated = itemView.findViewById(R.id.time_created);
    }

    public void init(String key){
        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
        ref.child("comments/"+key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                mContent.setText(comment.content);
                Date date = new Date(comment.time_created);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.ENGLISH);
                mTimeCreated.setText(sdf.format(date));
                ref.child("students/"+comment.user_id+"/name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUserName.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                ref.child("students/"+comment.user_id+"/photo_url").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Picasso.with(itemView.getContext()).load(dataSnapshot.getValue(String.class)).transform(new CircleTransform()).into(mUserAvatar);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }

}
