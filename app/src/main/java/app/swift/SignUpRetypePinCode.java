package app.swift;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class SignUpRetypePinCode extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_retype_pin_code);

        Intent signup_profile = getIntent();

        final String fname = signup_profile.getStringExtra("fname");
        final String lname = signup_profile.getStringExtra("lname");
        final String address = signup_profile.getStringExtra("address");
        final String gender = signup_profile.getStringExtra("gender");

        final String phone_number = signup_profile.getStringExtra("phone_number");

        final String pincode_1 = signup_profile.getStringExtra("pincode_1");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("accounts").child("63" + phone_number);
        mAuth = FirebaseAuth.getInstance();

        Pinview pinview = (Pinview)findViewById(R.id.pinView);
        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean b){


                if(pincode_1.toString().equals(pinview.getValue().toString())){
                    myRef.child("first_name").setValue(fname).toString();
                    myRef.child("last_name").setValue(lname).toString();
                    myRef.child("address").setValue(address).toString();
                    myRef.child("gender").setValue(gender).toString();
                    myRef.child("phone_number").setValue("63"+ phone_number).toString();
                    myRef.child(mAuth.getCurrentUser().getUid().toString()).setValue(true);
                    myRef.child("user_id").setValue(mAuth.getCurrentUser().getUid().toString());
                    myRef.child("user_type").setValue("Client");
                    myRef.child("pin_code").setValue(pinview.getValue().toString());
                    myRef.child("image_profile").setValue("https://firebasestorage.googleapis.com/v0/b/swift-a72e1.appspot.com/o/Photos%2FProfilePictures%2Ftemporary%20profile.png?alt=media&token=aa85f32b-9b93-42e7-849a-20cbed1a58a9");
                    myRef.child("created_at").setValue(ServerValue.TIMESTAMP);
                    myRef.child("rating").setValue(0);

                    Intent home = new Intent(SignUpRetypePinCode.this, Client_Home.class);
                    startActivity(home);



                }else{
                    Toast.makeText(SignUpRetypePinCode.this, "Incorrect Pin code", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


}
