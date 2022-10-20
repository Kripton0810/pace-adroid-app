package com.kripton.paceclasses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class PaymentSuccessfull extends AppCompatActivity {
    TextView paytext;
    ImageView payment_img;
    Button button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successfull);
        getSupportActionBar().hide();
        paytext = findViewById(R.id.paytext);
        YoYo.with(Techniques.SlideInUp)
                .duration(1000)
                .repeat(100)
                .playOn(paytext);
        payment_img = findViewById(R.id.payment_img);

        Glide.with(PaymentSuccessfull.this)
                .asGif()
                .load(R.drawable.paysuccess)
                .into(payment_img);
        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(view->{
            startActivity(new Intent(PaymentSuccessfull.this,MainDashedboard.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
    }
}