package gcm.play.android.samples.com.gcmquickstart.adapters;

/**
 * Created by Helmut on 02/02/2016.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import gcm.play.android.samples.com.gcmquickstart.R;
import gcm.play.android.samples.com.gcmquickstart.data.UserData;

/**
 * Aqui es parte del core de como se muestran los pinches mensajes en el ui
 */

public class CustomList extends ArrayAdapter<String> {
    private String[] ids;
    private String[] messages;
    private String[] timestamps;
    private int[] isRead;
    private int[] userId;
    private String[] picture;
    private Activity context;

    private Bitmap pictureFromUrl;

    public CustomList(Activity context, String[] ids, String[] messages, String[] timestamps, int[] isRead, int[] userId, String[] picture) {
        super(context, R.layout.list_item, ids);
        this.context = context;
        this.ids = ids;
        this.messages = messages;
        this.timestamps = timestamps;
        this.isRead = isRead;
        this.userId = userId;
        this.picture = picture;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem;
        //Log.d("RUTAS "+position+":", " - - "+picture[position]);
        if(picture[position] == ""){
            listViewItem = inflater.inflate(R.layout.list_item, null, true);

            //Extrae num de usuario local para asignar status correpondiente
            int userIdLocal = UserData.getUserId(context);

            TextView textViewId = (TextView) listViewItem.findViewById(R.id.textViewId);
            TextView textViewMessage = (TextView) listViewItem.findViewById(R.id.textViewName);
            TextView textViewTimeStamp = (TextView) listViewItem.findViewById(R.id.textViewEmail);
            ImageView imageViewIsRead = (ImageView)listViewItem.findViewById(R.id.ivIsRead);

            textViewId.setVisibility(View.GONE);

            textViewId.setText(ids[position]);
            textViewMessage.setText(messages[position]);
            textViewTimeStamp.setText(timestamps[position]);

            int isReadFromServer = isRead[position];
            int userIdFromServer = userId[position];

            //Asigna status correspondiente para cada notificación dependiendo del usuario del dispositivo
            if(userIdFromServer == userIdLocal && isReadFromServer == 1){
                imageViewIsRead.setImageResource(R.drawable.ic_whatsapp_checked);
            }else{
                imageViewIsRead.setImageResource(R.drawable.ic_whatsapp_received);
            }

        }else{

            listViewItem = inflater.inflate(R.layout.list_item_with_picture, null, true);

            //Extrae num de usuario local para asignar status correpondiente
            int userIdLocal = UserData.getUserId(context);

            TextView textViewId = (TextView) listViewItem.findViewById(R.id.textViewId);
            TextView textViewMessage = (TextView) listViewItem.findViewById(R.id.textViewName);
            TextView textViewTimeStamp = (TextView) listViewItem.findViewById(R.id.textViewEmail);
            ImageView imageViewIsRead = (ImageView)listViewItem.findViewById(R.id.ivIsRead);
            ImageView imageViewPicture = (ImageView)listViewItem.findViewById(R.id.ivPicture);

            textViewId.setVisibility(View.GONE);

            textViewId.setText(ids[position]);
            textViewMessage.setText(messages[position]);
            textViewTimeStamp.setText(timestamps[position]);
            downloadPictureFromURL(picture[position], imageViewPicture);
            //imageViewPicture.setImageBitmap();

            int isReadFromServer = isRead[position];
            int userIdFromServer = userId[position];

            //Asigna status correspondiente para cada notificación dependiendo del usuario del dispositivo
            if(userIdFromServer == userIdLocal && isReadFromServer == 1){
                imageViewIsRead.setImageResource(R.drawable.ic_whatsapp_checked);
            }else{
                imageViewIsRead.setImageResource(R.drawable.ic_whatsapp_received);
            }

        }

        return listViewItem;
    }

    //Descarga imágenes desde la ruta
    public void downloadPictureFromURL(final String imageHttpAddress, final ImageView imageView) {
        RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        // Petición para obtener la imagen
        ImageRequest request = new ImageRequest(
                imageHttpAddress,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.d("TAG", "ImageRequest completa");
                        imageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //imageView.setImageResource(android.R.drawable.ic_lock_power_off);
                        Log.d("TAG", "Error en ImageRequest para la ruta"+imageHttpAddress);
                    }
                });

        // Añadir petición a la cola
        requestQueue.add(request);
    }

}