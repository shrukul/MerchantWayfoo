package wayfoo.wayfoo.merchantwayfoo;

/**
 * Created by Axle on 26/03/2016.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.HashMap;
import java.util.List;

import static wayfoo.wayfoo.merchantwayfoo.Constants.FIRST_COLUMN;
import static wayfoo.wayfoo.merchantwayfoo.Constants.SECOND_COLUMN;
import static wayfoo.wayfoo.merchantwayfoo.Constants.THIRD_COLUMN;

public class PerOrder extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> list;
    private ProgressBar progressBar;
    AsyncHttpTask a;
    private Toolbar mToolbar;
    ListView listView;
    Button confirm, done, cancel;
    TextView phone, addr, price;
    LinearLayout optionsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perorder);

        if(getIntent().getExtras().containsKey("showOptions") && getIntent().getExtras().getBoolean("showOptions") == false) {
            optionsLayout = (LinearLayout) findViewById(R.id.optionsLayout);
            optionsLayout.setVisibility(View.GONE);
        }


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order");
        }

        phone = (TextView) findViewById(R.id.phone);
        addr = (TextView) findViewById(R.id.addr);
        price = (TextView) findViewById(R.id.price);
        Bundle b = getIntent().getExtras();
        String a1 = b.getString("contact");
        String a2 = b.getString("addr");
        String a3 = b.getString("total");
        phone.setText(a1);
        addr.setText(a2);
        price.setText(a3);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        confirm = (Button) findViewById(R.id.confirm);
        cancel = (Button) findViewById(R.id.cancel);
        done = (Button) findViewById(R.id.done);
        listView = (ListView) findViewById(R.id.listView1);
        final String oid = b.getString("oid");
        final String url = "http://wayfoo.com/perOrderMerchant.php?OID=" + oid;
        a = new AsyncHttpTask();
        a.execute(url);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmTask ct = new ConfirmTask();
                ct.execute("1", oid);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmTask ct = new ConfirmTask();
                ct.execute("0", oid);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmTask ct = new ConfirmTask();
                ct.execute("2", oid);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        final ProgressDialog progressDialog = new ProgressDialog(PerOrder.this,
                ProgressDialog.STYLE_HORIZONTAL);

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
//            progressDialog.setIndeterminate(true);
//            progressDialog.setMessage("Fetching Menu...");
//            progressDialog.show();

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
            progressBar.setVisibility(View.GONE);
//            progressDialog.dismiss();

            if (result == 1) {
                PerOrderAdapter adapter = new PerOrderAdapter(PerOrder.this, list);
                listView.setAdapter(adapter);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(PerOrder.this);
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
                AlertDialog a = builder.create();
                a.show();
                Button bq = a.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button bq2 = a.getButton(DialogInterface.BUTTON_POSITIVE);
                bq.setTextColor(ContextCompat.getColor(PerOrder.this, R.color.colorPrimary));
                bq2.setTextColor(ContextCompat.getColor(PerOrder.this, R.color.colorPrimary));
            }
        }
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("output");
            list = new ArrayList<HashMap<String, String>>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put(FIRST_COLUMN, post.optString("Name"));
                temp.put(SECOND_COLUMN, post.optString("Quantity"));
                System.out.println(post.optString("ItemID"));
                System.out.println(post);
                temp.put(THIRD_COLUMN, String.valueOf(Float.parseFloat(post.optString("Amount")) * Float.parseFloat(post.optString("Quantity"))));
                list.add(temp);
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

    class ConfirmTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("type", params[0]));
            nameValuePairs.add(new BasicNameValuePair("oid", params[1]));

            String jsonResult = "";

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(
                        "http://www.wayfoo.com/confirmOrder.php");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);

                System.out.println(response);

                HttpEntity entity = response.getEntity();
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            System.out.println("jsonResult" + jsonResult);
            return jsonResult;
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

            Intent i = new Intent(PerOrder.this, Orders.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }
}