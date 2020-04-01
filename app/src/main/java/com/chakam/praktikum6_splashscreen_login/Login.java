package com.chakam.praktikum6_splashscreen_login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chakam.praktikum6_splashscreen_login.Util.AppController;
import com.chakam.praktikum6_splashscreen_login.R;
import com.chakam.praktikum6_splashscreen_login.Util.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    ProgressDialog pDialog;
    Button btn_register, btn_login;
    EditText txt_username, txt_password;
    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private static final String TAG = Login.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public final static String TAG_USERNAME = "username";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedPreferences;
    boolean session = false;
    String username;

    public static final String my_shared_preferences = "my_sahred_preferences";
    public static final String session_status = "session_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            assert conMgr != null;
            if (conMgr.getActiveNetworkInfo() != null &&
                conMgr.getActiveNetworkInfo().isAvailable() &&
                conMgr.getActiveNetworkInfo().isConnected()){

            }else{
                Toast.makeText(getApplicationContext(),
                        "No Internet Connection",Toast.LENGTH_LONG).show();
            }
        }

        btn_login = (Button) findViewById(R.id.button_signinSignin);
        btn_register = (Button) findViewById(R.id.button_signupSignin);
        txt_username = (EditText) findViewById(R.id.et_emailSignin);
        txt_password = (EditText) findViewById(R.id.et_passwordSignin);

// Cek Session Login jika True maka langsung buka MainActivity
        sharedPreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedPreferences.getBoolean(session_status, false);
        username = sharedPreferences.getString(TAG_USERNAME,null);

        if (session){
            Intent intent = new Intent(Login.this, MainActivity.class);
                intent.putExtra(TAG_USERNAME, username);
                startActivity(intent);
                finish();
        }
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txt_username.getText().toString();
                String password = txt_password.getText().toString();

//   Mengecek kolom yang kosong
                if(username.trim().length() > 0 &&
                    password.trim().length() > 0 ){
                    if(conMgr.getActiveNetworkInfo() != null &&
                        conMgr.getActiveNetworkInfo().isAvailable() &&
                        conMgr.getActiveNetworkInfo().isConnected()){

                        cekLogin(username, password);
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            "kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });
    }

// Method Ceklogin
    private void cekLogin(final String username,
                          final String password){
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in....");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerAPI.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Login Response: " + response);
                        hideDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getInt(TAG_SUCCESS);

                            //Check For Eror node in json
                            if (success == 1) {
                                String username = jsonObject.getString(TAG_USERNAME);
                                Log.e("Successfully Login!", jsonObject.toString());
                                Toast.makeText(getApplicationContext(),
                                        jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                                //Menyimpan Login Ke Session
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(session_status, true);
                                editor.putString(TAG_USERNAME, username);
                                editor.commit();

                                //Memanggil MainActivity
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra(TAG_USERNAME, username);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: "+ error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
        }

        //Method showDialog
        private void showDialog(){
        if(!pDialog.isShowing())
            pDialog.show();
        }

        //Method hideDialog
        private void hideDialog(){
            if(pDialog.isShowing())
            pDialog.show();
        }
}
