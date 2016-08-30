package wayfoo.wayfoo.merchantwayfoo;

/**
 * Created by Axle on 09/02/2016.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MyRecyclerAdapterHotel extends
        RecyclerView.Adapter<MyRecyclerAdapterHotel.CustomViewHolder> {

    private final Context mContext;
    private static Context mc;
    static String tag = "Menu";

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView textView, price, itemID;
        Button plus;
        CardView card;


        public CustomViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.title1);
            this.price = (TextView) view.findViewById(R.id.price);
            this.plus = (Button) view.findViewById(R.id.add);
            this.itemID = (TextView) view.findViewById(R.id.itemID);
            mc = view.getContext();
            card = (CardView) view.findViewById(R.id.YogaCard);
        }

    }

    private static List<FeedItemHotel> feedItemList;

    public MyRecyclerAdapterHotel(Context context, List<FeedItemHotel> feedItemList) {
        MyRecyclerAdapterHotel.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_row_hotel, null, true);
        WindowManager windowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height=windowManager.getDefaultDisplay().getHeight();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.WRAP_CONTENT));
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder,final int i) {
        final FeedItemHotel feedItem = feedItemList.get(i);
        Typeface font1 = Typeface.createFromAsset(mContext.getAssets(),
                "font/RobotoCondensed-Regular.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder(
                Html.fromHtml(feedItem.getTitle()));
        SS.setSpan(new CustomTypeFace("", font1), 0, SS.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        customViewHolder.textView.setText(SS);
        SS = new SpannableStringBuilder(
                Html.fromHtml(feedItem.getPrice()));
        SS.setSpan(new CustomTypeFace("", font1), 0, SS.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        customViewHolder.price.setText(SS);
        SS = new SpannableStringBuilder(
                Html.fromHtml(feedItem.getItemID()));
        SS.setSpan(new CustomTypeFace("", font1), 0, SS.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        customViewHolder.itemID.setText(SS);
        if(Html.fromHtml(feedItem.getAvailable()).equals("0")){
            customViewHolder.card.setCardBackgroundColor(Color.YELLOW);
        }
        customViewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mc);
                builder.setCancelable(true);
                builder.setMessage("Are you sure you want to remove this item?");
                builder.setTitle("Confirm Action!");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences pref = PreferenceManager
                                .getDefaultSharedPreferences(mc);
                        String hotel = pref.getString("hotel", null);
                        insertToDatabase(hotel, String.valueOf(feedItem.getID()));
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
                AlertDialog a=builder.create();
                a.show();
                Button bq = a.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button bq2 = a.getButton(DialogInterface.BUTTON_POSITIVE);
                bq.setTextColor(ContextCompat.getColor(mc, R.color.white));
                bq2.setTextColor(ContextCompat.getColor(mc, R.color.white));
            }
        });
    }

    private void insertToDatabase(String name, String add){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramUsername = params[0];
                String paramImage = params[1];

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("hotel", paramUsername));
                nameValuePairs.add(new BasicNameValuePair("id",paramImage ));
                Log.d(tag,paramUsername+" " +paramImage);

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://www.wayfoo.com/removeitemmerchant.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();


                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                return "success";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Toast.makeText(mc, result, Toast.LENGTH_LONG).show();
                mc.startActivity(new Intent(mc,MainActivity.class));
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(name, add);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
}
