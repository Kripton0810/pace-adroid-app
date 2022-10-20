package com.kripton.paceclasses;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Signup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Signup extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam2;

    public Signup() {
        // Required empty public constructor
    }
    public static Signup newInstance(String param1, String param2) {
        Signup fragment = new Signup();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    TextInputEditText email,name,phone,password;
    RadioGroup radio_gender;
    Button signup;
    ProgressBar pbar;
    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    private static Pattern pattern;


    public boolean validateEmail(String email) {
        // non-static Matcher object because it's created from the input String
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    TextInputLayout pass_word;
    RadioButton r;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        email = view.findViewById(R.id.email);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        password = view.findViewById(R.id.password);
        radio_gender = view.findViewById(R.id.radio_gender);
        signup = view.findViewById(R.id.signup);
        pass_word = view.findViewById(R.id.pass_word);
        pbar = view.findViewById(R.id.pbar);
        signup.setOnClickListener(view1 -> {
            String gender=null;
            try {
                int id = radio_gender.getCheckedRadioButtonId();
                r = view.findViewById(id);
                gender = r.getText().toString();
            }catch (Exception e) {
                Toast.makeText(getContext(), "Select gender", Toast.LENGTH_SHORT).show();
            }
            if (checker(gender))
            {
                Toast.makeText(getContext(), "All done", Toast.LENGTH_SHORT).show();
                JSONObject object = new JSONObject();
                try {
                    object.put("email", email.getText().toString());
                    object.put("name", name.getText().toString());
                    object.put("phone", phone.getText().toString());
                    object.put("gender", gender);
                    object.put("password", password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pbar.setVisibility(View.VISIBLE);
                signup.setVisibility(View.GONE);
                RequestQueue queue = Volley.newRequestQueue(requireContext());
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getString(R.string.url) + "/auth/participant/register", object, response -> {
                    pbar.setVisibility(View.GONE);
                    signup.setVisibility(View.VISIBLE);
                    try {
                        Toast.makeText(getContext(), response.getString("msg") + " Pls. verify your account check mail", Toast.LENGTH_SHORT).show();
                        final FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame, new Login());
                        ft.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    pbar.setVisibility(View.GONE);
                    signup.setVisibility(View.VISIBLE);
                    try {
                        if (error.networkResponse.data != null) {
                            String res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            JSONObject object1 = new JSONObject(res);
                            Toast.makeText(getContext(), object1.getString("msg"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "User already registered " + error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(3000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);
            }


        });
        return view;
    }
    boolean checker(String gender){
        if (!TextUtils.isEmpty(Objects.requireNonNull(email.getText()).toString()))
        {
            if (validateEmail(email.getText().toString()))
            {
                if (!TextUtils.isEmpty(Objects.requireNonNull(name.getText()).toString()))
                {
                    if (!TextUtils.isEmpty(Objects.requireNonNull(phone.getText()).toString()))
                    {
                        if (!TextUtils.isEmpty(Objects.requireNonNull(password.getText()).toString())&&(password.length()>=8)) {
                            if (gender != null) {

                                return true;
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Gender required", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            password.setError("Password length min 8");
                        }
//                            else
//                            {
//                            }
                    }
                    else
                    {
                        phone.setError("Phone number required");
                    }

                }
                else
                {
                    name.setError("Name required");
                }
            }
            else
            {
                email.setError("Valid email required");

            }
        }
        else
        {
            email.setError("Email required");
        }
        return false;
    }
}