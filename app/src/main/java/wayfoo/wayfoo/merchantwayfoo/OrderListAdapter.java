package wayfoo.wayfoo.merchantwayfoo;

/**
 * Created by Axle on 09/02/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

public class OrderListAdapter extends
        RecyclerView.Adapter<OrderListAdapter.CustomViewHolder> {

    private final Context mContext;
    private static Context mc;
    static String tag = "OrdersList";

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView textView, price, id;
        CardView card;

        public CustomViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.title1);
            this.price = (TextView) view.findViewById(R.id.price);
            this.id = (TextView) view.findViewById(R.id.id);
            mc = view.getContext();
            card = (CardView) view.findViewById(R.id.YogaCard);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = getAdapterPosition();
                    OrderListModel feedItem = feedItemList.get(i);
                    String pagenext;
                    pagenext=Html.fromHtml(feedItem.getOID()).toString();
                    String contact=Html.fromHtml(feedItem.getContact()).toString();
                    String price=Html.fromHtml(feedItem.getTotal()).toString();
                    String Address=Html.fromHtml(feedItem.getAddr()).toString();
                    Intent intent = new Intent(mc, PerOrder.class);
                    intent.putExtra("oid", pagenext);
                    intent.putExtra("contact", contact);
                    intent.putExtra("addr", Address);
                    intent.putExtra("total", price);
                    mc.startActivity(intent);
                }
            });
        }

    }

    private static List<OrderListModel> feedItemList;

    public OrderListAdapter(Context context, List<OrderListModel> feedItemList) {
        OrderListAdapter.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //View view = LayoutInflater.from(viewGroup.getContext()).inflate(
        //        R.layout.card_view_row_hotel, null);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_row_orders, null, true);
        WindowManager windowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height=windowManager.getDefaultDisplay().getHeight();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT));
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder,int i) {
        final OrderListModel feedItem = feedItemList.get(i);
        Typeface font1 = Typeface.createFromAsset(mContext.getAssets(),
                "font/RobotoCondensed-Regular.ttf");

        SpannableStringBuilder SS = new SpannableStringBuilder(
                Html.fromHtml(feedItem.getTotal()));
        SS.setSpan(new CustomTypeFace("", font1), 0, SS.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        customViewHolder.price.setText(SS);

        if(Html.fromHtml(feedItem.getConfirm()).toString().equals("0")){
            SS = new SpannableStringBuilder("Pending");
            SS.setSpan(new CustomTypeFace("", font1), 0, SS.length(),
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            customViewHolder.textView.setText(SS);
        }else{
            SS = new SpannableStringBuilder("Confirmed");
            SS.setSpan(new CustomTypeFace("", font1), 0, SS.length(),
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            customViewHolder.textView.setText(SS);
        }
        SS = new SpannableStringBuilder(
                Html.fromHtml(feedItem.getOID()));
        SS.setSpan(new CustomTypeFace("", font1), 0, SS.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        customViewHolder.id.setText(SS);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
}
