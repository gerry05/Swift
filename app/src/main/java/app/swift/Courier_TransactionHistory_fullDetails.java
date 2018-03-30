package app.swift;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Courier_TransactionHistory_fullDetails extends AppCompatActivity {


    private FirebaseUser user;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private DatabaseReference postsRef, requestRef,accountRef,ignoreRef,transactionRef;

    String image_profile_db,firstName_db,lastName_db,user_type_db,client_id_db,post_id_db,vehicle_db,pickup_location_db,dropoff_location_db,
            document_db,receiver_db,sender_db,deliveryType_db, instructions_db,amount_db,distance_db,posted_at_db,quantity_db,senderNumber_db,receiverNumber_db= null;
    TextView name_txt,title_txt,amount_txt,distance_txt,vehicle_txt,pickup_txt,dropoff_txt,document_txt,receiver_txt,sender_txt,instructions_txt,date,quantity,senderNumber_txt,receiverNumber_txt;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier__transaction_history_full_details);

        Intent courier_transactionhistory = getIntent();

        String post_id = courier_transactionhistory.getStringExtra("post_id");


        name_txt = (TextView)findViewById(R.id.clientPost_name_txtVw);
        title_txt = (TextView)findViewById(R.id.clientPost_title_txtVw);
        amount_txt = (TextView)findViewById(R.id.clientPost_money_txtVw);
        distance_txt = (TextView)findViewById(R.id.clientPost_kilometer_txtVw);
        vehicle_txt = (TextView)findViewById(R.id.clientPost_vehicle_txtVw2);
        pickup_txt = (TextView)findViewById(R.id.clientPost_pickup_location_txtVw);
        dropoff_txt = (TextView)findViewById(R.id.clientPost_dropoff_location_txtVw);
        document_txt = (TextView)findViewById(R.id.clientPost_documentype1_txtVw);
        quantity = (TextView)findViewById(R.id.clientPost_quantity1_txtVw);
        receiver_txt = (TextView)findViewById(R.id.clientPost_receiver1_txtVw);
        sender_txt = (TextView)findViewById(R.id.clientPost_sender_name_txtVw);
        receiverNumber_txt = (TextView)findViewById(R.id.clientPost_receiverNumber1_txtVw);
        senderNumber_txt = (TextView)findViewById(R.id.clientPost_senderNumber1_txtVw);

        instructions_txt = (TextView)findViewById(R.id.clientPost_instructions1_txtVw);
        date = (TextView)findViewById(R.id.clientPost_date_txtVw);
        profileImage = (ImageView)findViewById(R.id.clientPost_profile_icon);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        postsRef = database.getReference("delivery_posts");

        Query post_query = postsRef.orderByChild("post_id").equalTo(post_id);

        post_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        amount_db = snapshot.child("amount").getValue().toString();
                        distance_db = snapshot.child("distance").getValue().toString();
                        pickup_location_db = snapshot.child("pickup_location").getValue().toString();
                        dropoff_location_db = snapshot.child("dropoff_location").getValue().toString();
                        document_db = snapshot.child("document_type").getValue().toString();
                        quantity_db = snapshot.child("quantity").getValue().toString();
                        instructions_db = snapshot.child("instructions").getValue().toString();
                        receiver_db = snapshot.child("receiver_name").getValue().toString();
                        sender_db = snapshot.child("sender_name").getValue().toString();
                        receiverNumber_db = snapshot.child("receiver_number").getValue().toString();
                        senderNumber_db = snapshot.child("sender_number").getValue().toString();

                        vehicle_db = snapshot.child("vehicle").getValue().toString();
                        client_id_db = snapshot.child("client_id").getValue().toString();
                        post_id_db = snapshot.child("post_id").getValue().toString();
                        posted_at_db = snapshot.child("posted_at").getValue().toString();
                    }

                    amount_txt.setText("₱"+ amount_db);
                    distance_txt.setText(distance_db + "km");
                    pickup_txt.setText(pickup_location_db);
                    dropoff_txt.setText(dropoff_location_db);
                    document_txt.setText(document_db);
                    quantity.setText(quantity_db);
                    instructions_txt.setText(instructions_db);
                    receiver_txt.setText(receiver_db);
                    sender_txt.setText(sender_db);
                    receiverNumber_txt.setText(receiverNumber_db);
                    senderNumber_txt.setText(senderNumber_db);

                    vehicle_txt.setText(vehicle_db);

                    if(posted_at_db == null){

                    }else{
                        DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy  hh:mm aaa");
                        Date netDate = (new Date(Long.parseLong(posted_at_db)));
                        date.setText(dateFormat.format(netDate));
                    }



                    accountRef = database.getReference("accounts");
                    Query accounts_query = accountRef.orderByChild("user_id").equalTo(client_id_db);

                    accounts_query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String fullname;
                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                firstName_db = snapshot.child("first_name").getValue().toString();
                                lastName_db = snapshot.child("last_name").getValue().toString();
                                image_profile_db = snapshot.child("image_profile").getValue().toString();
                                user_type_db = snapshot.child("user_type").getValue().toString();

                            }

                            fullname = firstName_db + " " + lastName_db;

                            title_txt.setText(user_type_db);
                            name_txt.setText(fullname);

                            Transformation transformation = new RoundedTransformationBuilder()
                                    // .borderColor(Color.WHITE)
                                    //  .borderWidthDp(1)
                                    .cornerRadiusDp(50)
                                    .oval(false)
                                    .build();
                            Picasso.with(Courier_TransactionHistory_fullDetails.this)
                                    .load(image_profile_db)
                                    .fit()
                                    .transform(transformation)
                                    .into(profileImage);
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


    public void backToCourierHome(View view ){
        Intent intent = new Intent(this, Courier_TransactionHistory.class);
        startActivity(intent);
    }
}
