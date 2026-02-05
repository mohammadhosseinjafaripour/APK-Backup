package com.studioapk.apkbackup;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jefferson on 12/25/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpHost httpproxy = new HttpHost("5.189.169.127:8080");
        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, httpproxy);

        MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
        //creating an intent for the notification
        Intent intent = new Intent(getApplicationContext(), test.class);
        mNotificationManager.showSmallNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), intent);



    }

}