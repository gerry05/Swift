package app.swift;



import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;


public class SignUpVerification extends AppCompatActivity {

    private static final String TAG ="PhoneLogin";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseDatabase database;

    EditText mNumber,mVerification_code;
    Button mSend,mVerify,mResend;



    ConstraintLayout first_layout;
    ConstraintLayout second_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_verification);

        MaterialDialog.Builder builder  = new MaterialDialog.Builder(SignUpVerification.this)
                .content("Loading...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();

        first_layout =  findViewById(R.id.signUp_form1);
        second_layout =  findViewById(R.id.signUp_form2);

        mNumber = (EditText) findViewById(R.id.sign_up_mobileNumber_eTxt);
        mVerification_code = (EditText) findViewById(R.id.sign_up_code_eTxt);
        mSend = (Button) findViewById(R.id.sign_up_send_number_btn);
        mVerify = (Button) findViewById(R.id.sign_up_verify_btn);
        mResend = (Button) findViewById(R.id.sign_up_resend_btn);

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(final PhoneAuthCredential credential) {
                mVerificationInProgress = false;
                dialog.dismiss();

                new SweetAlertDialog(SignUpVerification.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Verification complete!")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();

                                signInWithPhoneAuthCredential(credential);
                            }
                        })
                        .show();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                dialog.dismiss();
               // Toast.makeText(SignUpVerification.this,"Verification Failed", Toast.LENGTH_SHORT).show();
                new SweetAlertDialog(SignUpVerification.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Verification Failed")
                        .show();
                if (e instanceof FirebaseAuthInvalidCredentialsException){
                    //Toast.makeText(SignUpVerification.this,"Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    new SweetAlertDialog(SignUpVerification.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Invalid Phone Number")
                            .show();
                }else if( e instanceof FirebaseTooManyRequestsException){

                }

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                dialog.dismiss();
                //Toast.makeText(SignUpVerification.this,"Verification code has been send on your number", Toast.LENGTH_SHORT).show();
                new SweetAlertDialog(SignUpVerification.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Verification code has been sent on your number")
                        .show();

                mVerificationId = verificationId;
                mResendToken = token;

                first_layout.setVisibility(View.INVISIBLE);
                second_layout.setVisibility(View.VISIBLE);

            }
        };

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog.Builder builder  = new MaterialDialog.Builder(SignUpVerification.this)
                        .title("Checking your number...")
                        .content("Make sure you have internet connection.")
                        .contentGravity(GravityEnum.START)
                        .progress(true,0);
                final MaterialDialog dialog = builder.build();

                dialog.show();


               // Log.v("phone_test", mAuth.getCurrentUser().getPhoneNumber());

                database = FirebaseDatabase.getInstance();

                myRef = database.getReference("accounts");

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("63" + mNumber.getText().toString())){
                            dialog.dismiss();
                            new SweetAlertDialog(SignUpVerification.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("This number already exist")
                                    .show();

                        }else if (mNumber.getText().toString().length() < 10 || !mNumber.getText().toString().startsWith("9")
                                || mNumber.getText().toString().startsWith("09") ){
                            dialog.dismiss();
                            new SweetAlertDialog(SignUpVerification.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Invalid Phone Number!")
                                    .setContentText("Please Enter" + '\n' + "Ex. +63 9219396063")
                                    .show();
                        }else{
                            dialog.dismiss();
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    mNumber.getText().toString(),
                                    60,
                                    java.util.concurrent.TimeUnit.SECONDS,
                                    SignUpVerification.this,
                                    mCallbacks);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dialog.dismiss();
                    }
                });



            }
        });

        mResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        mNumber.getText().toString(),
                        60,
                        java.util.concurrent.TimeUnit.SECONDS,
                        SignUpVerification.this,
                        mCallbacks);
            }
        });



        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, mVerification_code.getText().toString());

                signInWithPhoneAuthCredential(credential);
            }
        });

    } // END onCreate

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        MaterialDialog.Builder builder  = new MaterialDialog.Builder(SignUpVerification.this)
                .content("Loading...")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            dialog.dismiss();
                            Intent signup_profile = getIntent();

                            String fname = signup_profile.getStringExtra("fname");
                            String lname = signup_profile.getStringExtra("lname");
                            String address = signup_profile.getStringExtra("address");
                            String gender = signup_profile.getStringExtra("gender");
                            System.out.println(fname);
                            final Intent intent = new Intent(SignUpVerification.this, SignUpEnterPinCode.class);

                            intent.putExtra("fname", fname);
                            intent.putExtra("lname", lname);
                            intent.putExtra("address", address);
                            intent.putExtra("gender", gender);


                            intent.putExtra("phone_number", mNumber.getText().toString());

                            new SweetAlertDialog(SignUpVerification.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Verification complete!")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            startActivity(intent);

                                        }
                                    })
                                    .show();
                        }else{
                            if(task.getException() instanceof  FirebaseAuthInvalidCredentialsException){
                                //Toast.makeText(SignUpVerification.this,"Invalid Verification", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                new SweetAlertDialog(SignUpVerification.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Invalid verification code")
                                        .show();

                            }
                        }
                    }
                });

    }

    public void backToLogin(View view) {
        Intent intent = new Intent(this, SignInorSignUp.class);
        startActivity(intent);
    }

}
