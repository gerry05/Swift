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

public class Courier_Notifications extends AppCompatActivity {


    private FirebaseUser user;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private DatabaseReference notificationsRef, accountRef;

    private RecyclerView mNotifications;

    String firstName_db,lastName_db,image_profile_db = null;
    String notification_status_db = null;
    String fullname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier__notifications);


        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mNotifications = (RecyclerView) findViewById(R.id.courier_notifications_recycleView);
        mNotifications.setHasFixedSize(true);
        mNotifications.setLayoutManager(layoutManager);

        getNotifications();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.courier_bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.ic_post:
                        Intent intent1 = new Intent(Courier_Notifications.this, Courier_Home.class);
                        startActivity(intent1);
                        break;

                    case R.id.ic_transaction:
                        Intent intent2 = new Intent(Courier_Notifications.this, Courier_Transaction.class);
                        startActivity(intent2);
                        break;

                    case R.id.ic_request:
                        Intent intent3 = new Intent(Courier_Notifications.this, Courier_Requests.class);
                        startActivity(intent3);
                        break;

                    case R.id.ic_notifications:
                        Intent intent4 = new Intent(Courier_Notifications.this, Courier_Notifications.class);
                        startActivity(intent4);
                        break;
                }

                return false;
            }
        });
    }// end of create


    public void getNotifications() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(Courier_Notifications.this)
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .content("Loading...")
                .progress(true, 0);
        final MaterialDialog dialog = builder.build();
        dialog.show();

        notificationsRef = database.getReference("notifications");
        final Query notification_query = notificationsRef.orderByChild("notification_receiver_user_id").equalTo(user.getUid());

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


        final FirebaseRecyclerAdapter<CourierNotifications,CourierNotificationsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CourierNotifications, CourierNotificationsViewHolder>
                (CourierNotifications.class, R.layout.courier_notification_cardview,CourierNotificationsViewHolder.class, notification_query) {
            @Override
            protected void populateViewHolder(final CourierNotificationsViewHolder viewHolder, final CourierNotifications model, int position) {
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
                                    .cornerRadiusDp(50)
                                    .oval(false)
                                    .build();
                            Picasso.with(Courier_Notifications.this)
                                    .load(image_profile_db)
                                    .fit()
                                    .transform(transformation)
                                    .into(viewHolder.profileImage);

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

    public static class CourierNotificationsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView date;
        ImageView profileImage;
        Button rateFeedback;

        public CourierNotificationsViewHolder(View itemView){
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
