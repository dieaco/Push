/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gcm.play.android.samples.com.gcmquickstart;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import gcm.play.android.samples.com.gcmquickstart.data.UserData;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnIniciarSesion;
    private CheckBox cbRecordar;

    private String userName = "";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        etUsername = (EditText)findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);
        btnIniciarSesion = (Button)findViewById(R.id.btnIniciarSesion);
        cbRecordar = (CheckBox)findViewById(R.id.cbRecordar);

        progressDialog = new ProgressDialog(this, R.style.MyTheme);

        //Se establece como usuario la cuenta registrada por el usuario para que haga su inicio de sesión
        userName = UserData.getUserAccount(this);
        Log.d("USER_ACCOUNT_LOCAL", userName);
        etUsername.setText(userName);

        //Establece como no editable el edit text de Usuario
        etUsername.setKeyListener(null);

        //progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setTitle("Registration ID");
        progressDialog.setMessage("Espere un momento...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progressDialog.dismiss();
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Toast.makeText(getApplicationContext(),getString(R.string.gcm_ready),Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(),getString(R.string.gcm_not_ready),Toast.LENGTH_LONG).show();

                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        //Evento click para iniciar sesión
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSession();
            }
        });

    }

    //Inicio de sesión y registro del RegId en la base de datos
    public void startSession(){
        progressDialog.setTitle("Inicio de sesión");
        progressDialog.setMessage("Validando...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if(validateFields()){
            validateLogin();
        }
    }

    //Validación de campos
    public boolean validateFields(){
        if(etPassword.getText().toString().equals("")){
            progressDialog.dismiss();
            etPassword.requestFocus();
            etPassword.setError(getString(R.string.emptyPassword));
            return false;
        }else if(!etPassword.getText().toString().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8}$")){
            progressDialog.dismiss();
            etPassword.requestFocus();
            etPassword.setError(getString(R.string.wrongPassword));
            return false;
        }else{
            return true;
        }
    }

    //Valida inicio de sesión desde  servidor
    public void validateLogin(){


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://suterm.eu5.org/validateLoginApp.php";
        StringRequest putRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {

                    public void onResponse(String response) {
                        // response
                        try {
                            progressDialog.dismiss();
                            Log.d("RESPONSE", response.toString());
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            int ERROR = jsonObject.getInt("ERROR");

                            //Validación de la respuesta JSON
                            if(ERROR == 0){
                                if(cbRecordar.isChecked())
                                    UserData.setLogged(MainActivity.this, true);
                                Intent intent = new Intent(MainActivity.this, NotificationsList.class);
                                startActivity(intent);
                                MainActivity.this.finish();
                            }else{
                                Toast.makeText(MainActivity.this, getString(R.string.wrongValidation), Toast.LENGTH_SHORT).show();
                                etPassword.requestFocus();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        progressDialog.dismiss();
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                //Reg ID almacenando localmente
                String token = UserData.getRegId(MainActivity.this);Log.d("REGID:", token);
                //Obtener el número serial del dispositivo
                String numSerie = Build.SERIAL;
                //User ID almacenado localmente
                int userId = UserData.getUserId(MainActivity.this);
                //Verificamos si se desea mantener sesión abierta o no
                boolean isLogged = cbRecordar.isChecked();
                String userIsLogged;
                if(isLogged)
                    userIsLogged = "1";
                else
                    userIsLogged = "0";

                Map<String, String>  params = new HashMap<String, String>();

                params.put("regisId", token.toString());
                params.put("numSerie", numSerie);
                params.put("userId", ""+userId);
                params.put("userIsLogged", userIsLogged);
                params.put("userName", etUsername.getText().toString());
                params.put("userPassword", etPassword.getText().toString());

                return params;
            }

        };

        queue.add(putRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
