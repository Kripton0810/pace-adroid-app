package com.kripton.paceclasses;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoogleFacebookSignup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoogleFacebookSignup extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GoogleFacebookSignup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GoogleFacebookSignup.
     */
    // TODO: Rename and change types and number of parameters
    public static GoogleFacebookSignup newInstance(String param1, String param2) {
        GoogleFacebookSignup fragment = new GoogleFacebookSignup();
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

    TextInputEditText email,name,phone;
    RadioGroup radio_gender;
    Button signup;
    ProgressBar pbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_google_facebook_signup, container, false);
        Bundle bundle = this.getArguments();
        email = view.findViewById(R.id.email);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        radio_gender = view.findViewById(R.id.radio_gender);
        signup = view.findViewById(R.id.signup);
        pbar = view.findViewById(R.id.pbar);
        if (bundle != null)
        {
            String eml = bundle.getString("email","");
            String nm = bundle.getString("name","");
            email.setText(eml);
            name.setText(nm);
            email.setClickable(false);
            name.setClickable(false);
            email.setFocusable(false);
            name.setFocusable(false);
        }
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();

                user.getIdToken(true)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            @Override
                            public void onComplete(@NonNull Task<GetTokenResult> task) {

                                int id = radio_gender.getCheckedRadioButtonId();
                                --id;
                                RadioButton r = (RadioButton) radio_gender.getChildAt(id);
                                String selectedtext = r.getText().toString();
                                String idToken = task.getResult().getToken();
                                JSONObject object = new JSONObject();
                                try {
                                    object.put("phone", phone.getText().toString());
                                    object.put("gender", selectedtext);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                RequestQueue queue = Volley.newRequestQueue(getContext());

                                pbar.setVisibility(View.VISIBLE);
                                signup.setVisibility(View.GONE);
                                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getString(R.string.url) + "/participant/management/complete-profile", object, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        pbar.setVisibility(View.GONE);
                                        signup.setVisibility(View.VISIBLE);
                                        try {
                                            Toast.makeText(getContext(), response.getString("msg") , Toast.LENGTH_SHORT).show();
                                            getActivity().finish();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        pbar.setVisibility(View.GONE);
                                        signup.setVisibility(View.VISIBLE);
                                        try {
                                            if (error.networkResponse.data != null) {
                                                String res = new String(error.networkResponse.data, "utf-8");
                                                JSONObject object = new JSONObject(res);
                                                Toast.makeText(getContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
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
                                request.setRetryPolicy(new DefaultRetryPolicy(3000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                queue.add(request);


                            }
                        });
            }
        });

        return view;
    }
}