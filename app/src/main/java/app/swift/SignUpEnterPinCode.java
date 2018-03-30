package app.swift;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.goodiebag.pinview.Pinview;

public class SignUpEnterPinCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_enter_pin_code);

        Pinview pinview = (Pinview)findViewById(R.id.pinView);
        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean b){

                Intent signup_profile = getIntent();

                String fname = signup_profile.getStringExtra("fname");
                String lname = signup_profile.getStringExtra("lname");
                String address = signup_profile.getStringExtra("address");
                String gender = signup_profile.getStringExtra("gender");

                String phone_number = signup_profile.getStringExtra("phone_number");

                Intent intent = new Intent(SignUpEnterPinCode.this, SignUpRetypePinCode.class);
                intent.putExtra("fname", fname);
                intent.putExtra("lname", lname);
                intent.putExtra("address", address);
                intent.putExtra("gender", gender);

                intent.putExtra("phone_number", phone_number);

                intent.putExtra("pincode_1", pinview.getValue().toString());
                startActivity(intent);
            }
        });

    }

}
