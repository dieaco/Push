package gcm.play.android.samples.com.gcmquickstart.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import gcm.play.android.samples.com.gcmquickstart.R;
import gcm.play.android.samples.com.gcmquickstart.images.ImagesUtil;
import gcm.play.android.samples.com.gcmquickstart.models.Message;
import gcm.play.android.samples.com.gcmquickstart.views.RoundedImageView;

/**
 * Created by Diego Acosta on 31/03/2016.
 */
public class MessageAdapter extends BaseAdapter {

    private ArrayList<Message> listMessage;
    private LayoutInflater inflater;
    private Context context;

    private MessageAdapter adapter;

    public MessageAdapter(Context context, ArrayList<Message> listMessage){
        this.listMessage = listMessage;
        this.context = context;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        adapter = this;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listMessage.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listMessage.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = convertView;
        if(convertView ==  null){
            Log.d("INFLATER", " - - - Se inflo LAYOUT");
            view = inflater.inflate(R.layout.list_item_with_picture, null);
        }

        TextView textViewId = (TextView) view.findViewById(R.id.textViewId);
        TextView textViewMessage = (TextView) view.findViewById(R.id.textViewName);
        TextView textViewTimeStamp = (TextView) view.findViewById(R.id.textViewEmail);
        ImageView imageViewIsRead = (ImageView)view.findViewById(R.id.ivIsRead);
        ImageView imageViewPicture = (ImageView)view.findViewById(R.id.ivPicture);

        Message message = listMessage.get(position);

        if(!ImagesUtil.existMessageImage(context, message)){
            //Descargala
            new AsyncImage().execute(message);
        }

        Bitmap bitmap = ImagesUtil.getMessageImage(context, message);
        Drawable drawable = new BitmapDrawable(context.getResources(), RoundedImageView.createCircleBitmap(bitmap));
        imageViewPicture.setImageDrawable(drawable);
        String imageName = ImagesUtil.getBitmapName(context, message);
        imageViewPicture.setTag(imageName);

        textViewId.setVisibility(View.GONE);

        textViewId.setText(message.getId());
        textViewMessage.setText(message.getMensaje());
        textViewTimeStamp.setText(message.getTimestamp());

        int isReadFromServer = message.getIsRead();

        //Asigna status correspondiente para cada notificación dependiendo del usuario del dispositivo
        if(isReadFromServer == 1){
            imageViewIsRead.setImageResource(R.drawable.ic_whatsapp_checked);
        }else{
            imageViewIsRead.setImageResource(R.drawable.ic_whatsapp_received);
        }

        return view;
    }

    private class AsyncImage extends AsyncTask<Message, Void, Void> {
        @Override
        protected Void doInBackground(Message... params){

            Message message = params[0];
            ImagesUtil.saveMessageImage(context, message);
            return null;
        }

        @Override
        protected void onPostExecute(Void param){
            /*try {
                adapter.notifyDataSetChanged();
            }catch (Exception e){
                e.printStackTrace();
            }*/
        }
    }

}
