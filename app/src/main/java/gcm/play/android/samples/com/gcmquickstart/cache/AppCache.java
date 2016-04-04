package gcm.play.android.samples.com.gcmquickstart.cache;

import java.util.ArrayList;

import gcm.play.android.samples.com.gcmquickstart.models.Message;

/**
 * Created by Diego Acosta on 31/03/2016.
 */
public class AppCache {

    private static ArrayList<Message> listMessages;

    public static ArrayList<Message> getListMessages(){
        return listMessages;
    }

    public static void setListMessages(ArrayList<Message> listMessages){
        AppCache.listMessages = listMessages;
    }

}
