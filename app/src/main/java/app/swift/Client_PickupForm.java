package app.swift;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

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
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;




public class Client_PickupForm extends AppCompatActivity {


    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference postRef;
    private FirebaseAuth mAuth;

    EditText pickup_location, dropoff_location, documentType, senderName,senderNumber, instructions,quantity;
    RadioGroup vehicle_rGroup;
    RadioButton bike_rBtn, motorcycle_rBtn;
    ConstraintLayout pickupForm_layout;

    List<String> posts = new ArrayList<String>(); // Result will be holded Here
    String user_id = null;
    String post_status = null;

    String[] items = new String[]{"Certificate","Project","Other"};
    String item="";
    ArrayAdapter <String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__pickup_form);


        Spinner document_ddown = findViewById(R.id.spinner1);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        document_ddown.setAdapter(adapter);
        document_ddown.setSelection(0);

        document_ddown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        item = "Certificate";
                        break;
                    case 1:
                        item = "School Record";
                        break;
                    case 2:
                        item = "Other";
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        MaterialDialog.Builder builder  = new MaterialDialog.Builder(Client_PickupForm.this)
                .content("Loading...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();


        pickup_location = (EditText) findViewById(R.id.pickupForm_pickupLocation_eTxt);
        dropoff_location = (EditText) findViewById(R.id.pickupForm_dropoffLocation_eTxt);
        quantity = (EditText) findViewById(R.id.pickupForm_quantity_eTxt);
        senderName = (EditText) findViewById(R.id.pickupForm_sender_eTxt);
        senderNumber = (EditText) findViewById(R.id.pickupForm_senderNumber_eTxt);

        instructions = (EditText) findViewById(R.id.pickupForm_instructions_eTxt);
        vehicle_rGroup = (RadioGroup) findViewById(R.id.pickupForm_vehicle_rGroup);
        bike_rBtn = (RadioButton) findViewById(R.id.pickupForm_vehicle_bike_rBtn);
        motorcycle_rBtn = (RadioButton) findViewById(R.id.pickupForm_vehicle_motorcycle_rBtn);
        pickupForm_layout = (ConstraintLayout) findViewById(R.id.clientDeliveryForm_layout);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        postRef = database.getReference("delivery_posts");


        Query post_query = postRef.orderByChild("client_id").equalTo(user.getUid());

        post_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        user_id = snapshot.child("client_id").getValue().toString();
                        post_status = snapshot.child("post_status").getValue().toString();
                        posts.add(String.valueOf(post_status));
                    }
                }

                for(String data:posts){
                    if(user_id == null || !user_id.equals(user.getUid()) || user_id.equals(user.getUid()) && data.equals("completed")){
                        dialog.dismiss();
                        pickupForm_layout.setVisibility(View.VISIBLE);
                        Log.d("delivery", "wala pa post");
                    }else if( user_id.equals(user.getUid()) && data.equals("pending") || user_id.equals(user.getUid()) && data.equals("processing")){
                        dialog.dismiss();
                        Log.d("delivery", "may post na");
                        pickupForm_layout.setVisibility(View.GONE);
                        Intent intent = new Intent(Client_PickupForm.this,Client_Post.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void backToHome(View view) {
        Intent intent = new Intent(this, Client_Home.class);
        startActivity(intent);
    }

    public void goToDeliveryFormReview(View view) {

        if (TextUtils.isEmpty(pickup_location.getText().toString()) || TextUtils.isEmpty(dropoff_location.getText().toString()) ||
                TextUtils.isEmpty(senderName.getText().toString())  ||  TextUtils.isEmpty(senderNumber.getText().toString()) ||senderNumber.getText().toString().length() <= 10 || TextUtils.isEmpty(quantity.getText().toString()) ||
                TextUtils.isEmpty(instructions.getText().toString())) {

            new SweetAlertDialog(Client_PickupForm.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Please fill in all fields")
                    .show();

        } else {
            Intent intent = new Intent(this, Client_PickupFormReview.class);
            intent.putExtra("pickup_location", pickup_location.getText().toString());
            intent.putExtra("dropoff_location", dropoff_location.getText().toString());
            intent.putExtra("documentType",item);
            intent.putExtra("quantity",quantity.getText().toString());

            intent.putExtra("senderName", senderName.getText().toString());
            intent.putExtra("senderNumber", senderNumber.getText().toString());

            intent.putExtra("instructions", instructions.getText().toString());

            if (bike_rBtn.isChecked()) {
                intent.putExtra("vehicle", "Bicycle");
            } else if (motorcycle_rBtn.isChecked()) {
                intent.putExtra("vehicle", "Motorcycle");
            }

            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed()
    {
        // di mag gana kung e press ang backpress
    }

}
