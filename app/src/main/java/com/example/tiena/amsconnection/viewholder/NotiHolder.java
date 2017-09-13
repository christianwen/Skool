package com.example.tiena.amsconnection.viewholder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.activity.ViewTaskActivity;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.example.tiena.amsconnection.helperclass.CircleTransform;

/**
 * Created by tiena on 28/08/2017.
 */

public class NotiHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private TextView mNotiTitle,mNotiContent,mNotiPublisherName,mNotiDeadline;
    private ImageView mNotiPhoto;
    private String key;


    public NotiHolder(View itemView){
        super(itemView);
        mNotiTitle = itemView.findViewById(R.id.noti_title);
        mNotiContent = itemView.findViewById(R.id.noti_content);
        mNotiPhoto = itemView.findViewById(R.id.noti_publisher_avatar);
        mNotiPublisherName = itemView.findViewById(R.id.noti_publisher_name);
        mNotiDeadline = itemView.findViewById(R.id.noti_deadline);
        itemView.setOnClickListener(this);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setTitle(String noti_title){
        mNotiTitle.setText(noti_title);
    }

    public void setContent(String noti_content){
        mNotiContent.setText(noti_content);
    }

    public void setImage(String image_url){
        Picasso.with(itemView.getContext()).load(image_url).transform(new CircleTransform()).into(mNotiPhoto);
    }

    public void setPublisherName(String publisherName){
        String str="- by "+publisherName;
        mNotiPublisherName.setText(str);
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
        intent.putExtra("key",key);
        itemView.getContext().startActivity(intent);
    }

}
