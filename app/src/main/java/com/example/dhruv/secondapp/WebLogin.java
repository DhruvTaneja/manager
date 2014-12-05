package com.example.dhruv.secondapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dhruv on 6/12/14.
 */
public class WebLogin extends AsyncTask<String, Void, String> {

    private String urlParams;
    private Context context;

    WebLogin(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            return downloadUrl();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }

    private String downloadUrl() throws NoSuchAlgorithmException, IOException, URISyntaxException {

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        InputStream is = null;
        String uname = "2K11/IT/026";
        String password = "saddahaqq101";
        String pass = new Algorithm(password).getMD5();
        String loginUrl = "http://www.dce.ac.in/placement/student_login.php";

            /*
            Using shared preferences to store the URL parameters
            to store the username and password altogether
            */
//        SharedPreferences sharedPreferences = getSharedPreferences("secondApp", 0);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        if(sharedPreferences.getString("urlParams", "not present").equals("not present")) {
//            urlParams = "txtUsername=" + uname + "&txtPassword=" + pass + "&Submit=Login";
//            Log.d("PREFS", "was not present in the sharedprefs");
//            editor.putString("urlParams", urlParams);
//            editor.commit();
//        }
//        else {
//            Log.d("PREFS", "is present in the sharedprefs");
//            urlParams = sharedPreferences.getString("urlParams", null);
//        }

        urlParams = "txtUsername=" + uname + "&txtPassword=" + pass + "&Submit=Login";
        URL url = new URL(loginUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        //  headers of the POST request
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Accept-encoding", "gzip, deflate");

        //  Writing the urlParams to the output stream
        //  to the POST request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(urlParams);
        wr.flush();
        wr.close();

        is = conn.getInputStream();
        String result;

        if(conn.getURL().getPath().equals("/placement/student_login.php"))
            result = "Login failed";
        else if(conn.getURL().getPath().equals("/placement/announcements.php"))
            result = "Login Successful";
        else
            result = conn.getURL().getPath();
        Log.d("LIST", String.valueOf(cookieManager.getCookieStore().getCookies()));
        return result;
    }

    public String getUrlParams() {
        return urlParams;
    }

}
