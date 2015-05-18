package team4x4.trackers.routetracker.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import de.greenrobot.event.EventBus;
import team4x4.trackers.routetracker.R;
import team4x4.trackers.routetracker.RoutesApplication;
import team4x4.trackers.routetracker.adapters.RecyclerViewScrollHandler;
import team4x4.trackers.routetracker.adapters.RouteRecyclerViewAdapter;
import team4x4.trackers.routetracker.models.Coordinate;
import team4x4.trackers.routetracker.models.Route;
import team4x4.trackers.routetracker.tasks.EventResults.RecyclerViewLoadedEvent;
import team4x4.trackers.routetracker.utilities.DatabaseHandler;

/**
 * Fragment to display the list of routes and route details.
 */
public class RouteListFragment extends Fragment {

    /**
     * Recycler view for routes.
     */
    private RecyclerView mRouteRecyclerView;

    /**
     * Swipe refresh layout for the route list.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * The map fragment that contains the google map.
     */
    private SupportMapFragment mMapFragment;

    /**
     * Google map instance for drawing the routes.
     */
    private GoogleMap mGoogleMap;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RouteListFragment() {
    }

    /**
     * Gets the route RecyclerView.
     *
     * @return The route RecyclerView.
     */
    public RecyclerView getRouteRecyclerView() {
        return mRouteRecyclerView;
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
        View view = inflater.inflate(R.layout.fragment_route_list, container, false);
        setUpRecyclerView(view);
        setUpSwipeRefreshView(view);
        setUpGoogleMap();
        return view;
    }

    /**
     * Sets view references for the route recycler view and sets its adapters.
     *
     * @param view Parent view.
     */
    private void setUpRecyclerView(View view) {
        mRouteRecyclerView = (RecyclerView) view.findViewById(R.id.route_recycler_view);
        mRouteRecyclerView.setHasFixedSize(true);
        mRouteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRouteRecyclerView.setAdapter(new RouteRecyclerViewAdapter() {
            @Override
            public void onClick(View v) {
                showMapView();
                drawRoute((((ViewHolder) v.getTag()).mId));
            }
        });
        mRouteRecyclerView.addOnScrollListener(new RecyclerViewScrollHandler());
        EventBus.getDefault().post(new RecyclerViewLoadedEvent());
    }

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
     * Sets up the swipe refresh feature for the route list.
     *
     * @param view The fragments view.
     */
    private void setUpSwipeRefreshView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        RoutesApplication.toast(getActivity(), "Sync completed.", Toast.LENGTH_SHORT, true);
                    }
                }, 3000);
            }
        });
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
    private void drawRoute(int routeId) {
        mGoogleMap.clear();
        Route routeToDraw = DatabaseHandler.getRouteById(routeId);
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(R.color.poly_line_color);
        for (Coordinate coordinate : routeToDraw.getCoordinates()) {
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
}
