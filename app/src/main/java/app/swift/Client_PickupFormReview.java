package app.swift;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;


public class Client_PickupFormReview extends AppCompatActivity {

    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef,postRef;
    private FirebaseAuth mAuth;


    TextView client_name,client_title,vehicle,pickup_location,dropoff_location,documentType,senderName,senderNumber,receiverName,receiverNumber,instructions,amount,distance,quantity;
    ImageView client_profileImg;
    String i_vehicle,i_pickup,i_dropoff,i_document,i_sender,i_senderNumber,i_instructions,i_quantity;
    String firstName = null;
    String lastName = null;
    String fullName;
    String image_profile = null;
    String user_type = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__pickup_form_review);

        MaterialDialog.Builder builder  = new MaterialDialog.Builder(Client_PickupFormReview.this)
                .content("Loading...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();


        client_name = (TextView) findViewById(R.id.deliveryReview_name_txtVw);
        client_title = (TextView) findViewById(R.id.deliveryReview_title_txtVw);
        vehicle = (TextView) findViewById(R.id.deliveryReview_vehicle_txtVw2);
        pickup_location = (TextView) findViewById(R.id.deliveryReview_pickup_location_txtVw);
        dropoff_location = (TextView) findViewById(R.id.deliveryReview_dropoff_location_txtVw);
        documentType = (TextView) findViewById(R.id.deliveryReview_documentype1_txtVw);
        quantity = (TextView) findViewById(R.id.deliveryReview_quantity1_txtVw);

        senderName = (TextView) findViewById(R.id.deliveryReview_sender1_txtVw);
        senderNumber = (TextView) findViewById(R.id.deliveryReview_senderNumber1_txtVw);
        receiverName = (TextView) findViewById(R.id.deliveryReview_receiverName1_txtVw);
        receiverNumber = (TextView) findViewById(R.id.deliveryReview_receiverNumber1_txtVw);


        instructions = (TextView) findViewById(R.id.deliveryReview_instructions1_txtVw);
        client_profileImg = (ImageView) findViewById(R.id.deliveryReview_profile_icon);
        amount = (TextView) findViewById(R.id.deliveryReview_money_txtVw);
        distance = (TextView) findViewById(R.id.deliveryReview_kilometer_txtVw) ;


        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("accounts");



        Intent delivery_form = getIntent();

        i_vehicle = delivery_form.getStringExtra("vehicle");
        i_pickup = delivery_form.getStringExtra("pickup_location");
        i_dropoff = delivery_form.getStringExtra("dropoff_location");
        i_document = delivery_form.getStringExtra("documentType");
        i_quantity = delivery_form.getStringExtra("quantity");
        i_sender = delivery_form.getStringExtra("senderName");
        i_senderNumber = delivery_form.getStringExtra("senderNumber");



        i_instructions = delivery_form.getStringExtra("instructions");

        vehicle.setText(i_vehicle);
        pickup_location.setText(i_pickup);
        dropoff_location.setText(i_dropoff);
        documentType.setText(i_document);
        senderName.setText(i_sender);
        senderNumber.setText(i_senderNumber);
        instructions.setText(i_instructions);
        quantity.setText(i_quantity);

        Query account_query = myRef.orderByChild("user_id").equalTo(user.getUid());

        account_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    firstName = snapshot.child("first_name").getValue().toString();
                    lastName = snapshot.child("last_name").getValue().toString();

                    image_profile = snapshot.child("image_profile").getValue().toString();
                    user_type = snapshot.child("user_type").getValue().toString();
                }

                fullName = firstName + " " + lastName;


                client_name.setText(fullName);
                client_title.setText(user_type);
                receiverName.setText(fullName);
                receiverNumber.setText("0" + user.getPhoneNumber().toString().substring(3));

                Transformation transformation = new RoundedTransformationBuilder()
                        // .borderColor(Color.WHITE)
                        //  .borderWidthDp(1)
                        .cornerRadiusDp(50)
                        .oval(false)
                        .build();
                Picasso.with(Client_PickupFormReview.this)
                        .load(image_profile)
                        .fit()
                        .transform(transformation)
                        .into(client_profileImg);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void post_delivery(View view){

        MaterialDialog.Builder builder  = new MaterialDialog.Builder(Client_PickupFormReview.this)
                .content("Posting...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();


        if(i_pickup.equals(null) || i_dropoff.equals(null)  || i_vehicle.equals(null) || i_document.equals(null) ||
                i_instructions.equals(null) || i_sender.equals(null) || user.getUid().equals(null)){
            dialog.dismiss();
            new SweetAlertDialog(Client_PickupFormReview.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("No data")
                    .show();
        }else{
            postRef = database.getReference().child("delivery_posts").push();

            postRef.child("posted_at").setValue(ServerValue.TIMESTAMP);
            postRef.child("pickup_location").setValue(i_pickup);
            postRef.child("dropoff_location").setValue(i_dropoff);
            postRef.child("delivery_type").setValue("pickup");
            postRef.child("vehicle").setValue(i_vehicle);
            postRef.child("post_id").setValue(postRef.getKey());
            postRef.child("post_status").setValue("pending");
            postRef.child("document_type").setValue(i_document);
            postRef.child("quantity").setValue(i_quantity);

            postRef.child("instructions").setValue(i_instructions);
            postRef.child("amount").setValue("15");
            postRef.child("distance").setValue("1");
            postRef.child("client_id").setValue(user.getUid());
            postRef.child("sender_name").setValue(i_sender);
            postRef.child("sender_number").setValue(i_senderNumber);
            postRef.child("receiver_name").setValue(fullName);
            postRef.child("receiver_number").setValue("0" + user.getPhoneNumber().toString().substring(3));


            new SweetAlertDialog(Client_PickupFormReview.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Posted successfully!")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dialog.dismiss();
                            Intent intent = new Intent(Client_PickupFormReview.this,Client_Home.class);
                            startActivity(intent);

                        }
                    })
                    .show();
        }

    }


    public void backToPickupForm(View view){
        Intent intent = new Intent(this, Client_PickupForm.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        // di mag gana kung e press ang backpress
    }
}
