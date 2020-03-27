package com.Faresa.POS.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.Faresa.pos.R;

public class PilihMode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_mode);

        RelativeLayout manual = findViewById(R.id.btn_manual);
        RelativeLayout barcode = findViewById(R.id.btn_barcode);

        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PenjualanActivity.class);
                startActivity(i);
            }
        });

        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PenjualanBarcode.class);
                startActivity(i);
            }
        });


    }
}
