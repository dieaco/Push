package gcm.play.android.samples.com.gcmquickstart;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import gcm.play.android.samples.com.gcmquickstart.data.UserData;

public class CreatePassword extends AppCompatActivity {

    private EditText etUsername;
    private EditText etNewPassword;
    private EditText etConfirmNewPassword;
    private Button btnCuentas;
    private Button btnRegister;

    private String userName = "";
    private int userId = 0;
    private boolean userIsLogged = false;

    private String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
        getSupportActionBar().hide();

        //Obtenemos preferencias almacenadas localmente para inicio de sesión
        userName = UserData.getUserAccount(this);
        userId = UserData.getUserId(this);
        userIsLogged = UserData.isLogged(this);

        //Valida si existen preferencias
        if(userIsLogged){
            launchNotificationsListActivty();
        }else if(userName.equals("") && userId == 0){
            initViews();
        }else{
            launchLoginActivity();
        }
    }

    //Inicializador de vistas y objetos
    public void initViews(){
        etUsername = (EditText)findViewById(R.id.etUsername2);
        etNewPassword = (EditText)findViewById(R.id.etNewPassword);
        etConfirmNewPassword = (EditText)findViewById(R.id.etConfirmPassword);
        btnCuentas = (Button)findViewById(R.id.btnCuentas2);
        btnRegister = (Button)findViewById(R.id.btnRegister);

        //Establece como no editable el edit text de Usuario
        etUsername.setKeyListener(null);

        //Evento click para mostrar dialogo con cuentas del dispositivo
        btnCuentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAccounts().show();
            }
        });

        //Evento click para registrar usuario y contraseña
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    //Cuadro de dialogo para mostrar cuentas del dispositivo
    public AlertDialog loadAccounts(){
        //contador de cuentas
        int cont = 0;
        final ArrayAdapter<String> array = new ArrayAdapter<String>(
                CreatePassword.this,
                android.R.layout.select_dialog_singlechoice
        );

        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                String emailType = account.type;
                array.add(possibleEmail);
                cont++;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(CreatePassword.this, R.style.MyTheme);
        builder.setTitle("Seleccione una cuenta")
                .setAdapter(array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etUsername.setText(array.getItem(which));
                    }
                });

        return builder.create();

    }

    //Registra usuario en el servidor
    public void registerUser(){
        if(validateFields()){
            serverRequest();

        }
    }

    //Consulta al servidor para obtener String del PHP
    public void serverRequest(){
        final ProgressDialog dialog = new ProgressDialog(this, R.style.MyTheme);
        dialog.setMessage("Cargando...");
        dialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "http://suterm.comli.com/regID.php";
        String url = "http://suterm.eu5.org/registerUser.php";
        StringRequest putRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {

                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            int ERROR = jsonObject.getInt("ERROR");

                            Log.d("ERROR", ""+ERROR);
                            if(ERROR == 1){
                                etUsername.setError(getString(R.string.repeatedAccount));
                                dialog.dismiss();
                            }else{
                                Log.d("USER_ID",""+userId);
                                int userId = jsonObject.getInt("user_id");
                                //Se almacenan preferencias de usuario
                                UserData.setUserId(getApplicationContext(), userId);
                                UserData.setUserAccount(getApplicationContext(), etUsername.getText().toString());
                                //Lanza actividad de inicio de sesión
                                launchLoginActivity();
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
                        //Log.d("Error.Response", token);
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                String userN = etUsername.getText().toString();
                String userP = etNewPassword.getText().toString();
                Log.d("VALORES", "Username: "+userN+" - Userpassword: "+userP);

                params.put("userName", etUsername.getText().toString());
                params.put("userPassword", etNewPassword.getText().toString());
                params.put("userType", "1");
                params.put("userCompany", "1");
                params.put("userIsLogged", "0");

                return params;
            }

        };

        queue.add(putRequest);

    }

    //Valida que no haya campos vacíos y que los campos cumplan con las características solicitadas
    public boolean validateFields(){
        if(etUsername.getText().toString().equals("")){
            etUsername.setError(getString(R.string.emptyUser));
            return  false;
        }else if(etNewPassword.getText().toString().equals("")){
            etNewPassword.requestFocus();
            etNewPassword.setError(getString(R.string.emptyPassword));
            return false;
        }else if(!etNewPassword.getText().toString().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8}$")){
            etNewPassword.requestFocus();
            etNewPassword.setError(getString(R.string.wrongPassword));
            return false;
        }else if(!etConfirmNewPassword.getText().toString().equals(etNewPassword.getText().toString())){
            etConfirmNewPassword.requestFocus();
            etConfirmNewPassword.setError(getString(R.string.equalPassword));
            return false;
        }else{
            return true;
        }
    }

    //Lanza actividad de inicio de sesión
    public void launchLoginActivity(){
        Intent intent = new Intent(CreatePassword.this, MainActivity.class);
        startActivity(intent);
        CreatePassword.this.finish();
    }

    //Lanza actividad principal
    public void launchNotificationsListActivty(){
        Intent intent = new Intent(CreatePassword.this, NotificationsList.class);
        startActivity(intent);
        CreatePassword.this.finish();
    }

}
