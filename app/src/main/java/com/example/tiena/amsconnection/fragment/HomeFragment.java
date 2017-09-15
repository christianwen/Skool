package com.example.tiena.amsconnection.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.activity.SignInActivity;
import com.example.tiena.amsconnection.activity.TeacherProfileActivity;
import com.example.tiena.amsconnection.activity.TimetableActivity;
import com.example.tiena.amsconnection.activity.ViewTaskActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.tiena.amsconnection.helperclass.CircleTransform;

/**
 * Created by tiena on 22/08/2017.
 */

public  class HomeFragment extends Fragment{
    FirebaseDatabase mDb= FirebaseDatabase.getInstance();
    FirebaseUser user;
    LinearLayout fragmentContainer;
    String class_id,user_id;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        final View homeFragmentLayout=inflater.inflate(R.layout.home_fragment, container, false);
        fragmentContainer = homeFragmentLayout.findViewById(R.id.fragment_container);
        ImageView userAvatar=homeFragmentLayout.findViewById(R.id.user_avatar);
        TextView userName=homeFragmentLayout.findViewById(R.id.user_name);

        class_id=getArguments().getString("class_id",null);
        Log.d("class_id",class_id);

        user= FirebaseAuth.getInstance().getCurrentUser();
        user_id=user.getUid();


        mDb.getReference("classes/"+class_id+"/tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<String> task_ids = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    task_ids.add(snapshot.getKey());
                }
                for(int i=task_ids.size()-1;i>=0;i--){
                    TaskViewFragment fragment = TaskViewFragment.newInstance(task_ids.get(i));
                    FrameLayout container = new FrameLayout(HomeFragment.this.getActivity());
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,50,0,0);
                    container.setLayoutParams(params);
                    int id = View.generateViewId();
                    container.setId(id);
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.add(id,fragment).commit();
                    fragmentContainer.addView(container);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return homeFragmentLayout;


    }


}

/*Uri photoUri=user.getPhotoUrl();
        Log.d("photo_url",photoUri.toString());
        Picasso.with(getActivity().getApplicationContext()).load(photoUri).transform(new CircleTransform()).into(userAvatar);
        userName.setText(user.getDisplayName());*/