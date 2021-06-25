package com.md.musculardistrophy.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.md.musculardistrophy.R;

public class Register_Activity extends AppCompatActivity {
    Button getOtp ;
    EditText userName , location ,phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__acitvity);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        userName = findViewById(R.id.username);
        location = findViewById(R.id.location);
        phoneNumber = findViewById(R.id.Phone_number);
        getOtp = findViewById(R.id.submit);

        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName_txt , location_txt , phoneNumber_txt ;
                userName_txt = userName.getText().toString() ;
                location_txt = location.getText().toString() ;
                phoneNumber_txt = phoneNumber.getText().toString() ;

                if (userName_txt.isEmpty()){
                    userName.setError("Enter your name");
                }
                else if (location_txt.isEmpty()){
                    location.setError("Enter your Location");
                }
                else if (phoneNumber_txt.isEmpty()){
                    phoneNumber.setError("Enter your phone number");
                }
                else {
                    Intent intent = new Intent(Register_Activity.this , Register_OTP.class);
                    intent.putExtra("userName" , userName_txt);
                    intent.putExtra("location" , location_txt);
                    intent.putExtra("phoneNumber" , "+91"+phoneNumber_txt);
                    startActivity(intent);
                }
            }
        });


    }
}