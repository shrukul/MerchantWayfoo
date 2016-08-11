package wayfoo.wayfoo.merchantwayfoo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.ImageView;

public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ImageView splashscr = (ImageView) findViewById(R.id.splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(SplashScreen.this);
                if (pref.contains("id")) {
                    startActivity(new Intent(SplashScreen.this,
                            MainActivity.class));
                    finish();
                } else {
                    Intent i = new Intent(SplashScreen.this, Login.class);
                    startActivity(i);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }

}