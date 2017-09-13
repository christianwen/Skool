package com.example.tiena.amsconnection.helperclass;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by tiena on 12/09/2017.
 */

public class FirebaseHelper {
    void getUserPhotoUrl(String user_id){
        FirebaseDatabase.getInstance().getReference("students/"+user_id+"/photo_url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photo_url = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
