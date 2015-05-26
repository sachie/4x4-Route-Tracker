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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import de.greenrobot.event.EventBus;
import team4x4.trackers.routetracker.R;
import team4x4.trackers.routetracker.adapters.RecyclerViewScrollHandler;
import team4x4.trackers.routetracker.adapters.RouteRecyclerViewAdapter;
import team4x4.trackers.routetracker.models.Coordinate;
import team4x4.trackers.routetracker.models.Route;
import team4x4.trackers.routetracker.tasks.EventResults.RecyclerViewLoadedEvent;
import team4x4.trackers.routetracker.tasks.EventResults.SyncCompleteEvent;
import team4x4.trackers.routetracker.utilities.DatabaseHandler;
import team4x4.trackers.routetracker.utilities.RouteSyncManager;

/**
 * Fragment to display the list of routes and route details.
 */
public class RouteListFragment extends Fragment {

    /**
     * Recycler view for routes.
     */
    private RecyclerView mRouteRecyclerView;

    /**
     * Data adapter for the route recycler view.
     */
    private RouteRecyclerViewAdapter mRouteRecyclerViewAdapter;

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
     * Id of the route that was last drawn.
     */
    private int mDrawnRouteId;

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
     * Called on fragment creation.
     * Overridden to register this class to the event bus.
     *
     * @param savedInstanceState Saved instance state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    /**
     * Called when the fragment is destroyed.
     * Overridden to unregister this class from the event bus.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        mRouteRecyclerViewAdapter = new RouteRecyclerViewAdapter() {
            @Override
            public void onClick(View v) {
                showMapView();
                mDrawnRouteId = (((ViewHolder) v.getTag()).mServerId);
                drawRoute();
            }
        };
        mRouteRecyclerView.setAdapter(mRouteRecyclerViewAdapter);
        mRouteRecyclerView.addOnScrollListener(new RecyclerViewScrollHandler());
        EventBus.getDefault().post(new RecyclerViewLoadedEvent());
    }

    /**
     * Called after a sync completes.
     * (Used by event bus)
     */
    public void onEvent(SyncCompleteEvent event) {
        mRouteRecyclerViewAdapter.update();
        drawRoute();
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
                RouteSyncManager.syncRoutes(getActivity(), new Runnable() {
                    @Override
                    public void run() {
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
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
    private void drawRoute() {
        mGoogleMap.clear();
        Route routeToDraw = DatabaseHandler.getRouteByServerId(mDrawnRouteId);
        if (routeToDraw != null && routeToDraw.getCoordinates() != null) {
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
}
