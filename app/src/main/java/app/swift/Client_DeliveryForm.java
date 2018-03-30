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



public class Client_DeliveryForm extends AppCompatActivity {


    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference postRef;
    private FirebaseAuth mAuth;

    EditText pickup_location, dropoff_location, documentType, receiverName, instructions,quantity,receiverNumber;
    RadioGroup vehicle_rGroup;
    RadioButton bike_rBtn, motorcycle_rBtn;
    ConstraintLayout deliveryForm_layout;

    List<String> posts = new ArrayList<String>(); // Result will be holded Here
    String user_id = null;
    String post_status = null;


    String[] items = new String[]{"Certificate","Project","Other"};
    String item="";
    ArrayAdapter <String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__delivery_form);

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


        MaterialDialog.Builder builder  = new MaterialDialog.Builder(Client_DeliveryForm.this)
                .content("Loading...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();


        pickup_location = (EditText) findViewById(R.id.deliveryForm_pickupLocation_eTxt);
        dropoff_location = (EditText) findViewById(R.id.deliveryForm_dropoffLocation_eTxt);

        receiverName = (EditText) findViewById(R.id.deliveryForm_receiverName_eTxt);
        receiverNumber = (EditText) findViewById(R.id.deliveryForm_receiverNumber_eTxt);
        instructions = (EditText) findViewById(R.id.deliveryForm_instructions_eTxt);
        quantity = (EditText) findViewById(R.id.deliveryForm_quantity_eTxt);
        vehicle_rGroup = (RadioGroup) findViewById(R.id.deliveryForm_vehicle_rGroup);
        bike_rBtn = (RadioButton) findViewById(R.id.deliveryForm_vehicle_bike_rBtn);
        motorcycle_rBtn = (RadioButton) findViewById(R.id.deliveryForm_vehicle_motorcycle_rBtn);
        deliveryForm_layout = (ConstraintLayout) findViewById(R.id.clientDeliveryForm_layout);

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
                        deliveryForm_layout.setVisibility(View.VISIBLE);
                        Log.d("delivery", "wala pa post");
                    }else if( user_id.equals(user.getUid()) && data.equals("pending") || user_id.equals(user.getUid()) && data.equals("processing")){
                        dialog.dismiss();
                        Log.d("delivery", "may post na");
                        deliveryForm_layout.setVisibility(View.GONE);
                        Intent intent = new Intent(Client_DeliveryForm.this,Client_Post.class);
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
                TextUtils.isEmpty(receiverName.getText().toString()) ||  TextUtils.isEmpty(receiverNumber.getText().toString()) || receiverNumber.getText().toString().length() <= 10 || TextUtils.isEmpty(quantity.getText().toString()) ||
                TextUtils.isEmpty(instructions.getText().toString())) {

            new SweetAlertDialog(Client_DeliveryForm.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Please fill in all fields")
                    .show();
        } else {
            Intent intent = new Intent(this, Client_DeliveryFormReview.class);
            intent.putExtra("pickup_location", pickup_location.getText().toString());
            intent.putExtra("dropoff_location", dropoff_location.getText().toString());
            intent.putExtra("documentType", item);
            intent.putExtra("quantity",quantity.getText().toString());
            intent.putExtra("receiverName", receiverName.getText().toString());
            intent.putExtra("receiverNumber", receiverNumber.getText().toString());
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
