package com.example.tiena.amsconnection.viewholder;



import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.activity.ChatActivity;
import com.example.tiena.amsconnection.helperclass.AnythingHelper;
import com.example.tiena.amsconnection.helperclass.CircleTransform;
import com.example.tiena.amsconnection.item.Message;
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
 * Created by tiena on 16/09/2017.
 */

public class ChatRoomHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView mName, mLastMessage, mLastMessageTime;
    ImageView mImage;
    String key;

    public ChatRoomHolder(View itemView){
        super(itemView);
        mName = itemView.findViewById(R.id.chat_room_name);
        mLastMessage = itemView.findViewById(R.id.last_message);
        mLastMessageTime = itemView.findViewById(R.id.last_message_time);
        mImage = itemView.findViewById(R.id.chat_room_image);
        itemView.setOnClickListener(this);
    }

    public void init(final String key){
        this.key = key;
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("chat_rooms/"+key+"/basics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mName.setText(dataSnapshot.child("name").getValue(String.class));
                String image_url = dataSnapshot.child("image_url").getValue(String.class);
                if(image_url==null||"".equals(image_url)){
                    Picasso.with(itemView.getContext()).load(R.drawable.default_avatar).transform(new CircleTransform()).into(mImage);
                }
                else{
                    Picasso.with(itemView.getContext()).load(image_url).transform(new CircleTransform()).into(mImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("chat_rooms/"+key+"/last_message_id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String lastMessageId = dataSnapshot.getValue(String.class);
                dbRef.child("messages/"+key+"/"+lastMessageId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Message lastMessage = dataSnapshot.getValue(Message.class);
                        if(lastMessage!=null) {

                            final String content = lastMessage.content;
                            dbRef.child("students/"+lastMessage.from+"/name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String name = dataSnapshot.getValue(String.class);
                                    int max_length = content.length() < 30 ? content.length() : 30;
                                    String text = name+": "+content.substring(0,max_length-1)+"...";
                                    mLastMessage.setText(text);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            if(lastMessage.time_created != null){
                                mLastMessageTime.setText(AnythingHelper.convertTimestampToDate(lastMessage.time_created,"HH:mm"));
                            }
                        }
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

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(itemView.getContext(), ChatActivity.class);
        intent.putExtra("room_id",key);
        itemView.getContext().startActivity(intent);
    }
}
