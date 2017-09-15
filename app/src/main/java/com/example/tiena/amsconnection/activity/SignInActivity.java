package com.example.tiena.amsconnection.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tiena.amsconnection.BuildConfig;
import com.example.tiena.amsconnection.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {
    private static final int AUTH_REQUEST_CODE=69;
    private LinearLayout parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        parentLayout=findViewById(R.id.parent_layout);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseAuth auth=FirebaseAuth.getInstance();


        if(auth.getCurrentUser()!=null){
            checkClass();
        }
        else{
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .build()

                    ,AUTH_REQUEST_CODE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.


        if (requestCode == AUTH_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {

                IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
                checkClass();

                finish();

            } else {
                // Sign in failed

                if (response == null) {
                    // User pressed back button
                    showSnackbar(getResources().getString(R.string.no_internet_connection));

                    //showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(getResources().getString(R.string.no_internet_connection));
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(getResources().getString(R.string.unknown_error));

                }
            }

            //showSnackbar(R.string.unknown_sign_in_response);
        }
    }

    private void showSnackbar(String message){
        Snackbar.make(parentLayout,message, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();
    }

    private void checkClass(){
        FirebaseDatabase.getInstance().getReference("students/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/class_id")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Toast.makeText(SignInActivity.this, dataSnapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
                        if(dataSnapshot.getValue()==null){
                            Toast.makeText(SignInActivity.this, "didnt have class id", Toast.LENGTH_SHORT).show();

                            Log.d("class_id","none");
                            startActivity(new Intent(SignInActivity.this,RequestInfoActivity.class));
                        }
                        else{
                            //startActivity(new Intent(SignInActivity.this,RequestInfoActivity.class));
                            Toast.makeText(SignInActivity.this, "already had class id", Toast.LENGTH_SHORT).show();
                            Log.d("class_id","already has");
                            Intent intent=new Intent(SignInActivity.this,MainActivity.class);
                            intent.putExtra("class_id",dataSnapshot.getValue(String.class));
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
