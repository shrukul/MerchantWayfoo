package wayfoo.wayfoo.merchantwayfoo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import wayfoo.wayfoo.merchantwayfoo.gcmservice.RegistrationIntentService;

/**
 * Created by Axle on 01/04/2016.
 */
public class Login extends AppCompatActivity{
    EditText title,pass;
    private Toolbar mToolbar;
    Button login;
    ProgressBar p;
    AsyncHttpTask a;
    String title1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        RegGCM();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Login");
        }
        Bundle b = getIntent().getExtras();
        title = (EditText) findViewById(R.id.title);
        pass = (EditText) findViewById(R.id.pass);
        p = (ProgressBar) findViewById(R.id.progressBar3);
        login = (Button) findViewById(R.id.post);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!title.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()) {
                    title1 = title.getText().toString().trim();
                    String pass1 = pass.getText().toString().trim();
                    p.setVisibility(View.VISIBLE);
                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(Login.this);
                    String regid=pref.getString("KEY_R",null);
                    final String url = "http://wayfoo.com/merchantlogin.php?name="+title1+"&pass="+pass1+"&regid="+regid;
                    a = new AsyncHttpTask();
                    a.execute(url);
                } else {
                    Toast.makeText(Login.this, "fill all info", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            p.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(
                            new InputStreamReader(
                                    urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println(response);
                    parseResult(response.toString());
                    result = 1;
                } else {
                    result = 0;
                }
            } catch (Exception e) {
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            p.setVisibility(View.GONE);

            System.out.println(result);
            if (result == 1) {
                SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(Login.this);
                if (pref.contains("id")) {
                    Intent it = new Intent(Login.this,
                            MainActivity.class);
                    it.putExtra("state",true);
                    startActivity(it);
                    finish();
                } else {
                    Toast.makeText(Login.this, "login invalid", Toast.LENGTH_LONG).show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setCancelable(true);
                builder.setMessage("Something seems to be wrong with the internet.");
                builder.setTitle("Oops!!");
                builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                AlertDialog a=builder.create();
                a.show();
                Button bq = a.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button bq2 = a.getButton(DialogInterface.BUTTON_POSITIVE);
                bq.setTextColor(ContextCompat.getColor(Login.this, R.color.colorPrimary));
                bq2.setTextColor(ContextCompat.getColor(Login.this, R.color.colorPrimary));
            }
        }
    }

    private void RegGCM() {

        // Start IntentService to register this application with GCM.
        Intent intent2 = new Intent(this, RegistrationIntentService.class);
        startService(intent2);
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("output");

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                if(!post.optString("ID").toString().isEmpty()) {
                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("id", post.optString("ID").trim());
                    editor.putString("hotel",title1);
                    editor.commit();
                }
            }
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
