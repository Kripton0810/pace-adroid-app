package com.kripton.paceclasses;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Security extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    TextView phone,pwd;
    LinearLayout lay_pwd;
    ImageView edit,pwd_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        String phon = getIntent().getStringExtra("phone");
        phone = findViewById(R.id.phone);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My Security");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edit = findViewById(R.id.edit);
        lay_pwd = findViewById(R.id.lay_pwd);
        pwd_img = findViewById(R.id.pwd_img);
        pwd = findViewById(R.id.pwd);
        phone.setText(phon);
        updateUI();

        edit.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Security.this);
            View v = LayoutInflater.from(Security.this).inflate(R.layout.new_phone_number_layout,null);
            builder.setView(v);
            TextInputEditText ph = v.findViewById(R.id.phone_replace);
            Button cnf = v.findViewById(R.id.submit);
            ProgressBar pbar = v.findViewById(R.id.pbar);
            ph.setText(phone.getText().toString());
            AlertDialog dialog = builder.create();
            dialog.show();
            cnf.setOnClickListener(view1 -> {
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                assert user != null;
                user.getIdToken(true)
                        .addOnCompleteListener(task -> {
                            String idToken = task.getResult().getToken();
                            pbar.setVisibility(View.VISIBLE);
                            cnf.setVisibility(View.GONE);
                            RequestQueue queue = Volley.newRequestQueue(Security.this);
                            JSONObject object = new JSONObject();
                            try {
                                object.put("phone", Objects.requireNonNull(ph.getText()).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JsonObjectRequest request= new JsonObjectRequest(Request.Method.PUT, getString(R.string.url) + "/participant/management/updatephone", object, response -> {
                                try {
                                    pbar.setVisibility(View.GONE);
                                    cnf.setVisibility(View.VISIBLE);
                                    updateUI();
                                    Toast.makeText(Security.this, ""+response.getString("msg"), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }, error -> {

                            }){
                                @Override
                                public Map<String, String> getHeaders() {
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("Authorization", "Bearer " + idToken);
                                    return map;
                                }
                            };
                            queue.add(request);
                        });
            });
            //
        });
        pwd_img.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Security.this);
            View v = LayoutInflater.from(Security.this).inflate(R.layout.pasword_reset_layout,null);
            TextInputEditText old = v.findViewById(R.id.oldpassword);
            TextInputEditText newp = v.findViewById(R.id.newpassword);
            TextInputEditText cnfp = v.findViewById(R.id.cnfpassword);
            builder.setView(v);
            Button cnf = v.findViewById(R.id.submit);
            ProgressBar pbar = v.findViewById(R.id.pbar);
            AlertDialog dialog = builder.create();
            dialog.show();
            cnf.setOnClickListener(view1 -> {
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                assert user != null;
                user.getIdToken(true)
                        .addOnCompleteListener(task -> {
                            String idToken = task.getResult().getToken();
                            pbar.setVisibility(View.VISIBLE);
                            cnf.setVisibility(View.GONE);
                            RequestQueue queue = Volley.newRequestQueue(Security.this);
                            JSONObject object = new JSONObject();
                            try {
                                object.put("oldpassword", Objects.requireNonNull(old.getText()).toString());
                                object.put("newpassowrd", Objects.requireNonNull(newp.getText()).toString());
                                object.put("confirmpassword", Objects.requireNonNull(cnfp.getText()).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JsonObjectRequest request= new JsonObjectRequest(Request.Method.POST, getString(R.string.url) + "/participant/management/passwordreset", object, response -> {
                                try {
                                    pbar.setVisibility(View.GONE);
                                    cnf.setVisibility(View.VISIBLE);
                                    Toast.makeText(Security.this, ""+response.getString("msg")+" Please re-login...", Toast.LENGTH_SHORT).show();
                                    new Config().Signout(new Config().config(Security.this),Security.this);
                                    dialog.dismiss();
                                    startActivity(new Intent(Security.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                            }, error -> {
                                pbar.setVisibility(View.GONE);
                                cnf.setVisibility(View.VISIBLE);
                                if (error!=null) {
                                    try {
                                        String res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                        JSONObject obj = new JSONObject(res);
                                        Toast.makeText(Security.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }){
                                @Override
                                public Map<String, String> getHeaders() {
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("Authorization", "Bearer " + idToken);
                                    return map;
                                }
                            };
                            queue.add(request);
                        });
            });
        });
    }

    void updateUI()
    {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user!=null)
        {
            user.getIdToken(true).addOnCompleteListener(task -> {

                String idToken = task.getResult().getToken();
                Log.d("Token",idToken);
                RequestQueue queue = Volley.newRequestQueue(Security.this);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getString(R.string.url) + "/participant/management/profile", null, response -> {
                    try {
                        Log.d("ResponseData",response.toString());
                        phone.setText(response.getString("phone"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    try {
                        String res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject object = new JSONObject(res);
                        Toast.makeText(Security.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Authorization", "Bearer " + idToken);
                        return map;
                    }
                };
                queue.add(request);
                RequestQueue q = Volley.newRequestQueue(Security.this);
                JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, getString(R.string.url) + "/participant/management/passwordavailable", null, response -> {
                    try {
                        if (response.getBoolean("available"))
                        {
                            lay_pwd.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Authorization", "Bearer " + idToken);
                        return map;
                    }
                };
                q.add(r);
            });
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}