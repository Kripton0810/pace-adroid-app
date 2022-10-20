package com.kripton.paceclasses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class PaymentError extends AppCompatActivity {
    TextView paytext;
    ImageView payment_img;
    Button button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_error);
        getSupportActionBar().hide();
        paytext = findViewById(R.id.paytext);
        paytext.setText(""+getIntent().getStringExtra("error"));
        payment_img = findViewById(R.id.payment_img);

        Glide.with(PaymentError.this)
                .asGif()
                .load(R.drawable.payment_error2)
                .into(payment_img);
        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(view->{
            finish();
        });
    }
}