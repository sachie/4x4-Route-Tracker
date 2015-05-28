package team4x4.trackers.routetracker;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import team4x4.trackers.routetracker.utilities.OkHttpStack;

/**
 * Application class. Extends SugarApp to enable SugarORM support.
 * Contains the Volley request queue and methods for toasting.
 */
public class RoutesApplication extends Application {

    /**
     * Volley Request Queue object.
     */
    private static RequestQueue mRequestQueue;

    /**
     * Gets the global volley request queue.
     *
     * @return Volley Request Queue.
     */
    public static synchronized RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            // Use the OkHttp HurlStack instance to support the PATCH method on devices running less than API 21
            mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack());
        }
        return mRequestQueue;
    }

    /**
     * Generates and shows a toast.
     *
     * @param context Context to use with the toast.
     * @param message Message to toast.
     * @param length  Length of the toast.
     */
    public static void toast(Context context, String message, int length, boolean positiveToast) {
        Toast toast = Toast.makeText(context, message, length);
        if (positiveToast) {
            toast.getView().setBackgroundColor(context.getResources().getColor(R.color.positive_toast_color));
        } else {
            toast.getView().setBackgroundColor(context.getResources().getColor(R.color.negative_toast_color));
        }
        toast.setGravity(Gravity.END | Gravity.BOTTOM, 20, 20);
        toast.show();
    }

    /**
     * Called on application start.
     */
    @Override
    public void onCreate() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .resetViewBeforeLoading(true)
                .displayer(new FadeInBitmapDisplayer(250, true, true, false))
                .showImageForEmptyUri(R.drawable.default_image)
                .showImageOnFail(R.drawable.default_image)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .denyCacheImageMultipleSizesInMemory()
                .threadPoolSize(20)
                .threadPriority(Thread.MAX_PRIORITY)
                .memoryCache(new LruMemoryCache(10 * 1024 * 1024))
                .diskCacheSize(25 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
