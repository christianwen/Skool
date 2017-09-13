package com.example.tiena.amsconnection.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.helperclass.AnythingHelper;
import com.example.tiena.amsconnection.helperclass.CircleTransform;
import com.example.tiena.amsconnection.helperclass.SquareTransform;
import com.example.tiena.amsconnection.item.Comment;
import com.example.tiena.amsconnection.item.Task;
import com.example.tiena.amsconnection.viewholder.CommentHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewTaskActivity extends Activity {

    String KEY;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    LinearLayout photoContainer,photoContainer2;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    int[] icons = {R.drawable.ic_confirm_active,R.drawable.ic_confirm_inactive,R.drawable.ic_comment_active,R.drawable.ic_comment_inactive};
    int[] colors = {R.color.ConfirmActive,R.color.ActionInactive,R.color.CommentActive,R.color.ActionInactive};
    private FirebaseRecyclerAdapter mAdapter;
    RecyclerView commentsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_task);
        photoContainer = findViewById(R.id.photo_container);
        photoContainer2 = findViewById(R.id.photo_container_2);
        commentsRecycler = findViewById(R.id.comments_recycler);
        commentsRecycler.setLayoutManager(new LinearLayoutManager(this));
        KEY = getIntent().getExtras().getString("key",null);
        setTaskLayout();
        setStatistics();
        setConfirmBtn();
        setCommentEdt();
        setComments();
    }

    boolean confirmed = false;

    void setConfirmBtn(){
        final Button confirmBtn = findViewById(R.id.confirm_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmed=!confirmed;
                toggleConfirm(confirmed);
                String user_id = user.getUid();
                if(confirmed){
                    dbRef.child("tasks/"+KEY+"/confirmations/"+user_id).setValue(true);
                }
                else{
                    dbRef.child("tasks/"+KEY+"/confirmations/"+user_id).setValue(null);
                }

            }
        });
    }

    void toggleConfirm(boolean confirmed){
        final Button confirmBtn = findViewById(R.id.confirm_button);

        int command = confirmed?0:1;
        confirmBtn.setTextColor(getResources().getColor(colors[command]));
        Drawable icon = getResources().getDrawable( icons[command] );
        confirmBtn.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
    }


    void setTaskPublisherInfo(String user_id){
        dbRef.child("students/"+user_id+"/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView) findViewById(R.id.user_name)).setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("students/"+user_id+"/photo_url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageView imageView = findViewById(R.id.user_avatar);
                Picasso.with(ViewTaskActivity.this).load(dataSnapshot.getValue(String.class)).transform(new CircleTransform()).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void setTaskLayout(){
        dbRef.child("tasks/"+KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String publisher_id = dataSnapshot.child("user_id").getValue(String.class);
                setTaskPublisherInfo(publisher_id);

                Long time_created = dataSnapshot.child("time_created").getValue(Long.class);
                if(time_created!=null){
                    String time = AnythingHelper.convertTimestampToDate(time_created);
                    ((TextView) findViewById(R.id.task_time_created)).setText(time);
                }

                if(dataSnapshot.child("confirmations/"+user.getUid()).getValue()!=null){
                    confirmed=true;
                    toggleConfirm(true);
                }

                ((TextView) findViewById(R.id.task_content)).setText(dataSnapshot.child("content").getValue(String.class));


                DataSnapshot photoUrls = dataSnapshot.child("photo_urls");
                if(photoUrls!=null) {
                    int photos_count=dataSnapshot.child("photos_count").getValue(int.class);
                    boolean hasManyPhotos = !(photos_count==1);
                    int count=0;
                    for (DataSnapshot photoUrl : photoUrls.getChildren()) {
                        count++;
                        if(count==5)return;
                        String url = photoUrl.getValue(String.class);
                        ImageView imageView = new ImageView(ViewTaskActivity.this);
                        LinearLayout.LayoutParams params;
                        if(hasManyPhotos){
                            params = new LinearLayout.LayoutParams(512, 512);
                            params.setMargins(10,10,10,10);
                            imageView.setLayoutParams(params);
                            if(photos_count>4&&count==4){
                                Picasso.with(ViewTaskActivity.this).load(url).transform(new SquareTransform(photos_count-4)).into(imageView);
                            }
                            else{
                                Picasso.with(ViewTaskActivity.this).load(url).transform(new SquareTransform()).into(imageView);

                            }
                        }
                        else {
                            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            imageView.setLayoutParams(params);
                            Picasso.with(ViewTaskActivity.this).load(url).into(imageView);
                        }
                        if(count<3) {
                            photoContainer.addView(imageView);
                        }else{
                            photoContainer2.addView(imageView);
                        }
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void setStatistics(){
        dbRef.child("tasks/"+KEY+"/confirmations_count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long confirmations_count = dataSnapshot.getValue(Long.class);
                TextView confirmationsCountTv = findViewById(R.id.confirm_count);
                if(confirmations_count!=null&&confirmations_count>0){
                    confirmationsCountTv.setText(confirmations_count+" confirmed");
                }
                else{
                    confirmationsCountTv.setText("");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("tasks/"+KEY+"/comments_count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long comments_count = dataSnapshot.getValue(Long.class);
                TextView confirmationsCountTv = findViewById(R.id.comments_count);
                if(comments_count!=null&&comments_count>0){
                    String suffix = comments_count == 1 ? "comment" : "comments";
                    confirmationsCountTv.setText(comments_count + " " + suffix);
                }
                else{
                    confirmationsCountTv.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void setCommentEdt(){
        final EditText commentEdt = findViewById(R.id.comment_edit_text);
        commentEdt.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        ImageButton sendBtn = findViewById(R.id.send_button);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content =  commentEdt.getText().toString();
                if(content.equals("")){
                    Toast.makeText(ViewTaskActivity.this, "Comments cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                String user_id = user.getUid();
                String task_id = KEY;
                Long time_created = System.currentTimeMillis();
                dbRef.child("comments").push().setValue(new Comment(user_id,task_id,content,time_created));
                commentEdt.setText("");
                commentEdt.clearFocus();
                hideKeyboard();
            }
        });
    }

    void setComments(){
        DatabaseReference ref = dbRef.child("tasks/"+KEY+"/comments");
        mAdapter = new FirebaseRecyclerAdapter<Boolean,CommentHolder>(
                Boolean.class,
                R.layout.comment,
                CommentHolder.class,
                ref
            ) {
                @Override
                protected void populateViewHolder(final CommentHolder holder, Boolean boo, int position) {
                    String key=mAdapter.getRef(position).getKey();
                    holder.init(key);
                }
            };


        commentsRecycler.setAdapter(mAdapter);
    }

    void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }



}