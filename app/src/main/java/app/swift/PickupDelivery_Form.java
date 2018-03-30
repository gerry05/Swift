package app.swift;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;


public class PickupDelivery_Form extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    EditText pickup,dropoff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_delivery__form);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        pickup = (EditText) findViewById(R.id.pickup_eTxt);
        dropoff = (EditText) findViewById(R.id.dropoff_eTxt);

    }


    public void post_PickupInfo(View view){


      myRef = database.getReference("pickup_posts");

      Query posts_query = myRef.orderByChild("user_id").equalTo(mAuth.getCurrentUser().getUid().toString());

      posts_query.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              String user_id = null;

              for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                  user_id = snapshot.child("user_id").getValue().toString();
              }

              if (user_id == null || !user_id.equals(mAuth.getCurrentUser().getUid().toString())) {
                 myRef = database.getReference("pickup_posts").push();

                 myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         myRef.child("user_id").setValue(mAuth.getCurrentUser().getUid());
                         myRef.child("pickup_location").setValue(pickup.getText().toString());
                         myRef.child("dropoff_location").setValue(dropoff.getText().toString());
                         myRef.child("delivery_type").setValue("pickup");
                         myRef.child("vehicle").setValue("Bicycle");
                         myRef.child("post_id").setValue(myRef.getKey());
                         myRef.child("post_status").setValue("pending");

                         new SweetAlertDialog(PickupDelivery_Form.this, SweetAlertDialog.SUCCESS_TYPE)
                                 .setTitleText("Posted successfully!")
                                 .show();
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });
              } else if(user_id.equals(mAuth.getCurrentUser().getUid().toString())) {
                  new SweetAlertDialog(PickupDelivery_Form.this, SweetAlertDialog.ERROR_TYPE)
                          .setTitleText("You've already posted.")
                          .show();
              }

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });

    }


    public void backToUserHome(View view){
        Intent intent = new Intent(PickupDelivery_Form.this, Client_Home.class);
        startActivity(intent);

    }
}
