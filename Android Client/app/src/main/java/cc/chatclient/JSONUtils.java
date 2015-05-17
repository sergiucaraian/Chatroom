package cc.chatclient;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
    private static final String FLAG_AUTHENTICATE = "auth",
            FLAG_MESSAGE = "message",
            FLAG_EXIT = "exit";

    // Send authentication information
    public static String getAuthentication(String name) {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_AUTHENTICATE);
            jObj.put("name", name);
            json = jObj.toString();
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    // Send exit message
    public static String getExitClient (String name) {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_EXIT);
            jObj.put("name",name);

            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public static String getMessage(String name, String message) {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("name", name);
            jObj.put("message", message);

            json = jObj.toString();
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
