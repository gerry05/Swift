package app.swift;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import q.rorbin.badgeview.QBadgeView;


public class Client_Post extends AppCompatActivity {

    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef,postRef,requestRef,notificationsRef;
    private FirebaseAuth mAuth;


    private TextView client_name,client_title,vehicle,pickup_location,dropoff_location,documentType,instructions,amount,distance,status ,receiver_name,sender_name,deliveryType,date,quantity;
    private ImageView client_profileImg;
    private Button deletePost_btn;
    ConstraintLayout client_post_layout;
    RelativeLayout noPost_layout;


    String dropoff_location_db = null;
    String pickup_location_db = null;
    String amount_db = null;
    String distance_db = null;
    String post_status_db = null;
    String vehicle_db = null;
    String document_db = null;
    String receiver_db = null;
    String sender_db = null;
    String instructions_db = null;
    String post_id_db = null;
    String delivery_type_db = null;
    String quantity_db = null;
    String posted_at_db;

    String notification_status_db= null;
    String request_status_db= null;

    List<String> requestId_array = new ArrayList<String>(); // Result will be holded Here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__post);


        MaterialDialog.Builder builder  = new MaterialDialog.Builder(Client_Post.this)
                .content("Loading...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();


        client_name = (TextView) findViewById(R.id.clientPost_name_txtVw);
        client_title = (TextView) findViewById(R.id.clientPost_title_txtVw);
        vehicle = (TextView) findViewById(R.id.clientPost_vehicle_txtVw2);
        pickup_location = (TextView) findViewById(R.id.clientPost_pickup_location_txtVw);
        dropoff_location = (TextView) findViewById(R.id.clientPost_dropoff_location_txtVw);
        documentType = (TextView) findViewById(R.id.clientPost_documentype1_txtVw);
        quantity = (TextView) findViewById(R.id.clientPost_quantity1_txtVw);
        instructions = (TextView) findViewById(R.id.clientPost_instructions1_txtVw);
        client_profileImg = (ImageView) findViewById(R.id.clientPost_profile_icon);
        amount = (TextView) findViewById(R.id.clientPost_money_txtVw);
        distance = (TextView) findViewById(R.id.clientPost_kilometer_txtVw);
        status = (TextView) findViewById(R.id.clientPost_status_txtVw);
        receiver_name = (TextView) findViewById(R.id.clientPost_receiver1_txtVw);
        sender_name = (TextView) findViewById(R.id.clientPost_sender_name_txtVw);
        deliveryType = (TextView) findViewById(R.id.clientPost_delivery_type_txtVw);
        date = (TextView) findViewById(R.id.clientPost_date_txtVw);

        deletePost_btn = (Button) findViewById(R.id.deliveryReview_deletePost_btn);
        client_post_layout = (ConstraintLayout) findViewById(R.id.constraintLayout4);
        noPost_layout = (RelativeLayout) findViewById(R.id.noPost_layout);


        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("accounts");

        Query account_query = myRef.orderByChild("user_id").equalTo(user.getUid());

        account_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String firstName = null;
                String lastName = null;
                String fullName;
                String image_profile = null;
                String user_type = null;


                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    firstName = snapshot.child("first_name").getValue().toString();
                    lastName = snapshot.child("last_name").getValue().toString();

                    image_profile = snapshot.child("image_profile").getValue().toString();
                    user_type = snapshot.child("user_type").getValue().toString();
                }

                fullName = firstName + " " + lastName;


                client_name.setText(fullName);
                client_title.setText(user_type);

                Transformation transformation = new RoundedTransformationBuilder()
                        // .borderColor(Color.WHITE)
                        //  .borderWidthDp(1)
                        .cornerRadiusDp(50)
                        .oval(false)
                        .build();
                Picasso.with(Client_Post.this)
                        .load(image_profile)
                        .fit()
                        .transform(transformation)
                        .into(client_profileImg);


                postRef = database.getReference("delivery_posts");
                Query post_query = postRef.orderByChild("client_id").equalTo(user.getUid());

