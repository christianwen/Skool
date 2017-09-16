package com.example.tiena.amsconnection.viewholder;



import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.activity.ChatActivity;
import com.example.tiena.amsconnection.helperclass.CircleTransform;
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

    public void init(String key){
        this.key = key;
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
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

        dbRef.child("chat_rooms/"+key+"/statistics").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String lastMessage = dataSnapshot.child("last_message").getValue(String.class);
                mLastMessage.setText(lastMessage);
                Long lastMessageTime = dataSnapshot.child("last_message_time").getValue(Long.class);
                if(lastMessageTime!=null) {
                    Date date = new Date(lastMessageTime);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                    mLastMessageTime.setText(sdf.format(date));
                }
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
