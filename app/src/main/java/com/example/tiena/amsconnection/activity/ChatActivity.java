package com.example.tiena.amsconnection.activity;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.helperclass.AnythingHelper;
import com.example.tiena.amsconnection.item.Message;
import com.example.tiena.amsconnection.viewholder.ChatHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StreamDownloadTask;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{
    String ROOM_ID ;
    DatabaseReference dbRef;
    FirebaseUser user;
    final static int SENT_BY_ME = 123;
    final static int SENT_BY_OTHERS = 234;
    final static int SENT_BY_OTHERS_SAME_AS_LAST = 345;
    final static int SENT_BY_OTHERS_WITH_TIME = 456;
    final static int SENT_BY_ME_WITH_TIME = 567;
    final static int MAX_TIME_OFFLINE = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ROOM_ID = getIntent().getExtras().getString("room_id");
        Log.d("room_id",ROOM_ID);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        dbRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)return;
        setAdapter();
        setSendMessageButton();
    }

    FirebaseRecyclerAdapter mAdapter;
    RecyclerView messageList;
    private void setAdapter(){
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        //manager.setReverseLayout(true);
        //manager.setStackFromEnd(true);

        messageList = findViewById(R.id.message_list);
        messageList.setLayoutManager(manager);

        final DatabaseReference ref = dbRef.child("messages/"+ROOM_ID);

        mAdapter = new FirebaseRecyclerAdapter<Message,ChatHolder>(
                Message.class,
                R.layout.message,
                ChatHolder.class,
                ref
        ) {
            @Override
            protected void populateViewHolder(ChatHolder holder, Message message, int position) {

                //String key = mAdapter.getRef(position).getKey();
                holder.init(message);
            }

            @Override
            public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = getLayoutInflater();
                View layout;

                switch (viewType){
                    case SENT_BY_ME:
                        layout = inflater.inflate(R.layout.message_from_me,parent,false);
                        return new ChatHolder(layout,true);
                    case SENT_BY_OTHERS:
                        layout = inflater.inflate(R.layout.message,parent,false);
                        return new ChatHolder(layout);
                    case SENT_BY_OTHERS_SAME_AS_LAST:
                        layout = inflater.inflate(R.layout.message,parent,false);
                        return new ChatHolder(layout,false,true);
                    case SENT_BY_ME_WITH_TIME:
                        layout = inflater.inflate(R.layout.message_from_me,parent,false);
                        return new ChatHolder(layout,true,false,true);

                    case SENT_BY_OTHERS_WITH_TIME:

                        layout = inflater.inflate(R.layout.message,parent,false);
                        return new ChatHolder(layout,false,true,true);
                }
                return null;
            }

            @Override
            public int getItemViewType(int position) {
                int viewType;
                String current_sender = getItem(position).from;
                String last_sender = position == 0 ? "" : getItem(position-1).from;
                Long current_time_created = getItem(position).time_created;
                Long last_time_created = position == 0 ? 0 : getItem(position-1).time_created;
                if(getItem(position).from.equals(user.getUid())){
                    if(current_time_created - last_time_created>MAX_TIME_OFFLINE * 1000){
                        viewType = SENT_BY_ME_WITH_TIME;
                    }else viewType = SENT_BY_ME;
                }
                else {
                    if(current_time_created - last_time_created >MAX_TIME_OFFLINE *1000){
                        viewType = SENT_BY_OTHERS_WITH_TIME;
                    }else
                    if(current_sender.equals(last_sender)){
                        viewType = SENT_BY_OTHERS_SAME_AS_LAST;
                    }else viewType = SENT_BY_OTHERS;
                }

                return viewType;
            }
        };

        messageList.setItemViewCacheSize(20);
        messageList.setDrawingCacheEnabled(true);
        messageList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        messageList.setAdapter(mAdapter);


        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = mAdapter.getItemCount();
                int lastVisiblePosition =
                        manager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    messageList.scrollToPosition(positionStart);
                }
            }
        });

    }

    private void setSendMessageButton(){
        ImageButton btnSend = findViewById(R.id.send_button);

        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.send_button){
            EditText chatEdt = findViewById(R.id.chat_edit_text);
            String content = chatEdt.getText().toString();
            if(content.equals("")){
                Toast.makeText(this, "Message cannot be blank.", Toast.LENGTH_SHORT).show();
                return;
            }
            String from = user.getUid();

            Long time_created = System.currentTimeMillis();

            String key = dbRef.child("messages/"+ROOM_ID).push().getKey();
            dbRef.child("messages/"+ROOM_ID+"/"+key).setValue(new Message(content,from,time_created));
            dbRef.child("chat_rooms/"+ROOM_ID+"/last_message_id").setValue(key);
            chatEdt.setText("");
            AnythingHelper.hideKeyboard(this);
            messageList.scrollToPosition(mAdapter.getItemCount()-1);
        }
    }

}
