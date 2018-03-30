package app.swift;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.google.firebase.database.ServerValue;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import q.rorbin.badgeview.QBadgeView;


public class Courier_Home extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private DatabaseReference myRef,postsRef, requestRef,accountRef,ignoreRef,transactionRef,ratings_feedbacksRef,notificationsRef;

    String my_vehicle;

    private RecyclerView mPosts;

    String user_id = null;
    String post_id = null;
    String post_id_db = null;
    String notification_status_db= null;
    List<String> posts_status_array = new ArrayList<String>(); // Result will be holded Here
    List<String> posts_id_array = new ArrayList<String>(); // Result will be holded Here
    TextView noPost_txt;
    TextView refreshPost;


    //Filtering the vehicle (DROPDOWN)
    String[] vehicles = new String[]{"Bicycle","Motorcycle"};
    String vehicle="";
    ArrayAdapter<String> adapter;

    //ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier__home);


        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        noPost_txt = (TextView) findViewById(R.id.noPost_txt);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mPosts=(RecyclerView) findViewById(R.id.courier_posts_recycleView);
        mPosts.setHasFixedSize(true);

        mPosts.setLayoutManager(layoutManager);

//        myRef = database.getReference("accounts").child(user.getPhoneNumber().replaceAll("\\W",""));
//
//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                my_vehicle = dataSnapshot.child("vehicle").getValue().toString();
//
//               getUsers_posts(my_vehicle);
//
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });





        Spinner document_ddown = findViewById(R.id.vehicles_spinner);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vehicles);

        document_ddown.setAdapter(adapter);
        document_ddown.setSelection(0);
        document_ddown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //vehicle = "Bicycle";
                        getUsers_posts_bicycle();
                        break;
                    case 1:
                       // vehicle = "Motorcycle";
                        getUsers_posts_motorcycle();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.courier_bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
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
                        if(snapshot.child("notification_status").exists()){
                            notification_status_db = snapshot.child("notification_status").getValue().toString();
                            if(notification_status_db.equals("pending")){
                                ++seen_count;
                            }
                        }

                    }
                    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.courier_bottomNavView_Bar);
                    BottomNavigationMenuView bottomNavigationMenuView =
                            (BottomNavigationMenuView) navigation.getChildAt(0);
                    View v = bottomNavigationMenuView.getChildAt(3); // number of menu from left
                    new QBadgeView(Courier_Home.this)
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
                        Intent intent1 = new Intent(Courier_Home.this, Courier_Home.class);
                        startActivity(intent1);
                        break;

                    case R.id.ic_transaction:
                        Intent intent2 = new Intent(Courier_Home.this, Courier_Transaction.class);
                        startActivity(intent2);
                        break;

                    case R.id.ic_request:
                        Intent intent3 = new Intent(Courier_Home.this, Courier_Requests.class);
                        startActivity(intent3);
                        break;

                    case R.id.ic_notifications:
                        Intent intent4 = new Intent(Courier_Home.this, Courier_Notifications.class);
                        startActivity(intent4);
                        break;
                }

                return false;
            }
        });
    } // end of create

    public void getUsers_posts_bicycle(){

        MaterialDialog.Builder builder  = new MaterialDialog.Builder(Courier_Home.this)
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .content("Retrieving Posts")
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();

        requestRef = database.getReference("courier_requests");
        Query getCourier_requests = requestRef.orderByChild("courier_id").equalTo(user.getUid());
        getCourier_requests.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    post_id_db = snapshot.child("post_id").getValue().toString();
                    posts_id_array.add(String.valueOf(post_id_db));
                }

                postsRef = database.getReference().child("delivery_posts");
                postsRef.keepSynced(false);


                final Query post_query = postsRef.orderByChild("vehicle").equalTo("Bicycle");

                post_query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                final FirebaseRecyclerAdapter<UsersPosts,UsersPostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UsersPosts, UsersPostsViewHolder>
                        (UsersPosts.class, R.layout.courier_posts_cardview,UsersPostsViewHolder.class, post_query) {
                    @Override
                    protected void populateViewHolder(final UsersPostsViewHolder viewHolder, final UsersPosts model, int position) {


                        myRef = database.getReference("accounts").child(user.getPhoneNumber().replaceAll("\\W",""));

                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    my_vehicle = dataSnapshot.child("vehicle").getValue().toString();

                                    if(my_vehicle.equals("Bicycle") || my_vehicle.equals("Both")){
                                        viewHolder.send_request.setEnabled(true);
                                        viewHolder.ignore_post.setEnabled(true);
                                    }else if(my_vehicle.equals("Motorcycle")){
                                        viewHolder.send_request.setEnabled(false);
                                        viewHolder.ignore_post.setEnabled(false);
                                        new SweetAlertDialog(Courier_Home.this)
                                                .setTitleText("Message")
                                                .setContentText("You're not using Bicycle!")
                                                .setConfirmText("OK")
                                                .show();
                                    }
                                }

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });




                        if(model.getPost_status() == null){

                        }else if(model.getPost_status().equals("processing") || model.getPost_status().equals("completed")){
                            viewHolder.setLayout_GONE();

                        }
                        viewHolder.setPickup_location(model.getPickup_location());
                        viewHolder.setDropoff_location( model.getDropoff_location());
                        viewHolder.setAmount("₱" + model.getAmount());
                        viewHolder.setDistance(model.getDistance() + "km");








                        if(model.getPosted_at() == null){

                        }else{
                            DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy  hh:mm aaa");
                            Date netDate = (new Date(Long.parseLong(String.valueOf(model.getPosted_at()))));
                            viewHolder.date.setText(dateFormat.format(netDate));
                        }




                        for(String data1:posts_id_array){
                            if(data1 == null){
                                noPost_txt.setVisibility(View.VISIBLE);
                            }else if(data1 != null && data1.equals(model.getPost_id())) {
                                viewHolder.setLayout_GONE();
                                System.out.println("GONE");
                            }

                        }
                        ignoreRef = database.getReference("ignore_posts");
                        Query ignoreposts_query = ignoreRef.orderByChild("courier_id").equalTo(user.getUid());
                        ignoreposts_query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                dialog.dismiss();

                                if(dataSnapshot.exists()){
                                    String ignore_post_id_db = null;
                                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                        ignore_post_id_db = snapshot.child("post_id").getValue().toString();
                                        posts_id_array.add(String.valueOf(ignore_post_id_db));
                                    }


                                    if(ignore_post_id_db != null){
                                        for(String data:posts_id_array){
                                            Pattern pattern = Pattern.compile( model.getPost_id() );
                                            Matcher matcher = pattern.matcher(data);
                                            while(matcher.find()){
                                                viewHolder.setLayout_GONE();

                                            }
                                        }
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        accountRef = database.getReference("accounts");
                        Query account_query = accountRef.orderByChild("user_id").equalTo(model.getClient_id());
                        account_query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String image_profile_db = null;
                                String first_name_db = null;
                                String last_name_db = null;
                                String fullname;


                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    image_profile_db = snapshot.child("image_profile").getValue().toString();
                                    first_name_db = snapshot.child("first_name").getValue().toString();
                                    last_name_db = snapshot.child("last_name").getValue().toString();

                                }

                                fullname = first_name_db + " " + last_name_db;
                                viewHolder.setUser_Name(fullname);

                                Transformation transformation = new RoundedTransformationBuilder()
                                        .borderColor(Color.WHITE)
                                        .borderWidthDp(1)
                                        .cornerRadiusDp(100)
                                        .oval(false)
                                        .build();
                                Picasso.with(Courier_Home.this)
                                        .load(image_profile_db)
                                        .fit()
                                        .transform(transformation)
                                        .into(viewHolder.profileImage);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        viewHolder.send_request.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                transactionRef = database.getReference("transactions");
                                Query transaction_query = transactionRef.orderByChild("post_id").equalTo(model.getPost_id());


                                transaction_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.exists()){
                                            new SweetAlertDialog(Courier_Home.this)
                                                    .setTitleText("This post isn't available")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            viewHolder.setLayout_GONE();
                                                            sweetAlertDialog.dismissWithAnimation();
                                                        }
                                                    })
                                                    .show();
                                        }else if(!dataSnapshot.exists()){
                                            transactionRef = database.getReference("transactions");
                                            Query transaction_query = transactionRef.orderByChild("courier_id").equalTo(user.getUid()).limitToLast(1);

                                            transaction_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String transaction_status_db = null;

                                                    if(dataSnapshot.exists()){
                                                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                                            transaction_status_db = snapshot.child("transaction_status").getValue().toString();
                                                        }


                                                        if(transaction_status_db.equals("pending") || transaction_status_db.equals("processing")){
                                                            new SweetAlertDialog(Courier_Home.this)
                                                                    .setTitleText("Message")
                                                                    .setContentText("Please finish your current transaction!")
                                                                    .setConfirmText("OK")
                                                                    .show();
                                                            System.out.println(transaction_status_db.toString());
                                                        }else if(transaction_status_db.equals("completed")){
                                                            new SweetAlertDialog(Courier_Home.this)
                                                                    .setTitleText("You are requesting on this post.")
                                                                    .setConfirmText("Request!")
                                                                    .setCancelText("Cancel")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                            String client_id;
                                                                            String post_id;

                                                                            client_id = model.getClient_id();
                                                                            post_id = model.getPost_id();
                                                                            requestRef = database.getReference("courier_requests").push();

                                                                            requestRef.child("request_id").setValue(requestRef.getKey());
                                                                            requestRef.child("post_id").setValue(post_id);
                                                                            requestRef.child("client_id").setValue(client_id);
                                                                            requestRef.child("courier_id").setValue(user.getUid());
                                                                            requestRef.child("request_status").setValue("pending");
                                                                            requestRef.child("notification_status").setValue("pending");
                                                                            requestRef.child("requested_at").setValue(ServerValue.TIMESTAMP);
                                                                            viewHolder.setLayout_GONE();

                                                                            sweetAlertDialog
                                                                                    .setTitleText("Request submitted")
                                                                                    .setConfirmText("OK")
                                                                                    .setConfirmClickListener(null)
                                                                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);



                                                                        }
                                                                    })
                                                                    .show();
                                                        }
                                                    }else if(!dataSnapshot.exists()){
                                                        new SweetAlertDialog(Courier_Home.this)
                                                                .setTitleText("You are requesting on this post.")
                                                                .setConfirmText("Request!")
                                                                .setCancelText("Cancel")
                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                        String client_id;
                                                                        String post_id;

                                                                        client_id = model.getClient_id();
                                                                        post_id = model.getPost_id();
                                                                        requestRef = database.getReference("courier_requests").push();

                                                                        requestRef.child("request_id").setValue(requestRef.getKey());
                                                                        requestRef.child("post_id").setValue(post_id);
                                                                        requestRef.child("client_id").setValue(client_id);
                                                                        requestRef.child("courier_id").setValue(user.getUid());
                                                                        requestRef.child("request_status").setValue("pending");
                                                                        requestRef.child("notification_status").setValue("pending");
                                                                        requestRef.child("requested_at").setValue(ServerValue.TIMESTAMP);

                                                                        viewHolder.setLayout_GONE();

                                                                        sweetAlertDialog
                                                                                .setTitleText("Request submitted")
                                                                                .setConfirmText("OK")
                                                                                .setConfirmClickListener(null)
                                                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                                                        // sweetAlertDialog.dismissWithAnimation();
                                                                    }
                                                                })
                                                                .show();
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
                        });

                        viewHolder.ignore_post.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                new SweetAlertDialog(Courier_Home.this)
                                        .setTitleText("Want to ignore this? " )
                                        .setConfirmText("Yes!")
                                        .setCancelText("Cancel")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                requestRef = database.getReference("ignore_posts").push();
                                                requestRef.child("courier_id").setValue(user.getUid());
                                                requestRef.child("post_id").setValue(model.getPost_id());
                                                viewHolder.setLayout_GONE();
                                                sweetAlertDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();

                            }
                        });

                        viewHolder.fullDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Courier_Home.this, Courier_Posts_fullDetails.class);
                                intent.putExtra("post_id", model.getPost_id());
                                startActivity(intent);

                            }
                        });

                        viewHolder.viewProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Courier_Home.this, Courier_Post_viewProfile.class);
                                intent.putExtra("client_id", model.getClient_id());
                                startActivity(intent);
                            }
                        });
                    }
                };
                mPosts.setAdapter(firebaseRecyclerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    } // end of getUsers_posts_bicycle()


    public void getUsers_posts_motorcycle(){

        MaterialDialog.Builder builder  = new MaterialDialog.Builder(Courier_Home.this)
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .content("Retrieving Posts")
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();

        requestRef = database.getReference("courier_requests");
        Query getCourier_requests = requestRef.orderByChild("courier_id").equalTo(user.getUid());
        getCourier_requests.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    post_id_db = snapshot.child("post_id").getValue().toString();
                    posts_id_array.add(String.valueOf(post_id_db));
                }

                postsRef = database.getReference().child("delivery_posts");
                postsRef.keepSynced(false);


                final Query post_query = postsRef.orderByChild("vehicle").equalTo("Motorcycle");

                post_query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                final FirebaseRecyclerAdapter<UsersPosts,UsersPostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UsersPosts, UsersPostsViewHolder>
                        (UsersPosts.class, R.layout.courier_posts_cardview,UsersPostsViewHolder.class, post_query) {
                    @Override
                    protected void populateViewHolder(final UsersPostsViewHolder viewHolder, final UsersPosts model, int position) {

                        myRef = database.getReference("accounts").child(user.getPhoneNumber().replaceAll("\\W",""));
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    my_vehicle = dataSnapshot.child("vehicle").getValue().toString();

                                    if(my_vehicle.equals("Motorcycle") || my_vehicle.equals("Both")){
                                        viewHolder.send_request.setEnabled(true);
                                        viewHolder.ignore_post.setEnabled(true);
                                    }else if(my_vehicle.equals("Bicycle")){
                                        new SweetAlertDialog(Courier_Home.this)
                                                .setTitleText("Message")
                                                .setContentText("You're not using Motorcycle!")
                                                .setConfirmText("OK")
                                                .show();
                                        viewHolder.send_request.setEnabled(false);
                                        viewHolder.ignore_post.setEnabled(false);
                                    }


                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });




                        if(model.getPost_status() == null){

                        }else if(model.getPost_status().equals("processing") || model.getPost_status().equals("completed")){
                            viewHolder.setLayout_GONE();
                        }
                        viewHolder.setPickup_location(model.getPickup_location());
                        viewHolder.setDropoff_location( model.getDropoff_location());
                        viewHolder.setAmount("₱" + model.getAmount());
                        viewHolder.setDistance(model.getDistance() + "km");

                        if(model.getPosted_at() == null){

                        }else{
                            DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy  hh:mm aaa");
                            Date netDate = (new Date(Long.parseLong(String.valueOf(model.getPosted_at()))));
                            viewHolder.date.setText(dateFormat.format(netDate));
                        }

                        for(String data1:posts_id_array){
                            if(data1 == null){
                                noPost_txt.setVisibility(View.VISIBLE);
                            }else if(data1 != null && data1.equals(model.getPost_id())) {
                                viewHolder.setLayout_GONE();
                                System.out.println("GONE");
                            }

                        }
                        ignoreRef = database.getReference("ignore_posts");
                        Query ignoreposts_query = ignoreRef.orderByChild("courier_id").equalTo(user.getUid());
                        ignoreposts_query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                dialog.dismiss();

                                if(dataSnapshot.exists()){
                                    String ignore_post_id_db = null;
                                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                        ignore_post_id_db = snapshot.child("post_id").getValue().toString();
                                        posts_id_array.add(String.valueOf(ignore_post_id_db));
                                    }


                                    if(ignore_post_id_db != null){
                                        for(String data:posts_id_array){
                                            Pattern pattern = Pattern.compile( model.getPost_id() );
                                            Matcher matcher = pattern.matcher(data);
                                            while(matcher.find()){
                                                viewHolder.setLayout_GONE();

                                            }
                                        }
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        accountRef = database.getReference("accounts");
                        Query account_query = accountRef.orderByChild("user_id").equalTo(model.getClient_id());
                        account_query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String image_profile_db = null;
                                String first_name_db = null;



                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    image_profile_db = snapshot.child("image_profile").getValue().toString();
                                    first_name_db = snapshot.child("first_name").getValue().toString();
                                }


                                viewHolder.setUser_Name(first_name_db);

                                Transformation transformation = new RoundedTransformationBuilder()
                                        .borderColor(Color.WHITE)
                                        .borderWidthDp(1)
                                        .cornerRadiusDp(100)
                                        .oval(false)
                                        .build();
                                Picasso.with(Courier_Home.this)
                                        .load(image_profile_db)
                                        .fit()
                                        .transform(transformation)
                                        .into(viewHolder.profileImage);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        viewHolder.send_request.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                transactionRef = database.getReference("transactions");
                                Query transaction_query = transactionRef.orderByChild("post_id").equalTo(model.getPost_id());


                                transaction_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.exists()){
                                            new SweetAlertDialog(Courier_Home.this)
                                                    .setTitleText("This post isn't available")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            viewHolder.setLayout_GONE();
                                                            sweetAlertDialog.dismissWithAnimation();
                                                        }
                                                    })
                                                    .show();
                                        }else if(!dataSnapshot.exists()){
                                            transactionRef = database.getReference("transactions");
                                            Query transaction_query = transactionRef.orderByChild("courier_id").equalTo(user.getUid()).limitToLast(1);

                                            transaction_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String transaction_status_db = null;

                                                    if(dataSnapshot.exists()){
                                                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                                            transaction_status_db = snapshot.child("transaction_status").getValue().toString();
                                                        }


                                                        if(transaction_status_db.equals("pending") || transaction_status_db.equals("processing")){
                                                            new SweetAlertDialog(Courier_Home.this)
                                                                    .setTitleText("Message")
                                                                    .setContentText("Please finish your current transaction!")
                                                                    .setConfirmText("OK")
                                                                    .show();
                                                            System.out.println(transaction_status_db.toString());
                                                        }else if(transaction_status_db.equals("completed")){
                                                            new SweetAlertDialog(Courier_Home.this)
                                                                    .setTitleText("You are requesting on this post.")
                                                                    .setConfirmText("Request!")
                                                                    .setCancelText("Cancel")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                            String client_id;
                                                                            String post_id;

                                                                            client_id = model.getClient_id();
                                                                            post_id = model.getPost_id();
                                                                            requestRef = database.getReference("courier_requests").push();

                                                                            requestRef.child("request_id").setValue(requestRef.getKey());
                                                                            requestRef.child("post_id").setValue(post_id);
                                                                            requestRef.child("client_id").setValue(client_id);
                                                                            requestRef.child("courier_id").setValue(user.getUid());
                                                                            requestRef.child("request_status").setValue("pending");
                                                                            requestRef.child("requested_at").setValue(ServerValue.TIMESTAMP);
                                                                            viewHolder.setLayout_GONE();

                                                                            sweetAlertDialog
                                                                                    .setTitleText("Request submitted")
                                                                                    .setConfirmText("OK")
                                                                                    .setConfirmClickListener(null)
                                                                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);



                                                                        }
                                                                    })
                                                                    .show();
                                                        }
                                                    }else if(!dataSnapshot.exists()){
                                                        new SweetAlertDialog(Courier_Home.this)
                                                                .setTitleText("You are requesting on this post.")
                                                                .setConfirmText("Request!")
                                                                .setCancelText("Cancel")
                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                        String client_id;
                                                                        String post_id;

                                                                        client_id = model.getClient_id();
                                                                        post_id = model.getPost_id();
                                                                        requestRef = database.getReference("courier_requests").push();

                                                                        requestRef.child("request_id").setValue(requestRef.getKey());
                                                                        requestRef.child("post_id").setValue(post_id);
                                                                        requestRef.child("client_id").setValue(client_id);
                                                                        requestRef.child("courier_id").setValue(user.getUid());
                                                                        requestRef.child("request_status").setValue("pending");
                                                                        requestRef.child("requested_at").setValue(ServerValue.TIMESTAMP);

                                                                        viewHolder.setLayout_GONE();

                                                                        sweetAlertDialog
                                                                                .setTitleText("Request submitted")
                                                                                .setConfirmText("OK")
                                                                                .setConfirmClickListener(null)
                                                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                                                        // sweetAlertDialog.dismissWithAnimation();
                                                                    }
                                                                })
                                                                .show();
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
                        });

                        viewHolder.ignore_post.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                new SweetAlertDialog(Courier_Home.this)
                                        .setTitleText("Want to ignore this? " )
                                        .setConfirmText("Yes!")
                                        .setCancelText("Cancel")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                requestRef = database.getReference("ignore_posts").push();
                                                requestRef.child("courier_id").setValue(user.getUid());
                                                requestRef.child("post_id").setValue(model.getPost_id());
                                                viewHolder.setLayout_GONE();
                                                sweetAlertDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();

                            }
                        });

                        viewHolder.fullDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Courier_Home.this, Courier_Posts_fullDetails.class);
                                intent.putExtra("post_id", model.getPost_id());
                                startActivity(intent);

                            }
                        });

                        viewHolder.viewProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Courier_Home.this, Courier_Post_viewProfile.class);
                                intent.putExtra("client_id", model.getClient_id());
                                startActivity(intent);
                            }
                        });
                    }
                };
                mPosts.setAdapter(firebaseRecyclerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    } // end of getUsers_posts_motorcycle()








