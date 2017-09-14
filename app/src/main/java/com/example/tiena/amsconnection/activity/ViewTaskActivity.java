package com.example.tiena.amsconnection.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.example.tiena.amsconnection.fragment.TaskViewFragment;
import com.example.tiena.amsconnection.helperclass.AnythingHelper;
import com.example.tiena.amsconnection.helperclass.CircleTransform;
import com.example.tiena.amsconnection.helperclass.SquareTransform;
import com.example.tiena.amsconnection.item.Comment;
import com.example.tiena.amsconnection.viewholder.CommentHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewTaskActivity extends Activity implements View.OnClickListener,TaskViewFragment.OnFragmentInteractionListener{

    String KEY;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    LinearLayout photoContainer,photoContainer2;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
        getFragmentManager().beginTransaction().add(R.id.fragment_container, TaskViewFragment.newInstance(KEY)).commit();
        setCommentEdt();
        setComments();
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
        DatabaseReference ref = dbRef.child("tasks/"+KEY+"/details/comments");
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

    void showKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.confirm_count){
            Intent intent = new Intent(ViewTaskActivity.this,CheckConfirmationsActivity.class);
            intent.putExtra("task_id",KEY);
            startActivity(intent);

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCommentButtonClicked() {
        Log.d("action","click on comment button");
        ((EditText)findViewById(R.id.comment_edit_text)).performClick();

    }
}