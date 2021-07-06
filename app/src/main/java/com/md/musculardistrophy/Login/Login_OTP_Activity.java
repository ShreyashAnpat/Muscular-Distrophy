package com.md.musculardistrophy.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.rpc.context.AttributeContext;
import com.md.musculardistrophy.MainActivity;
import com.md.musculardistrophy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login_OTP_Activity extends AppCompatActivity {
    private static final String TAG = "";
    String number ,otpID ;
    Button verifyOTP ;
    EditText otp ;
    FirebaseAuth mAuth ;
    ProgressDialog progressDialog ;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__o_t_p_);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        number = getIntent().getStringExtra("PhoneNumber");
        verifyOTP = findViewById(R.id.verifyOTP);
        otp = findViewById(R.id.OTP);
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("fr");
        Log.d(TAG, "onCreate: " +number);
        InitiateOtp(number);
        progressDialog = new ProgressDialog(Login_OTP_Activity.this);
        progressDialog.setMessage("Verifying ...!");
        progressDialog.setCanceledOnTouchOutside(false);

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String OTP = otp.getText().toString() ;
                if (OTP.isEmpty()){
                    otp.setError("Enter OTP");
                    progressDialog.cancel();
                }
                else if (OTP.length()<6){
                    progressDialog.cancel();
                    otp.setError("Enter Valid OTP");
                }
                else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpID,OTP);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


    }

    private void InitiateOtp(String number) {
        Toast.makeText(Login_OTP_Activity.this ,number , Toast.LENGTH_LONG);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpID = s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

//                        Snackbar.make(getCurrentFocus(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });        // OnVerificationStateChangedCallbacks
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.cancel();
                            Intent intent = new Intent(Login_OTP_Activity.this, MainActivity.class);
                            intent.putExtra("flag" , "0");
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.cancel();
                            Snackbar.make(getCurrentFocus(), "You hav Enter invalid OTP", Snackbar.LENGTH_LONG).show();
                                                   }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(getCurrentFocus(),e.getMessage().toString() ,Snackbar.LENGTH_LONG ).show();
                progressDialog.cancel();
            }
        });
    }
}