                post_query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot.exists());
                        if(dataSnapshot.exists()){

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                pickup_location_db = snapshot.child("pickup_location").getValue().toString();
                                dropoff_location_db = snapshot.child("dropoff_location").getValue().toString();
                                amount_db = snapshot.child("amount").getValue().toString();
                                distance_db = snapshot.child("distance").getValue().toString();
                                post_status_db = snapshot.child("post_status").getValue().toString().substring(0, 1).toUpperCase() + snapshot.child("post_status").getValue().toString().substring(1);
                                vehicle_db = snapshot.child("vehicle").getValue().toString();
                                document_db = snapshot.child("document_type").getValue().toString();
                                quantity_db = snapshot.child("quantity").getValue().toString();
                                receiver_db = snapshot.child("receiver_name").getValue().toString();
                                sender_db = snapshot.child("sender_name").getValue().toString();
                                instructions_db = snapshot.child("instructions").getValue().toString();
                                post_id_db = snapshot.child("post_id").getValue().toString();
                                delivery_type_db = snapshot.child("delivery_type").getValue().toString();
                                posted_at_db = snapshot.child("posted_at").getValue().toString();
                            }


                        }else{
                            System.out.println("doesn't exist");
                        }

                        if(post_status_db == null){
                            noPost_layout.setVisibility(View.VISIBLE);
                            client_post_layout.setVisibility(View.GONE);
                            dialog.dismiss();
                        }else if(post_status_db.equals("Pending") || post_status_db.equals("Processing")){
                            noPost_layout.setVisibility(View.GONE);
                            client_post_layout.setVisibility(View.VISIBLE);
                            pickup_location.setText(pickup_location_db);
                            dropoff_location.setText(dropoff_location_db);
                            amount.setText("₱" + amount_db);
                            distance.setText(distance_db + "km");
                            status.setText("Status: "+post_status_db);
                            vehicle.setText(vehicle_db);
                            documentType.setText(document_db);
                            instructions.setText(instructions_db);
                            sender_name.setText(sender_db);
                            quantity.setText(quantity_db);
                            receiver_name.setText(receiver_db);
                            deliveryType.setText(delivery_type_db.substring(0,1).toUpperCase() + delivery_type_db.substring(1).toLowerCase());

                            if(posted_at_db == null){

                            }else{
                                DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy  hh:mm aaa");
                                Date netDate = (new Date(Long.parseLong(posted_at_db)));
                                System.out.println(dateFormat.format(netDate));
                            }
                           // date.setText(dateFormat.format(netDate));



                            dialog.dismiss();

                        }else if(post_status_db.equals("Completed")){
                            noPost_layout.setVisibility(View.VISIBLE);
                            client_post_layout.setVisibility(View.GONE);
                            dialog.dismiss();
                        }


                        deletePost_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                new SweetAlertDialog(Client_Post.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Are you sure?")
                                        .setConfirmText("Yes, delete it!")
                                        .setCancelText("Cancel")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                requestRef = database.getReference("courier_requests");
                                                Query request_query = requestRef.orderByChild("post_id").equalTo(post_id_db);

                                                request_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String request_requestId_db = null;

                                                        if(dataSnapshot.exists()){
                                                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                                                request_requestId_db = snapshot.child("request_id").getValue().toString();
                                                                requestId_array.add(String.valueOf(request_requestId_db));
                                                            }
                                                            for(String data: requestId_array){
                                                                requestRef.child(data).removeValue();
                                                            }

                                                        }else if(!dataSnapshot.exists()){
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                postRef.child(post_id_db).removeValue();
                                                sweetAlertDialog.dismissWithAnimation();
                                                Intent intent = getIntent();
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                startActivityForResult(intent,0);
                                            }
                                        })
                                        .show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
















        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
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
                    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
                    BottomNavigationMenuView bottomNavigationMenuView =
                            (BottomNavigationMenuView) navigation.getChildAt(0);
                    View v = bottomNavigationMenuView.getChildAt(3); // number of menu from left
                    new QBadgeView(Client_Post.this)
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


        //Notification for request activity
        requestRef = database.getReference("courier_requests");
        final Query request_query = requestRef.orderByChild("client_id").equalTo(user.getUid());

        request_query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int seen_count = 0;

                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(snapshot.child("notification_status").exists()) {
                            request_status_db = snapshot.child("notification_status").getValue().toString();
                            if (request_status_db.equals("pending")) {
                                ++seen_count;
                            }
                        }
                    }
                    System.out.println(seen_count);
                    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
                    BottomNavigationMenuView  bottomNavigationMenuView =
                            (BottomNavigationMenuView) navigation.getChildAt(0);
                    View v = bottomNavigationMenuView.getChildAt(2); // number of menu from left
                    new QBadgeView(Client_Post.this)
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

                    case R.id.ic_create:
                        Intent intent1 = new Intent(Client_Post.this, Client_Home.class);
                        startActivity(intent1);
                        break;

                    case R.id.ic_post:
                        Intent intent2 = new Intent(Client_Post.this, Client_Post.class);
                        startActivity(intent2);
                        break;

                    case R.id.ic_request:
                        Intent intent3 = new Intent(Client_Post.this, Client_Requests.class);
                        startActivity(intent3);
                        break;

                    case R.id.ic_notifications:
                        Intent intent4 = new Intent(Client_Post.this, Client_Notifications.class);
                        startActivity(intent4);
                        break;
                }
                return false;
            }
        });
    }




    @Override
    public void onBackPressed()
    {
        // di mag gana kung e press ang backpress
    }
}
