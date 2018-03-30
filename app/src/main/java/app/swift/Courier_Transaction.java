package app.swift;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import q.rorbin.badgeview.QBadgeView;

public class Courier_Transaction extends AppCompatActivity {

    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef,postRef,transactionRef,accountsRef,ratings_feedbacksRef,notificationsRef;
    private FirebaseAuth mAuth;


    private TextView client_name,client_title,vehicle,pickup_location,dropoff_location,documentType,receiverName,instructions,amount,distance,status ,sender_or_receiver_name;
    private ImageView client_profileImg;
    private Button going_btn,dropoff_btn;


    ConstraintLayout client_post_layout;
    RelativeLayout noPost_layout;

    // delivery
    String dropoff_location_db,pickup_location_db,amount_db,distance_db,
            post_status_db,vehicle_db,document_db,receiver_db,sender_db,
            instructions_db,post_id_db,delivery_type_db = null;

    //accounts
    String firstName = null;
    String lastName = null;
    String fullName;
    String image_profile = null;
    String notification_status_db = null;

    //Transactions
    String transaction_id_db, transaction_client_id_db,transaction_courier_id_db,transaction_request_id_db,transaction_post_id_db,transaction_status_db = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier__transaction);

        client_name = (TextView) findViewById(R.id.clientPost_name_txtVw);
        client_title = (TextView) findViewById(R.id.clientPost_title_txtVw);
        vehicle = (TextView) findViewById(R.id.clientPost_vehicle_txtVw2);
        pickup_location = (TextView) findViewById(R.id.clientPost_pickup_location_txtVw);
        dropoff_location = (TextView) findViewById(R.id.clientPost_dropoff_location_txtVw);
        documentType = (TextView) findViewById(R.id.clientPost_documentype1_txtVw);
        receiverName = (TextView) findViewById(R.id.clientPost_receover1_txtVw);
        instructions = (TextView) findViewById(R.id.clientPost_instructions1_txtVw);
        client_profileImg = (ImageView) findViewById(R.id.clientPost_profile_icon);
        amount = (TextView) findViewById(R.id.clientPost_money_txtVw);
        distance = (TextView) findViewById(R.id.clientPost_kilometer_txtVw);
        status = (TextView) findViewById(R.id.clientPost_status_txtVw);
        sender_or_receiver_name = (TextView) findViewById(R.id.clientPost_receiver_txtVw);
        going_btn = (Button) findViewById(R.id.courier_view_client_transaction_going_btn);
        dropoff_btn = (Button) findViewById(R.id.courier_view_client_transaction_dropoff_btn);

        client_post_layout = (ConstraintLayout) findViewById(R.id.constraintLayout4);
        noPost_layout = (RelativeLayout) findViewById(R.id.noPost_layout);


        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        transactionRef = database.getReference("transactions");

        final Query transactions_query = transactionRef.orderByChild("courier_id").equalTo(user.getUid()).limitToLast(1);


        transactions_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        transaction_status_db = snapshot.child("transaction_status").getValue().toString();
                        transaction_post_id_db = snapshot.child("post_id").getValue().toString();
                        transaction_client_id_db = snapshot.child("client_id").getValue().toString();
                        transaction_id_db = snapshot.child("transaction_id").getValue().toString();
                    }

                    if(transaction_status_db.equals("pending")){
                        status.setText("Status: "+transaction_status_db);
                        going_btn.setEnabled(true);
                        dropoff_btn.setEnabled(false);
                    }else if(transaction_status_db.equals("processing")){
                        status.setText("Status: "+transaction_status_db);
                        going_btn.setEnabled(false);
                        dropoff_btn.setEnabled(true);
                    }

                    if(transaction_status_db.equals("pending") || transaction_status_db.equals("processing")){
                        client_post_layout.setVisibility(View.VISIBLE);
                        noPost_layout.setVisibility(View.GONE);

                        postRef = database.getReference("delivery_posts");
                        Query post_query = postRef.orderByChild("post_id").equalTo(transaction_post_id_db);

                        post_query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                               if(dataSnapshot.exists()){
                                   for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                       pickup_location_db = snapshot.child("pickup_location").getValue().toString();
                                       dropoff_location_db = snapshot.child("dropoff_location").getValue().toString();
                                       amount_db = snapshot.child("amount").getValue().toString();
                                       distance_db = snapshot.child("distance").getValue().toString();
                                       post_status_db = snapshot.child("post_status").getValue().toString().substring(0, 1).toUpperCase() + snapshot.child("post_status").getValue().toString().substring(1);
                                       vehicle_db = snapshot.child("vehicle").getValue().toString();
                                       document_db = snapshot.child("document_type").getValue().toString();
                                       receiver_db = snapshot.child("receiver_name").getValue().toString();
                                       sender_db = snapshot.child("sender_name").getValue().toString();
                                       instructions_db = snapshot.child("instructions").getValue().toString();
                                       post_id_db = snapshot.child("post_id").getValue().toString();
                                       delivery_type_db = snapshot.child("delivery_type").getValue().toString();
                                   }

                                   pickup_location.setText(pickup_location_db);
                                   dropoff_location.setText(dropoff_location_db);
                                   amount.setText("₱" + amount_db);
                                   distance.setText(distance_db + "km");
                                   vehicle.setText(vehicle_db);
                                   documentType.setText(document_db);
                                   receiverName.setText(receiver_db);
                                   instructions.setText(instructions_db);


                               }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        accountsRef = database.getReference("accounts");

                        Query account_query = accountsRef.orderByChild("user_id").equalTo(transaction_client_id_db);

                        account_query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                if(dataSnapshot.exists()){

                                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                        firstName = snapshot.child("first_name").getValue().toString();
                                        lastName = snapshot.child("last_name").getValue().toString();

                                        image_profile = snapshot.child("image_profile").getValue().toString();

                                    }

                                    fullName = firstName + " " + lastName;


                                    client_name.setText(fullName);

                                    Transformation transformation = new RoundedTransformationBuilder()

                                            .cornerRadiusDp(50)
                                            .oval(false)
                                            .build();
                                    Picasso.with(Courier_Transaction.this)
                                            .load(image_profile)
                                            .fit()
                                            .transform(transformation)
                                            .into(client_profileImg);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        going_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                transactionRef = database.getReference("transactions").child(transaction_id_db);
                                transactionRef.child("transaction_status").setValue("processing");


                                notificationsRef = database.getReference("notifications").push();
                                notificationsRef.child("notification_id").setValue(notificationsRef.getKey());
                                notificationsRef.child("notification_receiver_user_id").setValue(transaction_client_id_db);
                                notificationsRef.child("notification_sender_user_id").setValue(user.getUid());
                                notificationsRef.child("message").setValue("is now going to the pickup location.");
                                notificationsRef.child("created_at").setValue(ServerValue.TIMESTAMP);
                                notificationsRef.child("notification_status").setValue("pending");


                                new SweetAlertDialog(Courier_Transaction.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Have a safe ride!")
                                        .setContentText("You're now going to the pickup location.")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                                Intent intent = getIntent();
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                startActivityForResult(intent,0);
                                            }
                                        })
                                        .show();

                            }
                        });

                        dropoff_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                transactionRef = database.getReference("transactions").child(transaction_id_db);
                                transactionRef.child("transaction_status").setValue("completed");
                                transactionRef.child("completed_at").setValue(ServerValue.TIMESTAMP);

                                postRef = database.getReference("delivery_posts").child(transaction_post_id_db);
                                postRef.child("post_status").setValue("completed");
                                status.setText("Status: none");

                                notificationsRef = database.getReference("notifications").push();
                                notificationsRef.child("notification_id").setValue(notificationsRef.getKey());
                                notificationsRef.child("notification_receiver_user_id").setValue(transaction_client_id_db);
                                notificationsRef.child("notification_sender_user_id").setValue(user.getUid());
                                notificationsRef.child("message").setValue("has successfully delivered your documents.");
                                notificationsRef.child("created_at").setValue(ServerValue.TIMESTAMP);
                                notificationsRef.child("notification_status").setValue("pending");


                                new SweetAlertDialog(Courier_Transaction.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Good job!")
                                        .setContentText("You can now request to other posts")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();


                                                final Dialog dialog = new Dialog(Courier_Transaction.this);
                                                dialog.setCancelable(false);
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.setContentView(R.layout.activity_courier__rate_to_client);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                dialog.show();

                                                ImageView client_profileImg = (ImageView)  dialog.findViewById(R.id.courier_rate_client_profile_icon);
                                                TextView client_fullName = (TextView) dialog.findViewById(R.id.courier_rate_client_txtVw1);
                                                final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.courier_rate_client_ratingBar);
                                                final TextView ratingTxt = (TextView) dialog.findViewById(R.id.courier_rate_client_ratingcount_txtVw);
                                                Button rating_submitBtn = (Button) dialog.findViewById(R.id.courier_rate_client_submit_btn);
                                                Button rating_cancelBtn = (Button) dialog.findViewById(R.id.courier_rate_client_nothanks_btn);
                                                final EditText feedback_eTxt = (EditText) dialog.findViewById(R.id.courier_rate_client_feedback_eTxt);

                                                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                                    @Override
                                                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                                        ratingTxt.setText(""+ (int) rating);
                                                    }
                                                });


                                                rating_submitBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        if(TextUtils.isEmpty(feedback_eTxt.getText().toString())){
//                                                            new SweetAlertDialog(Courier_Transaction.this, SweetAlertDialog.WARNING_TYPE)
//                                                                    .setTitleText("Please fill in all fields!")
//                                                                    .show();
                                                            ratings_feedbacksRef = database.getReference("ratings_feedbacks").push();
                                                            ratings_feedbacksRef.child("rating_feedback_id").setValue(ratings_feedbacksRef.getKey());
                                                            ratings_feedbacksRef.child("rate_by_user_id").setValue(transaction_client_id_db);
                                                            ratings_feedbacksRef.child("rating").setValue((int) ratingBar.getRating());
                                                            ratings_feedbacksRef.child("feedback").setValue(null);
                                                            ratings_feedbacksRef.child("respondent_user_id").setValue(user.getUid());
                                                            ratings_feedbacksRef.child("created_at").setValue(ServerValue.TIMESTAMP);
                                                            ratings_feedbacksRef.child("feedback").setValue("Empty message.");

                                                            notificationsRef = database.getReference("notifications").push();
                                                            notificationsRef.child("notification_id").setValue(notificationsRef.getKey());
                                                            notificationsRef.child("notification_receiver_user_id").setValue(transaction_client_id_db);
                                                            notificationsRef.child("notification_sender_user_id").setValue(user.getUid());
                                                            notificationsRef.child("message").setValue("gave you a feedback and " + (int) ratingBar.getRating() + " stars.");
                                                            notificationsRef.child("created_at").setValue(ServerValue.TIMESTAMP);
                                                            notificationsRef.child("rating_feedback").setValue("pending");
                                                            notificationsRef.child("notification_status").setValue("pending");


                                                            new SweetAlertDialog(Courier_Transaction.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                    .setTitleText("Thanks for rating!")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                            dialog.dismiss();
                                                                            startActivity(new Intent(Courier_Transaction.this,Courier_Home.class));
                                                                            sweetAlertDialog.dismissWithAnimation();
                                                                        }
                                                                    })
                                                                    .show();
                                                        }else{
                                                            ratings_feedbacksRef = database.getReference("ratings_feedbacks").push();
                                                            ratings_feedbacksRef.child("rating_feedback_id").setValue(ratings_feedbacksRef.getKey());
                                                            ratings_feedbacksRef.child("rate_by_user_id").setValue(transaction_client_id_db);
                                                            ratings_feedbacksRef.child("rating").setValue((int) ratingBar.getRating());
                                                            ratings_feedbacksRef.child("feedback").setValue(feedback_eTxt.getText().toString());
                                                            ratings_feedbacksRef.child("respondent_user_id").setValue(user.getUid());
                                                            ratings_feedbacksRef.child("created_at").setValue(ServerValue.TIMESTAMP);


                                                            notificationsRef = database.getReference("notifications").push();
                                                            notificationsRef.child("notification_id").setValue(notificationsRef.getKey());
                                                            notificationsRef.child("notification_receiver_user_id").setValue(transaction_client_id_db);
                                                            notificationsRef.child("notification_sender_user_id").setValue(user.getUid());
                                                            notificationsRef.child("message").setValue("gave you a feedback and " + (int) ratingBar.getRating() + " stars.");
                                                            notificationsRef.child("created_at").setValue(ServerValue.TIMESTAMP);
                                                            notificationsRef.child("rating_feedback").setValue("pending");
                                                            notificationsRef.child("notification_status").setValue("pending");


                                                            new SweetAlertDialog(Courier_Transaction.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                    .setTitleText("Thanks for rating!")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                            dialog.dismiss();
                                                                            startActivity(new Intent(Courier_Transaction.this,Courier_Home.class));
                                                                            sweetAlertDialog.dismissWithAnimation();
                                                                        }
                                                                    })
                                                                    .show();
                                                        }

                                                    }
                                                });



                                                rating_cancelBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                    }
                                                });


                                                client_fullName.setText("Please rate " + fullName);
                                                Transformation transformation = new RoundedTransformationBuilder()
                                                        // .borderColor(Color.WHITE)
                                                        //  .borderWidthDp(1)
                                                        .cornerRadiusDp(100)
                                                        .oval(false)
                                                        .build();
                                                Picasso.with(Courier_Transaction.this)
                                                        .load(image_profile)
                                                        .fit()
                                                        .transform(transformation)
                                                        .into(client_profileImg);

                                            }
                                        })
                                        .show();
                            }
                        });


                    }else if(transaction_status_db.equals("completed")){
                        client_post_layout.setVisibility(View.GONE);
                        noPost_layout.setVisibility(View.VISIBLE);
                    }

                }else if(!dataSnapshot.exists()){
                    client_post_layout.setVisibility(View.GONE);
                    noPost_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.courier_bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);


        //Notification for notification activity
        notificationsRef = database.getReference("notifications");
        final Query notifications_query = notificationsRef.orderByChild("notification_receiver_user_id").equalTo(user.getUid());

        notifications_query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int seen_count = 0;

                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(snapshot.child("notification_status").exists()) {
                            notification_status_db = snapshot.child("notification_status").getValue().toString();
                            if (notification_status_db.equals("pending")) {
                                ++seen_count;
                            }
                        }
                    }
                    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.courier_bottomNavView_Bar);
                    BottomNavigationMenuView bottomNavigationMenuView =
                            (BottomNavigationMenuView) navigation.getChildAt(0);
                    View v = bottomNavigationMenuView.getChildAt(3); // number of menu from left
                    new QBadgeView(Courier_Transaction.this)
                            .bindTarget(v)
                            .setBadgeNumber(seen_count)
                            .setExactMode(false)
                            .setBadgeGravity(Gravity.END | Gravity.TOP)
                            .setBadgeTextSize(12,true)
                            .setGravityOffset(11,2,true)
                            .setShowShadow(false)
                            .setBadgeBackgroundColor(0xfffdb03a);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.ic_post:
                        Intent intent1 = new Intent(Courier_Transaction.this, Courier_Home.class);
                        startActivity(intent1);
                        break;

                    case R.id.ic_transaction:
                        Intent intent2 = new Intent(Courier_Transaction.this, Courier_Transaction.class);
                        startActivity(intent2);
                        break;

                    case R.id.ic_request:
                        Intent intent3 = new Intent(Courier_Transaction.this, Courier_Requests.class);
                        startActivity(intent3);
                        break;

                    case R.id.ic_notifications:
                        Intent intent4 = new Intent(Courier_Transaction.this, Courier_Notifications.class);
                        startActivity(intent4);
                        break;
                }

                return false;
            }
        });



    }
}
