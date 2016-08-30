package wayfoo.wayfoo.merchantwayfoo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button menu, order, avail, history;
    private Toolbar mToolbar;
/*    private ToggleButton tb;*/

//    @Override
//    public void onBackPressed() {
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(1);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Mechant");
        menu = (Button) findViewById(R.id.card);
//        tb = (ToggleButton) findViewById(R.id.open);
//        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("state"))
//            tb.setChecked(getIntent().getExtras().getBoolean("state"));
        order = (Button) findViewById(R.id.order);
        avail = (Button) findViewById(R.id.avail);
        history = (Button) findViewById(R.id.history);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HotelMenu.class));
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Orders.class));
            }
        });

        history.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DateSelect.class));
            }
        });

        avail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setMessage("Do you want to reset the Menu?");
                builder.setTitle("Confirm Action!");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        reset();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog a=builder.create();
                a.show();
            }
        });
/*        tb.setTextOff("Closed");
        tb.setTextOn("Open");*/
/*        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            int val;
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    val = 1;
                }else{
                    val = 0;
                }
                setAvail(val);
            }
        });*/

/*        tb.setOnClickListener(new View.OnClickListener(){
            int val;
            @Override
            public void onClick(View v) {
                if(tb.isChecked())
                    val=0;
                else
                    val=1;
                setAvail(val);
            }

        });*/
    }

/*
    private void setAvail(int val){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            int result = -1;

            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                    ProgressDialog.STYLE_HORIZONTAL);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Changing state...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                String jsonResult = "";
                SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(MainActivity.this);
                String name = pref.getString("hotel",null);

                nameValuePairs.add(new BasicNameValuePair("avail", params[0]));
                nameValuePairs.add(new BasicNameValuePair("hotel", name));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://www.wayfoo.com/setAvail.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    System.out.println(response);

                    HttpEntity entity = response.getEntity();
                    jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
                    return "success";

                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                System.out.println("jsonResult" + jsonResult);
                return "";
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
                if (result.equals("") || result == null) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar snackbar = Snackbar.make(parentLayout, "Server Connection Failed.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    Boolean b;
                    b = tb.isChecked();
                    tb.setChecked(!b);
                    return;
                }
                else {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar snackbar = Snackbar.make(parentLayout, "Successfully updated.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(""+val);
    }
*/

    private void reset(){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {


            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Resetting Menu...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(MainActivity.this);
                String name = pref.getString("hotel",null);
                String jsonResult = "";

                nameValuePairs.add(new BasicNameValuePair("hotel", name));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://www.wayfoo.com/resetMenu.php");
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
                Toast.makeText(getApplicationContext(), "Reset Successful", Toast.LENGTH_LONG).show();
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
}
