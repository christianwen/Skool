package com.example.tiena.amsconnection.viewholder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.activity.ViewTaskActivity;
import com.example.tiena.amsconnection.item.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.example.tiena.amsconnection.helperclass.CircleTransform;

/**
 * Created by tiena on 28/08/2017.
 */

public class NotiHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private TextView mNotiContent,mNotiDeadline;
    private ImageView mNotiPhoto;
    private String key;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public NotiHolder(View itemView){
        super(itemView);

        mNotiContent = itemView.findViewById(R.id.noti_content);
        mNotiPhoto = itemView.findViewById(R.id.noti_publisher_avatar);
        mNotiDeadline = itemView.findViewById(R.id.noti_deadline);
        itemView.setOnClickListener(this);
    }

    public void init(String key){
        this.key = key;

        dbRef.child("tasks/"+key+"/details/reads/"+user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null){
                    itemView.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.SpringGreen));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("tasks/"+key+"/basics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Task task = dataSnapshot.getValue(Task.class);
                //boolean isRead = dataSnapshot.child("reads/"+user.getUid()).getValue(boolean.class);

                final String content = task.content;
                //mNotiContent.setText(task.content);
                if(!task.deadline.equals("")){
                    setDeadline(task.deadline);
                }
                dbRef.child("students/"+task.user_id+"/photo_url")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Toast.makeText(getActivity(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                                Picasso.with(itemView.getContext()).load(dataSnapshot.getValue(String.class)).placeholder(R.drawable.placeholder_image).fit().transform(new CircleTransform()).into(mNotiPhoto);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                dbRef.child("students/"+dataSnapshot.child("user_id").getValue(String.class)+"/name")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String text = "<b>"+dataSnapshot.getValue(String.class)+"</b>"+" added a new task: ";
                                int max_length = content.length() > 20 ? 20 : content.length();
                                text += '"' + content.substring(0,max_length) +".."+'"';

                                mNotiContent.setText(Html.fromHtml(text));
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



    public void setDeadline(String deadline){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try{
            java.util.Date parsedDate=sdf.parse(deadline);
            String str=new SimpleDateFormat("HH",Locale.ENGLISH).format(parsedDate);
            String date=new SimpleDateFormat("EEE, MMM dd",Locale.US).format(parsedDate);
            int lesson= Integer.parseInt(str)-6;
            String dd="Deadline: "+date + " Tiết " +lesson;
            mNotiDeadline.setText(dd);
        }catch (ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(itemView.getContext(), ViewTaskActivity.class);
        intent.putExtra("task_id",key);

        if(user!=null) {
            dbRef.child("tasks/"+key+"/details/reads/"+user.getUid()).setValue(true);
        }
        itemView.getContext().startActivity(intent);
    }

}
