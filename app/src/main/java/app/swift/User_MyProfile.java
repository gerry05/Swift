package app.swift;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class User_MyProfile extends AppCompatActivity {

    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef,ratings_feedbacksRef;
    private TextView fullName_txt, gender_txt,address_txt, people_who_rates;
    private ImageView image_profile;
    RatingBar ratingBar;
    Button showFeedbacks_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__my_profile);


        MaterialDialog.Builder builder  = new MaterialDialog.Builder(User_MyProfile.this)
                .content("Loading...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();



        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("accounts");

        fullName_txt = (TextView) findViewById(R.id.fullName_txt);
        gender_txt = (TextView) findViewById(R.id.gender_txt);
        address_txt = (TextView) findViewById(R.id.address_txt);
        ratingBar = (RatingBar) findViewById(R.id.courier_profile_rating_bar);
        people_who_rates = (TextView) findViewById(R.id.courier_profile_users_count_txtVw);
        showFeedbacks_btn = (Button) findViewById(R.id.showfeedbacks_btn);

        image_profile = (ImageView) findViewById(R.id.image_profile);


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
                String rating = null;

                String image_profile_db = null;

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    firstName = snapshot.child("first_name").getValue().toString();
                    lastName = snapshot.child("last_name").getValue().toString();
                    gender = snapshot.child("gender").getValue().toString();
                    address = snapshot.child("address").getValue().toString();
                    rating = snapshot.child("rating").getValue().toString();
                    image_profile_db = snapshot.child("image_profile").getValue().toString();

                }


                fullName = firstName + " " + lastName;


                ratingBar.setRating(Integer.parseInt(rating));
                fullName_txt.setText(fullName);
                gender_txt.setText(gender);
                address_txt.setText(address);


                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.WHITE)
                        .borderWidthDp(1)
                        .cornerRadiusDp(100)
                        .oval(false)
                        .build();
                Picasso.with(User_MyProfile.this)
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
        final Query ratingsFeedbacks_query = ratings_feedbacksRef.orderByChild("rate_by_user_id").equalTo(user.getUid());

        ratingsFeedbacks_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    people_who_rates.setText(String.valueOf(dataSnapshot.getChildrenCount()));


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        showFeedbacks_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(User_MyProfile.this,UsersFeedbacks.class);
                startActivity(intent);
            }
        });


    }

    public void goToUserEditProfile(View view) {
        Intent intent = new Intent(this, User_EditProfile.class);
        startActivity(intent);
    }
    public void backToUserNavMenu(View view) {
        Intent intent = new Intent(this, User_NavMenu.class);
        startActivity(intent);

    }

}
