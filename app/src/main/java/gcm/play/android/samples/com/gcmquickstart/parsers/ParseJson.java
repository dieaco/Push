package gcm.play.android.samples.com.gcmquickstart.parsers;

/**
 * Created by Helmut on 02/02/2016.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ParseJson {


    public static String[] id;
    public static String[] mensaje;
    public static String[] timestamp;
    public static int[] isRead;
    public static int[] userId;

    public static final String JSON_ARRAY = "result";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "mensaje";
    public static final String KEY_EMAIL = "timestamp";
    public static final String KEY_IS_READ = "isRead";
    public static final String KEY_USER_ID = "userId";

    private JSONArray users = null;

    private String json;

    public ParseJson(String json){
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject=null;
        try {
            jsonObject = new JSONObject(json);
            users = jsonObject.getJSONArray(JSON_ARRAY);

            id = new String[users.length()];
            mensaje = new String[users.length()];
            timestamp = new String[users.length()];
            isRead = new int[users.length()];
            userId = new int[users.length()];

            for(int i=0;i<users.length();i++){
                JSONObject jo = users.getJSONObject(i);
                id[i] = jo.getString(KEY_ID);
                mensaje[i] = jo.getString(KEY_NAME);
                timestamp[i] = jo.getString(KEY_EMAIL);
                isRead[i] = jo.getInt(KEY_IS_READ);
                userId[i] = jo.getInt(KEY_USER_ID);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}