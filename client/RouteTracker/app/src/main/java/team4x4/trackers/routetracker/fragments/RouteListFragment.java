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

import de.greenrobot.event.EventBus;
import team4x4.trackers.routetracker.R;
import team4x4.trackers.routetracker.RoutesApplication;
import team4x4.trackers.routetracker.adapters.RecyclerViewScrollHandler;
import team4x4.trackers.routetracker.adapters.RouteRecyclerViewAdapter;
import team4x4.trackers.routetracker.tasks.EventResults.RecyclerViewLoadedEvent;

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
        mRouteRecyclerView = (RecyclerView) view.findViewById(R.id.route_recycler_view);
        mRouteRecyclerView.setHasFixedSize(true);
        mRouteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRouteRecyclerView.setAdapter(new RouteRecyclerViewAdapter(getActivity()));
        mRouteRecyclerView.addOnScrollListener(new RecyclerViewScrollHandler());
        EventBus.getDefault().post(new RecyclerViewLoadedEvent());
        setUpSwipeRefreshView(view);
        return view;
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
}
