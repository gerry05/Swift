package app.swift;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.Date;

import q.rorbin.badgeview.QBadgeView;

public class Client_Notifications extends AppCompatActivity {


    private FirebaseUser user;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private DatabaseReference notificationsRef, accountRef,ratings_feedbacksRef,requestRef;

    private RecyclerView mNotifications;

    String firstName_db,lastName_db,image_profile_db = null;
    // notifications
    String notification_status = null;
    String fullname;

    String notification_status_db= null;
    String request_status_db= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__notifications);


        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mNotifications = (RecyclerView) findViewById(R.id.client_notifications_recycleView);
        mNotifications.setHasFixedSize(true);
        mNotifications.setLayoutManager(layoutManager);

        getNotifications();


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);





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
                            if(request_status_db.equals("pending")){
                                ++seen_count;
                            }
                        }

                    }
                    System.out.println(seen_count);
                    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
                    BottomNavigationMenuView  bottomNavigationMenuView =
                            (BottomNavigationMenuView) navigation.getChildAt(0);
                    View v = bottomNavigationMenuView.getChildAt(2); // number of menu from left
                    new QBadgeView(Client_Notifications.this)
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
                switch (item.getItemId()) {

                    case R.id.ic_create:
                        Intent intent1 = new Intent(Client_Notifications.this, Client_Home.class);
                        startActivity(intent1);
                        break;

                    case R.id.ic_post:
                        Intent intent2 = new Intent(Client_Notifications.this, Client_Post.class);
                        startActivity(intent2);
                        break;

                    case R.id.ic_request:
                        Intent intent3 = new Intent(Client_Notifications.this, Client_Requests.class);
                        startActivity(intent3);
                        break;

                    case R.id.ic_notifications:
                        Intent intent4 = new Intent(Client_Notifications.this, Client_Notifications.class);
                        startActivity(intent4);
                        break;
                }


                return false;
            }
        });
    } // end of create

    public void getNotifications() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(Client_Notifications.this)
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .content("Loading...")
                .progress(true, 0);
        final MaterialDialog dialog = builder.build();
        dialog.show();

        notificationsRef = database.getReference("notifications");
        Query notification_query = notificationsRef.orderByChild("notification_receiver_user_id").equalTo(user.getUid());

        notification_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dialog.dismiss();

                   for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                      snapshot.child("notification_status").getRef().setValue("seen");
                   }


                }else{
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final FirebaseRecyclerAdapter<ClientNotifications, ClientNotificationsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ClientNotifications, ClientNotificationsViewHolder>
                (ClientNotifications.class, R.layout.client_notification_cardview, ClientNotificationsViewHolder.class, notification_query) {
            @Override
            protected void populateViewHolder(final ClientNotificationsViewHolder viewHolder, final ClientNotifications model, int position) {
              //  viewHolder.setMessage(model.getMessage());





                accountRef = database.getReference("accounts");
                Query accounts_query = accountRef.orderByChild("user_id").equalTo(model.getNotification_sender_user_id());
                accounts_query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            dialog.dismiss();

                          for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                              firstName_db = snapshot.child("first_name").getValue().toString();
                              lastName_db = snapshot.child("last_name").getValue().toString();
                              image_profile_db = snapshot.child("image_profile").getValue().toString();
                          }

                          fullname = firstName_db + " " + lastName_db;
                          viewHolder.setMessage(fullname + " "+ model.getMessage());

                            if(model.getCreated_at() == null){

                            }else{
                                DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy  hh:mm aaa");
                                Date netDate = (new Date(Long.parseLong(String.valueOf(model.getCreated_at()))));
                                viewHolder.date.setText(dateFormat.format(netDate));
                            }

                            Transformation transformation = new RoundedTransformationBuilder()
                                    //.borderColor(Color.WHITE)
                                    //.borderWidthDp(1)
                                    .cornerRadiusDp(50)
                                    .oval(false)
                                    .build();
                            Picasso.with(Client_Notifications.this)
                                    .load(image_profile_db)
                                    .fit()
                                    .transform(transformation)
                                    .into(viewHolder.profileImage);

                            if(model.getRating_feedback() == null || model.getRating_feedback().equals("completed")){

                            }else if(model.getRating_feedback().equals("pending")){
                                viewHolder.rateFeedback.setVisibility(View.VISIBLE);

                                viewHolder.rateFeedback.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Dialog dialog = new Dialog(Client_Notifications.this);
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
//                                                    new SweetAlertDialog(Client_Notifications.this, SweetAlertDialog.WARNING_TYPE)
//                                                            .setTitleText("Please fill-in the empty field!")
//                                                            .show();
                                                    ratings_feedbacksRef = database.getReference("ratings_feedbacks").push();
                                                    ratings_feedbacksRef.child("rating_feedback_id").setValue(ratings_feedbacksRef.getKey());
                                                    ratings_feedbacksRef.child("rate_by_user_id").setValue(model.getNotification_sender_user_id());
                                                    ratings_feedbacksRef.child("rating").setValue((int) ratingBar.getRating());
                                                    ratings_feedbacksRef.child("feedback").setValue("Empty message.");
                                                    ratings_feedbacksRef.child("respondent_user_id").setValue(user.getUid());
                                                    ratings_feedbacksRef.child("created_at").setValue(ServerValue.TIMESTAMP);

                                                    notificationsRef = database.getReference("notifications").push();
                                                    notificationsRef.child("notification_id").setValue(notificationsRef.getKey());
                                                    notificationsRef.child("notification_receiver_user_id").setValue(model.getNotification_sender_user_id());
                                                    notificationsRef.child("notification_sender_user_id").setValue(user.getUid());
                                                    notificationsRef.child("message").setValue("gave you a feedback and " + (int) ratingBar.getRating() + " stars.");
                                                    notificationsRef.child("created_at").setValue(ServerValue.TIMESTAMP);
                                                    notificationsRef.child("notification_status").setValue("pending");

                                                    notificationsRef = database.getReference("notifications").child(model.getNotification_id());
                                                    notificationsRef.child("rating_feedback").setValue("completed");

                                                    new SweetAlertDialog(Client_Notifications.this, SweetAlertDialog.SUCCESS_TYPE)
                                                            .setTitleText("Thanks for rating!")
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                    dialog.dismiss();
                                                                    Intent intent = getIntent();
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                    startActivityForResult(intent,0);
                                                                    sweetAlertDialog.dismissWithAnimation();
                                                                }
                                                            })
                                                            .show();
                                                }else{
                                                    ratings_feedbacksRef = database.getReference("ratings_feedbacks").push();
                                                    ratings_feedbacksRef.child("rating_feedback_id").setValue(ratings_feedbacksRef.getKey());
                                                    ratings_feedbacksRef.child("rate_by_user_id").setValue(model.getNotification_sender_user_id());
                                                    ratings_feedbacksRef.child("rating").setValue((int) ratingBar.getRating());
                                                    ratings_feedbacksRef.child("feedback").setValue(feedback_eTxt.getText().toString());
                                                    ratings_feedbacksRef.child("respondent_user_id").setValue(user.getUid());
                                                    ratings_feedbacksRef.child("created_at").setValue(ServerValue.TIMESTAMP);

                                                    notificationsRef = database.getReference("notifications").push();
                                                    notificationsRef.child("notification_id").setValue(notificationsRef.getKey());
                                                    notificationsRef.child("notification_receiver_user_id").setValue(model.getNotification_sender_user_id());
                                                    notificationsRef.child("notification_sender_user_id").setValue(user.getUid());
                                                    notificationsRef.child("message").setValue("gave you a feedback and " + (int) ratingBar.getRating() + " stars.");
                                                    notificationsRef.child("created_at").setValue(ServerValue.TIMESTAMP);
                                                    notificationsRef.child("notification_status").setValue("pending");

                                                    notificationsRef = database.getReference("notifications").child(model.getNotification_id());
                                                    notificationsRef.child("rating_feedback").setValue("completed");

                                                    new SweetAlertDialog(Client_Notifications.this, SweetAlertDialog.SUCCESS_TYPE)
                                                            .setTitleText("Thanks for rating!")
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                    dialog.dismiss();
                                                                    Intent intent = getIntent();
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                    startActivityForResult(intent,0);
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


                                        client_fullName.setText("Please rate " + fullname);
                                        Transformation transformation = new RoundedTransformationBuilder()
                                                // .borderColor(Color.WHITE)
                                                //  .borderWidthDp(1)
                                                .cornerRadiusDp(100)
                                                .oval(false)
                                                .build();
                                        Picasso.with(Client_Notifications.this)
                                                .load(image_profile_db)
                                                .fit()
                                                .transform(transformation)
                                                .into(client_profileImg);

                                    }
                                });
                            }
                        }else{
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mNotifications.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ClientNotificationsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView date;
        ImageView profileImage;
        Button rateFeedback;

        public ClientNotificationsViewHolder(View itemView){
            super(itemView);
            mView=itemView;
            profileImage =(ImageView) mView.findViewById(R.id.client_notification_profile_icon);
            date = (TextView) mView.findViewById(R.id.client_notification_date_txtVw);
            rateFeedback = (Button) mView.findViewById(R.id.client_notification_rateFeedback_btn);
        }

        public void setMessage(String message){
            TextView message_txt = (TextView) mView.findViewById(R.id.client_notification_message_txtVw);
            message_txt.setText(message);
        }
    }

}

