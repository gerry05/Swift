package app.swift;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;


public class SignUpProfile extends AppCompatActivity {

     EditText fname;
     EditText lname;
     EditText address;
     RadioButton genderMale;
     RadioButton genderFemale;


     FirebaseDatabase database;
     DatabaseReference myRef;

     Dialog dialog_fillin_allfields;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_profile);

        dialog_fillin_allfields = new Dialog(this);

        fname = findViewById(R.id.sign_up_fname_eTxt);
        lname = findViewById(R.id.sign_up_lname_eTxt);
        address = findViewById(R.id.sign_up_address_eTxt);
        genderMale = findViewById(R.id.sign_up_male_Rbtn);
        genderFemale = findViewById(R.id.sign_up_female_Rbtn);




        database = FirebaseDatabase.getInstance();

    }

    public void backToLogin(View view) {
        Intent intent = new Intent(this, SignInorSignUp.class);
        startActivity(intent);
    }

    public void next_enterMobile (View view) {


        if(TextUtils.isEmpty(fname.getText().toString()) || TextUtils.isEmpty(lname.getText().toString())
                || TextUtils.isEmpty(address.getText().toString())){

            new SweetAlertDialog(SignUpProfile.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Please fill in all fields")
                    .show();
        }else{

            new MaterialDialog.Builder(this)
                    .title("Terms and Conditions of Use")
                    .content("PLEASE NOTE: For Users:\n" +
                            "\n" +
                            "1. We will be providing the couriers with a customize bag for them to put the documents.\n" +
                            "\n" +
                            "2. The company is not the one to blame for documents that are loss, but the company will give an insurance of 1000 pesos.\n" +
                            "\n" +
                            "\n" +
                            "Restrictions:\n" +
                            "\n" +
                            "You may not: (i) remove any copyright, trademark or other proprietary notices from any portion of" +
                            " the Services; (ii) reproduce, modify, prepare derivative works based upon, distribute," +
                            " license, lease, sell, resell, transfer, publicly display, publicly perform, transmit," +
                            " stream, broadcast or otherwise exploit the Services except as expressly permitted by Swift;" +
                            " (iii) decompile, reverse engineer or disassemble the Services except as may be permitted by applicable law; " +
                            "(iv) link to, mirror or frame any portion of the Services; (v) cause or launch any programs or scripts for " +
                            "the purpose of scraping, indexing, surveying, or otherwise data mining any portion of the Services or unduly " +
                            "burdening or hindering the operation and/or functionality of any aspect of the Services; or (vi) attempt to " +
                            "gain unauthorized access to or impair any aspect of the Services or its related systems or networks.")
                    .positiveText("Agree")
                    .negativeText("Disagree")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Intent intent = new Intent(SignUpProfile.this, SignUpVerification.class);
                            intent.putExtra("fname",fname.getText().toString().substring(0,1).toUpperCase() + fname.getText().toString().substring(1).toLowerCase());
                            intent.putExtra("lname",lname.getText().toString().substring(0,1).toUpperCase() + lname.getText().toString().substring(1).toLowerCase());
                            intent.putExtra("address",address.getText().toString().substring(0,1).toUpperCase() + address.getText().toString().substring(1).toLowerCase());

                            if(genderMale.isChecked()){
                                intent.putExtra("gender","Male");
                            }else if(genderFemale.isChecked()){
                                intent.putExtra("gender", "Female");
                            }

                            startActivity(intent);
                        }
                    })
                    .show();
//            Intent intent = new Intent(this, SignUpVerification.class);
//            intent.putExtra("fname",fname.getText().toString().substring(0,1).toUpperCase() + fname.getText().toString().substring(1).toLowerCase());
//            intent.putExtra("lname",lname.getText().toString().substring(0,1).toUpperCase() + lname.getText().toString().substring(1).toLowerCase());
//            intent.putExtra("address",address.getText().toString().substring(0,1).toUpperCase() + address.getText().toString().substring(1).toLowerCase());
//
//            if(genderMale.isChecked()){
//                intent.putExtra("gender","Male");
//            }else if(genderFemale.isChecked()){
//                intent.putExtra("gender", "Female");
//            }
//
//            startActivity(intent);

        }
    }

}
