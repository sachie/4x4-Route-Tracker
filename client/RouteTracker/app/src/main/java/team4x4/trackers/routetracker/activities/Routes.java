package team4x4.trackers.routetracker.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.melnykov.fab.FloatingActionButton;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import de.greenrobot.event.EventBus;
import team4x4.trackers.routetracker.R;
import team4x4.trackers.routetracker.fragments.RecordRouteFragment;
import team4x4.trackers.routetracker.fragments.RouteListFragment;
import team4x4.trackers.routetracker.tasks.EventResults.PushNotificationEvent;
import team4x4.trackers.routetracker.tasks.EventResults.RecyclerViewLoadedEvent;
import team4x4.trackers.routetracker.utilities.RouteSyncManager;

/**
 * Activity for the route list screen.
 */
@EActivity(R.layout.activity_routes)
public class Routes extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "message";

    public static final String PROPERTY_REG_ID = "registration_id";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "4X4 Tracker";

    private static final String PROPERTY_APP_VERSION = "appVersion";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * FAB for initiation a record session.
     */
    @ViewById(R.id.record_action_button)
    protected FloatingActionButton mRecordActionButton;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "404429574037";

    GoogleCloudMessaging mGoogleCloudMessagingClient;

    AtomicInteger mMessageId = new AtomicInteger();

    Context mContext;

    String mRegisterId;

    /**
     * Route list fragment instance.
     */
    private RouteListFragment mRouteListFragment = new RouteListFragment();

    /**
     * Record route fragment instance.
     */
    private RecordRouteFragment mRecordRouteFragment = new RecordRouteFragment();

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Called on activity creation.
     *
     * @param savedInstanceState Saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.route_frame_layout, mRecordRouteFragment, "mRouteListFragment")
                .hide(mRecordRouteFragment)
                .commit();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.route_frame_layout, mRouteListFragment, "mRouteListFragment")
                .commit();
        setFragmentManagerListeners();
        mContext = getApplicationContext();
        checkPushNotifications();
        RouteSyncManager.syncRoutes(mContext, null, false);
    }

    private void checkPushNotifications() {

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            mGoogleCloudMessagingClient = GoogleCloudMessaging.getInstance(this);
            mRegisterId = getRegistrationId(mContext);

            if (mRegisterId.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Called after the fragments recycler view references are created.
     * (Used by event bus)
     */
    public void onEvent(RecyclerViewLoadedEvent event) {
        mRecordActionButton.attachToRecyclerView(mRouteListFragment.getRouteRecyclerView());
    }

    /**
     * Called on a push notification event.
     * (Used by event bus)
     */
    public void onEvent(PushNotificationEvent event) {
        RouteSyncManager.syncRoutes(mContext, null, false);
    }

    /**
     * Sets listeners for the fragment manager.
     */
    private void setFragmentManagerListeners() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment routeListFragment = getSupportFragmentManager().findFragmentByTag("mRouteListFragment");
                if (routeListFragment != null && routeListFragment.isVisible()) {
                    mRecordActionButton.show();
                }
            }
        });
    }

    /**
     * Called when the back button is pressed.
     * Overridden to check the backstack in a child fragment manager.
     * (Work around for a bug in fragment managers)
     */
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (mRouteListFragment != null && mRouteListFragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
            mRouteListFragment.getChildFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Click handler for the record route FAB.
     */
    @Click(R.id.record_action_button)
    protected void recordRouteButtonClick() {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mRouteListFragment)
                .show(mRecordRouteFragment)
                .addToBackStack(null)
                .commit();
        mRecordActionButton.hide();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (mGoogleCloudMessagingClient == null) {
                        mGoogleCloudMessagingClient = GoogleCloudMessaging.getInstance(mContext);
                    }
                    mRegisterId = mGoogleCloudMessagingClient.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + mRegisterId;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(mContext, mRegisterId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //  mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(Routes.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }
}
