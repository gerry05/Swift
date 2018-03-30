package app.swift;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;



public class User_Become_a_Driver extends AppCompatActivity {


    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef,accountsRef;
    private StorageReference storageReference;

    private Uri getImageUri = null;

    private static final int GALLERY_REQUEST = 1;

    private ImageButton upload_image_btn;
    RadioGroup vehicle_group;
    RadioButton vehicle_bicycle,vehicle_motorcycle;
    ConstraintLayout user_becomes_driver_form1;
    RelativeLayout user_becomes_driver_form2;
    Button submit_application;
    ImageView vehicle_icon;
    TextView instruction,vehicle_tobe_used;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__become_a__driver);


        MaterialDialog.Builder builder  = new MaterialDialog.Builder(User_Become_a_Driver.this)
                .content("Loading...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();

        upload_image_btn = (ImageButton) findViewById(R.id.upload_image);
        vehicle_group = (RadioGroup) findViewById(R.id.courier_vehicle_rGroup);
        vehicle_bicycle = (RadioButton) findViewById(R.id.courier_bicycle_rBtn);
        vehicle_motorcycle = (RadioButton) findViewById(R.id.courier_motorcycle_rBtn);
        submit_application = (Button) findViewById(R.id.user_becomes_driver_submit_btn);
        instruction = (TextView) findViewById(R.id.be_a_rider_instruction_txtVw);
        vehicle_tobe_used = (TextView) findViewById(R.id.be_a_rider_vehicle_label);
        vehicle_icon = (ImageView) findViewById(R.id.be_a_rider_vehicle_icon);

        user_becomes_driver_form2 = (RelativeLayout) findViewById(R.id.user_becomes_driver_form2);

        storageReference = FirebaseStorage.getInstance().getReference();

        database = FirebaseDatabase.getInstance().getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        myRef = database.getReference("applicants");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss();
                if(dataSnapshot.hasChild(user.getUid())){

                    instruction.setVisibility(View.GONE);
                    upload_image_btn.setVisibility(View.GONE);
                    vehicle_group.setVisibility(View.GONE);
                    vehicle_bicycle.setVisibility(View.GONE);
                    vehicle_motorcycle.setVisibility(View.GONE);
                    submit_application.setVisibility(View.GONE);
                    vehicle_tobe_used.setVisibility(View.GONE);
                    vehicle_icon.setVisibility(View.GONE);

                    user_becomes_driver_form2.setVisibility(View.VISIBLE);
                }else if(!dataSnapshot.hasChild(user.getUid())){

                    instruction.setVisibility(View.VISIBLE);
                    upload_image_btn.setVisibility(View.VISIBLE);
                    vehicle_group.setVisibility(View.VISIBLE);
                    vehicle_bicycle.setVisibility(View.VISIBLE);
                    vehicle_motorcycle.setVisibility(View.VISIBLE);
                    submit_application.setVisibility(View.VISIBLE);
                    vehicle_tobe_used.setVisibility(View.VISIBLE);
                    vehicle_icon.setVisibility(View.VISIBLE);
                    user_becomes_driver_form2.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        upload_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        submit_application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doUploadImage();
            }
        });

    }
    private void doUploadImage(){

        MaterialDialog.Builder builder  = new MaterialDialog.Builder(User_Become_a_Driver.this)
                .content("Please wait...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();

        myRef = database.getReference("applicants").child(user.getUid().toString());

        if(getImageUri == null){
            dialog.dismiss();

            new SweetAlertDialog(User_Become_a_Driver.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Please select a Photo")
                    .show();
        }

        if(getImageUri != null ){
            StorageReference filepath = storageReference.child("Photos/DriversLicense").child(getImageUri.getLastPathSegment());

            filepath.putFile(getImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    myRef.child("drivers_license_picture").setValue(downloadUri.toString());
                    myRef.child("user_id").setValue(user.getUid().toString());
                    myRef.child("status").setValue("pending");
                    myRef.child("created_at").setValue(ServerValue.TIMESTAMP);
                    myRef.child("applicant_id").setValue(myRef.getKey());
                    if(vehicle_bicycle.isChecked()){
                        myRef.child("vehicle").setValue("Bicycle");


                    }else if(vehicle_motorcycle.isChecked()){
                        myRef.child("vehicle").setValue("Motorcycle");


                    }
                    dialog.dismiss();
                    new SweetAlertDialog(User_Become_a_Driver.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Application Submitted!")
                            .show();
                }
            });
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.OFF)


                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                getImageUri = result.getUri();

                upload_image_btn.setImageURI(getImageUri);

                System.out.println("ASDASDSAD" + getImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }

    public void open_Courier_NavMenu(View view){
        Intent intent = new Intent(this,User_NavMenu.class);
        startActivity(intent);

    }
}
