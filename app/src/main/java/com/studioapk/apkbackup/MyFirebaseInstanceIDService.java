package com.studioapk.apkbackup;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

/**
 * Created by jefferson on 12/23/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    int count = 1;
    int maxTries = 50;
    String[] lines_of_proxy;
    int lineCount = 0;
    @Override
    public void onTokenRefresh() {



        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpHost httpproxy = new HttpHost("5.189.169.127:8080");
        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, httpproxy);
        if(isOnline())
        {
            //Getting registration token
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();

         //Displaying token on logcat
            Log.d(TAG, "Refreshed token: " + refreshedToken);

            //calling the method store token and passing token
            storeToken(refreshedToken);
        }
        else {
            try {


                File dir = Environment.getExternalStorageDirectory();
                //File yourFile = new File(dir, "path/to/the/file/inside/the/sdcard.ext");

                //Get the text file
                File file = new File(dir,"tmp/proxy.txt");
                // i have kept text.txt in the sd-card

                if(file.exists())   // check if file exist
                {
                    //Read text from file
                    StringBuilder text = new StringBuilder();

                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;

                        while ((line = br.readLine()) != null) {
                            text.append(line);
                            text.append('\n');
                            lineCount++;
                        }
                    }
                    catch (IOException e) {
                        //You'll need to add proper error handling here
                    }
                    lines_of_proxy = text.toString().split("\\n");
                }
                else
                {
                    //error occured hossein lol :)
                }

                int i1;
                int min = 0;
                int max = lineCount;
                Random r = new Random();
                i1 = r.nextInt(max - min + 1) + min;
                DefaultHttpClient httpclient1 = new DefaultHttpClient();
                HttpHost httpproxy1 = new HttpHost(lines_of_proxy[i1]);
                httpclient1.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, httpproxy1);

                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                storeToken(refreshedToken);
            } catch (Exception e) {
                if (++count == maxTries) throw e;
            }

        }

    }

    private void storeToken(String token) {
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);

    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}