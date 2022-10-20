package com.kripton.paceclasses;

import static com.facebook.FacebookSdk.getApplicationContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.FragmentTransitionSupport;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import info.androidhive.fontawesome.FontTextView;

public class MainDashedboard extends AppCompatActivity {
    CircleImageView profile_pic;
    final  int HOME_PAGE = 1;
    final  int MY_COURSES = 2;
    final  int MY_CART = 3;
    static int CURRENT = 0;
    FirebaseAuth auth;
    FirebaseUser user;
    final String dark = "#1D2D3A";
    final String white = "#FFFFFF";
    FontTextView my_cart,my_courses,my_home,logout;
    LinearLayout search_box;
    TextView username;
    static String nm,em,phon;
    FrameLayout myframe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashedboard);
        getSupportActionBar().hide();
        init();
        updateUI();
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                if (user!=null) {
                    Intent intent = new Intent(MainDashedboard.this, MyAccount.class);
                    intent.putExtra("email",em);
                    intent.putExtra("phone",phon);
                    intent.putExtra("name",nm);
                    ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainDashedboard.this, profile_pic, ViewCompat.getTransitionName(profile_pic));
                    startActivity(intent, compat.toBundle());
//                    finish();
                }
                else
                {
                    Intent intent = new Intent(MainDashedboard.this, MainActivity.class);
                    startActivity(intent);
//                    finish();
                }

            }
        });
        search_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainDashedboard.this,Search.class);
                startActivity(intent);
                Animatoo.animateSwipeLeft(MainDashedboard.this);
            }
        });
        CURRENT = HOME_PAGE;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(myframe.getId(),new Home()).commit();
        my_home.setTextColor(Color.parseColor(dark));
        my_courses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabColorInit();
                my_courses.setTextColor(Color.parseColor(dark));
                setFragement(new MyCourses(),MY_COURSES);
            }
        });
        my_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                tabColorInit();
//                my_cart.setTextColor(Color.parseColor(dark));
//                setFragement(new MyCart(),MY_CART);
                MyCart cart = new MyCart();
                cart.show(getSupportFragmentManager(),cart.getTag());
            }
        });
        my_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabColorInit();
                my_home.setTextColor(Color.parseColor(dark));
                setFragement(new Home(),HOME_PAGE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (CURRENT!=HOME_PAGE)
        {
            tabColorInit();
            my_home.setTextColor(Color.parseColor(dark));
            setFragement(new Home(),HOME_PAGE);
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void setFragement(Fragment fragement, int fragementnumber)
    {
        if (fragementnumber!=CURRENT) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(myframe.getId(), fragement).commit();
            CURRENT = fragementnumber;
        }
    }
    private void tabColorInit()
    {
        my_home.setTextColor(Color.parseColor(white));
        my_courses.setTextColor(Color.parseColor(white));
        my_cart.setTextColor(Color.parseColor(white));
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
        myframe = findViewById(R.id.myframe);
        my_home = findViewById(R.id.my_home);
        my_courses = findViewById(R.id.my_courses);
        username = findViewById(R.id.username);
        search_box = findViewById(R.id.search_box);
        logout = findViewById(R.id.logout);
        my_cart = findViewById(R.id.my_cart);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Dexter.withContext(MainDashedboard.this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Config().Signout(new Config().config(MainDashedboard.this),MainDashedboard.this);
                finish();
                startActivity(getIntent());
            }
        });
    }
    public void updateUI()
    {

        if (user!=null)
        {
            logout.setVisibility(View.VISIBLE);
            SharedPreferences preferences = getSharedPreferences("Profile",MODE_PRIVATE);
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {

                    String idToken = task.getResult().getToken();
                    Log.d("Token",idToken);
                    RequestQueue queue = Volley.newRequestQueue(MainDashedboard.this);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getString(R.string.url) + "/participant/management/profile", null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (TextUtils.equals(response.getString("phone"),""))
                                {
                                    new Config().Signout(new Config().config(MainDashedboard.this),MainDashedboard.this);
                                    logout.setVisibility(View.GONE);
                                }
                                else {
                                    Log.d("ResponseData", response.toString());
                                    username.setText(response.getString("name"));
                                    nm = response.getString("name");
                                    em = response.getString("email");
                                    phon = response.getString("phone");
                                    Glide.with(MainDashedboard.this)
                                            .load(response.getString("profilepic"))
                                            .placeholder(R.drawable.ic_person_icon)
                                            .dontAnimate()
                                            .skipMemoryCache(true)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .into(profile_pic);
//                                    Toast.makeText(MainDashedboard.this,response.getString("profilepic") , Toast.LENGTH_SHORT).show();
                                }

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
                                    Toast.makeText(MainDashedboard.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
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
        }else {
            username.setText("");
            Glide.with(MainDashedboard.this)
                    .load(R.drawable.ic_person_icon)
                    .dontAnimate()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(profile_pic);
            logout.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        user = auth.getCurrentUser();
        updateUI();
    }
}