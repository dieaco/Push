package gcm.play.android.samples.com.gcmquickstart.webservices;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gcm.play.android.samples.com.gcmquickstart.models.Message;

/**
 * Created by Diego Acosta on 31/03/2016.
 */
public class GetMessages {

    private Context context;

    public GetMessages(Context context){
        this.context = context;
    }

    public ArrayList<Message> getMessages(JSONObject response){
        JSONObject jsonObject = response;

        JSONArray array = null;

        ArrayList<Message> listMessages = new ArrayList<Message>();

        try {
            array = jsonObject.getJSONArray("result");

            for(int i=0; i <array.length(); i++)
            {
                JSONObject json = array.getJSONObject(i);
                Message message = parseMessage(json);
                listMessages.add(message);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return listMessages;
    }

    private Message parseMessage(JSONObject json){
        String id = "";
        String mensaje = "";
        String timestamp = "";
        int isRead = 0;
        int userId = 0;
        String picture = "";

        try {
            id = json.getString("id");
            mensaje = json.getString("mensaje");
            timestamp = json.getString("timestamp");
            isRead = json.getInt("isRead");
            userId = json.getInt("userId");
            picture = json.getString("picture");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Message message = new Message(id, mensaje, timestamp, isRead, userId, picture);

        return message;
    }

}
