package app.swift;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class Courier_MyProfile extends AppCompatActivity {

    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef,ratings_feedbacksRef;

    private TextView fullName_txt, gender_txt,address_txt, vehicle_txt,people_who_rate;
    private ImageView image_profile;
    RatingBar ratingBar;
    Button showFeedbacks_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier__my_profile);


        MaterialDialog.Builder builder  = new MaterialDialog.Builder(Courier_MyProfile.this)
                .content("Loading your profile...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();

        database = FirebaseDatabase.getInstance().getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = database.getReference("accounts");

        fullName_txt = (TextView) findViewById(R.id.courier_fullName_txt);
        gender_txt = (TextView) findViewById(R.id.courier_gender_txt);
        address_txt = (TextView) findViewById(R.id.courier_address_txt);
        vehicle_txt = (TextView) findViewById(R.id.courier_vehicle_txt);
        image_profile = (ImageView) findViewById(R.id.courier_profile);
        people_who_rate = (TextView) findViewById(R.id.courier_profile_users_count_txtVw);
        ratingBar = (RatingBar) findViewById(R.id.courier_profile_rating_bar);
        showFeedbacks_btn = (Button) findViewById(R.id.showfeedbacks_btn);


        showFeedbacks_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Courier_MyProfile.this, Courier_Feedbacks.class);
                startActivity(intent);
            }
        });

        Query query = myRef.orderByChild("user_id").equalTo(user.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss();
                String firstName = null;
                String lastName = null;
                String fullName;
                String gender = null;
                String address = null;
                String vehicle = null;
                String rating = null;
                String image_profile_db = null;

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    firstName = snapshot.child("first_name").getValue().toString();
                    lastName = snapshot.child("last_name").getValue().toString();
                    gender = snapshot.child("gender").getValue().toString();
                    address = snapshot.child("address").getValue().toString();
                    rating = snapshot.child("rating").getValue().toString();
                    vehicle = snapshot.child("vehicle").getValue().toString();
                    image_profile_db = snapshot.child("image_profile").getValue().toString();

                }


                fullName = firstName + " " + lastName;


                fullName_txt.setText(fullName);
                gender_txt.setText(gender);
                address_txt.setText(address);
                ratingBar.setRating(Integer.parseInt(rating));
                vehicle_txt.setText(vehicle);



                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.WHITE)
                        .borderWidthDp(1)
                        .cornerRadiusDp(100)
                        .oval(false)
                        .build();
                Picasso.with(Courier_MyProfile.this)
                        .load(image_profile_db)
                        .fit()
                        .transform(transformation)
                        .into(image_profile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ratings_feedbacksRef = database.getReference("ratings_feedbacks");
        Query ratingsFeedbacks_query = ratings_feedbacksRef.orderByChild("rate_by_user_id").equalTo(user.getUid());

        ratingsFeedbacks_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    people_who_rate.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void goToCourierEditProfile(View view){
        Intent intent = new Intent(this,Courier_EditProfile.class);
        startActivity(intent);
    }
    public void backToCourierNavMenu(View view){
        Intent intent = new Intent(this,Courier_NavMenu.class);
        startActivity(intent);
    }
}
