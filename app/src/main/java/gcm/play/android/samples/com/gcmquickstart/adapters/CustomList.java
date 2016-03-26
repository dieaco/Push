package gcm.play.android.samples.com.gcmquickstart.adapters;

/**
 * Created by Helmut on 02/02/2016.
 */
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
    private Activity context;

    public CustomList(Activity context, String[] ids, String[] messages, String[] timestamps, int[] isRead, int[] userId) {
        super(context, R.layout.list_item, ids);
        this.context = context;
        this.ids = ids;
        this.messages = messages;
        this.timestamps = timestamps;
        this.isRead = isRead;
        this.userId = userId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_item, null, true);

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

        return listViewItem;
    }
}