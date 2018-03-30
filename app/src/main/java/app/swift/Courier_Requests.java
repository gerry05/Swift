package app.swift;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import q.rorbin.badgeview.QBadgeView;

public class Courier_Requests extends AppCompatActivity {


    private FirebaseUser user;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private DatabaseReference myRef,postsRef, requestRef,accountRef,ignoreRef,transactionRef,notificationsRef;

    private RecyclerView mRequests;
    String pickup_db,dropoff_db,amount_db,distance_db,posted_at_db = null;
    String notification_status_db= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier__requests);
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mRequests=(RecyclerView) findViewById(R.id.courier_requests_recycleView);
        mRequests.setHasFixedSize(true);
        mRequests.setLayoutManager(layoutManager);

        getCourier_requests();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.courier_bottomNavView_Bar);
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
                    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.courier_bottomNavView_Bar);
                    BottomNavigationMenuView bottomNavigationMenuView =
                            (BottomNavigationMenuView) navigation.getChildAt(0);
                    View v = bottomNavigationMenuView.getChildAt(3); // number of menu from left
                    new QBadgeView(Courier_Requests.this)
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
                        Intent intent1 = new Intent(Courier_Requests.this, Courier_Home.class);
                        startActivity(intent1);
                        break;

                    case R.id.ic_transaction:
                        Intent intent2 = new Intent(Courier_Requests.this, Courier_Transaction.class);
                        startActivity(intent2);
                        break;

                    case R.id.ic_request:
                        Intent intent3 = new Intent(Courier_Requests.this, Courier_Requests.class);
                        startActivity(intent3);
                        break;

                    case R.id.ic_notifications:
                        Intent intent4 = new Intent(Courier_Requests.this, Courier_Notifications.class);
                        startActivity(intent4);
                        break;
                }

                return false;
            }
        });
    } // end of create


    public void getCourier_requests(){
        MaterialDialog.Builder builder  = new MaterialDialog.Builder(Courier_Requests.this)
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .content("Loading...")
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();

        requestRef = database.getReference("courier_requests");
        final Query request_query = requestRef.orderByChild("courier_id").equalTo(user.getUid());

        request_query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    final FirebaseRecyclerAdapter<CourierRequests, CourierRequestsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CourierRequests, CourierRequestsViewHolder>
                            (CourierRequests.class,R.layout.courier_requests_cardview,CourierRequestsViewHolder.class, request_query) {
                        @Override
                        protected void populateViewHolder(final CourierRequestsViewHolder viewHolder, final CourierRequests model, int position) {

                            if(model.getRequest_status().equals("pending")){
                                viewHolder.setLayout_VISIBLE();
                            }else if(!model.getRequest_status().equals("pending")){
                                viewHolder.setLayout_GONE();
                            }

                            accountRef = database.getReference("accounts");
                            Query account_query = accountRef.orderByChild("user_id").equalTo(model.getClient_id());
                            account_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    dialog.dismiss();
                                    String image_profile_db = null;
                                    String first_name_db = null;
                                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                        image_profile_db = snapshot.child("image_profile").getValue().toString();
                                        first_name_db = snapshot.child("first_name").getValue().toString();
                                    }


                                    viewHolder.setUser_Name(first_name_db);
                                    // viewHolder.setUser_profileImage(getApplicationContext(), image_profile_db);


                                    Transformation transformation = new RoundedTransformationBuilder()
                                            //.borderColor(Color.WHITE)
                                            //.borderWidthDp(1)
                                            .cornerRadiusDp(100)
                                            .oval(false)
                                            .build();
                                    Picasso.with(Courier_Requests.this)
                                            .load(image_profile_db)
                                            .fit()
                                            .transform(transformation)
                                            .into(viewHolder.profileImage);


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            postsRef = database.getReference("delivery_posts");
                            Query post_query = postsRef.orderByChild("post_id").equalTo(model.getPost_id());

                            post_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                            pickup_db = snapshot.child("pickup_location").getValue().toString();
                                            dropoff_db = snapshot.child("dropoff_location").getValue().toString();
                                            amount_db = snapshot.child("amount").getValue().toString();
                                            distance_db = snapshot.child("distance").getValue().toString();
                                            posted_at_db = snapshot.child("posted_at").getValue().toString();
                                        }

                                        viewHolder.setPickup_location(pickup_db);
                                        viewHolder.setDropoff_location(dropoff_db);
                                        viewHolder.setAmount("₱" + amount_db);
                                        viewHolder.setDistance(distance_db + "km");

                                        DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy  hh:mm aaa");
                                        Date netDate = (new Date(Long.parseLong(posted_at_db)));
                                        viewHolder.date.setText(dateFormat.format(netDate));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            viewHolder.cancel_post.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    new SweetAlertDialog(Courier_Requests.this,SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Are you sure?")
                                            .setConfirmText("Yes, I'm sure!")
                                            .setCancelText("Cancel")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    requestRef.child(model.getRequest_id()).removeValue();
                                                    sweetAlertDialog.dismissWithAnimation();
                                                }
                                            })
                                            .show();

                                }
                            });

                            viewHolder.fullDetails.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Courier_Requests.this, Courier_Requests_Posts_fullDetails.class);
                                    intent.putExtra("post_id", model.getPost_id());
                                    intent.putExtra("request_id", model.getRequest_id());
                                    startActivity(intent);
                                }
                            });


                        }
                    };
                    mRequests.setAdapter(firebaseRecyclerAdapter);
                }else{
                    dialog.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    } // end of getCourier_requests()

    public static class CourierRequestsViewHolder extends  RecyclerView.ViewHolder{
        View mView;
        Button cancel_post;
        Button fullDetails;
        ImageView profileImage;
        TextView date;
        public CourierRequestsViewHolder(View itemView){
            super(itemView);
            mView=itemView;
            cancel_post = (Button) mView.findViewById(R.id.courier_view_post_cancel_btn);
            fullDetails = (Button) mView.findViewById(R.id.courier_view_post_cardview_btn);
            profileImage =(ImageView) mView.findViewById(R.id.courier_view_post_profile_icon);
            date = (TextView) mView.findViewById(R.id.courier_view_post_date_txtVw);
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


}
