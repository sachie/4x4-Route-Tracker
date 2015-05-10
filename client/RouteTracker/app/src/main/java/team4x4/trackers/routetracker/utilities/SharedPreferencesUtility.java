package team4x4.trackers.routetracker.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import team4x4.trackers.routetracker.R;

/**
 * Class to access the devices shared preferences.
 */
public class SharedPreferencesUtility {

    /**
     * The activity context.
     */
    private final Context mContext;

    /**
     * Creates an instance of shared preference utility.
     *
     * @param context The activity context.
     */
    public SharedPreferencesUtility(Context context) {
        mContext = context;
    }

    /**
     * Gets string saved in shared preferences by key.
     *
     * @param key key for key value pair.
     * @return Value found for key, or empty if not found.
     */
    public String getString(String key) {
        SharedPreferences settings = mContext.getSharedPreferences(
                mContext.getString(R.string.shared_prefs_file_name), 0);
        return settings.getString(key, "");
    }

    /**
     * Saves string value for provided key.
     *
     * @param key   Key for key value pair.
     * @param value Value for key value pair.
     */
    @SuppressLint("CommitPrefEdits")
    public void saveString(String key, String value) {
        SharedPreferences settings = mContext.getSharedPreferences(
                mContext.getString(R.string.shared_prefs_file_name), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Gets boolean saved in shared preferences by key.
     *
     * @param key          key for key value pair.
     * @param defaultValue The default value.
     * @return Value found for key, or default value if empty.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences settings = mContext.getSharedPreferences(
                mContext.getString(R.string.shared_prefs_file_name), 0);

        return settings.getBoolean(key, defaultValue);
    }

    /**
     * Saves boolean value for provided key.
     *
     * @param key   Key for key value pair.
     * @param value Value for key value pair.
     */
    @SuppressLint("CommitPrefEdits")
    public void saveBoolean(String key, boolean value) {
        SharedPreferences settings = mContext.getSharedPreferences(
                mContext.getString(R.string.shared_prefs_file_name), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * Gets float saved in shared preferences by key.
     *
     * @param key          key for key value pair.
     * @param defaultValue The default value.
     * @return Value found for key, or default value if empty.
     */
    public float getFloat(String key, float defaultValue) {
        SharedPreferences settings = mContext.getSharedPreferences(
                mContext.getString(R.string.shared_prefs_file_name), 0);
        return settings.getFloat(key, defaultValue);
    }

    /**
     * Saves float value for provided key.
     *
     * @param key   Key for key value pair.
     * @param value Value for key value pair.
     */
    @SuppressLint("CommitPrefEdits")
    public void saveFloat(String key, float value) {
        SharedPreferences settings = mContext.getSharedPreferences(
                mContext.getString(R.string.shared_prefs_file_name), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        editor.commit();
    }
}