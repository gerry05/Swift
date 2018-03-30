package app.swift;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import q.rorbin.badgeview.QBadgeView;

public class Client_Requests extends AppCompatActivity {

    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef,requestsRef,accountRef,transactionRef, deliveryRef,notificationsRef;
    private FirebaseAuth mAuth;

    private RecyclerView mRequests;

    String image_profile_db = null;
    String first_name_db = null;
    String last_name_db = null;
    String rating_db = null;
    String request_status_db = null;
    String request_id_db = null;
    String transaction_postId_db = null;
    String transaction_requestId_db = null;
    String notification_status_db= null;
    List<String> request_status_array = new ArrayList<String>(); // Result will be holded Here
    List<String> transaction_postId_array = new ArrayList<String>(); // Result will be holded Here
    List<String> requestId_array = new ArrayList<String>(); // Result will be holded Here
    List<String> exclude_transaction_requestId_array = new ArrayList<String>(); // Result will be holded Here



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__requests);




        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mRequests = (RecyclerView) findViewById(R.id.client_requests_recycleView);
        mRequests.setHasFixedSize(true);
        mRequests.setLayoutManager(layoutManager);

        client_getRequests();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
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
                    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
                    BottomNavigationMenuView bottomNavigationMenuView =
                            (BottomNavigationMenuView) navigation.getChildAt(0);
                    View v = bottomNavigationMenuView.getChildAt(3); // number of menu from left
                    new QBadgeView(Client_Requests.this)
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
                        Intent intent1 = new Intent(Client_Requests.this, Client_Home.class);
                        startActivity(intent1);
                        break;

                    case R.id.ic_post:
                        Intent intent2 = new Intent(Client_Requests.this, Client_Post.class);
                        startActivity(intent2);
                        break;

                    case R.id.ic_request:
                        Intent intent3 = new Intent(Client_Requests.this, Client_Requests.class);
                        startActivity(intent3);
                        break;

                    case R.id.ic_notifications:
                        Intent intent4 = new Intent(Client_Requests.this, Client_Notifications.class);
                        startActivity(intent4);
                        break;
                }


                return false;
            }
        });
    }


    public void client_getRequests(){

        requestsRef = database.getReference("courier_requests");

        final Query getCourier_requests = requestsRef.orderByChild("client_id").equalTo(user.getUid());


        getCourier_requests.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        snapshot.child("notification_status").getRef().setValue("seen");
                    }



                    FirebaseRecyclerAdapter<Client_postRequests,Client_CourierRequestsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Client_postRequests, Client_CourierRequestsViewHolder>
                            (Client_postRequests.class, R.layout.client_request_cardview, Client_CourierRequestsViewHolder.class, getCourier_requests) {
                        @Override
                        protected void populateViewHolder(final Client_CourierRequestsViewHolder viewHolder, final Client_postRequests model, int position) {

                            if(model.getRequested_at() == null){

                            }else{
                                DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy  hh:mm aaa");
                                Date netDate = (new Date(Long.parseLong(String.valueOf(model.getRequested_at()))));
                                viewHolder.date.setText(dateFormat.format(netDate));
                            }

                            if(model.getRequest_status().equals("accepted") || model.getRequest_status().equals("declined")){
                                viewHolder.setLayout_GONE();
                            }else if(model.getRequest_status().equals("pending")){
                               viewHolder.setLayout_VISIBLE();
                            }

                            accountRef = database.getReference("accounts");
                            Query account_query = accountRef.orderByChild("user_id").equalTo(model.getCourier_id());
                            account_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String fullname;

                                if(dataSnapshot.exists()){
                                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                        image_profile_db = snapshot.child("image_profile").getValue().toString();
                                        first_name_db = snapshot.child("first_name").getValue().toString();
                                        last_name_db = snapshot.child("last_name").getValue().toString();

                                        rating_db = snapshot.child("rating").getValue().toString();
                                    }

                                    fullname = first_name_db + " " + last_name_db;
                                    viewHolder.setUser_Name(fullname);
                                    viewHolder.ratingBar.setRating(Integer.parseInt(rating_db));

                                    Transformation transformation = new RoundedTransformationBuilder()
                                            //.borderColor(Color.WHITE)
                                            //.borderWidthDp(1)
                                            .cornerRadiusDp(100)
                                            .oval(false)
                                            .build();
                                    Picasso.with(Client_Requests.this)
                                            .load(image_profile_db)
                                            .fit()
                                            .transform(transformation)
                                            .into(viewHolder.profileImage);
                                }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            viewHolder.viewProfile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Client_Requests.this, Client_Request_viewProfile.class);
                                    intent.putExtra("courier_id",model.getCourier_id());
                                    startActivity(intent);
                                }
                            });


                            viewHolder.hire_courier.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    transactionRef = database.getReference("transactions");
                                    Query transaction_query = transactionRef.orderByChild("post_id").equalTo(model.getPost_id());

                                    transaction_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                                transaction_requestId_db = snapshot.child("request_id").getValue().toString();
                                                transaction_postId_db = snapshot.child("post_id").getValue().toString();
                                                transaction_postId_array.add(String.valueOf(transaction_postId_db));
                                            }

                                            if(dataSnapshot.exists()){
                                                System.out.println("Transaction: Ga exist");

                                                new SweetAlertDialog(Client_Requests.this, SweetAlertDialog.WARNING_TYPE)
                                                        .setTitleText("You already hired someone")
                                                        .setConfirmText("OK")
                                                        .show();
                                            }else if(!dataSnapshot.exists()){
                                                System.out.println("Transaction: Wala ga exist");

                                                transactionRef = database.getReference("transactions").push();
                                                transactionRef.child("transaction_id").setValue(transactionRef.getKey());
                                                transactionRef.child("post_id").setValue(model.getPost_id());
                                                transactionRef.child("client_id").setValue(model.getClient_id());
                                                transactionRef.child("courier_id").setValue(model.getCourier_id());
                                                transactionRef.child("request_id").setValue(model.getRequest_id());
                                                transactionRef.child("transaction_status").setValue("pending");

                                                requestsRef = database.getReference("courier_requests").child(model.getRequest_id());
                                                requestsRef.child("request_status").setValue("accepted");

                                                deliveryRef = database.getReference("delivery_posts").child(model.getPost_id());
                                                deliveryRef.child("post_status").setValue("processing");

                                                notificationsRef = database.getReference("notifications").push();
                                                notificationsRef.child("notification_id").setValue(notificationsRef.getKey());
                                                notificationsRef.child("notification_receiver_user_id").setValue(model.getCourier_id());
                                                notificationsRef.child("notification_sender_user_id").setValue(user.getUid());
                                                notificationsRef.child("message").setValue("hired you.");
                                                notificationsRef.child("created_at").setValue(ServerValue.TIMESTAMP);
                                                notificationsRef.child("notification_status").setValue("pending");

                                                new SweetAlertDialog(Client_Requests.this, SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("You hired " + first_name_db)
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                Intent intent = new Intent(Client_Requests.this,Client_Post.class);
                                                                startActivity(intent);
                                                                sweetAlertDialog.dismissWithAnimation();
                                                            }
                                                        })
                                                        .show();
                                                requestsRef = database.getReference("courier_requests");
                                                Query request_query = requestsRef.orderByChild("post_id").equalTo(model.getPost_id());

                                                request_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String courier_request_id_db = null;
                                                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                                            courier_request_id_db = snapshot.child("request_id").getValue().toString();
                                                            requestId_array.add(String.valueOf(courier_request_id_db));

                                                        }

                                                        for(String data:requestId_array){
                                                            if(!data.equals(model.getRequest_id())){
                                                                requestsRef = database.getReference("courier_requests").child(data);
                                                                requestsRef.child("request_status").setValue("declined");
                                                            }
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
                            viewHolder.decline_courier.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    viewHolder.setLayout_GONE();
                                    requestsRef = database.getReference("courier_requests").child(model.getRequest_id());
                                    requestsRef.child("request_status").setValue("declined");
                                    new SweetAlertDialog(Client_Requests.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("You declined " + first_name_db)
                                            .setConfirmText("OK")
                                            .show();

                                }
                            });

                        }
                    };
                    mRequests.setAdapter(firebaseRecyclerAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class Client_CourierRequestsViewHolder extends  RecyclerView.ViewHolder{
        View mView;
        ImageView profileImage;
        Button hire_courier;
        Button decline_courier;
        TextView date;
        RelativeLayout requestLayout;
        RatingBar ratingBar;
        TextView viewProfile;
        public Client_CourierRequestsViewHolder(View itemView){
            super(itemView);
            mView=itemView;
            profileImage =(ImageView) mView.findViewById(R.id.courier_request_profile_icon);
            hire_courier = (Button) mView.findViewById(R.id.courier_request_hire_btn);
            decline_courier = (Button) mView.findViewById(R.id.courier_request_decline_btn);
            date = (TextView) mView.findViewById(R.id.client_request_time_txtVw);
            requestLayout = mView.findViewById(R.id.requestLayout);
            ratingBar = (RatingBar) mView.findViewById(R.id.client_request_rating_bar);
            viewProfile = (TextView) mView.findViewById(R.id.courier_name_txtVw);

        }

        public void setUser_Name(String post_userName){
            TextView userName = (TextView) mView.findViewById(R.id.courier_name_txtVw);
            userName.setText(post_userName);
        }

        public void setLayout_GONE(){
            RelativeLayout post_layout = (RelativeLayout) mView.findViewById(R.id.requestLayout);
            post_layout.setVisibility(View.GONE);
        }
        public void setLayout_VISIBLE(){
            RelativeLayout post_layout = (RelativeLayout) mView.findViewById(R.id.requestLayout);
            post_layout.setVisibility(View.VISIBLE);
        }
    }

}
