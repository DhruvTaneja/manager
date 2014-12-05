package com.example.dhruv.secondapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button login_button = (Button) findViewById(R.id.login_button);
        Button announcements = (Button) findViewById(R.id.button_announcements);

        /*
        Shared preferences are used to check if the application
        is running for the first time
        */
        SharedPreferences sharedPreferences = getSharedPreferences("secondApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(sharedPreferences.getBoolean("firstrun", false))
            Log.d("PREFS", "getboolean - firstrun - true");
        else
            Log.d("PREFS", "no");
        Boolean firstRun = sharedPreferences.getBoolean("firstrun", true);
        if(firstRun) {
            Log.d("RUN", "Running for the first time");
            editor.putBoolean("firstrun", false);
            editor.commit();
        }
        else
            Log.d("RUN", "Been there, done that");

        login_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                createConnection();
            }
        });

        announcements.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                watchAnnouncements();
            }
        });
    }

    private void watchAnnouncements() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
            new Announcements().execute();
    }

    public class Announcements extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                return getAnnouncements();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private String getAnnouncements() throws IOException {
            String announcementUrl = "http://www.dce.ac.in/placement/announcements.php";
            final URL url;
            try {
                url = new URL(announcementUrl);
            } catch (MalformedURLException e) {     // if the URL is not in a good format
                e.printStackTrace();
                Toast.makeText(Login.this, "URL could not be reached", Toast.LENGTH_LONG).show();
                return null;
            }
            //  establishing connection to the announcement page
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            //  fetching the response text using an InputStream
            InputStream is = conn.getInputStream();
            String result = readStream(is);

            if(result.equals(""))   //  if the InputStream returned an empty string
                return "No response :(";

            //  Separating all the lines using \n as the delimiter
            String[] lines = result.split("\n");

            for (int i = 0; i < lines.length; i++) {
                /*
                "contentTextTitle2" is the class name of the tr tags which contain the
                announcement titles and dates. The line following the tr tag with this
                class name contains title of the announcement
                */
                if (lines[i].contains("contentTextTitle2") && lines[i + 1].contains("strong")) {    //  to filter the 1st 4 irrelevant data under the cTT2 class
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
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent();

//            CookieManager cookieManager = new CookieManager();
//            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//            CookieHandler.setDefault(cookieManager);
//
//            CookieStore cookieStore = cookieManager.getCookieStore();
//            List<HttpCookie> cookieList = cookieStore.getCookies();

//            Log.d("LIST", String.valueOf(cookieList));

            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setMessage(result)
                    .setTitle("Result")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            builder.create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        Handle action bar item clicks here. The action bar will
        automatically handle clicks on the Home/Up button, so long
        as you specify a parent activity in AndroidManifest.xml.
        */
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void createConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()) {
            new WebLogin(Login.this).execute();
        }
        else
            Toast.makeText(Login.this, "Connection not available", Toast.LENGTH_LONG).show();
    }
}