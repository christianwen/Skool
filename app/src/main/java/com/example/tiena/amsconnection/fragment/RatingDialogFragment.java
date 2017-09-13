package com.example.tiena.amsconnection.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.item.Rating;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.techery.properratingbar.ProperRatingBar;

/**
 * Created by tiena on 6/09/2017.
 */

public class RatingDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        final int RATING_SCORE=getArguments().getInt("rating_score");
        final String TEACHER_ID = getArguments().getString("teacher_id");
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)return null;
        final String USER_ID= user.getUid();

        View ratingLayout=getActivity().getLayoutInflater().inflate(R.layout.rating_dialog,null);
        ProperRatingBar ratingBar = ratingLayout.findViewById(R.id.rating_score);
        ratingBar.setRating(RATING_SCORE);
        final EditText ratingComment=ratingLayout.findViewById(R.id.rating_comment);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final DatabaseReference ref= FirebaseDatabase.getInstance().getReference("ratings");
        builder.setView(ratingLayout)
                .setTitle("Rating")
                .setPositiveButton("Public", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String comment = ratingComment.getText().toString();
                        ref.push().setValue(new Rating(RATING_SCORE,comment,USER_ID,TEACHER_ID,true));
                        Toast.makeText(getActivity(), "Successfully rate as public.", Toast.LENGTH_SHORT).show();
                    }
                })

                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Anonym.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String comment = ratingComment.getText().toString();
                        ref.push().setValue(new Rating(RATING_SCORE,comment,USER_ID,TEACHER_ID,false));
                        Toast.makeText(getActivity(), "Successfully rate as anonymous.", Toast.LENGTH_SHORT).show();

                    }
                });
        return builder.create();
    }
}
