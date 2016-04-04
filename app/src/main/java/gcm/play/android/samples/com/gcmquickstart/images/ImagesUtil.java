package gcm.play.android.samples.com.gcmquickstart.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import gcm.play.android.samples.com.gcmquickstart.R;
import gcm.play.android.samples.com.gcmquickstart.models.Message;

/**
 * Created by Diego Acosta on 31/03/2016.
 */
public class ImagesUtil {

    public static Bitmap getBitmapFromURL(String source) throws IOException {
        URL url = new URL(source);

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        return bitmap;
    }

    public static boolean existMessageImage(Context context, Message message){

        if(message == null)
            return false;
        else{
            String name = getImageName(message.getPicture());
            String imageName = "imageMessage_" + name;
            Log.d("ID DE IMAGEN: ", " -  - - - "+imageName+" - - - - SUBSTRING:"+name);
            File file = context.getFileStreamPath(imageName);
            if(file.exists())
                return true;
            return false;
        }
    }

    public static void saveMessageImage(Context context, Message message){
        String url = message.getPicture();
        String name = getImageName(message.getPicture());
        String imageName = "imageMessage_" + name;

        if(!existMessageImage(context, message)){
            try {
                Bitmap bitmap = getBitmapFromURL(url);
                FileOutputStream output = context.openFileOutput(imageName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG,50,output);
                output.close();
            }catch (Exception e){

            }
        }

    }

    public static Bitmap getMessageImage(Context context, Message message){
        String name = getImageName(message.getPicture());
        String imageName = "imageMessage_" + name;

        try {
            InputStream is = context.openFileInput(imageName);
            return BitmapFactory.decodeStream(is);
        }catch (FileNotFoundException e){
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.transparent_background);
        }
    }

    public static String getImageName(String path){
        int lastIndexOf = path.lastIndexOf("/");
        String subs = path.substring(lastIndexOf + "/".length(), path.length());
        return subs;
    }

    public static String getBitmapName(Context context, Message message){
        String name = getImageName(message.getPicture());
        String imageName = "imageMessage_" + name;
        return imageName;
    }

    public static Bitmap getBitmapImage(Context context, String imageName){
        try {
            InputStream is = context.openFileInput(imageName);
            return BitmapFactory.decodeStream(is);
        }catch (FileNotFoundException e){
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.transparent_background);
        }
    }

}
