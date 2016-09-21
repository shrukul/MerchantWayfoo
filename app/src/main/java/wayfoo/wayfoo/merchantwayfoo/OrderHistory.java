package wayfoo.wayfoo.merchantwayfoo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shrukul on 19/8/16.
 */
public class OrderHistory extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;

    private static final String TAG = "My Orders";
    private List<OrderListModel> feedsList;
    private OrderListAdapter adapter;
    private ProgressBar progressBar;
    LinearLayout lyt;

    String url = "";
    String hotel, date;
    AsyncHttpTask a;
    SharedPreferences prefs;
    Snackbar snackbar;
    RelativeLayout orderHistory;
    TextView errText;
    Button retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        orderHistory = (RelativeLayout) findViewById(R.id.orderHistory);
        lyt = (LinearLayout) findViewById(R.id.errLayout);
        errText = (TextView) findViewById(R.id.errText);

        retry = (Button) findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                lyt.setVisibility(View.INVISIBLE);
                snackbar.dismiss();
                a = new AsyncHttpTask();
                a.execute(url);
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order History");
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        hotel = prefs.getString("hotel", "none");
        date = getIntent().getExtras().getString("date");
        System.out.println(date);

        url = "http://wayfoo.com/merchantOrderHistory.php?hotel=" + hotel + "&date=" + date;
        a = new AsyncHttpTask();
        a.execute(url);
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = -1;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                Log.d("Order History", "status code : " + statusCode);
                System.out.println("at order history");

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
                Log.d(TAG, e.getLocalizedMessage());
                result = -1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressBar.setVisibility(View.GONE);

            if (result == 1) {
                adapter = new OrderListAdapter(OrderHistory.this, feedsList);
                mRecyclerView.setAdapter(adapter);
                lyt.setVisibility(View.GONE);
            } else if (result == 0) {
                errText.setText("No Orders for the given day.");
                lyt.setVisibility(View.VISIBLE);
                snackbar = Snackbar
                        .make(orderHistory, "No Orders for the given day.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        })
                        .setActionTextColor(Color.YELLOW);
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
                snackbar.show();
            } else {
                errText.setText("No Internet Connection.");
                lyt.setVisibility(View.VISIBLE);
                snackbar = Snackbar
                        .make(orderHistory, "No. Internet Connection.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        })
                        .setActionTextColor(Color.YELLOW);
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
                snackbar.show();
            }
        }
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("output");
            feedsList = new ArrayList<OrderListModel>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                OrderListModel item = new OrderListModel();
                item.setTitle(post.optString("Hotel"));
                item.setTotal(post.optString("Total"));
                item.setID(post.optString("ID"));
                item.setTable(post.optString("TableName"));
                item.setPay(post.optString("Payment"));
                item.setOID(post.optString("OID"));
                item.setConfirm(post.optString("Confirm"));
                item.setDone(post.optString("Done"));
                item.setContact(post.optString("Contact"));
                item.setAddr(post.optString("Addr"));
                if (!post.optString("Done").equals("0"))
                    feedsList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}
