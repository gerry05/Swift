package app.swift;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignInEnterPinCode extends AppCompatActivity {

    Dialog DialogBox;

    FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_enter_pin_code);

        DialogBox = new Dialog(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = database.getReference("accounts");




        Pinview pinview = (Pinview)findViewById(R.id.pinView);
        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(final Pinview pinview, boolean b){

                Query query = myRef.orderByChild("phone_number").equalTo(mAuth.getCurrentUser().getPhoneNumber().replaceAll("\\W",""));
                Query query_loggedIn = myRef.orderByChild("user_id").equalTo(user.getUid());

                if(user != null){
                    //User is signed in

                    query_loggedIn.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String pin_code = pinview.getValue().toString();


                            String pin_code_key = null;
                            String user_level_key = null;

                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                pin_code_key = snapshot.child("pin_code").getValue().toString();
                                user_level_key = snapshot.child("user_type").getValue().toString();
                            }

                            if(pin_code.equals(pin_code_key) && user_level_key.equals("Client")) {

                                Intent user_home_activity = new Intent(SignInEnterPinCode.this, Client_Home.class);
                                startActivity(user_home_activity);
                            }else if(pin_code.equals(pin_code_key) && user_level_key.equals("Courier")){
                                Intent user_home_activity = new Intent(SignInEnterPinCode.this, Courier_Home.class);
                                startActivity(user_home_activity);
                            }else if(!pin_code.equals(pin_code_key)){

                                Toast.makeText(SignInEnterPinCode.this, "Incorrect Pin code", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }else{

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String pin_code = pinview.getValue().toString();

                            String pin_code_key = null;

                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                pin_code_key = snapshot.child("pin_code").getValue().toString();
                            }

                            if(pin_code.equals(pin_code_key)){

                                Intent user_home_activity = new Intent(SignInEnterPinCode.this,Client_Home.class);
                                startActivity(user_home_activity);
                            }else if(!pin_code.equals(pin_code_key)){

                                Toast.makeText(SignInEnterPinCode.this, "Incorrect Pin code", Toast.LENGTH_SHORT).show();
                            }


                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


    }


}
