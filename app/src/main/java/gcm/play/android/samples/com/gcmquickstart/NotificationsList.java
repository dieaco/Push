package gcm.play.android.samples.com.gcmquickstart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gcm.play.android.samples.com.gcmquickstart.adapters.MessageAdapter;
import gcm.play.android.samples.com.gcmquickstart.data.UserData;
import gcm.play.android.samples.com.gcmquickstart.images.ImagesUtil;
import gcm.play.android.samples.com.gcmquickstart.models.Message;
import gcm.play.android.samples.com.gcmquickstart.views.RoundedImageView;
import gcm.play.android.samples.com.gcmquickstart.webservices.GetMessages;

public class NotificationsList extends AppCompatActivity {

    private ListView listView;
    private ProgressDialog progressDialog;

    private Spinner spinner;
    private String[] messages_to_show = {"Mostrar 10", "Mostrar 25", "Mostrar 50", "Mostrar 100"};

    String quantity_to_show = "10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Transformar drawable a Bitmpap
        Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sutermsg);
        //Establecer border redondeado a Bitmap
        Drawable drawable = new BitmapDrawable(getResources(), RoundedImageView.createCircleBitmap(sourceBitmap));
        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(drawable);
        actionBar.setDisplayUseLogoEnabled(true);

        setContentView(R.layout.activity_notifications_list);
        initViews();


    }

    @Override
    protected void onResume(){
        progressDialog.setTitle("Aviso");
        progressDialog.setMessage("Cargando Mensajes...");
        progressDialog.show();
        super.onResume();
        //Carga ListView con los datos de la base
        sendRequest(quantity_to_show);
        //Animación de slogan
        //tvSlogan.animate().rotationX(720f).setDuration(5000);
    }

    //Inicializa todos los elementos de la aplicación
    public void initViews(){
        //Inicialización de ListView
        listView = (ListView)findViewById(R.id.listView);
        //Iniialización de dialogo de progreso
        progressDialog = new ProgressDialog(this, R.style.MyTheme);

        //Evento click para cada elemento de la lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView vId = (TextView) view.findViewById(R.id.textViewId);
                TextView vMessage = (TextView) view.findViewById(R.id.textViewName);
                ImageView imageViewPicture = (ImageView) view.findViewById(R.id.ivPicture);
                ImageView imageViewStatus = (ImageView) view.findViewById(R.id.ivIsRead);

                String messageId = vId.getText().toString();
                String message = vMessage.getText().toString();
                String imageViewTag = imageViewPicture.getTag().toString();

                Log.d("ID_ITEM: ", vId.getText().toString() + " - MESSAGE: " + vMessage.getText().toString());

                updateMessageStatus(messageId, message, imageViewTag, imageViewStatus);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView vId = (TextView) view.findViewById(R.id.textViewId);
                String messageId = vId.getText().toString();
                Toast.makeText(NotificationsList.this, "Item:" + messageId, Toast.LENGTH_LONG).show();
                return true;
            }
        });

        //Inicialización de Spinner
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.textview_spinner, messages_to_show));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        quantity_to_show = "10";
                        break;
                    case 1:
                        quantity_to_show = "25";
                        break;
                    case 2:
                        quantity_to_show = "50";
                        break;
                    case 3:
                        quantity_to_show = "100";
                        break;
                }

                progressDialog.setTitle("Aviso");
                progressDialog.setMessage("Cargando Mensajes...");
                progressDialog.show();

                sendRequest(quantity_to_show);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("SIN SELECCIÓN:", " - No se seleccionó nada en el spinner");
            }
        });
    }

    //Actualización o inserción en el servidor del status de cada notificación
    public void updateMessageStatus(final String messageId, final String message, final String imageView, final ImageView imageViewStatus){
        progressDialog.setTitle("Aviso");
        progressDialog.setMessage("Actualizando...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "http://suterm.comli.com/regID.php";
        String url = "http://www.entuizer.tech/administrators/suterm/webServices/updateMessageStatus.php";
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

                            messageConfirmation(messageId, message, imageView, imageViewStatus).show();

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
    public AlertDialog messageConfirmation(String messageId, String message, String imageView, final ImageView imageViewStatus){
        ImageView image = null;
        if(image == null){
            image =  new ImageView(this);
        }
        Bitmap bitmap = ImagesUtil.getBitmapImage(this, imageView);
        image.setImageBitmap(bitmap);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme)
                .setTitle("SUTERM")
                .setMessage("# MENSAJE: "+messageId+" - TEXTO: "+message)
                .setCancelable(true)
                .setView(image)
                .setPositiveButton("Listo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Actualización del listado una vez que se presiona el botón Listo
                        //sendRequest(quantity_to_show);
                        imageViewStatus.setImageResource(R.drawable.bullet);
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    //Consulta de Notificaciones a la BD
    private void sendRequest(final String limit){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.entuizer.tech/administrators/suterm/webServices/getMessages.php";
        queue.getCache().remove(url);
        StringRequest putRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {

                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            GetMessages getMessages = new GetMessages(getApplicationContext());
                            ArrayList<Message> listMessages = getMessages.getMessages(jsonObject);
                            MessageAdapter messageAdapter = new MessageAdapter(getApplicationContext(), listMessages);
                            listView.setAdapter(messageAdapter);
                            progressDialog.dismiss();
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
                //Extrae UserId Local
                int userId = UserData.getUserId(NotificationsList.this);

                Map<String, String> params = new HashMap<String, String>();
                Log.d("USERID:"," - "+userId+" - LIMIT: "+limit);
                params.put("userId", "" + userId);
                params.put("limit", limit);

                return params;
            }

        };

        queue.add(putRequest);
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
        String url = "http://www.entuizer.tech/administrators/suterm/webServices/updateSessionStatus.php";
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
        String url = "http://www.entuizer.tech/administrators/suterm/webServices/updateDeviceStatus.php";
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
