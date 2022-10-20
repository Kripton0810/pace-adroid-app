package com.kripton.paceclasses;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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
    RecyclerView multipal_courses;
    AllCourseDisplayAdapter allCourseDisplayAdapter;
    List<AllCoursesDisplayModel> modelList;
    ShimmerFrameLayout shimmer_layout;
    List<PopularCourseModel> popular_course_model;
    LinearLayout my_linear_layout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        multipal_courses = view.findViewById(R.id.multipal_courses);
        my_linear_layout= view.findViewById(R.id.my_linear_layout);
        shimmer_layout = view.findViewById(R.id.shimmer_layout);
        allCourseDisplayer();
        return view;
    }
    public void allCourseDisplayer()
    {
        modelList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getString(R.string.url) + "/courses", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    popular_course_model = new ArrayList<>();
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        String catName = obj.getString("name");
                        JSONArray array = obj.getJSONArray("course");
                        for (int j = 0; j < array.length(); j++) {
                            JSONObject course = array.getJSONObject(j);
                            popular_course_model.add(new PopularCourseModel(course.getString("name"),course.getString("instuctorname"),course.getString("id"),course.getString("price"),course.getString("image"),course.getString("rating")));
                        }
                        shimmer_layout.stopShimmer();
                        shimmer_layout.hideShimmer();
                        shimmer_layout.setVisibility(View.GONE);
                        modelList.add(new AllCoursesDisplayModel(catName,popular_course_model));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                multipal_courses.setLayoutManager(layoutManager);
                my_linear_layout.setGravity(Gravity.START);
                multipal_courses.setVisibility(View.VISIBLE);
                allCourseDisplayAdapter  = new AllCourseDisplayAdapter(modelList,getActivity());
                multipal_courses.setAdapter(allCourseDisplayAdapter);
                allCourseDisplayAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }
}