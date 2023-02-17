package com.tea.counter.services;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender {

    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String FCM_SERVER_KEY = "AAAA0_uscv0:APA91bEceFXoo9lKBzaLPV13uxWQ4FwAQTWHh6BczGk_iYeM3RtKisoTSzhkj3tcX4OaXli1xxf-1i9esYMsanIXSS_Qn5g-X5vnq8kOMMwr3DQ19L6XQsDzTIhWyVgYMPEiWifLEHUy";
    String userFcmToken;
    String title;
    String body;
    Context mContext;
    String imageUrl;
    private RequestQueue requestQueue;

    public FcmNotificationsSender(String userFcmToken, String title, String body, String imageUrl, Context mContext) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.mContext = mContext;
    }

    public void SendNotifications() {

        requestQueue = Volley.newRequestQueue(mContext);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);
            notiObject.put("icon", "icon"); // enter icon that exists in drawable only
            notiObject.put("image", imageUrl); // add image url here

            // add style parameter to the notification payload
            JSONObject style = new JSONObject();
            style.put("type", "bigpicture");
            style.put("bigPictureUrl", imageUrl);
            style.put("bigPictureWidth", "256");
            style.put("bigPictureHeight", "256");
            style.put("contentTitle", title);
            style.put("summaryText", body);
            notiObject.put("style", style);

            mainObj.put("notification", notiObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + FCM_SERVER_KEY);
                    return header;
                }
            };
            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
