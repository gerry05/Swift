package app.swift;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

public class User_NavMenu extends AppCompatActivity {

    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef,ratings_feedbacksRef,accountsRef;
    private TextView fullName_txt, userType_txt;
    private ImageView image_profile;
    List<String> ratings_list = new ArrayList<String>(); // Result will be holded Here
    String rating_db = null;

    String firstName = null;
    String lastName = null;
    String fullName;
    String userType = null;

    String image_profile_db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__nav_menu);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = database.getReference("accounts");

        getRatingsFeedbacks();

        fullName_txt = (TextView) findViewById(R.id.nav_drawer_user_name_txtVw);
       userType_txt = (TextView) findViewById(R.id.nav_drawer_user_userType_txtVw);

        image_profile = (ImageView) findViewById(R.id.nav_drawer_user_profilePicture_img);


        Query query = myRef.orderByChild("user_id").equalTo(user.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    if(snapshot.child("first_name").exists() && snapshot.child("last_name").exists() &&
                            snapshot.child("user_type").exists() && snapshot.child("image_profile").exists()){

                        firstName = snapshot.child("first_name").getValue().toString();
                        lastName = snapshot.child("last_name").getValue().toString();
                        userType = snapshot.child("user_type").getValue().toString();
                        image_profile_db = snapshot.child("image_profile").getValue().toString();
                    }



                }

                fullName = firstName + " " + lastName;
                fullName_txt.setText(fullName);
                 userType_txt.setText(userType);
                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.WHITE)
                        .borderWidthDp(1)
                        .cornerRadiusDp(50)
                        .oval(false)
                        .build();
                Picasso.with(User_NavMenu.this)
                        .load(image_profile_db)
                        .fit()
                        .transform(transformation)
                        .into(image_profile);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getRatingsFeedbacks(){
        ratings_feedbacksRef = database.getReference("ratings_feedbacks");
        Query ratingsfeedbacks_query = ratings_feedbacksRef.orderByChild("rate_by_user_id").equalTo(user.getUid());
        ratingsfeedbacks_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int rate_count;
                    int total_rate = 0;
                    int rate_sum = 0;
                    int rate = 0;
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        rating_db = snapshot.child("rating").getValue().toString();
                        ratings_list.add(String.valueOf(rating_db));
                    }
                    for(String data: ratings_list){
                        rate_sum += Integer.parseInt(data);
                    }
                    System.out.println(rate_sum);
                    total_rate = rate_sum / ratings_list.size();
                    System.out.println("Total rate: " + total_rate);
                    accountsRef = database.getReference("accounts").child(user.getPhoneNumber().replaceAll("\\W",""));
                    accountsRef.child("rating").setValue(total_rate);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void goToUserMyProfile(View view){
        Intent intent = new Intent(this,User_MyProfile.class);
        startActivity(intent);
    }

    public void goToTransactionHistory(View view){
        Intent intent = new Intent(this,Client_TransactionHistory.class);
        startActivity(intent);
    }
    public void backToUserHome(View view){
        Intent intent = new Intent(this,Client_Home.class);
        startActivity(intent);
    }
    public void goToUserBe_a_Rider(View view){
        Intent intent = new Intent(this,User_Become_a_Driver.class);
        startActivity(intent);
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        Intent main = new Intent(User_NavMenu.this,SignInorSignUp.class);
        startActivity(main);
    }
}
