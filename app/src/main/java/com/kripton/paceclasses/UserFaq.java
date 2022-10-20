package com.kripton.paceclasses;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserFaq extends AppCompatActivity {
    TextInputEditText email,name,query,desc;
    Button submit;
    ProgressBar p_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_faq);
        Objects.requireNonNull(getSupportActionBar()).hide();
        init();

        submit.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(Objects.requireNonNull(email.getText()).toString()))
            {
                if (!TextUtils.isEmpty(Objects.requireNonNull(name.getText()).toString()))
                {
                    if (!TextUtils.isEmpty(query.getText().toString()))
                    {
                        if (!TextUtils.isEmpty(desc.getText().toString()))
                        {
                            p_bar.setVisibility(View.VISIBLE);
                            submit.setVisibility(View.GONE);
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            JSONObject object = new JSONObject();
                            try {
                                object.put("email",email.getText().toString());
                                object.put("name",name.getText().toString());
                                object.put("query",query.getText().toString());
                                object.put("desc",desc.getText().toString());
                                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getString(R.string.url) + "/courses/management/faq", object, response -> {
                                    p_bar.setVisibility(View.GONE);
                                    submit.setVisibility(View.VISIBLE);
                                    query.setText("");
                                    desc.setText("");
                                    try {
                                        Toast.makeText(UserFaq.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }, error -> {
                                    p_bar.setVisibility(View.GONE);
                                    submit.setVisibility(View.VISIBLE);
                                    Toast.makeText(UserFaq.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                                });

                                queue.add(request);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            desc.setError("Description required");
                        }
                    }
                    else
                    {
                        query.setError("Subject required");
                    }
                }
                else
                {
                    name.setError("Name required");
                }
            }
            else
            {
                email.setError("Email required");
            }
        });

    }
    void init()
    {
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        query = findViewById(R.id.query);
        desc = findViewById(R.id.desc);
        submit = (Button) findViewById(R.id.submit);
        p_bar = findViewById(R.id.p_bar);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user!=null)
        {
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            String idToken = task.getResult().getToken();
                            RequestQueue queue = Volley.newRequestQueue(UserFaq.this);
                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getString(R.string.url) + "/participant/management/profile", null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                            Log.d("ResponseData", response.toString());
                                            name.setText(response.getString("name"));
                                            email.setText(response.getString("email"));
                                            email.setFocusable(false);
                                            name.setClickable(false);
                                        email.setClickable(false);
                                        name.setFocusable(false);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    try {
                                        if (error.networkResponse.data!=null) {
                                            String res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                            JSONObject object = new JSONObject(res);
                                            Toast.makeText(UserFaq.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }){
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("Authorization", "Bearer " + idToken);
                                    return map;
                                }
                            };
                            queue.add(request);
                        }
                    });
        }
    }
}