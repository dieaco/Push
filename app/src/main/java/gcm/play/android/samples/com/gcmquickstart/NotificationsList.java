package gcm.play.android.samples.com.gcmquickstart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import gcm.play.android.samples.com.gcmquickstart.adapters.CustomList;
import gcm.play.android.samples.com.gcmquickstart.data.UserData;
import gcm.play.android.samples.com.gcmquickstart.parsers.ParseJson;

public class NotificationsList extends AppCompatActivity {

    private static final String JSON_URL = "http://suterm.eu5.org/SendData.php";
    private ImageView btnGet;
    private ListView listView;
    private ProgressDialog progressDialog;
    private TextView tvSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_list);

        /* PASO DEL MENSAJE DE LA NOTIFICACIÓN ATRAVES DE BUNDLE PARA MOSTRAR UN TOAST */
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String mensaje = extras.getString("message");
            //Toast.makeText(this, "Mensaje: "+mensaje, Toast.LENGTH_LONG).show();
        }
        /* TERMINA MENSAJE DE LA NOTIFICACIÓN PARA MOSTRAR TOAST */
        initViews();


    }

    @Override
    protected void onResume(){
        progressDialog.setTitle("Aviso");
        progressDialog.setMessage("Cargando Mensajes...");
        progressDialog.show();
        super.onResume();
        //Carga ListView con los datos de la base
        sendRequest();
        //Animación de slogan
        //tvSlogan.animate().rotationX(720f).setDuration(5000);
    }

    //Inicializa todos los elementos de la aplicación
    public void initViews(){
        //btnGet = (ImageView)findViewById(R.id.btnGet);
        listView = (ListView)findViewById(R.id.listView);
        //tvSlogan = (TextView)findViewById(R.id.tvSlogan);

        //Iniialización de dialogo de progreso
        progressDialog = new ProgressDialog(this, R.style.MyTheme);

        //Evento click para refrescar listView de noitificaciones
        /*btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Aviso");
                progressDialog.setMessage("Cargando Mensajes...");
                progressDialog.show();
                //Actuliza la lista para visualizar nuevas notificaciones
                sendRequest();
            }
        });*/

        //Evento click para cada elemento de la lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView vId = (TextView) view.findViewById(R.id.textViewId);
                TextView vMessage = (TextView) view.findViewById(R.id.textViewName);

                String messageId = vId.getText().toString();
                String message = vMessage.getText().toString();

                Log.d("ID_ITEM: ", vId.getText().toString() + " - MESSAGE: " + vMessage.getText().toString());

                updateMessageStatus(messageId, message);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView vId = (TextView) view.findViewById(R.id.textViewId);
                String messageId = vId.getText().toString();
                Toast.makeText(NotificationsList.this,"Item:"+messageId, Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    //Actualización o inserción en el servidor del status de cada notificación
    public void updateMessageStatus(final String messageId, final String message){
        progressDialog.setTitle("Aviso");
        progressDialog.setMessage("Actualizando...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "http://suterm.comli.com/regID.php";
        String url = "http://suterm.eu5.org/updateMessageStatus.php";
        StringRequest putRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {

                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        // response
                        Log.d("Response", response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            int ERROR = jsonObject.getInt("ERROR");

                            Log.d("ERROR", ""+ERROR);

                            messageConfirmation(messageId, message).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                //Extrae UserId Local
                int userId = UserData.getUserId(NotificationsList.this);

                Map<String, String>  params = new HashMap<String, String>();

                Log.d("VALORES", "MESSAGEID: " + messageId + " - USERID: " + userId);

                params.put("messageId", messageId);
                params.put("userId", ""+userId);

                return params;
            }

        };

        queue.add(putRequest);
    }

    //Dialogo de confirmación
    public AlertDialog messageConfirmation(String messageId, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme)
                .setTitle("SUTERM")
                .setMessage("# MENSAJE: "+messageId+" - TEXTO: "+message)
                .setCancelable(true)
                .setPositiveButton("Listo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Actualización del listado una vez que se presiona el botón Listo
                        sendRequest();
                    }
                });

        return builder.create();
    }

    //Consulta de Notificaciones a la BD
    private void sendRequest(){
        RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "http://suterm.comli.com/regID.php";
        String url = "http://suterm.eu5.org/getMessages.php";
        StringRequest putRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {

                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        // response
                        Log.d("Response", response);
                        showJSON(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                //Extrae UserId Local
                int userId = UserData.getUserId(NotificationsList.this);

                Map<String, String> params = new HashMap<String, String>();
                Log.d("USERID:"," - "+userId);
                params.put("userId", "" + userId);

                return params;
            }

        };

        queue.add(putRequest);
    }

    //Parsea los datos que vienen de la base para adaptarlos al ListView
    public void showJSON(String json){
        /*int userIdLocal = UserData.getUserId(getApplicationContext());
        Toast.makeText(getApplicationContext(),"UserIDLocal: "+userIdLocal,Toast.LENGTH_LONG).show();*/
        ParseJson pj = new ParseJson(json);
        pj.parseJSON();
        CustomList cl = new CustomList(this, ParseJson.id,ParseJson.mensaje,ParseJson.timestamp, ParseJson.isRead, ParseJson.userId);
        // CustomList cls = new CustomList(this, ParseJson.timestamp);
        listView.setAdapter(cl);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.signout) {
            signoutConfirmation().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Dialogo de confirmación para cierre de sesión
    public AlertDialog signoutConfirmation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme)
                .setTitle("SUTERM")
                .setMessage("¿Está seguro que desea cerrar su sesión?")
                .setCancelable(true)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Actualización del listado una vez que se presiona el botón Listo
                        signOutFromServer();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    //Cerrar la sesión desde el servidor
    public void signOutFromServer(){
        progressDialog.setTitle("Aviso");
        progressDialog.setMessage("Actualizando...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "http://suterm.comli.com/regID.php";
        String url = "http://suterm.eu5.org/updateSessionStatus.php";
        StringRequest putRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {

                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        // response
                        Log.d("Response", response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            int ERROR = jsonObject.getInt("ERROR");

                            Log.d("ERROR", "" + ERROR);

                            UserData.setLogged(NotificationsList.this, false);
                            UserData.setRegId(NotificationsList.this, "");

                            launchMainActivity();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                //Extrae UserId Local y RegIdLocal
                String regId = UserData.getRegId(NotificationsList.this);
                int userId = UserData.getUserId(NotificationsList.this);

                Map<String, String> params = new HashMap<String, String>();

                Log.d("VALORES", "REGID: " + regId + " - USERID: " + userId);

                params.put("regisId", regId);
                params.put("userId", "" + userId);

                return params;
            }

        };

        queue.add(putRequest);
    }

    public void launchMainActivity(){
        Intent intent = new Intent(NotificationsList.this, MainActivity.class);
        startActivity(intent);
        NotificationsList.this.finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("onDestroy", "Actulizació de user device status");
        if(!UserData.isLogged(NotificationsList.this)){
            updateUserDeviceStatus();
        }

    }

    //Actualiza User_device_status en la BD para evitar la recepción de Notificaciones si su sesión no está abierta
    public void updateUserDeviceStatus(){

        RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "http://suterm.comli.com/regID.php";
        String url = "http://suterm.eu5.org/updateDeviceStatus.php";
        StringRequest putRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {

                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                //Extrae UserId Local y RegIdLocal
                String regId = UserData.getRegId(NotificationsList.this);
                int userId = UserData.getUserId(NotificationsList.this);

                Map<String, String> params = new HashMap<String, String>();

                Log.d("VALORES", "REGID: " + regId + " - USERID: " + userId);

                params.put("regisId", regId);
                params.put("userId", "" + userId);

                return params;
            }

        };

        queue.add(putRequest);
    }
}
