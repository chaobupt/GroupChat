package com.example.groupchat;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//This class contains methods to save the user’s session id in shared preferences.
public class JsonUtils {

    private Context context;
    private SharedPreferences sharedPref;

    private static final String KEY_SHARED_PREF = "ANDROID_WEB_CHAT";
    private static final int KEY_MODE_PRIVATE = 0;
    private static final String KEY_SESSION_ID = "sessionId",
            FLAG_MESSAGE = "message";
    private static final int TYPE_IMG = 1;
    private static final int TYPE_TEXT = 2;
    private static final int TYPE_REPOST = 3;
    private static final int TYPE_Feature = 4;
    private static final int TYPE_NotifyUpdate = 5;
    private static final int TYPE_Regions = 6;


    public JsonUtils(Context context) {
        this.context = context;
        sharedPref = this.context.getSharedPreferences(KEY_SHARED_PREF,
                KEY_MODE_PRIVATE);
    }

    public void storeSessionId(String sessionId) {
        Editor editor = sharedPref.edit();
        editor.putString(KEY_SESSION_ID, sessionId);
        editor.commit();
    }

    public String getSessionId() {
        return sharedPref.getString(KEY_SESSION_ID, null);
    }


    public String getSendImgMessageJSON(byte[] message) {
        String json = null;
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", getSessionId());
            jObj.put("message", message);
            jObj.put("type",TYPE_IMG);

            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getSendImgMessageJSON(String message) {
        String json = null;
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", getSessionId());
            jObj.put("message", message);
            jObj.put("type",TYPE_IMG);

            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getSendFeatureMessageJSON(String message) {
        String json = null;
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", getSessionId());
            jObj.put("message", message);
            jObj.put("type",TYPE_Feature);

            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getSendNotifyUpdateMessageJSON(String message) {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", getSessionId());
            jObj.put("message", message);
            jObj.put("type", TYPE_NotifyUpdate);

            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getSendRegionsMessageJSON(String message) {
        String json = null;
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", getSessionId());
            jObj.put("message", message);
            jObj.put("type",TYPE_Regions);

            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

//    public String getSendRepostMessageJSON(String fileName, int groupId) {
//        String json = null;
//        try {
//            JSONObject jObj = new JSONObject();
//            jObj.put("flag", FLAG_MESSAGE);
//            jObj.put("sessionId", getSessionId());
//            jObj.put("fileName", fileName);
//            jObj.put("groupId", groupId);
//            jObj.put("type",TYPE_REPOST);
//
//            json = jObj.toString();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return json;
//    }

    public String getSendRepostMessageJSON(String ukFile, int groupId) {
        String json = null;
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", getSessionId());
            jObj.put("ukFile", ukFile);
            jObj.put("groupId", groupId);
            jObj.put("type",TYPE_REPOST);

            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getSendTextMessageJSON(String message) {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", getSessionId());
            jObj.put("message", message);
            jObj.put("type", TYPE_TEXT);

            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

}