package app.swift;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import q.rorbin.badgeview.QBadgeView;


public class Client_Home extends AppCompatActivity {

    private FirebaseUser user;
    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    DatabaseReference myRef,ratings_feedbacksRef,accountsRef,notificationsRef,requestRef;



    String notification_status_db= null;
    String request_status_db= null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__home);


        database = FirebaseDatabase.getInstance().getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
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
                        if(snapshot.child("notification_status").exists()) {
                            notification_status_db = snapshot.child("notification_status").getValue().toString();
                            if (notification_status_db.equals("pending")) {
                                ++seen_count;
                            }
                        }
                    }
                    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
                    BottomNavigationMenuView  bottomNavigationMenuView =
                            (BottomNavigationMenuView) navigation.getChildAt(0);
                    View v = bottomNavigationMenuView.getChildAt(3); // number of menu from left
                        new QBadgeView(Client_Home.this)
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
                        if(snapshot.child("notification_status").exists()){
                            request_status_db = snapshot.child("notification_status").getValue().toString();
                            if(request_status_db.equals("pending")) {
                                ++seen_count;
                            }
                        }
                    }
                    System.out.println(seen_count);
                    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
                    BottomNavigationMenuView  bottomNavigationMenuView =
                            (BottomNavigationMenuView) navigation.getChildAt(0);
                    View v = bottomNavigationMenuView.getChildAt(2); // number of menu from left
                    new QBadgeView(Client_Home.this)
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
                        Intent intent1 = new Intent(Client_Home.this, Client_Home.class);
                        startActivity(intent1);
                        break;

                    case R.id.ic_post:
                        Intent intent2 = new Intent(Client_Home.this, Client_Post.class);
                        startActivity(intent2);
                        break;

                    case R.id.ic_request:
                        Intent intent3 = new Intent(Client_Home.this, Client_Requests.class);
                        startActivity(intent3);
                        break;

                    case R.id.ic_notifications:
                        Intent intent4 = new Intent(Client_Home.this, Client_Notifications.class);
                        startActivity(intent4);
                        break;
                }

                return false;
            }
        });

    }// end of create



    public void goToDeliveryForm(View view){
        Intent intent = new Intent(this,Client_DeliveryForm.class);
        startActivity(intent);
    }

    public void goToPickupForm(View view){
        Intent intent = new Intent(this,Client_PickupForm.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
       // di mag gana kung e press ang backpress
    }

}
