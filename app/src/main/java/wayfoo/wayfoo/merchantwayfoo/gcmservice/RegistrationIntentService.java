package wayfoo.wayfoo.merchantwayfoo.gcmservice;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import wayfoo.wayfoo.merchantwayfoo.R;

/**
 * Created by shrukul on 27/1/16.
 */
// abbreviated tag name
public class RegistrationIntentService extends IntentService {

    SharedPreferences preferences;
    String token;

    // abbreviated tag name
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences("MerchantWayfooPref", 0);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API

        InstanceID instanceID = InstanceID.getInstance(this);

        try {
            instanceID.deleteInstanceID();
        } catch (IOException e) {
            e.printStackTrace();
        }

        instanceID = InstanceID.getInstance(this);

        String senderId = getApplicationContext().getResources().getString(R.string.gcm_defaultSenderId);
        try {
            // request token that will be used by the server to send push notifications
            System.out.println("senderID: " + senderId);
            token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.d(TAG, "GCM Registration Token: " + token);

            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("KEY_R", token);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
