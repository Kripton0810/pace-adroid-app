package com.kripton.paceclasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountEdit extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    TextView phone,email,mygender;
    RadioGroup gender;
    EditText name;
    boolean name_ck,gender_ck;
    ImageView checker,gender_checker;
    RadioButton male,female,other;
    CircleImageView profile_pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_edit);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profile_pic = findViewById(R.id.profile_pic);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        gender = findViewById(R.id.gender);
        female = findViewById(R.id.female);
        male = findViewById(R.id.male);
        other = findViewById(R.id.others);
        checker = findViewById(R.id.checker);
        gender_checker = findViewById(R.id.gender_checker);
        mygender = findViewById(R.id.mygender);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        name_ck = false;
        String nm = getIntent().getStringExtra("name");
        String em = getIntent().getStringExtra("email");
        email.setText(em);
        name.setText(nm);
        String ph = getIntent().getStringExtra("phone");
        phone.setText(ph);
        gender_ck = false;
        if (user!=null)
        {
            user.getIdToken(true).addOnCompleteListener(task -> {
                String idToken = task.getResult().getToken();
                Log.d("Token",idToken);
                RequestQueue queue = Volley.newRequestQueue(MyAccountEdit.this);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getString(R.string.url) + "/participant/management/profile", null, response -> {
                    try {
                        Log.d("ResponseData",response.toString());
                        name.setText(response.getString("name"));
                        Glide.with(getApplicationContext())
                                .load(response.getString("profilepic"))
                                .placeholder(R.drawable.ic_person_icon)
                                .dontAnimate()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(profile_pic);
                        String gen = response.getString("gender");
                        mygender.setText(gen);
                        if (gen.equalsIgnoreCase("male"))
                        {
                            updateRadio();
                            male.setChecked(true);
                        }
                        else if (gen.equalsIgnoreCase("female"))
                        {
                            updateRadio();
                            female.setChecked(true);

                        }
                        else
                        {
                            updateRadio();
                            other.setChecked(true);

                        }
                        email.setText(response.getString("email"));
                        phone.setText(response.getString("phone"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

                    if (error!=null) {
                        try {
                            String res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            JSONObject object = new JSONObject(res);
                            Toast.makeText(MyAccountEdit.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
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
            gender_checker.setOnClickListener(view -> {
                if (gender_ck)
                {
                    update();
                    gender.setVisibility(View.GONE);
                    gender_checker.setImageDrawable(getDrawable(R.drawable.ic_edit_square));
                    gender_ck=false;
                }
                else
                {
                    gender.setVisibility(View.VISIBLE);
                    gender_checker.setImageDrawable(getDrawable(R.drawable.ic_baseline_done_24));
                    gender_ck=true;

                }
            });
            gender.setOnCheckedChangeListener((radioGroup, i) -> {
                RadioButton r;
                r = findViewById(i);
                mygender.setText(r.getText());
            });
            checker.setOnClickListener(view -> {
                if (name_ck)
                {
                    update();
                    name.setEnabled(false);
                    checker.setImageDrawable(getDrawable(R.drawable.ic_edit_square));
                    name_ck = false;
                }
                else
                {
                    name.setEnabled(true);
                    name.requestFocus();
                    checker.setImageDrawable(getDrawable(R.drawable.ic_baseline_done_24));
                    InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    methodManager.showSoftInput(name,InputMethodManager.SHOW_IMPLICIT);
                    name_ck = true;
                }

            });
        }
        profile_pic.setOnClickListener(view -> ImagePicker.with(MyAccountEdit.this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(64)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)
                .start(100));

    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
//                    profile_pic.setImageBitmap(bitmap);
                    uploadBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void update()
    {
        user.getIdToken(true)
                .addOnCompleteListener(task -> {
                    String idToken = task.getResult().getToken();
    JSONObject object = new JSONObject();
    try {
        object.put("phone",phone.getText().toString());
        object.put("gender",mygender.getText().toString());
        object.put("name",name.getText().toString());
    } catch (JSONException e) {
        e.printStackTrace();
    }
    Log.d("UpdateCont",object.toString());
    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
    JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, getString(R.string.url) + "/participant/management/update", object, response -> {
        try {
            Toast.makeText(MyAccountEdit.this, ""+response.getString("msg"), Toast.LENGTH_SHORT).show();
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
    }
    private void uploadBitmap(final Bitmap bitmap) {
        ProgressDialog progress = new ProgressDialog(MyAccountEdit.this);
        progress.setMessage("Updating your profile pic...");
        progress.setIndeterminate(true);
        progress.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        user.getIdToken(true)
                .addOnCompleteListener(task -> {
                    String token = task.getResult().getToken();
                    VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, getString(R.string.url)+"/participant/management/userpic",
                            response -> {
                                progress.hide();
                                progress.dismiss();
                                String res;
                                try {
                                    if (response.data != null) {
                                        res = new String(response.data, StandardCharsets.UTF_8);
                                        JSONObject object = new JSONObject(res);
                                        Toast.makeText(MyAccountEdit.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                                        Glide.with(getApplicationContext())
                                                .load(object.getString("img_url"))
                                                .placeholder(R.drawable.ic_person_icon)
                                                .dontAnimate()
                                                .skipMemoryCache(true)
                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .into(profile_pic);

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            },
                            error -> {
                                progress.hide();
                                progress.dismiss();
                                try {
                                    String res;
                                    if (error!=null) {
                                        if (error.networkResponse.data != null) {
                                            res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                            JSONObject object = new JSONObject(res);
                                            Toast.makeText(MyAccountEdit.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }) {

                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String,String> map = new HashMap<>();
//                map.put();//token
                            map.put("Authorization","Bearer "+token);
                            return map;
                        }

                        @Override
                        protected Map<String, DataPart> getByteData() {
                            Map<String, DataPart> params = new HashMap<>();
                            long imagename = System.currentTimeMillis();
                            params.put("data", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                            return params;
                        }
                    };
                    Volley.newRequestQueue(MyAccountEdit.this).add(volleyMultipartRequest);
                });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void updateRadio()
    {
        male.setChecked(false);
        female.setChecked(false);
        other.setChecked(false);
    }
}