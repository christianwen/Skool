package com.example.tiena.amsconnection.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.activity.AddTaskActivity;
import com.example.tiena.amsconnection.activity.MainActivity;
import com.example.tiena.amsconnection.item.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by tiena on 5/09/2017.
 */

public class TaskDeadlineDialogFragment extends DialogFragment {
    List<String> MONTHS ;



    public interface DeadlineDialogListener{
        void onClickPositiveButton(DialogFragment dialog,String timestamp);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        MONTHS = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.WheelArrayMonthAsString)));

        View deadlinePicker=getActivity().getLayoutInflater().inflate(R.layout.wheel_deadline_picker,null);
        String CURRENT_MONTH=new SimpleDateFormat("MM").format(new Date());
        String CURRENT_DAY=new SimpleDateFormat("dd").format(new Date());

        final WheelPicker deadlinePickerMonth=deadlinePicker.findViewById(R.id.deadline_picker_month);
        deadlinePickerMonth.setData(MONTHS);
        deadlinePickerMonth.setSelectedItemPosition(3);
        final WheelPicker deadlinePickerDay=deadlinePicker.findViewById(R.id.deadline_picker_day);
        deadlinePickerDay.setSelectedItemPosition(Integer.parseInt(CURRENT_DAY)-1);
        final WheelPicker deadlinePickerLesson=deadlinePicker.findViewById(R.id.deadline_picker_lesson);
        deadlinePickerLesson.setSelectedItemPosition(3);
        Log.d("pos",deadlinePickerMonth.getSelectedItemPosition()+"");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(deadlinePicker)
                .setTitle("Deadline")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int month_pos=deadlinePickerMonth.getCurrentItemPosition();
                        int day_pos=deadlinePickerDay.getCurrentItemPosition();
                        int lesson_pos=deadlinePickerLesson.getCurrentItemPosition();

                        String CURRENT_YEAR=new SimpleDateFormat("yyyy",Locale.ENGLISH).format(new Date());
                        String parseString=(month_pos+1)+","+(day_pos+1)+","+CURRENT_YEAR+","+(lesson_pos+7);
                        SimpleDateFormat sdf=new SimpleDateFormat("MM,dd,yyyy,HH", Locale.US);
                        try{
                            Date parsedDate=sdf.parse(parseString);
                            java.sql.Timestamp timestamp=new java.sql.Timestamp(parsedDate.getTime());
                            Log.d("timestamp",timestamp.toString());
                            mListener.onClickPositiveButton(TaskDeadlineDialogFragment.this,timestamp.toString());

                            Toast.makeText(getActivity(), "Set deadline successfully", Toast.LENGTH_SHORT).show();
                        } catch (ParseException e){
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton(R.string.task_deadline_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return builder.create();
    }

    DeadlineDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DeadlineDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
