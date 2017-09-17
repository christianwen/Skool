package com.example.tiena.amsconnection.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.helperclass.AnythingHelper;
import com.example.tiena.amsconnection.helperclass.CircleTransform;
import com.example.tiena.amsconnection.item.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Created by tiena on 17/09/2017.
 */

public class ChatHolder extends RecyclerView.ViewHolder {
    ImageView mSenderAvatar;
    TextView mSenderName, mContent, mTimeCreated;
    boolean isSentByMe = false;
    boolean isSameAsLast = false;
    boolean isWithTime = false;

    public ChatHolder(View itemView) {
        super(itemView);
        mSenderAvatar = itemView.findViewById(R.id.user_avatar);
        mSenderName = itemView.findViewById(R.id.user_name);
        mSenderName.setVisibility(View.VISIBLE);
        mContent = itemView.findViewById(R.id.message_content);
    }

    public ChatHolder(View itemView, boolean isSentByMe){
        super(itemView);
        this.isSentByMe = isSentByMe;
        mContent = itemView.findViewById(R.id.message_content);
    }

    public ChatHolder(View itemView, boolean isSentByMe, boolean isSameAsLast){
        super(itemView);
        this.isSameAsLast = isSameAsLast;
        mContent = itemView.findViewById(R.id.message_content);
    }

    public ChatHolder(View itemView, boolean isSentByMe, boolean isSameAsLast,boolean isWithTime){
        super(itemView);
        this.isWithTime = isWithTime;
        this.isSentByMe = isSentByMe;
        if(isWithTime){
            mTimeCreated = itemView.findViewById(R.id.message_time_created);
            mTimeCreated.setVisibility(View.VISIBLE);
        }
        if(!isSentByMe) {
            mSenderAvatar = itemView.findViewById(R.id.user_avatar);
            mSenderName = itemView.findViewById(R.id.user_name);
            mSenderName.setVisibility(View.VISIBLE);
        }
        mContent = itemView.findViewById(R.id.message_content);
    }

    public void init(Message message) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        mContent.setText(message.content);
        if (!isSentByMe && !isSameAsLast) {
            dbRef.child("students/" + message.from + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    mSenderName.setText(name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            dbRef.child("students/" + message.from + "/photo_url").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String photo_url = dataSnapshot.getValue(String.class);
                    if (photo_url == null || "".equals(photo_url)) {
                        Picasso.with(itemView.getContext())
                                .load(R.drawable.default_avatar).fit()
                                .transform(new CircleTransform())
                                .into(mSenderAvatar);
                    } else {
                        Picasso.with(itemView.getContext())
                                .load(photo_url).fit()
                                .transform(new CircleTransform())
                                .into(mSenderAvatar);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if(isWithTime){
            String time = AnythingHelper.convertTimestampToDate(message.time_created,"HH:mm");
            mTimeCreated.setText(time);
        }
    }
}
