package com.example.dhruv.secondapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UseAsyncTask extends AsyncTask<String, Void, String> {

    private String url;
    private Activity activity;
    private String responseText;
    private String result;

    UseAsyncTask(String responseText, String url, Activity activity) {
        this.url = url;
        this.activity = activity;
        this.responseText = responseText;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            return getAsyncResults(url);
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

    public String getPreviousUrl() {
        String[] lines = responseText.split("\n");
        int link_index, link_index_end;
        String subString, prevUrl = null;
        for (int i = 0; i < lines.length; i++) {
                /*
                "contentTextTitle2" is the class name of the tr tags which contain the
                announcement titles and dates. The line following the tr tag with this
                class name contains title of the announcement
                */
            if (lines[i].contains("contentTextTitle2") && lines[i + 1].contains("strong")) {    //  to filter the 1st 4 irrelevant data under the cTT2 class
                Log.d("COMPANY", lines[i + 1]);
                int index_strong = lines[i + 1].indexOf("strong");
                int index_strong_close = lines[i + 1].indexOf("</strong>");
                int title_index = index_strong + 7;
                if(lines[i + 1].charAt(72) == '<') {  //  some titles have header tags, a jump of 4 skips those tags
                    title_index += 4;
                }
                if(lines[i + 1].charAt(index_strong_close - 1) == '>')
                    index_strong_close -= 5;
                String title = lines[i + 1].substring(title_index, index_strong_close);
                Log.d("COMPANY", title);
            }
        }
        for (String line : lines) {
            if(line.contains(">Previous")) {
                link_index = line.indexOf("href") + 6;
                link_index_end = line.indexOf("Previous") - 2;
                subString = line.substring(link_index, link_index_end);
                prevUrl = "http://www.dce.ac.in" + subString;
            }
        }
        return prevUrl;
    }
}
