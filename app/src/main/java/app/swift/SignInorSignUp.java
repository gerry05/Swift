package app.swift;


import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignInorSignUp extends AppCompatActivity {

    //private FirebaseAuth mAuth;

    DatabaseReference myRef;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MaterialDialog.Builder builder  = new MaterialDialog.Builder(SignInorSignUp.this)
                .content("Checking connection...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("accounts");
       final  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if(user != null){
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(user.getPhoneNumber().replaceAll("\\W",""))){
                       dialog.dismiss();
                        Intent pin_code = new Intent(SignInorSignUp.this, SignInEnterPinCode.class);
                        pin_code.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        pin_code.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(pin_code,0);
                        overridePendingTransition(0,0);
                    }else{
                        dialog.dismiss();
                        setContentView(R.layout.main_activity);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dialog.dismiss();
                    setContentView(R.layout.main_activity);

                }
            });

        }else{
            dialog.dismiss();
            setContentView(R.layout.main_activity);
        }

    }


    /** Called when the user taps the button */
    public void goSignIn(View view) {
        Intent intent = new Intent(this, SignInVerification.class);
        startActivity(intent);
    }

    public void goSignUp (View view) {
        Intent intent = new Intent(this,SignUpProfile.class);
        startActivity(intent);
    }


}
