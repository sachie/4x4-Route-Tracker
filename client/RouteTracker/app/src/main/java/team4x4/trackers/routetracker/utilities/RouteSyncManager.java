package team4x4.trackers.routetracker.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.Arrays;

import de.greenrobot.event.EventBus;
import team4x4.trackers.routetracker.R;
import team4x4.trackers.routetracker.RoutesApplication;
import team4x4.trackers.routetracker.models.Route;
import team4x4.trackers.routetracker.tasks.EventResults.SyncCompleteEvent;

/**
 * Class that handles syncing routes from the server.
 */
public class RouteSyncManager {

    /**
     * Starts a route sync.
     *
     * @param context  Context of the calling class.
     * @param callback Callback to run after a successful sync.
     * @param toast    Displays a toast of the sync result if True.
     */
    public static void syncRoutes(final Context context, final Runnable callback, final boolean toast) {
        Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        DatabaseHandler.clearTables();
                        DatabaseHandler.saveRoutes(Arrays.asList(new Gson().fromJson(response.toString(), Route[].class)));
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (callback != null) {
                            callback.run();
                        }
                        if (toast) {
                            RoutesApplication.toast(context, "Sync Complete", Toast.LENGTH_SHORT, true);
                        }
                        EventBus.getDefault().post(new SyncCompleteEvent());
                    }
                }.execute();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (callback != null) {
                    callback.run();
                }
                if (toast) {
                    RoutesApplication.toast(context, "Sync Failed", Toast.LENGTH_LONG, false);
                }
            }
        };
        JsonArrayRequest request = new JsonArrayRequest(context.getString(R.string.route_url), responseListener, errorListener);
        request.setShouldCache(false);
        RoutesApplication.getRequestQueue(context).add(request);
    }
}
