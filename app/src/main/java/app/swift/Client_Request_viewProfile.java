package app.swift;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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

public class Client_Request_viewProfile extends AppCompatActivity {


    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference accountsRef,ratings_feedbacksRef;

    private TextView fullName_txt, gender_txt,address_txt, vehicle_txt,people_who_rate;
    private ImageView image_profile;
    RatingBar ratingBar;
    Button showFeedbacks_btn;
    Button hire_btn;
    Button decline_btn;

    String firstName = null;
    String lastName = null;
    String fullName;
    String gender = null;
    String address = null;
    String vehicle = null;
    String rating = null;
    String image_profile_db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__request_view_profile);

        Intent client_request = getIntent();

        final String courier_id = client_request.getStringExtra("courier_id");

        database = FirebaseDatabase.getInstance().getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        accountsRef = database.getReference("accounts");

        fullName_txt = (TextView) findViewById(R.id.riders_profile_name_txtVw);
        gender_txt = (TextView) findViewById(R.id.riders_profile_gender_txtVw);
        address_txt = (TextView) findViewById(R.id.riders_profile_address_txtVw);
        vehicle_txt = (TextView) findViewById(R.id.riders_profile_vehicle_txtVw);
        image_profile = (ImageView) findViewById(R.id.riders_profile_profile_icon);
        people_who_rate = (TextView) findViewById(R.id.riders_profile_total_rate_txtVw);
        ratingBar = (RatingBar) findViewById(R.id.riders_profile_rating_bar);
        showFeedbacks_btn = (Button) findViewById(R.id.showfeedbacks_btn);

        showFeedbacks_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Client_Request_viewProfile.this, Client_viewCouriersProfileFeedbacks.class);
                intent.putExtra("courier_id",courier_id);
                startActivity(intent);
            }
        });

        Query accounts_query = accountsRef.orderByChild("user_id").equalTo(courier_id);

        accounts_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
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
                    Picasso.with(Client_Request_viewProfile.this)
                            .load(image_profile_db)
                            .fit()
                            .transform(transformation)
                            .into(image_profile);


                    ratings_feedbacksRef = database.getReference("ratings_feedbacks");
                    Query ratingsFeedbacks_query = ratings_feedbacksRef.orderByChild("rate_by_user_id").equalTo(courier_id);

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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void backToClientRequest(View view){
        Intent intent = new Intent(this,Client_Requests.class);
        startActivity(intent);
    }
}
