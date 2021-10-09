package com.example.newfestivalpost.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.newfestivalpost.AnalyticsApplication;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Utills.Constance;
import com.example.newfestivalpost.Utills.SharedPrefrenceConfig;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ActivitySplashScreen extends AppCompatActivity {
    Handler handler;
    Context context;
    Tracker mTracker;
    SharedPrefrenceConfig sharedprefconfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        context = ActivitySplashScreen.this;
        sharedprefconfig = new SharedPrefrenceConfig(context);

        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        Log.d("fdsfsd",""+sharedprefconfig.getPrefString(context, Constance.language,"ss"));
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(context, ActivityHome.class);
                startActivity(intent);
                finish();
            }
        }, 2000);


    }
    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Image~" + "Google Analytics Testing");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
