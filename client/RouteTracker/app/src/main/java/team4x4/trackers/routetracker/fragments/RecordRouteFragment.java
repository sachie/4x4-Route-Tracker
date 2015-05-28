package team4x4.trackers.routetracker.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import team4x4.trackers.routetracker.R;
import team4x4.trackers.routetracker.models.Coordinate;
import team4x4.trackers.routetracker.models.Route;
import team4x4.trackers.routetracker.utilities.RouteSyncManager;

/**
 * Fragment to display details of a route.
 */
public class RecordRouteFragment extends Fragment {


    private Button button ;

    /**
     * The map fragment that contains the google map.
     */
    private SupportMapFragment mMapFragment;

    /**
     * Google map instance for drawing the routes.
     */
    private GoogleMap mGoogleMap;

    /**
     *  List to store points for drawing route
     */
    private List<Location> routeCoordinates;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecordRouteFragment() {
    }

    TextView timerTextView;
    long startTime = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("Time Elapsed %d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };


    /**
     * Called on creation of the fragment view.
     *
     * @param inflater           Layout inflater.
     * @param container          Containing view group of the fragment view.
     * @param savedInstanceState Saved instance state.
     * @return Inflated fragment view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        routeCoordinates = new ArrayList<Location>();
        View view =  inflater.inflate(R.layout.fragment_record_route, container, false);
        timerTextView = (TextView)view.findViewById(R.id.text_record_time);

        button =(Button)view.findViewById(R.id.track_save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerHandler.removeCallbacks(timerRunnable);
                showSaveAlert();
            }
        });


        try {
            setUpGoogleMap();
        }
        catch (Exception e) {
            Log.d("EX", e.getMessage());
        }

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        showMapView();

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        // Don't initialize location manager, retrieve it from system services.
        LocationManager locationManager = (LocationManager) this
                .getActivity().getSystemService(Context.LOCATION_SERVICE);

        try {
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                showLocationAlert();
            }
        }
        catch (Exception ex){}

        LocationListener locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("providerEnabled","Provider enabled: " + provider);
                //Toast.makeText(MainActivity.this,"Provider enabled: " + provider, Toast.LENGTH_SHORT.show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("providerEnabled", "Provider disabled: " + provider);
                showLocationAlert();
            }

            @Override
            public void onLocationChanged(Location location) {
                // Do work with new location. Implementation of this method will be covered later.
                doWorkWithNewLocation(location);
            }
        };

        long minTime = 5 * 1000; // Minimum time interval for update in seconds, i.e. 5 seconds.
        long minDistance = 10; // Minimum distance change for update in meters, i.e. 10 meters.

// Assign LocationListener to LocationManager in order to receive location updates.
// Acquiring provider that is used for location updates will also be covered later.
// Instead of LocationListener, PendingIntent can be assigned, also instead of
// provider name, criteria can be used, but we won't use those approaches now.
        locationManager.requestLocationUpdates(getProviderName(), minTime,
                minDistance, locationListener);


    }

    /// UI ACTIONS

    private void showLocationAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set title
        alertDialogBuilder.setTitle(R.string.record_record_route);

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.record_turn_on_location)
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void showSaveAlert(){

        Log.d("", "showSaveAlert");
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.save_dialog);

        final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);

        Button buttonYes = (Button) dialog.findViewById(R.id.buttonYes);
        Button buttonNo = (Button) dialog.findViewById(R.id.buttonCancel);
        final EditText routeTitle = (EditText) dialog.findViewById(R.id.tripTitle);


        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // LINK SAVE CODE HERE
                try {
                    RouteSyncManager.uploadRoute(getActivity().getApplicationContext(), null, createRouteObject(routeTitle.getText().toString(), (int) ratingBar.getRating()));
                    dialog.dismiss();
                    getActivity().getSupportFragmentManager().popBackStack();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (AuthFailureError authFailureError) {
                    authFailureError.printStackTrace();
                }

                // dialog.dismiss();

            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "click on cancel");
                timerHandler.postDelayed(timerRunnable, 0);
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    private Route createRouteObject(String routeName, int rating) {
        Route route = new Route();
        route.setTitle(routeName);
        route.setDifficultyRating(rating);

        List<Coordinate> coordinates = new ArrayList<Coordinate>();

        if(routeCoordinates.size()<1){
            Location loc = new Location("dummyprovider");
            loc.setLatitude(12.43);
            loc.setLongitude(32.54);
            routeCoordinates.add(0, loc);
        }

        for (Location location : routeCoordinates) {
            Coordinate coordinate = new Coordinate(location.getLatitude(), location.getLongitude());
            coordinates.add(coordinate);
        }
        route.setCoordinates(coordinates);

        // TODO:Calculate distance on server. Dummy for the moment
        Random rn= new Random();
        int distance = rn.nextInt(10)+1;
        route.setDistance(distance);

        return route;
    }

    /// MAP VIEW METHODS
    /**
     * Sets up the google map fragment and map instance.
     */
    private void setUpGoogleMap() {
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
        }
        if (mGoogleMap == null) {
            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap = googleMap;
                    mGoogleMap.setMyLocationEnabled(true);
                }
            });
        }
        if (!mMapFragment.isAdded()) {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.map_layout, mMapFragment)
                    .hide(mMapFragment)
                    .commit();
        }
    }

    /**
     * Shows the map view.
     */
    private void showMapView() {
        getChildFragmentManager()
                .beginTransaction()
                .show(mMapFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Draws the route on the map.
     */
    private void drawRoute(List<Location> coordinates) {
        mGoogleMap.clear();
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(R.color.poly_line_color);
        for (Location coordinate : coordinates) {
            polylineOptions.add(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()));
        }
        mGoogleMap.addPolyline(polylineOptions);
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polylineOptions.getPoints()) {
            builder.include(latLng);
        }
        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
            }
        });
    }

    /// LOCATION MANAGER METHODS

    private void doWorkWithNewLocation(Location location){
        if (validateLocation(location)){
            // add value to points array
            routeCoordinates.add(location);

            // pass points array to google map
            drawRoute(routeCoordinates);
        }
    }

    private boolean validateLocation(Location location){
        return true;
    }

    /**
     * Get provider name.
     * @return Name of best suiting provider.
     * */
    String getProviderName() {
        LocationManager locationManager = (LocationManager) this
                .getActivity().getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        return locationManager.getBestProvider(criteria, true);
    }

}
