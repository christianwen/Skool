package com.example.tiena.amsconnection.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.activity.CheckConfirmationsActivity;
import com.example.tiena.amsconnection.activity.ViewTaskActivity;
import com.example.tiena.amsconnection.helperclass.AnythingHelper;
import com.example.tiena.amsconnection.helperclass.CircleTransform;
import com.example.tiena.amsconnection.helperclass.SquareTransform;
import com.example.tiena.amsconnection.item.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaskViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskViewFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    View layout;

    int[] colors = {R.color.ConfirmActive,R.color.ActionInactive,R.color.CommentActive,R.color.ActionInactive};
    // TODO: Rename and change types of parameters
   private String TASK_ID;

    LinearLayout photoContainer,photoContainer2;

    private OnFragmentInteractionListener mListener;

    public TaskViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment TaskViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskViewFragment newInstance(String task_id) {
        TaskViewFragment fragment = new TaskViewFragment();
        Bundle args = new Bundle();
        args.putString("task_id",task_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            TASK_ID = getArguments().getString("task_id",null);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_task_view, container, false);
        photoContainer = layout.findViewById(R.id.photo_container);
        photoContainer2 = layout.findViewById(R.id.photo_container_2);
        setTaskLayout();
        setStatistics();
        setConfirmBtn();
        return layout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onCommentButtonClicked(String task_id);
    }

    boolean confirmed = false;

    void setConfirmBtn(){
        final Button confirmBtn = layout.findViewById(R.id.confirm_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmed=!confirmed;
                toggleConfirm(confirmed);
                String user_id = user.getUid();

                if(confirmed){
                    dbRef.child("tasks/"+TASK_ID+"/details/confirmations/"+user_id).setValue(true);
                }
                else{
                    dbRef.child("tasks/"+TASK_ID+"/details/confirmations/"+user_id).setValue(null);
                }

            }
        });
    }

    void toggleConfirm(boolean confirmed){
        final Button confirmBtn = layout.findViewById(R.id.confirm_button);

        int ref = confirmed ? R.color.ConfirmActive : R.color.ActionInactive;
        confirmBtn.getCompoundDrawables()[0].setColorFilter(getResources().getColor(ref), PorterDuff.Mode.SRC_ATOP);
        confirmBtn.setTextColor(getResources().getColor(ref));

    }


    void setTaskPublisherInfo(String user_id){


        dbRef.child("students/"+user_id+"/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView) layout.findViewById(R.id.user_name)).setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("students/"+user_id+"/photo_url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageView imageView = layout.findViewById(R.id.user_avatar);
                Picasso.with(getActivity()).load(dataSnapshot.getValue(String.class)).fit().transform(new CircleTransform()).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void setTaskLayout(){
        layout.findViewById(R.id.comment_button).setOnClickListener(this);
        dbRef.child("tasks/"+TASK_ID+"/details/confirmations/"+user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                confirmed = dataSnapshot.getValue()!=null;
                toggleConfirm(confirmed);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("tasks/"+TASK_ID+"/basics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Task task = dataSnapshot.getValue(Task.class);
                setTaskPublisherInfo(task.user_id);
                Long time_created = task.time_created;
                if(time_created!=null){
                    String time = AnythingHelper.convertTimestampToDate(time_created);
                    ((TextView) layout.findViewById(R.id.task_time_created)).setText(time);
                }
                if(dataSnapshot.child("confirmations/"+user.getUid()).getValue()!=null){
                    confirmed=true;
                    toggleConfirm(true);
                }
                ((TextView) layout.findViewById(R.id.task_content)).setText(dataSnapshot.child("content").getValue(String.class));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("tasks/"+TASK_ID+"/details/photo_urls").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    List<String> urls = new ArrayList<>();
                    for (DataSnapshot photoUrl : dataSnapshot.getChildren()) {
                        urls.add(photoUrl.getValue(String.class));
                    }

                    if(urls.size()==1){
                        ImageView imageView = new ImageView(getActivity());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        imageView.setLayoutParams(params);
                        Picasso.with(getActivity()).load(urls.get(0)).placeholder(R.drawable.placeholder_image).fit().into(imageView);
                    }
                    else {

                        int max_image_num = urls.size() < 4 ? urls.size() : 4;
                        for (int i = 0; i < max_image_num; i++) {
                            ImageView imageView = new ImageView(getActivity());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(512, 512);
                            params.setMargins(10, 10, 10, 10);
                            imageView.setLayoutParams(params);

                            SquareTransform squareTransform = i==max_image_num-1 ? new SquareTransform(urls.size() - 4) : new SquareTransform();
                            Picasso.with(getActivity()).load(urls.get(i)).placeholder(R.drawable.placeholder_image).transform(squareTransform).into(imageView);

                            if (i < 2) {
                                photoContainer.addView(imageView);
                            } else {
                                photoContainer2.addView(imageView);
                            }
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
        dbRef.child("tasks/"+TASK_ID+"/statistics/confirmations_count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long confirmations_count = dataSnapshot.getValue(Long.class);
                TextView confirmationsCountTv = layout.findViewById(R.id.confirm_count);
                if(confirmations_count!=null&&confirmations_count>0){
                    confirmationsCountTv.setText(confirmations_count+" confirmed");
                }
                else{
                    confirmationsCountTv.setText("");
                }
                confirmationsCountTv.setOnClickListener(TaskViewFragment.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("tasks/"+TASK_ID+"/statistics/comments_count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long comments_count = dataSnapshot.getValue(Long.class);
                TextView confirmationsCountTv = layout.findViewById(R.id.comments_count);
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

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.confirm_count){
            Intent intent = new Intent(getActivity(),CheckConfirmationsActivity.class);
            intent.putExtra("task_id",TASK_ID);
            startActivity(intent);

        }

        if(view.getId()==R.id.comment_button){
            mListener.onCommentButtonClicked(TASK_ID);
        }
    }
}
