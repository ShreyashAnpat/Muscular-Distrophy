package com.md.musculardistrophy.Login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.md.musculardistrophy.MainActivity;
import com.md.musculardistrophy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

public class Login_Activity extends AppCompatActivity {

    EditText PhoneNumber ;
    CountryCodePicker ccp ;
    Button getOtp ;
    TextView signUp ;
    FirebaseFirestore firebaseFirestore ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        PhoneNumber = findViewById(R.id.number);
        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(PhoneNumber);
        getOtp = findViewById(R.id.button);
        signUp = findViewById(R.id.SignUp);
        firebaseFirestore = FirebaseFirestore.getInstance() ;



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Activity.this , Register_Activity.class));
            }
        });

        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = PhoneNumber.getText().toString() ;
                if (number.isEmpty()){
                    PhoneNumber.setError("Enter Phone Number");
                }
                else {
                    firebaseFirestore.collection("user").whereEqualTo("phoneNumber", ccp.getFullNumberWithPlus().replace(" ","")).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (!value.isEmpty()) {
                                Intent intent = new Intent(Login_Activity.this , Login_OTP_Activity.class);
                                intent.putExtra("PhoneNumber" , ccp.getFullNumberWithPlus().replace(" ",""));
                                startActivity(intent);
                            }
                            else {
                                PhoneNumber.setError("Number is not register");
                            }
                        }
                    });

                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!= null){
            Intent intent  = new Intent(Login_Activity.this, MainActivity.class);
            intent.putExtra("flag", "0");
            startActivity(intent);
            finish();
        }
    }
}