//    public void getUsers_posts(final String my_vehicle){
//
//
//        MaterialDialog.Builder builder  = new MaterialDialog.Builder(Courier_Home.this)
//                .contentGravity(GravityEnum.START)
//                .cancelable(false)
//                .content("Retrieving Posts")
//                .progress(true,0);
//        final MaterialDialog dialog = builder.build();
//        dialog.show();
//
//        requestRef = database.getReference("courier_requests");
//        Query getCourier_requests = requestRef.orderByChild("courier_id").equalTo(user.getUid());
//        getCourier_requests.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
//                    post_id_db = snapshot.child("post_id").getValue().toString();
//                    posts_id_array.add(String.valueOf(post_id_db));
//                }
//
//                postsRef = database.getReference().child("delivery_posts");
//             postsRef.keepSynced(false);
//
//
//                final Query post_query = postsRef.orderByChild("vehicle").equalTo(my_vehicle);
//
//                post_query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(!dataSnapshot.exists()){
//                            dialog.dismiss();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//                            final FirebaseRecyclerAdapter<UsersPosts,UsersPostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UsersPosts, UsersPostsViewHolder>
//                                    (UsersPosts.class, R.layout.courier_posts_cardview,UsersPostsViewHolder.class, post_query) {
//                                @Override
//                                protected void populateViewHolder(final UsersPostsViewHolder viewHolder, final UsersPosts model, int position) {
//
//                                    if(model.getPost_status() == null){
//
//                                    }else if(model.getPost_status().equals("processing") || model.getPost_status().equals("completed")){
//                                        viewHolder.setLayout_GONE();
//
//                                    }
//                                    viewHolder.setPickup_location(model.getPickup_location());
//                                    viewHolder.setDropoff_location( model.getDropoff_location());
//                                    viewHolder.setAmount("₱" + model.getAmount());
//                                    viewHolder.setDistance(model.getDistance() + "km");
//
//                                    if(model.getPosted_at() == null){
//
//                                    }else{
//                                        DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy  hh:mm aaa");
//                                        Date netDate = (new Date(Long.parseLong(String.valueOf(model.getPosted_at()))));
//                                        viewHolder.date.setText(dateFormat.format(netDate));
//                                    }
//
//
//
//
//                                    for(String data1:posts_id_array){
//                                        if(data1 == null){
//                                            noPost_txt.setVisibility(View.VISIBLE);
//                                        }else if(data1 != null && data1.equals(model.getPost_id())) {
//                                            viewHolder.setLayout_GONE();
//                                            System.out.println("GONE");
//                                        }
//
//                                    }
//                                    ignoreRef = database.getReference("ignore_posts");
//                                    Query ignoreposts_query = ignoreRef.orderByChild("courier_id").equalTo(user.getUid());
//                                    ignoreposts_query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            dialog.dismiss();
//
//                                            if(dataSnapshot.exists()){
//                                                String ignore_post_id_db = null;
//                                                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
//                                                    ignore_post_id_db = snapshot.child("post_id").getValue().toString();
//                                                    posts_id_array.add(String.valueOf(ignore_post_id_db));
//                                                }
//
//
//                                                if(ignore_post_id_db != null){
//                                                    for(String data:posts_id_array){
//                                                        Pattern pattern = Pattern.compile( model.getPost_id() );
//                                                        Matcher matcher = pattern.matcher(data);
//                                                        while(matcher.find()){
//                                                            viewHolder.setLayout_GONE();
//
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//
//
//                                    accountRef = database.getReference("accounts");
//                                    Query account_query = accountRef.orderByChild("user_id").equalTo(model.getClient_id());
//                                    account_query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            String image_profile_db = null;
//                                            String first_name_db = null;
//
//
//
//                                            for(DataSnapshot snapshot: dataSnapshot.getChildren()){
//                                                image_profile_db = snapshot.child("image_profile").getValue().toString();
//                                                first_name_db = snapshot.child("first_name").getValue().toString();
//                                            }
//
//
//                                            viewHolder.setUser_Name(first_name_db);
//
//                                            Transformation transformation = new RoundedTransformationBuilder()
//                                                    .borderColor(Color.WHITE)
//                                                    .borderWidthDp(1)
//                                                    .cornerRadiusDp(100)
//                                                    .oval(false)
//                                                    .build();
//                                            Picasso.with(Courier_Home.this)
//                                                    .load(image_profile_db)
//                                                    .fit()
//                                                    .transform(transformation)
//                                                    .into(viewHolder.profileImage);
//
//
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                    viewHolder.send_request.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            transactionRef = database.getReference("transactions");
//                                            Query transaction_query = transactionRef.orderByChild("post_id").equalTo(model.getPost_id());
//
//
//                                            transaction_query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                                    if(dataSnapshot.exists()){
//                                                        new SweetAlertDialog(Courier_Home.this)
//                                                                .setTitleText("This post isn't available")
//                                                                .setConfirmText("OK")
//                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                                    @Override
//                                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                                                        viewHolder.setLayout_GONE();
//                                                                        sweetAlertDialog.dismissWithAnimation();
//                                                                    }
//                                                                })
//                                                                .show();
//                                                    }else if(!dataSnapshot.exists()){
//                                                        transactionRef = database.getReference("transactions");
//                                                        Query transaction_query = transactionRef.orderByChild("courier_id").equalTo(user.getUid()).limitToLast(1);
//
//                                                        transaction_query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                            @Override
//                                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                String transaction_status_db = null;
//
//                                                                if(dataSnapshot.exists()){
//                                                                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
//                                                                        transaction_status_db = snapshot.child("transaction_status").getValue().toString();
//                                                                    }
//
//
//                                                                    if(transaction_status_db.equals("pending") || transaction_status_db.equals("processing")){
//                                                                        new SweetAlertDialog(Courier_Home.this)
//                                                                                .setTitleText("Message")
//                                                                                .setContentText("Please finish your current transaction!")
//                                                                                .setConfirmText("OK")
//                                                                                .show();
//                                                                        System.out.println(transaction_status_db.toString());
//                                                                    }else if(transaction_status_db.equals("completed")){
//                                                                        new SweetAlertDialog(Courier_Home.this)
//                                                                                .setTitleText("You are requesting on this post.")
//                                                                                .setConfirmText("Request!")
//                                                                                .setCancelText("Cancel")
//                                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                                                    @Override
//                                                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                                                                        String client_id;
//                                                                                        String post_id;
//
//                                                                                        client_id = model.getClient_id();
//                                                                                        post_id = model.getPost_id();
//                                                                                        requestRef = database.getReference("courier_requests").push();
//
//                                                                                        requestRef.child("request_id").setValue(requestRef.getKey());
//                                                                                        requestRef.child("post_id").setValue(post_id);
//                                                                                        requestRef.child("client_id").setValue(client_id);
//                                                                                        requestRef.child("courier_id").setValue(user.getUid());
//                                                                                        requestRef.child("request_status").setValue("pending");
//                                                                                        requestRef.child("requested_at").setValue(ServerValue.TIMESTAMP);
//                                                                                        viewHolder.setLayout_GONE();
//
//                                                                                        sweetAlertDialog
//                                                                                                .setTitleText("Request submitted")
//                                                                                                .setConfirmText("OK")
//                                                                                                .setConfirmClickListener(null)
//                                                                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//
//
//
//                                                                                    }
//                                                                                })
//                                                                                .show();
//                                                                    }
//                                                                }else if(!dataSnapshot.exists()){
//                                                                    new SweetAlertDialog(Courier_Home.this)
//                                                                            .setTitleText("You are requesting on this post.")
//                                                                            .setConfirmText("Request!")
//                                                                            .setCancelText("Cancel")
//                                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                                                @Override
//                                                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                                                                    String client_id;
//                                                                                    String post_id;
//
//                                                                                    client_id = model.getClient_id();
//                                                                                    post_id = model.getPost_id();
//                                                                                    requestRef = database.getReference("courier_requests").push();
//
//                                                                                    requestRef.child("request_id").setValue(requestRef.getKey());
//                                                                                    requestRef.child("post_id").setValue(post_id);
//                                                                                    requestRef.child("client_id").setValue(client_id);
//                                                                                    requestRef.child("courier_id").setValue(user.getUid());
//                                                                                    requestRef.child("request_status").setValue("pending");
//                                                                                    requestRef.child("requested_at").setValue(ServerValue.TIMESTAMP);
//
//                                                                                    viewHolder.setLayout_GONE();
//
//                                                                                    sweetAlertDialog
//                                                                                            .setTitleText("Request submitted")
//                                                                                            .setConfirmText("OK")
//                                                                                            .setConfirmClickListener(null)
//                                                                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//
//                                                                                    // sweetAlertDialog.dismissWithAnimation();
//                                                                                }
//                                                                            })
//                                                                            .show();
//                                                                }
//
//                                                            }
//
//                                                            @Override
//                                                            public void onCancelled(DatabaseError databaseError) {
//
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                        }
//                                    });
//
//                                    viewHolder.ignore_post.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            new SweetAlertDialog(Courier_Home.this)
//                                                    .setTitleText("Want to ignore this? " )
//                                                    .setConfirmText("Yes!")
//                                                    .setCancelText("Cancel")
//                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                        @Override
//                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                                            requestRef = database.getReference("ignore_posts").push();
//                                                            requestRef.child("courier_id").setValue(user.getUid());
//                                                            requestRef.child("post_id").setValue(model.getPost_id());
//                                                            viewHolder.setLayout_GONE();
//                                                            sweetAlertDialog.dismissWithAnimation();
//                                                        }
//                                                    })
//                                                    .show();
//
//                                        }
//                                    });
//
//                                    viewHolder.fullDetails.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Intent intent = new Intent(Courier_Home.this, Courier_Posts_fullDetails.class);
//                                            intent.putExtra("post_id", model.getPost_id());
//                                            startActivity(intent);
//
//                                        }
//                                    });
//
//                                    viewHolder.viewProfile.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Intent intent = new Intent(Courier_Home.this, Courier_Post_viewProfile.class);
//                                            intent.putExtra("client_id", model.getClient_id());
//                                            startActivity(intent);
//                                        }
//                                    });
//                                }
//                            };
//                           mPosts.setAdapter(firebaseRecyclerAdapter);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//
//    } // end of getUsers_posts()

    public static class UsersPostsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Button send_request;
        Button ignore_post;
        Button fullDetails;
        TextView date;
        TextView viewProfile;
        ImageView profileImage;
        public  UsersPostsViewHolder(View itemView){
            super(itemView);
            mView=itemView;
            send_request = (Button) mView.findViewById(R.id.courier_view_post_request_btn);
            ignore_post = (Button) mView.findViewById(R.id.courier_view_post_ignore_btn);
            fullDetails = (Button) mView.findViewById(R.id.courier_view_post_cardview_btn);
            date = (TextView) mView.findViewById(R.id.courier_view_post_date_txtVw);
            profileImage =(ImageView) mView.findViewById(R.id.courier_view_post_profile_icon);
            viewProfile = (TextView) mView.findViewById(R.id.courier_view_post_fname_txtVw);
        }



        public void setPickup_location(String pickup_location){
            TextView pickup = (TextView) mView.findViewById(R.id.courier_view_post_the_pickup_location_txtVw);
            pickup.setText(pickup_location);
        }
        public void setDropoff_location(String dropoff_location){
            TextView dropoff = (TextView) mView.findViewById(R.id.courier_view_post_the_dropoff_txtVw);
            dropoff.setText(dropoff_location);
        }
        public void setUser_Name(String post_userName){
            TextView userName = (TextView) mView.findViewById(R.id.courier_view_post_fname_txtVw);
            userName.setText(post_userName);
        }
        public void setUser_Type(String post_userType){
            TextView userType = (TextView) mView.findViewById(R.id.courier_view_post_title_txtVw);
            userType.setText(post_userType);
        }
        public void setAmount(String post_amount){
            TextView postAmount = (TextView) mView.findViewById(R.id.courier_view_post_amount_to_amount_txtVw);
            postAmount.setText(post_amount);
        }
        public void setDistance(String post_distance){
            TextView postDistance = (TextView) mView.findViewById(R.id.courier_view_post_the_distance_txtVw);
            postDistance.setText(post_distance);
        }

        public void setLayout_GONE(){
            ConstraintLayout post_layout = (ConstraintLayout) mView.findViewById(R.id.postLayout);
            post_layout.setVisibility(View.GONE);
        }
        public void setLayout_VISIBLE(){
            ConstraintLayout post_layout = (ConstraintLayout) mView.findViewById(R.id.postLayout);
            post_layout.setVisibility(View.VISIBLE);
        }
    }




    public void onBackPressed()
    {
        // di mag gana kung e press ang backpress
    }

}
