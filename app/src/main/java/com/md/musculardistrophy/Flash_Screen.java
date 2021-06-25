package com.md.musculardistrophy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.md.musculardistrophy.Login.Login_Activity;

import java.util.Timer;
import java.util.TimerTask;

public class Flash_Screen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash__screen);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(Flash_Screen.this, Login_Activity.class));
                finish();
            }
        },4000);


    }


}