package com.kripton.paceclasses;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PaymentDetials extends AppCompatActivity {

    ProgressBar waitbar;
    RecyclerView recyclerView;
    List<PaymentModel> models;
    PaymentAdapeter adapeter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detials);
//        Objects.requireNonNull(getSupportActionBar()).hide();
        Objects.requireNonNull(getSupportActionBar()).setTitle("My Payments");
        waitbar = findViewById(R.id.waitbar);
        recyclerView = findViewById(R.id.recyclerView);
        models = new ArrayList<>();
        waitbar.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null)
        {
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        String idToken = task.getResult().getToken();
                        RequestQueue queue = Volley.newRequestQueue(PaymentDetials.this);
                        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getString(R.string.url) + "/participant/management/mypayments", null, response -> {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                JSONObject object = response.getJSONObject(i);
                                models.add(new PaymentModel(object.getString("method"),object.getString("id"),object.getString("amount"),object.getString("cname"),object.getString("created_at")));

                                } catch (JSONException e) {
                                e.printStackTrace();
                                }
                                }
                                waitbar.setVisibility(View.GONE);
                                adapeter = new PaymentAdapeter(models,PaymentDetials.this);
                                recyclerView.setAdapter(adapeter);
                                adapeter.notifyDataSetChanged();
                        }, error -> {
                            Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();

                        }){
                            @Override
                            public Map<String, String> getHeaders() {
                                HashMap<String, String> map = new HashMap<>();
                                map.put("Authorization", "Bearer " + idToken);
                                return map;
                            }
                        };
//                        request.setRetryPolicy(new DefaultRetryPolicy(3000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        request.setRetryPolicy(new RetryPolicy() {
                            @Override
                            public int getCurrentTimeout() {
                                return 50000;
                            }

                            @Override
                            public int getCurrentRetryCount() {
                                return 50000;
                            }

                            @Override
                            public void retry(VolleyError error) throws VolleyError {

                            }
                        });
                        queue.add(request);
                    });
        }


    }
}
