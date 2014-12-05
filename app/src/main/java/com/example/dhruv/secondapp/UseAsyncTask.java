package com.example.dhruv.secondapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UseAsyncTask extends AsyncTask<String, Void, String> {

    private String pageUrl;
    private Activity activity;
    private String result;

    UseAsyncTask(String url, Activity thisActivity) {
        pageUrl = url;
        activity = thisActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            return getAsyncResults(pageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        //  do nothing
    }

    public String getAsyncResults(String pageUrl) throws IOException {
        final URL url;
        try {
            url = new URL(pageUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(activity, "URL could not be reached", Toast.LENGTH_LONG).show();
            return null;
        }

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(15000);
        conn.setDoOutput(true);

        InputStream is = conn.getInputStream();
        result = readStream(is);

        if(result.equals(""))
            return "No response";
        return result;
    }

    public String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
