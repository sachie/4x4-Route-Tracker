package team4x4.trackers.routetracker.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static void uploadRoute(final Context context, final Runnable callback, final Route route) throws JSONException, AuthFailureError {
        int serverId = DatabaseHandler.getRouteList().size();
        route.setServerId(serverId);
        JSONObject jsonObject = new JSONObject();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonStr = gson.toJson(route, Route.class);
        jsonObject.put(String.valueOf(serverId), new JSONObject(jsonStr));
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, context.getString(R.string.route_url),jsonObject,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        RoutesApplication.toast(context, "Upload Successful", Toast.LENGTH_LONG, true);
                        syncRoutes(context, null, false);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        RoutesApplication.toast(context, "Upload Failed", Toast.LENGTH_LONG, false);
                    }
                });
        RoutesApplication.getRequestQueue(context).add(request);
    }
}
