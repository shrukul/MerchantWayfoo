package wayfoo.wayfoo.merchantwayfoo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

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
 * Created by Axle on 15/03/2016.
 */
public class Search extends AppCompatActivity {

    public SearchView search1;

    private RecyclerView mRecyclerView;

    private static final String TAG = "Location";
    private List<FeedItemHotel> feedsList;
    private MyRecyclerAdapterHotel adapter;
    AsyncHttpTask a;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        search1 = (SearchView) findViewById(R.id.search);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String name = pref.getString("hotel",null);
        final String url = "http://wayfoo.com/hotel.php?name="+name;
        a = new AsyncHttpTask();
        a.execute(url);
        search1.setIconifiedByDefault(false);
        search1.setOnQueryTextListener(listener);
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {

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
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 1) {
                adapter = new MyRecyclerAdapterHotel(Search.this, feedsList);
                mRecyclerView.setAdapter(adapter);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Search.this);
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
                bq.setTextColor(ContextCompat.getColor(Search.this, R.color.colorPrimary));
                bq2.setTextColor(ContextCompat.getColor(Search.this, R.color.colorPrimary));
            }
        }
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("output");
            feedsList = new ArrayList<FeedItemHotel>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                FeedItemHotel item = new FeedItemHotel();
                item.setID(Integer.parseInt(post.optString("ID")));
                item.setTitle(post.optString("Name"));
                item.setType(post.optString("Type"));
                item.setPrice(post.optString("Price"));
                item.setVeg(post.optString("NonVeg"));
                item.setAvailable(post.optString("Available"));
                feedsList.add(item);
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

    OnQueryTextListener listener = new OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String query) {
            query = query.toLowerCase();

            final List<FeedItemHotel> filteredList = new ArrayList<>();

            for (int i = 0; i < feedsList.size(); i++) {

                final String text = feedsList.get(i).getTitle().toLowerCase();
                if (text.contains(query)) {
                    filteredList.add(feedsList.get(i));
                }
            }

            mRecyclerView.setLayoutManager(new LinearLayoutManager(Search.this));
            adapter = new MyRecyclerAdapterHotel(Search.this,filteredList);
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            return true;

        }
        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    };

}
