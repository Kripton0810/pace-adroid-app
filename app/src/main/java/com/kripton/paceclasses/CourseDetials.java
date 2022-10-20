package com.kripton.paceclasses;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CourseDetials extends AppCompatActivity {
    TextView curriculum,review,course_title,instructor_name,course_star,course_desc,startingmonth,duration,price;
    LinearLayout review_layout,my_comment_sec;
    RecyclerView course_index,comment;
    List<String> index;
    EditText mycomment;
    ImageView course_image,back;
    ProgressBar pbar_comment;
    RatingBar rating;
    Button comment_submit,enroll,add_to_cart;
    CommentAdapter commentAdapter;
    List<CommentsModel> commentList;
    String cid = "";
    final  int CURRICULUM = 1;
    final  int REVIEW = 2;
    static  int CURRENT;
    static String idToken = null,cname=null,prices=null,durations= null,startd = null,img=null;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detials);
        index = new ArrayList<>();
        commentList = new ArrayList<>();
//        /courses/management/postComment
        comment = findViewById(R.id.comment);
        cid = getIntent().getStringExtra("cid");
        progressDialog = new ProgressDialog(CourseDetials.this);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Loading info...");
        progressDialog.setMessage("Please wait till i find the data...");
        progressDialog.show();
        Objects.requireNonNull(getSupportActionBar()).hide();
        CURRENT = CURRICULUM;
        review = findViewById(R.id.review);
        comment_submit = findViewById(R.id.comment_submit);
        curriculum = findViewById(R.id.curriculum);
        back = findViewById(R.id.back);
        review_layout = findViewById(R.id.review_layout);
        course_index = findViewById(R.id.course_index);
        add_to_cart = findViewById(R.id.add_to_cart);
        course_index.setVisibility(View.VISIBLE);
        review_layout.setVisibility(View.GONE);
        rating = findViewById(R.id.rating);
        mycomment = findViewById(R.id.mycomment);
        duration = findViewById(R.id.duration);
        my_comment_sec = findViewById(R.id.my_comment_sec);
        price = findViewById(R.id.price);
        enroll = findViewById(R.id.enroll);
        course_desc = findViewById(R.id.course_desc);
        course_star = findViewById(R.id.course_star);
        instructor_name= findViewById(R.id.instructor_name);
        course_image = findViewById(R.id.course_image);
        course_title= findViewById(R.id.course_title);
        pbar_comment = findViewById(R.id.pbar_comment);
        startingmonth= findViewById(R.id.startingmonth);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CourseDetials.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        commentAdapter = new CommentAdapter(commentList,CourseDetials.this);
        comment.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();
        RequestQueue qc = Volley.newRequestQueue(CourseDetials.this);
        JsonArrayRequest commentArray = new JsonArrayRequest(Request.Method.GET, getString(R.string.url) + "/courses/management/getcomment/"+cid, null, response -> {
            Log.d("Comments",response.toString());
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject obj = response.getJSONObject(i);
                    commentList.add(new CommentsModel(obj.getString("profilepic"), obj.getString("name"),obj.getString("star"),obj.getString("date"),obj.getString("comment")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            commentAdapter = new CommentAdapter(commentList,CourseDetials.this);
            comment.setAdapter(commentAdapter);
            commentAdapter.notifyDataSetChanged();
        }, error -> {

        });
        qc.add(commentArray);
        back.setOnClickListener(view -> finish());

//        comment.setLayoutManager(linearLayoutManager);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null) {
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        idToken = task.getResult().getToken();

                        RequestQueue queue = Volley.newRequestQueue(CourseDetials.this);
                        JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET, getString(R.string.url)+"/courses/single/"+cid, null, response -> {
                            try {
                                progressDialog.hide();
                                progressDialog.cancel();
                                Glide.with(CourseDetials.this)
                                        .load(response.getString("image"))
                                        .dontAnimate()
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .into(course_image);
                                img = response.getString("image");
                                cname = response.getString("name");
                                course_title.setText(response.getString("name"));
                                instructor_name.setText(response.getString("instructorName"));
                                course_star.setText(response.getString("rating"));
                                if (response.getBoolean("iscomment"))
                                {
                                    my_comment_sec.setVisibility(View.VISIBLE);
                                    rating.setEnabled(true);
                                    rating.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    my_comment_sec.setVisibility(View.GONE);
                                    rating.setEnabled(false);
                                    rating.setVisibility(View.GONE);
                                }
                                startingmonth.setText(response.getString("startingmonth"));
                                duration.setText(response.getString("duration")+" "+response.getString("durationtype"));
                                course_desc.setText(response.getString("desc"));
                                startd = response.getString("startingmonth");
                                durations = response.getString("duration")+" "+response.getString("durationtype");
                                prices =response.getString("price");
                                price.setText("Rs. "+response.getString("price"));
                                JSONArray content = response.getJSONArray("content");
                                for (int i = 0; i < content.length(); i++) {
                                    String cont = content.getString(i);
                                    index.add(cont);
                                }
                                course_index.setLayoutManager(linearLayoutManager);
                                CourseIndexAdapter indexAdapter = new CourseIndexAdapter(index);

                                course_index.setAdapter(indexAdapter);
                                indexAdapter.notifyDataSetChanged();
                                rating.setRating((float) response.getDouble("mystars"));
                                add_to_cart.setOnClickListener(lisnet->{
                                    RequestQueue queu = Volley.newRequestQueue(CourseDetials.this);
                                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, getString(R.string.url) + "/courses/management/addtocart/" + cid, null, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Toast.makeText(CourseDetials.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(CourseDetials.this, "Please Login with your account...", Toast.LENGTH_SHORT).show();
                                        }
                                    }){
                                        @Override
                                        public Map<String, String> getHeaders() {
                                            HashMap<String, String> map = new HashMap<>();
                                            map.put("Authorization", "Bearer " + idToken);
                                            return map;
                                        }
                                    };
                                    queu.add(req);
                                });

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
        else {
            getCourse();
        }
        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser myprofile = FirebaseAuth.getInstance().getCurrentUser();
                if (myprofile!=null) {
                    Intent intent = new Intent(CourseDetials.this, CheckoutCourse.class);
                    intent.putExtra("cid", cid);
                    intent.putExtra("name", cname);
                    intent.putExtra("img",img);
                    intent.putExtra("cprice",prices);
                    intent.putExtra("cduration",durations);
                    intent.putExtra("startd",startd);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(CourseDetials.this, "Please login...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CourseDetials.this, MainActivity.class);
                    startActivity(intent);

                }
            }
        });


        comment_submit.setOnClickListener(listener->{
            String com = mycomment.getText().toString();
            if (!com.trim().isEmpty())
            {
                FirebaseUser myprofile = FirebaseAuth.getInstance().getCurrentUser();
                if (myprofile!=null) {
                    if (rating.getRating() > 0) {
                        pbar_comment.setVisibility(View.VISIBLE);
                        comment_submit.setVisibility(View.GONE);
                        myprofile.getIdToken(true).addOnCompleteListener(task -> {
                            String idToken = task.getResult().getToken();
                            RequestQueue requestQueue = Volley.newRequestQueue(CourseDetials.this);
                            JSONObject object = new JSONObject();
                            try {
                                object.put("courseid", cid);
                                object.put("comment", com);
                                object.put("star", rating.getRating());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.url) + "/courses/management/postComment", object, response -> {
                                my_comment_sec.setVisibility(View.GONE);
                                rating.setEnabled(false);
                                commentList.add(new CommentsModel("https://eitrawmaterials.eu/wp-content/uploads/2016/09/person-icon.png", "Me", rating.getRating() + "", "just now", com));
                                commentAdapter.notifyDataSetChanged();
                                pbar_comment.setVisibility(View.GONE);
                                comment_submit.setVisibility(View.VISIBLE);
                                Toast.makeText(CourseDetials.this, "Thank you for your support...", Toast.LENGTH_SHORT).show();
                            }, error -> {
                                pbar_comment.setVisibility(View.GONE);
                                comment_submit.setVisibility(View.VISIBLE);

                            }) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("Authorization", "Bearer " + idToken);
                                    return map;
                                }
                            };
                            requestQueue.add(objectRequest);
                        });
                    }
                    else
                    {
                        Toast.makeText(this, "please rate this course...", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "please login", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, "Comment can't empty", Toast.LENGTH_SHORT).show();
            }
        });
        curriculum.setOnClickListener(listener-> switchCategory(CURRICULUM));
        review.setOnClickListener(listener-> switchCategory(REVIEW));
    }
    public void resetBackground()
    {
        curriculum.setBackground(null);
        review_layout.setVisibility(View.GONE);
        course_index.setVisibility(View.GONE);
        review.setBackground(null);
    }
    public  void switchCategory(int ch)
    {
        if (ch!=CURRENT)
        {
            CURRENT = ch;
            resetBackground();
            if (ch == CURRICULUM)
            {
                curriculum.setBackground(getDrawable(R.drawable.round_for_green_course));
                curriculum.setTextColor(Color.parseColor("#FFFFFF"));
                review.setTextColor(Color.parseColor("#1D2D3A"));
                course_index.setVisibility(View.VISIBLE);
            }
            else if (ch == REVIEW) {
                review.setBackground(getDrawable(R.drawable.round_for_green_course));
                review.setTextColor(Color.parseColor("#FFFFFF"));
                curriculum.setTextColor(Color.parseColor("#1D2D3A"));
                review_layout.setVisibility(View.VISIBLE);
            }
        }
    }
    private void getCourse()
    {

        RequestQueue queue = Volley.newRequestQueue(CourseDetials.this);
        JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET, getString(R.string.url)+"/courses/single/"+cid, null, response -> {
            try {
                progressDialog.hide();
                progressDialog.cancel();
                Glide.with(CourseDetials.this)
                        .load(response.getString("image"))
                        .dontAnimate()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(course_image);
                course_title.setText(response.getString("name"));
                instructor_name.setText(response.getString("instructorName"));
                course_star.setText(response.getString("rating"));
                if (response.getBoolean("iscomment"))
                {
                    my_comment_sec.setVisibility(View.VISIBLE);
                    rating.setEnabled(true);
                    rating.setVisibility(View.VISIBLE);
                }
                else
                {
                    my_comment_sec.setVisibility(View.GONE);
                    rating.setEnabled(false);
                    rating.setVisibility(View.GONE);
                }
                startingmonth.setText(response.getString("startingmonth"));
                duration.setText(response.getString("duration")+" "+response.getString("durationtype"));
                course_desc.setText(response.getString("desc"));
                price.setText("Rs. "+response.getString("price"));
                JSONArray content = response.getJSONArray("content");
                for (int i = 0; i < content.length(); i++) {
                    String cont = content.getString(i);
                    index.add(cont);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CourseDetials.this);
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                course_index.setLayoutManager(linearLayoutManager);
                CourseIndexAdapter indexAdapter = new CourseIndexAdapter(index);

                course_index.setAdapter(indexAdapter);
                indexAdapter.notifyDataSetChanged();
                rating.setRating((float) response.getDouble("mystars"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });
        queue.add(request);
    }

}