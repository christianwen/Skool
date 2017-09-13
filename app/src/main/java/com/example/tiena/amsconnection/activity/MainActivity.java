package com.example.tiena.amsconnection.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.FragmentActivity;

import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.tiena.amsconnection.fragment.DashboardFragment;
import com.example.tiena.amsconnection.fragment.NotiFragment;
import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.fragment.SearchFragment;
import com.example.tiena.amsconnection.item.User;
import com.example.tiena.amsconnection.fragment.HomeFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity {
    private RecyclerView notiList;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDb;
    private SearchFragment searchFragment=new SearchFragment();
    private NotiFragment notiFragment=new NotiFragment();
    private HomeFragment homeFragment=new HomeFragment();
    private DashboardFragment dashFragment=new DashboardFragment();

    FragmentTransaction ft=getFragmentManager().beginTransaction();
    private ImageView searchBtn;
    private ArrayList<User> users=new ArrayList<>();
    private Map<String,String> userInfo=new HashMap<String,String>();
    private FirebaseRecyclerAdapter mAdapter=null;
    private String CLASS_ID;
    View homeBtn;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    ft=getFragmentManager().beginTransaction();
                    //ft.add(R.id.content,homeFragment);
                    ft.hide(notiFragment);
                    ft.hide(dashFragment);
                    ft.show(homeFragment);

                    ft.commit();
                    return true;
                case R.id.navigation_dashboard:
                    ft=getFragmentManager().beginTransaction();

                    ft.hide(notiFragment);
                    ft.hide(homeFragment);
                    ft.show(dashFragment);

                    ft.commit();
                    return true;
                case R.id.navigation_notifications:
                    ft=getFragmentManager().beginTransaction();

                    ft.hide(dashFragment);
                    ft.hide(homeFragment);
                    ft.show(notiFragment);
                    ft.commit();
                    return true;
            }

            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //startActivity(new Intent(this,RequestInfoActivity.class));
        setContentView(R.layout.activity_main);
        ft.add(R.id.content,homeFragment).add(R.id.content,dashFragment).add(R.id.content,notiFragment).commit();



        BottomNavigationView navigation =findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(getIntent()!=null&&getIntent().getExtras()!=null){
            Bundle bundle=getIntent().getExtras();
            if(bundle.getString("fragment",null)!=null) {
                View notiButton = findViewById(R.id.navigation_notifications);
                notiButton.performClick();

            }
            else{
                homeBtn=findViewById(R.id.navigation_home);
                homeBtn.performClick();
            }
            if(bundle.getString("class_id",null)!=null){
                CLASS_ID=bundle.getString("class_id");
            }
        }



        Bundle args=new Bundle();
        args.putString("class_id",CLASS_ID);
        homeFragment.setArguments(args);
        notiFragment.setArguments(args);
        dashFragment.setArguments(args);

        searchBtn=findViewById(R.id.searchBtn);
        mDatabase=FirebaseDatabase.getInstance();
        com.github.clans.fab.FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                intent.putExtra("class_id",CLASS_ID);
                startActivity(intent);
            }
        });

        mDb=mDatabase.getReference();

        getUserInfo();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    private void initiateView(){


    }

    private void getUserInfo(){
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            //Toast.makeText(this, user.getDisplayName(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, user.getPhotoUrl().toString(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, user.getUid(), Toast.LENGTH_SHORT).show();
            final DatabaseReference ref= mDatabase.getReference("students/"+user.getUid()+"/photo_url");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!user.getPhotoUrl().toString().equals(dataSnapshot.getValue(String.class))){
                        ref.setValue(user.getPhotoUrl().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                String email = profile.getEmail();
                Uri photoUrl = profile.getPhotoUrl();
            };

        }
        else{

        }
    }

    @Override
    public void onBackPressed() {
        homeBtn.performClick();
    }
}
