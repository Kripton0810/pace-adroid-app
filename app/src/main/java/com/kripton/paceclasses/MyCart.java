package com.kripton.paceclasses;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCart#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCart extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyCart() {
    }
    public static MyCart newInstance(String param1, String param2) {
        MyCart fragment = new MyCart();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    RecyclerView recyclerView;
    List<CartModel> list;
    ProgressBar progressBar2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v =  inflater.inflate(R.layout.fragment_my_cart, container, false);
        recyclerView = v.findViewById(R.id.cartrv);
        progressBar2 = v.findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.VISIBLE);
        list = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null)
        {
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            String idToken = task.getResult().getToken();
                            RequestQueue q = Volley.newRequestQueue(requireContext());
                            JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, getString(R.string.url) + "/courses/management/getcart", null, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    Log.d("ResponseCart",response.toString());
                                    for (int i = 0; i < response.length() ; i++) {
                                        try {
                                            JSONObject object = response.getJSONObject(i);
                                            CartModel model = new CartModel(object.getString("id"),object.getString("courseid"),object.getString("cname"),object.getString("price"),object.getString("image") );
                                            list.add(model);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    progressBar2.setVisibility(View.GONE);
                                    CartAdapter adapter = new CartAdapter(list,requireActivity());
                                    adapter.setOnItemClicklistener(new CartAdapter.OnItemClickListener() {
                                        @Override
                                        public void onDeleteClick(int pos) {
                                            String url = "/courses/management/deletetocart/"+list.get(pos).getCourseid();
                                            Log.d("SendUrl",url);
                                            RequestQueue queue = Volley.newRequestQueue(requireContext());
                                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, getString(R.string.url) + url, null, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Log.d("DeleteRespo",response.toString());
                                                    list.remove(pos);
                                                    adapter.notifyItemRemoved(pos);
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {

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

                                        }
                                    });
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }){
                            @Override
                            public Map<String, String> getHeaders() {
                                HashMap<String, String> map = new HashMap<>();
                                map.put("Authorization", "Bearer " + idToken);
                                return map;
                            }
                        };
                            q.add(arrayRequest);


                        }
                    });
        }


        return v;
    }
}