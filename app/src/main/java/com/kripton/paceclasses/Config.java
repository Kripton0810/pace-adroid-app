package com.kripton.paceclasses;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public GoogleSignInClient config(Activity activity)
    {
        GoogleSignInOptions signInOptions= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id_2)).requestEmail().build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activity,signInOptions);
        return googleSignInClient;
    }
    public JSONObject getUser(Activity activity)
    {
        return null;
    }
    public void Signout(GoogleSignInClient client,Activity activity)
    {
        SharedPreferences preferences = activity.getSharedPreferences("Profile",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("name");
        editor.remove("phone");
        editor.remove("email");
        editor.apply();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        LoginManager.getInstance().logOut();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null)
        {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(activity, "Logout Successful...", Toast.LENGTH_SHORT).show();
            client.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }
}
