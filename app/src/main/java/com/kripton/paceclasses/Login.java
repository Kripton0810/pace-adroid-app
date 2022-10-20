package com.kripton.paceclasses;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.BuildConfig;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextInputEditText password, email;
    Button login;
    FirebaseAuth auth;
    ProgressBar pbar, pbar_login;
    FirebaseUser user;
    FlexboxLayout login_part;
    TextView verificaion, skip, create_account;
    ImageView google, facebook, github;
    GoogleSignInClient googleSignInClient;
    LoginButton fb_login_button;
    CallbackManager callbackManager;
    ActivityResultLauncher<Intent> signinactivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        if (task.isSuccessful()) {
                            try {
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                try {
                                    AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                                    Log.d("AuthID", account.getIdToken());
                                    auth.signInWithCredential(authCredential).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            login_part.setVisibility(View.GONE);
                                            pbar_login.setVisibility(View.VISIBLE);
                                            firebaseAuthWithGoogle(account.getIdToken());
                                        } else {
                                            Log.d("API 1", "Error-> " + task1.getException());

                                        }

                                    });

                                } catch (Exception ignored) {
                                }
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            });
    private String mParam2;
    public Login() {
        // Required empty public constructor
    }

    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
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
            // TODO: Rename and change types of parameters
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
        googleSignInClient = new Config().config(requireActivity());
        SharedPreferences preferences = requireActivity().getSharedPreferences("Profile", MODE_PRIVATE);
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        verificaion = view.findViewById(R.id.verificaion);
        email = view.findViewById(R.id.email);
        facebook = view.findViewById(R.id.facebook);
        create_account = view.findViewById(R.id.create_account);
        pbar = view.findViewById(R.id.pbar);
        callbackManager = CallbackManager.Factory.create();
        pbar_login = view.findViewById(R.id.pbar_login);
        login_part = view.findViewById(R.id.login_part);
        fb_login_button = view.findViewById(R.id.fb_login_button);
        password = view.findViewById(R.id.password);
        login = view.findViewById(R.id.login);
        skip = view.findViewById(R.id.skip);
        github = view.findViewById(R.id.github);
        google = view.findViewById(R.id.google);

        create_account.setOnClickListener(view15 -> {
            final FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame, new Signup(), "NewFragmentTag");
            ft.commit();
        });
        google.setOnClickListener(view16 -> googleSignIn());
        fb_login_button.setReadPermissions("email", "public_profile");

        fb_login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                Toast.makeText(requireContext(), "step 4", Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.d("facebook-id-tok", loginResult.getAccessToken().toString());
            }

            @Override
            public void onCancel() {
//                Toast.makeText(requireContext(), "step 5", Toast.LENGTH_SHORT).show();
                Log.d("Token-fb", "Cancel");

            }

            @Override
            public void onError(@NonNull FacebookException e) {
//                Toast.makeText(requireContext(), "step 5", Toast.LENGTH_SHORT).show();
                Log.d("Token-fb", e.toString());

            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fb_login_button.performClick();
//                Toast.makeText(requireContext(), "step 1", Toast.LENGTH_SHORT).show();
            }
        });
        github.setOnClickListener(view14 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View v = LayoutInflater.from(requireContext()).inflate(R.layout.github_login_box, null);
            builder.setView(v);
            TextInputEditText gemail = v.findViewById(R.id.gemail);
            Button cnf = v.findViewById(R.id.submit);
            AlertDialog dialog = builder.create();
            dialog.show();
            cnf.setOnClickListener(view1 -> {
                login_part.setVisibility(View.GONE);
                pbar_login.setVisibility(View.VISIBLE);
                OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
                Task<AuthResult> pendingResultTask = auth.getPendingAuthResult();

                provider.addCustomParameter("login", Objects.requireNonNull(gemail.getText()).toString());
                List<String> scopes =
                        new ArrayList<String>() {
                            {
                                add("user:email");
                            }
                        };

                provider.setScopes(scopes);
                if (pendingResultTask != null) {
                    pendingResultTask
                            .addOnSuccessListener(
                                    authResult -> {
                                        auth = FirebaseAuth.getInstance();
                                        user = auth.getCurrentUser();
                                        assert user != null;
                                        user.getIdToken(true)
                                                .addOnCompleteListener(task -> {
                                                    dialog.dismiss();
                                                    signInToken(task);
                                                });
                                    })
                            .addOnFailureListener(
                                    e -> {
                                        login_part.setVisibility(View.VISIBLE);
                                        pbar_login.setVisibility(View.GONE);
                                        // Handle failure.
                                        Toast.makeText(requireContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                } else {
                    auth
                            .startActivityForSignInWithProvider(/* activity= */ requireActivity(), provider.build())
                            .addOnSuccessListener(
                                    authResult -> {
                                        auth = FirebaseAuth.getInstance();
                                        user = auth.getCurrentUser();
                                        assert user != null;
                                        user.getIdToken(true)
                                                .addOnCompleteListener(task -> {
                                                    dialog.dismiss();
                                                    signInToken(task);
                                                });
                                    })
                            .addOnFailureListener(
                                    e -> {
                                        login_part.setVisibility(View.VISIBLE);
                                        pbar_login.setVisibility(View.GONE);
                                        // Handle failure.
                                        Toast.makeText(requireContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                }

            });

        });


        skip.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), MainDashedboard.class);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("skip", true);
            editor.apply();
            startActivity(intent);
            requireActivity().finish();
        });
        verificaion.setOnClickListener(view12 -> {
            if (!TextUtils.isEmpty(Objects.requireNonNull(email.getText()).toString())) {
                String unm = email.getText().toString();
                JSONObject object = new JSONObject();
                try {
                    object.put("email", unm);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestQueue queue = Volley.newRequestQueue(requireContext());
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getString(R.string.url) + "/participant/management/verification", object, response -> {
                    try {
                        verificaion.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> {
                    try {
                        String res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject object1 = new JSONObject(res);
                        Toast.makeText(getContext(), object1.getString("msg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
                queue.add(request);
            } else {
                Toast.makeText(getContext(), "Email required... fill the email box", Toast.LENGTH_SHORT).show();
            }
        });
        login.setOnClickListener(view13 -> loginWithPassword());
        return view;
    }

    public void googleSignIn() {
        Intent signinIntent = googleSignInClient.getSignInIntent();
        signinactivity.launch(signinIntent);
    }

    public void loginWithPassword() {
        String unm = Objects.requireNonNull(email.getText()).toString();
        String pass = Objects.requireNonNull(password.getText()).toString();
        if (!TextUtils.isEmpty(unm)) {
            if (!TextUtils.isEmpty(pass)) {
                pbar.setVisibility(View.VISIBLE);
                login.setVisibility(View.GONE);
                JSONObject userDetials = new JSONObject();
                try {
                    userDetials.put("email", unm);
                    userDetials.put("password", pass);
                    RequestQueue queue = Volley.newRequestQueue(requireContext());
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getString(R.string.url) + "/auth/participant/login", userDetials, response -> {
                        pbar.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                        verificaion.setVisibility(View.INVISIBLE);
                        auth.signInWithEmailAndPassword(unm, pass)
                                .addOnCompleteListener(requireActivity(), task -> {
                                    auth = FirebaseAuth.getInstance();
                                    user = auth.getCurrentUser();
                                    SharedPreferences preferences = requireActivity().getSharedPreferences("Profile", MODE_PRIVATE);
                                    if (preferences.getBoolean("skip", false)) {

                                        Toast.makeText(getContext(), "Login Successful...", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent i = new Intent(getContext(), MainDashedboard.class);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putBoolean("skip", true);
                                        editor.apply();
                                        startActivity(i);
                                    }
                                    requireActivity().finish();
                                });
                    }, error -> {
                        pbar.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                        try {
                            String res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            JSONObject object = new JSONObject(res);
                            Toast.makeText(getContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                            if (error.networkResponse.statusCode == 403) {
                                verificaion.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                    queue.add(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                password.setError("Password require");
            }
        } else {
            email.setError("Email require");
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
                        assert user != null;
                        user.getIdToken(true).addOnCompleteListener(this::signInToken);
                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    public void signInToken(Task<GetTokenResult> task) {
        String idToken = task.getResult().getToken();
        JSONObject object = new JSONObject();
        try {
            object.put("idToken", idToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getString(R.string.url) + "/auth/participant/auth-social-user", object, response -> {
            try {
                login_part.setVisibility(View.VISIBLE);
                pbar_login.setVisibility(View.GONE);
                Log.d("response", response.toString());
                if (!TextUtils.equals(response.getString("phone"), "")) {
                    SharedPreferences preferences = requireActivity().getSharedPreferences("Profile", MODE_PRIVATE);
                    if (preferences.getBoolean("skip", false)) {
                        Toast.makeText(getContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(getContext(), MainDashedboard.class);
                        startActivity(i);
                    }

                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w("TokenFails", "Fetching FCM registration token failed", task.getException());
                                        return;
                                    }
                                    // Get new FCM registration token
                                    String token = task.getResult();
                                    Log.d("FirebaseToken", token);
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        user.getIdToken(true)
                                                .addOnCompleteListener(task2 -> {
                                                    String idToken = task2.getResult().getToken();
                                                    JSONObject object = new JSONObject();
                                                    try {
                                                        object.put("devicetoken", token);
                                                        String url = getString(R.string.url) + "/participant/management/firebasetoken";
                                                        RequestQueue queue = Volley.newRequestQueue(requireContext());
                                                        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                Log.d("Notification", response.toString());
                                                            }
                                                        }, error -> Log.d("Notification", error.getMessage())) {
                                                            @Override
                                                            public Map<String, String> getHeaders() {
                                                                HashMap<String, String> map = new HashMap<>();
                                                                map.put("Authorization", "Bearer " + idToken);
                                                                return map;
                                                            }
                                                        };
                                                        queue.add(objectRequest);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                });
                                    }
                                }
                            });
                    requireActivity().finish();
                } else {
                    auth = FirebaseAuth.getInstance();
                    user = auth.getCurrentUser();
                    Toast.makeText(getContext(), "Move to registration part", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    GoogleFacebookSignup signup = new GoogleFacebookSignup();
                    bundle.putString("email", user.getEmail());
                    bundle.putString("name", user.getDisplayName());

                    signup.setArguments(bundle);
                    final FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frame, signup);
                    ft.commit();

//                        fragment.setArguments(bundle);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            login_part.setVisibility(View.VISIBLE);
            pbar_login.setVisibility(View.GONE);
            try {
                String res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject object1 = new JSONObject(res);
                Toast.makeText(getContext(), object1.getString("msg"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        queue.add(request);
    }

    private void handleFacebookAccessToken(AccessToken token) {
//        Toast.makeText(requireContext(), "step 3", Toast.LENGTH_SHORT).show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {

                            if (task.isSuccessful()) {
                                pbar_login.setVisibility(View.VISIBLE);
                                login_part.setVisibility(View.GONE);
                                user = auth.getCurrentUser();

                                if (user != null) {
                                    user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                                signInToken(task);
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), "No user found!!", Toast.LENGTH_SHORT).show();
                                    pbar_login.setVisibility(View.GONE);
                                    login_part.setVisibility(View.VISIBLE);
                                }

                            } else {
                                Log.d("Session_token", task.getResult().toString());
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "This email is sign-in with different method", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            LoginManager.getInstance().logOut();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(requireContext(), "step 2", Toast.LENGTH_SHORT).show();
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}