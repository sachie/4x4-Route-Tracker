package team4x4.trackers.routetracker.fragments;

import android.app.Dialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import team4x4.trackers.routetracker.R;
import team4x4.trackers.routetracker.activities.Routes;
import team4x4.trackers.routetracker.models.Coordinate;
import team4x4.trackers.routetracker.models.Route;
import team4x4.trackers.routetracker.utilities.DatabaseHandler;

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

        button =(Button)view.findViewById(R.id.track_save_button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        Log.d("Trigger","TRIGGER");

        // Don't initialize location manager, retrieve it from system services.
        LocationManager locationManager = (LocationManager) this
                .getActivity().getSystemService(Context.LOCATION_SERVICE);

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
                /*
                Toast.makeText(MainActivity.this,
                        "Provider disabled: " + provider, Toast.LENGTH_SHORT)
                        .show();*/
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
    private void saveButtonClick(){
        Toast.makeText(getActivity(),
                "Test", Toast.LENGTH_SHORT)
                .show();
    }

    private void showLocationAlert() {

    }

    private void showSaveAlert(){

        Log.d("", "showSaveAlert");
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.save_dialog);

        final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);

        Button buttonYes = (Button) dialog.findViewById(R.id.buttonYes);
        Button buttonNo = (Button) dialog.findViewById(R.id.buttonCancel);
        final EditText tripTitle= (EditText) dialog.findViewById(R.id.tripTitle);


        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),
                        String.valueOf("Trip Name : "+tripTitle.getText().toString() +"\n"+"Rating: "+ratingBar.getRating()),
                        Toast.LENGTH_SHORT).show();
                // dialog.dismiss();
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "click on cancel");
                dialog.dismiss();

            }
        });

        dialog.show();

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

        Log.d("BLAH", String.valueOf(location.getLatitude()));
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
