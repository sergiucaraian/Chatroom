package chatserver;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
    private static final String FLAG_CONNECTED = "connected", 
            FLAG_NEW = "new", 
            FLAG_MESSAGE = "message",
            FLAG_EXIT = "exit";
    
    // Get session information
    public static String getConfirmation() {
        String json = null;
        
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_CONNECTED);
            json = jObj.toString();
        } catch(JSONException e) {
            e.printStackTrace();
        }
        
        return json;
    }
    
    // Message sent when new user is connected
    public static String getNewClient(String name) {
        String json = null;
        
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_NEW);
            jObj.put("name", name);
            
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return json;
    }
    
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
