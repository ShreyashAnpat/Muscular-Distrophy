package com.md.musculardistrophy.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.md.musculardistrophy.MainActivity;
import com.md.musculardistrophy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Register_OTP extends AppCompatActivity {
    EditText otp ;
    Button verifyOTP ;
    String userName , location , phoneNumber , otpID , userID;
    FirebaseFirestore firebaseFirestore ;
    ProgressDialog progressDialog ;
    FirebaseAuth auth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__o_t_p);
        auth = FirebaseAuth.getInstance();
        otp = findViewById(R.id.OTP);
        verifyOTP = findViewById(R.id.verifyOTP);
        userName = getIntent().getStringExtra("userName");
        location = getIntent().getStringExtra("location");
        phoneNumber = getIntent().getStringExtra("phoneNumber");



        progressDialog = new ProgressDialog(Register_OTP.this);
        progressDialog.setMessage("Verifying Phone number");
        progressDialog.setCanceledOnTouchOutside(false);
        InitiateOtp(phoneNumber);
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
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpID,otp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


    }

    private void InitiateOtp(String number) {
        Toast.makeText(Register_OTP.this ,number , Toast.LENGTH_LONG);
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

                        Snackbar.make(getCurrentFocus(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseFirestore = FirebaseFirestore.getInstance();
                            auth = FirebaseAuth.getInstance();
                            userID = auth.getUid() ;
                            HashMap<String , Object> userData = new HashMap<>();
                            userData.put("userName", userName);
                            userData.put("phoneNumber" , phoneNumber);
                            userData.put("location" , location);
                            userData.put("userID" ,userID);
                            userData.put("Profile" , "https://firebasestorage.googleapis.com/v0/b/muscular-dystrophy-e096a.appspot.com/o/profile.png?alt=media&token=4440449d-bf34-43dc-a411-23de0a145c47");
                            firebaseFirestore.collection("user").document(userID).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.cancel();
                                    Intent intent = new Intent(Register_OTP.this, MainActivity.class);
                                    intent.putExtra("flag" , "0");
                                    startActivity(intent);
                                    finish();
                                }
                            });


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