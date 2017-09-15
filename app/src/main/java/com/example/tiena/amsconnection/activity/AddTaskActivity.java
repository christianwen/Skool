package com.example.tiena.amsconnection.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.fragment.TaskDeadlineDialogFragment;
import com.example.tiena.amsconnection.helperclass.BitmapHelper;
import com.example.tiena.amsconnection.helperclass.CircleTransform;
import com.example.tiena.amsconnection.item.Task;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AddTaskActivity extends Activity implements TaskDeadlineDialogFragment.DeadlineDialogListener{
    String CLASS_ID;
    final int PICK_PHOTO_FOR_AVATAR=999;
    final int REQUEST_READ_EXTERNAL_STORAGE=888;
    final int BITMAP_MAX_SIZE=256;

    String deadline="";
    FirebaseUser user;
    List<Bitmap> bitmaps = new ArrayList<>();
    LinearLayout photoContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_task);

        CLASS_ID=getIntent().getExtras().getString("class_id");
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)return;
        Picasso.with(this).load(user.getPhotoUrl()).transform(new CircleTransform()).into((ImageView) findViewById(R.id.user_avatar));
        ((TextView) findViewById(R.id.user_name)).setText(user.getDisplayName());
        photoContainer = findViewById(R.id.photo_demo_container);
        setAttachPhotoBtn();
        setDeadlineBtn();
        setAddTaskBtn();

    }

    void setAttachPhotoBtn(){
        Button attachPhotoBtn = findViewById(R.id.attach_photo_button);
        attachPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddTaskActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddTaskActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    } else {
                        ActivityCompat.requestPermissions(AddTaskActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_READ_EXTERNAL_STORAGE);
                    }
                }
                else{
                    pickImage();
                }

            }
        });

        ((findViewById(R.id.add_photo_button))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
    }

    void setDeadlineBtn(){
        Button setDeadlineBtn = findViewById(R.id.set_deadline_button);
        setDeadlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskDeadlineDialogFragment dialog=new TaskDeadlineDialogFragment();
                dialog.show(getFragmentManager(),"task_deadline");
            }
        });
    }

    void setAddTaskBtn(){
        Button addTaskBtn = findViewById(R.id.add_task_button);
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = ((EditText) findViewById(R.id.task_content)).getText().toString();
                String user_id = user.getUid();
                String class_id = CLASS_ID;
                String deadline = AddTaskActivity.this.deadline;

                final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("tasks");
                final String key=dbRef.push().getKey();
                dbRef.child(key+"/basics").setValue(new Task(user_id,class_id,content,deadline,System.currentTimeMillis()));
                //dbRef.child(key+"/photos_count").setValue(bitmaps.size());

                if(bitmaps!=null) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference("tasks/" + key);
                    int count=0;
                    for(Bitmap bitmap : bitmaps){
                        count++;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = storageRef.child(count+"").putBytes(data);
                        final int finalCount = count;
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddTaskActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                dbRef.child(key+"/details/photo_urls").push().setValue(downloadUrl.toString());
                                Log.d("photo_url",downloadUrl.toString());
                                if(finalCount ==bitmaps.size()){
                                    Toast.makeText(AddTaskActivity.this, "Add task successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddTaskActivity.this,ViewTaskActivity.class);
                                    intent.putExtra("task_id",key);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                }

            }
        });


    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                Log.d("err","error when uploading image");
                return;
            }
            try {
                //InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapHelper.compressImage(data.getData().toString(),AddTaskActivity.this);
                bitmaps.add(bitmap);
                int bitmapHeight = bitmap.getHeight();
                int bitmapWidth = bitmap.getWidth();
                Bitmap displayBitmap=null;

                if(bitmapHeight>bitmapWidth) {
                    Bitmap squareBitmap = Bitmap.createBitmap(bitmap, 0, (bitmapHeight-bitmapWidth)/2,bitmapWidth,bitmapWidth);
                    displayBitmap = Bitmap.createScaledBitmap(squareBitmap, BITMAP_MAX_SIZE, BITMAP_MAX_SIZE, true);
                    squareBitmap.recycle();
                }
                else{
                    int nh = (int) (bitmapWidth * (((double) BITMAP_MAX_SIZE) / bitmapHeight));
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, nh, BITMAP_MAX_SIZE, true);
                    displayBitmap = Bitmap.createBitmap(scaledBitmap,(nh-BITMAP_MAX_SIZE)/2,0,BITMAP_MAX_SIZE,BITMAP_MAX_SIZE);
                    scaledBitmap.recycle();

                }

                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMarginEnd(10);
                imageView.setLayoutParams(params);

                imageView.setImageBitmap(displayBitmap);

                photoContainer.addView(imageView,0);
                if(bitmaps.size()>3){
                    photoContainer.getChildAt(3).setVisibility(View.GONE);
                    ImageView image = (ImageView) photoContainer.getChildAt(2);
                    Bitmap viewBitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    Bitmap alteredBitmap = BitmapHelper.drawTextAndTint(viewBitmap,"+"+(bitmaps.size()-3));

                    image.setImageBitmap(alteredBitmap);

                }

                (findViewById(R.id.add_photo_button)).setVisibility(View.VISIBLE);

            }catch (Exception e){
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    @Override
    public void onClickPositiveButton(DialogFragment dialog, String timestamp) {
        deadline = timestamp;
        ((TextView) findViewById(R.id.task_deadline)).setText(deadline);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                }
                return;
            }

        }
    }


}
