package com.kripton.paceclasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ng.max.slideview.SlideView;

public class CheckoutCourse extends AppCompatActivity implements PaymentResultListener {
    SlideView swipe_checkout;
    ProgressDialog progressDialog;
    static String cid;
    ImageView image;
    TextView cname, cprice, stunm, stuemail, phone, duration, startsfrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_course);
        swipe_checkout = findViewById(R.id.swipe_checkout);
        progressDialog = new ProgressDialog(CheckoutCourse.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Loading info...");
        progressDialog.setMessage("Please wait while preparing the bill");
        progressDialog.show();
        image = findViewById(R.id.image);
        cname = findViewById(R.id.cname);
        cprice = findViewById(R.id.cprice);
        stunm = findViewById(R.id.stunm);
        stuemail = findViewById(R.id.stuemail);
        phone = findViewById(R.id.phone);
        duration = findViewById(R.id.duration);
        startsfrom = findViewById(R.id.startsfrom);
        Checkout.preload(getApplicationContext());
        Objects.requireNonNull(getSupportActionBar()).hide();
        updateUI();
        String img_url = getIntent().getStringExtra("img");
        String cnm = getIntent().getStringExtra("name");
        cid = getIntent().getStringExtra("cid");
        String cpr = getIntent().getStringExtra("cprice");
        String cduration = getIntent().getStringExtra("cduration");
        String startd = getIntent().getStringExtra("startd");
        Glide.with(CheckoutCourse.this)
                .load(img_url)
                .into(image);
        cname.setText(cnm);
        cprice.setText("Rs. " + cpr);
        duration.setText(cduration);
        startsfrom.setText(startd);
        swipe_checkout.setOnSlideCompleteListener(slideView -> {
            // vibrate the device
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(400);
            startPayment(cnm, cpr + "00");

            // go to a new activity
        });
    }

    public void startPayment(String cname, String amt) {
        RequestQueue queue = Volley.newRequestQueue(CheckoutCourse.this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,getString(R.string.url)+"/courses/payment/makeorder/"+cid,null,list->{

            Checkout checkout = new Checkout();
            checkout.setKeyID("rzp_test_rzycbSZXkYhKHm");

            checkout.setImage(R.drawable.ic_main_logo_splash);

            final Activity activity = this;

            try {
                JSONObject options = new JSONObject();
                options.put("name", "Pace classes");
                options.put("description", cname);
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                options.put("theme.color", "#00A9B7");
                options.put("order_id", list.getString("id"));
                options.put("currency", "INR");
                options.put("amount", amt);
                JSONObject retryObj = new JSONObject();
                retryObj.put("enabled", true);
                retryObj.put("max_count", 4);
                options.put("retry", retryObj);

                checkout.open(activity, options);

            } catch (Exception e) {
                Log.e("RError", "Error in starting Razorpay Checkout", e);
            }
        },error -> {

        });
        request.setRetryPolicy(new DefaultRetryPolicy(3000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    @Override
    public void onPaymentSuccess(String s) {
        ProgressDialog dialog = new ProgressDialog(CheckoutCourse.this);
        dialog.setMessage("Don't press back");
        dialog.setTitle("We are making your payment it will take less then 1 minute");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        String idToken = task.getResult().getToken();

                        RequestQueue queue = Volley.newRequestQueue(CheckoutCourse.this);
                        JSONObject object = new JSONObject();
                        try {
                            object.put("courseid", cid);
                            object.put("payid", s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getString(R.string.url) + "/courses/management/register", object, response -> {
                            dialog.cancel();
                            dialog.hide();
                            startActivity(new Intent(CheckoutCourse.this, PaymentSuccessfull.class));
                            finish();
                        }, error -> {
                            dialog.cancel();
                            dialog.hide();
                        }) {
                            @Override
                            public Map<String, String> getHeaders() {
                                HashMap<String, String> map = new HashMap<>();
                                map.put("Authorization", "Bearer " + idToken);
                                return map;
                            }
                        };
                        request.setRetryPolicy(new DefaultRetryPolicy(3000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(request);
                    });
        }


//        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        try {
            JSONObject object = new JSONObject(s);
            JSONObject er = object.getJSONObject("error");
            Toast.makeText(this, " " + er.getString("description") + " ", Toast.LENGTH_SHORT).show();
            Intent in = new Intent(CheckoutCourse.this, PaymentError.class);
            in.putExtra("error", " " + er.getString("description") + " ");
            startActivity(in);
//            error
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("ErrorPay", s);
    }

    void updateUI() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.getIdToken(true).addOnCompleteListener(task -> {

                String idToken = task.getResult().getToken();
                Log.d("Token", idToken);
                RequestQueue queue = Volley.newRequestQueue(CheckoutCourse.this);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getString(R.string.url) + "/participant/management/profile", null, response -> {
                    try {
                        progressDialog.cancel();
                        progressDialog.hide();
                        stunm.setText(response.getString("name"));
                        stuemail.setText(response.getString("email"));
                        phone.setText(response.getString("phone"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    try {
                        if (error.networkResponse.data != null) {
                            String res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            JSONObject object = new JSONObject(res);
                            Toast.makeText(CheckoutCourse.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CheckoutCourse.this, "your internet is slow", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Authorization", "Bearer " + idToken);
                        return map;
                    }
                };
                queue.add(request);
            });
        }
    }
}