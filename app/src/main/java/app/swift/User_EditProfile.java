package app.swift;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;



public class User_EditProfile extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference storageReference;

    private Uri getImageUri = null;

    private static final int GALLERY_REQUEST = 1;

    EditText fname,lname,address;
    RadioButton male,female;
    RadioGroup gender_rGroup;
    private Button editProfile_done_btn;
    private ImageButton setupImage_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__edit_profile);


        MaterialDialog.Builder builder  = new MaterialDialog.Builder(User_EditProfile.this)
                .content("Loading...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();

        fname = (EditText) findViewById(R.id.user_profile_firstname_eTxt);
        lname = (EditText) findViewById(R.id.user_profile_lastname_eTxt);
        address = (EditText) findViewById(R.id.user_profile_address_eTxt);
        male = (RadioButton) findViewById(R.id.user_profile_male_Rbtn);
        female = (RadioButton) findViewById(R.id.user_profile_female_Rbtn);

        gender_rGroup = (RadioGroup) findViewById(R.id.gender_rGroup);
        setupImage_btn = (ImageButton) findViewById(R.id.user_profile_insert_pic_btn);
        editProfile_done_btn = (Button) findViewById(R.id.user_profile_done_btn);

        storageReference = FirebaseStorage.getInstance().getReference();

        database = FirebaseDatabase.getInstance().getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = database.getReference("accounts");
        Query query = myRef.orderByChild("user_id").equalTo(user.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss();
                String firstName_db = null;
                String lastName_db = null;
                String address_db = null;
                String gender_db = null;

                String image_url_db = null;

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    firstName_db = snapshot.child("first_name").getValue().toString();
                    lastName_db = snapshot.child("last_name").getValue().toString();
                    address_db = snapshot.child("address").getValue().toString();
                    gender_db = snapshot.child("gender").getValue().toString();

                    image_url_db = snapshot.child("image_profile").getValue().toString();
                }

                if(gender_db.equals("Male")){
                    gender_rGroup.check(R.id.user_profile_male_Rbtn);
                    System.out.println("male");
                }else if(gender_db.equals("Female")){
                    gender_rGroup.check(R.id.user_profile_female_Rbtn);
                    System.out.println("female");
                }
                fname.setText(firstName_db);
                lname.setText(lastName_db);
                address.setText(address_db);


                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.WHITE)
                        .borderWidthDp(1)
                        .cornerRadiusDp(100)
                        .oval(false)
                        .build();
                Picasso.with(User_EditProfile.this)
                        .load(image_url_db)
                        .fit()
                        .transform(transformation)
                        .into(setupImage_btn);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setupImage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        editProfile_done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doUploadImage();
            }
        });
    }

    private void doUploadImage(){
        MaterialDialog.Builder builder  = new MaterialDialog.Builder(User_EditProfile.this)
                .content("Loading...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();


        myRef = database.getReference("accounts").child(user.getPhoneNumber().toString().replaceAll("\\W",""));
        if(getImageUri == null){

            if(TextUtils.isEmpty(fname.getText().toString()) || TextUtils.isEmpty(lname.getText().toString())
                    || TextUtils.isEmpty(address.getText().toString())){

                dialog.dismiss();

                new SweetAlertDialog(User_EditProfile.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Please fill in all fields")
                        .show();
            }else {

                myRef.child("first_name").setValue(fname.getText().toString().substring(0,1).toUpperCase() + fname.getText().toString().substring(1).toLowerCase());
                myRef.child("last_name").setValue(lname.getText().toString().substring(0,1).toUpperCase() + lname.getText().toString().substring(1).toLowerCase());
                myRef.child("address").setValue(address.getText().toString().substring(0,1).toUpperCase() + address.getText().toString().substring(1).toLowerCase());

                if(male.isChecked()){
                    myRef.child("gender").setValue("Male");
                }else if(female.isChecked()){
                    myRef.child("gender").setValue("Female");
                }

                dialog.dismiss();
                new SweetAlertDialog(User_EditProfile.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Update Successful")

                        .show();
            }
        }
        if(getImageUri != null ){
            StorageReference filepath = storageReference.child("Photos/ProfilePictures").child(getImageUri.getLastPathSegment());

            filepath.putFile(getImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    if(TextUtils.isEmpty(fname.getText().toString()) || TextUtils.isEmpty(lname.getText().toString())
                            || TextUtils.isEmpty(address.getText().toString())){
                        dialog.dismiss();

                        new SweetAlertDialog(User_EditProfile.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Please fill in all fields")
                                .show();

                    }else {
                        myRef.child("first_name").setValue(fname.getText().toString().substring(0,1).toUpperCase() + fname.getText().toString().substring(1).toLowerCase());
                        myRef.child("last_name").setValue(lname.getText().toString().substring(0,1).toUpperCase() + lname.getText().toString().substring(1).toLowerCase());
                        myRef.child("address").setValue(address.getText().toString().substring(0,1).toUpperCase() + address.getText().toString().substring(1).toLowerCase());

                        myRef.child("image_profile").setValue(downloadUri.toString());

                        if(male.isChecked()){
                            myRef.child("gender").setValue("Male");
                        }else if(female.isChecked()){
                            myRef.child("gender").setValue("Female");
                        }

                        dialog.dismiss();

                        new SweetAlertDialog(User_EditProfile.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Update Successful")
                                .show();
                    }


                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                getImageUri = result.getUri();
                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.WHITE)
                        .borderWidthDp(1)
                        .cornerRadiusDp(100)
                        .oval(false)
                        .build();

                Picasso.with(User_EditProfile.this)
                        .load(getImageUri)
                        .fit()
                        .transform(transformation)
                        .into(setupImage_btn);


                System.out.println("ASDASDSAD" + getImageUri);

            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }

        }

    }

    public void backToUserMyProfile(View view) {
        Intent intent = new Intent(this, User_MyProfile.class);
        startActivity(intent);
    }
}
