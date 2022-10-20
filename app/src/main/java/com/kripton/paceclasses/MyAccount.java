package com.kripton.paceclasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccount extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    CircleImageView profile_pic;
    TextView username,useremail,signout;
    ImageView main_acc_edit;
    FlexboxLayout help,faq,security,notification,payment;
    static String phonenum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        getSupportActionBar().hide();
        init();
        updateUI();

    }
    private void init()
    {
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        profile_pic = findViewById(R.id.profile_pic);
        payment = findViewById(R.id.payment);
        security = findViewById(R.id.security);
        username = findViewById(R.id.username);
        notification = findViewById(R.id.notification);
        useremail = findViewById(R.id.useremail);
        signout = findViewById(R.id.signout);
        faq = findViewById(R.id.faq);
        help = findViewById(R.id.help);
        main_acc_edit = findViewById(R.id.main_acc_edit);
        String nm = getIntent().getStringExtra("name");
        String em = getIntent().getStringExtra("email");
        useremail.setText(em);
        username.setText(nm);
        String ph = getIntent().getStringExtra("phone");
        phonenum = ph;
        main_acc_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAccount.this,MyAccountEdit.class);
                intent.putExtra("phone",phonenum);
                intent.putExtra("name",nm);
                intent.putExtra("email",em);
                startActivity(intent);
//                finish();
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Config().Signout(new Config().config(MyAccount.this),MyAccount.this);
                finish();
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAccount.this,HelpCenter.class);
                startActivity(intent);
            }
        });
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAccount.this,PaymentDetials.class);
                startActivity(intent);
            }
        });
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAccount.this,UserFaq.class);
                startActivity(intent);
            }
        });
        security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAccount.this,Security.class);
                intent.putExtra("phone",phonenum);
                startActivity(intent);
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAccount.this,Notification.class);
                startActivity(intent);
            }
        });
    }

    void updateUI()
    {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user!=null)
        {
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {

                    String idToken = task.getResult().getToken();
                    Log.d("Token",idToken);
                    RequestQueue queue = Volley.newRequestQueue(MyAccount.this);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getString(R.string.url) + "/participant/management/profile", null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("ResponseData",response.toString());
                                username.setText(response.getString("name"));
                                Glide.with(getApplicationContext())
                                        .load(response.getString("profilepic"))
                                        .placeholder(R.drawable.ic_person_icon)
                                        .dontAnimate()
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .into(profile_pic);
                                useremail.setText(response.getString("email"));
                                phonenum = response.getString("phone");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                if (error.networkResponse.data!=null) {
                                    String res = new String(error.networkResponse.data, "utf-8");
                                    JSONObject object = new JSONObject(res);
                                    Toast.makeText(MyAccount.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                                else 
                                {
                                    Toast.makeText(MyAccount.this, "your internet is slow", Toast.LENGTH_SHORT).show();
                                }
                            } catch (UnsupportedEncodingException | JSONException e) {
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
        else
        {
            startActivity(new Intent(MyAccount.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateFade(MyAccount.this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateUI();
    }
}