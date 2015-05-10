package team4x4.trackers.routetracker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import org.androidannotations.annotations.EActivity;

import team4x4.trackers.routetracker.R;
import team4x4.trackers.routetracker.utilities.Constants;
import team4x4.trackers.routetracker.utilities.SharedPreferencesUtility;

/**
 * Activity for the splash screen.
 */
@EActivity(R.layout.activity_splash)
public class Splash extends AppCompatActivity {

    /**
     * The timeout duration of splash screen in milliseconds.
     */
    private static final int SPLASH_TIME_OUT = 1000;

    /**
     * The android context.
     */
    private Context mContext;

    /**
     * The timeout handler of Splash.
     */
    private Handler mSplashHandler;

    /**
     * Called on activity creation.
     *
     * @param savedInstanceState Saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = this;
        mSplashHandler = new Handler();
        startTimer();
    }

    /**
     * Called when the activity is resumed.
     */
    @Override
    protected void onResume() {
        if (mSplashHandler != null) {
            mSplashHandler.removeCallbacksAndMessages(null);
            startTimer();
        }
        super.onResume();
    }

    /**
     * Called when the activity is stopped.
     */
    @Override
    public void onStop() {
        if (mSplashHandler != null) {
            mSplashHandler.removeCallbacksAndMessages(null);
        }
        super.onStop();
    }

    /**
     * Starts a delayed function that switches the activity.
     */
    private void startTimer() {
        mSplashHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferencesUtility mSharedPreference = new SharedPreferencesUtility(mContext);
                if (!mSharedPreference.getBoolean(Constants.ACTIVATED, false)) {
                    startActivity(new Intent(mContext, Login_.class));
                    finish();
                } else {
                    startActivity(new Intent(mContext, Routes_.class));
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
