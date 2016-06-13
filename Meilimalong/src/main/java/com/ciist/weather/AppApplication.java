package com.ciist.weather;



import android.app.Application;
import android.app.Service;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

/**
 * 主Application
 */
public class AppApplication extends Application {
    public Vibrator mVibrator;
    public String city;

    @Override
    public void onCreate() {
        super.onCreate();
        mVibrator = (Vibrator) getApplicationContext().getSystemService(
                Service.VIBRATOR_SERVICE);
    }

}
