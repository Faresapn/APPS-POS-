package com.Faresa.POS.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.Faresa.POS.Helper.SessionManager;
import com.Faresa.pos.R;
import com.bumptech.glide.Glide;

public class Splash_Activity extends AppCompatActivity {


    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView animasi = findViewById(R.id.animasi);
        Glide.with(this).load(R.drawable.loading).into(animasi);

        session = new SessionManager(getApplicationContext());

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(session.isLoggedIn()) {
                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(in);
                    finish();
                }else{
                    Intent in = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(in);
                    finish();
                }
            }
        }, 4000);




    }
}
