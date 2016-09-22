package wayfoo.wayfoo.merchantwayfoo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axle on 22/09/2016.
 */
public class CloseHotel extends AppCompatActivity {
    Button open, close;
    private Toolbar mToolbar;
    String c = null;
    AsyncHttpTask a;
    TextView t;
    String res = "hi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.close);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Close/Open Hotel");
        close = (Button) findViewById(R.id.close);
        open = (Button) findViewById(R.id.open);
        t = (TextView) findViewById(R.id.status);
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String hotel = pref.getString("hotel", null);
        String url = "http://wayfoo.com/checkHotelStatus.php?hotel=" + hotel;
        a = new AsyncHttpTask();
        a.execute(url);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = "1";
                reset();
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = "2";
                reset();
            }
        });

    }

    private void reset() {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {


            final ProgressDialog progressDialog = new ProgressDialog(CloseHotel.this,
                    R.style.AppTheme_Dark_Dialog);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Processing...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(CloseHotel.this);
                String name = pref.getString("hotel", null);
                Log.d("this", name + c + "tt");
                String jsonResult = "";

                nameValuePairs.add(new BasicNameValuePair("hotel", name));
                nameValuePairs.add(new BasicNameValuePair("close", c));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://www.wayfoo.com/closeHotel.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    System.out.println(response);

                    HttpEntity entity = response.getEntity();
                    jsonResult = inputStreamToString(response.getEntity().getContent()).toString();


                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                System.out.println("jsonResult" + jsonResult);
                return "success";
            }

            private StringBuilder inputStreamToString(InputStream is) {
                String rLine = "";
                StringBuilder answer = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                try {
                    while ((rLine = br.readLine()) != null) {
                        answer.append(rLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Answer :" + answer);
                return answer;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                progressDialog.dismiss();
                Intent i = new Intent(CloseHotel.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }


    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = -1;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    try {
                        BufferedReader r = new BufferedReader(
                                new InputStreamReader(
                                        urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            response.append(line);
                        }
                        parseResult(response.toString());
                        result = 1;
                    } catch (Exception E) {
                        result = 0;
                    }
                } else {
                    result = 0;
                }
            } catch (Exception e) {
                result = -1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (res.equals("1")) {
                t.setText("Current Status : Open");
                close.setVisibility(View.VISIBLE);
            } else if (res.equals("0")) {
                t.setText("Current Status : Closed");
                open.setVisibility(View.VISIBLE);
            } else {
                t.setText("No Internet");
            }


        }
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            res= (String) response.optJSONObject("output").get("Avail");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        if (a != null) a.cancel(true);
        super.onDestroy();
    }

    public void onStop() {
        if (a != null) a.cancel(true);
        super.onStop();
    }
}
