package wayfoo.wayfoo.merchantwayfoo.gcmservice;

/**
 * Created by shrukul on 18/3/16.
 */

import com.google.android.gms.gcm.GcmListenerService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import wayfoo.wayfoo.merchantwayfoo.Orders;
import wayfoo.wayfoo.merchantwayfoo.R;

public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435315;
    PendingIntent pendingIntent;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String balance = data.getString("balance");
        String typ = "Transaction Successful";
        Intent myIntent = new Intent(this, Orders.class);
        pendingIntent = PendingIntent.getActivity(
                this,
                0,
                myIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        String type = data.getString("type");
        System.out.println("type "+type);
        if (type.equals("1")) {
            System.out.println("Here");
            message = "A new order has been placed. The amount payable is ₹"+message;
        } else{
            System.out.println("There");
            message = "Hey there";
        }

        createNotification(typ, message);
    }

    // Creates notification based on title and body received
    private void createNotification(String typ, String body) {
        Context context = getBaseContext();
/*        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notify).setContentTitle(title)
                .setContentText(body);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());*/
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        android.support.v7.app.NotificationCompat.Builder mBuilder = (android.support.v7.app.NotificationCompat.Builder) new android.support.v7.app.NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notify)
                .setContentTitle("MerchantWayFoo")
                .setContentText(typ)
                .setContentIntent(pendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.app_icon))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        ;
        notificationManager.notify(1, mBuilder.build());
    }

}
