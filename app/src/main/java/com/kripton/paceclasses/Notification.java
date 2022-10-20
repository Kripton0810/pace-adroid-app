package com.kripton.paceclasses;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Notification extends AppCompatActivity {
    RecyclerView notification_rv;
    List<NotificationModel> list;
    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        list = new ArrayList<>();
        notification_rv = findViewById(R.id.notification_rv);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        getSupportActionBar().setTitle("My Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Notification.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        notification_rv.setLayoutManager(linearLayoutManager);
        if (user!=null)
        {
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        String idToken = task.getResult().getToken();
                        RequestQueue queue = Volley.newRequestQueue(Notification.this);
                        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getString(R.string.url) + "/participant/management/mynotification", null, response -> {
                            try {
                                for (int i = 0; i < response.length() ; i++) {
                                    JSONObject resp = response.getJSONObject(i);
                                    list.add(new NotificationModel(resp.getString("id"),resp.getString("msg")));
                                }
                                Log.d("Notification", response.length()+"");

                                NotificationAdapter adapter = new NotificationAdapter(list,Notification.this);
                                adapter.setOnItemClicklistener(new NotificationAdapter.OnItemClickListener() {
                                    @Override
                                    public void onDeleteClick(int pos) {
                                        RequestQueue queue = Volley.newRequestQueue(Notification.this);
                                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, getString(R.string.url) + "/participant/management/notification/" + list.get(pos).getId(), null, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                list.remove(pos);
                                                adapter.notifyItemRemoved(pos);
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });
                                        queue.add(request);
                                    }
                                });
                                notification_rv.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
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
}