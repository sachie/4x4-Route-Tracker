package team4x4.trackers.routetracker.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.melnykov.fab.FloatingActionButton;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import team4x4.trackers.routetracker.R;
import team4x4.trackers.routetracker.fragments.RecordRouteFragment;
import team4x4.trackers.routetracker.fragments.RouteListFragment;
import team4x4.trackers.routetracker.models.Coordinate;
import team4x4.trackers.routetracker.models.Route;
import team4x4.trackers.routetracker.tasks.EventResults.RecyclerViewLoadedEvent;
import team4x4.trackers.routetracker.utilities.DatabaseHandler;

/**
 * Activity for the route list screen.
 */
@EActivity(R.layout.activity_routes)
public class Routes extends AppCompatActivity {

    /**
     * FAB for initiation a record session.
     */
    @ViewById(R.id.record_action_button)
    protected FloatingActionButton mRecordActionButton;

    /**
     * Route list fragment instance.
     */
    private RouteListFragment mRouteListFragment = new RouteListFragment();

    /**
     * Record route fragment instance.
     */
    private RecordRouteFragment mRecordRouteFragment = new RecordRouteFragment();

    /**
     * Called on activity creation.
     *
     * @param savedInstanceState Saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.route_frame_layout, mRouteListFragment, "mRouteListFragment").commit();
        setFragmentManagerListeners();
        addDummyData();
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
     * Adds a few dummy records if the database is empty.
     * (HAS TO BE REMOVED ONCE THE JSON API IS SET UP)
     */
    private void addDummyData() {
        if (DatabaseHandler.getRouteList().size() == 0) {
            List<Coordinate> coordinates = new ArrayList<>();
            coordinates.add(new Coordinate(1, 2));
            coordinates.add(new Coordinate(2, 1));
            new Route("Yala Route A", 2, 6, new ArrayList<>(coordinates)).persist();
            List<Coordinate> coordinates2 = new ArrayList<>();
            coordinates2.add(new Coordinate(3, 4));
            coordinates2.add(new Coordinate(4, 3));
            new Route("Yala Route B", 3, 7, new ArrayList<>(coordinates2)).persist();
            List<Coordinate> coordinates3 = new ArrayList<>();
            coordinates3.add(new Coordinate(5, 6));
            coordinates3.add(new Coordinate(6, 5));
            new Route("Yala Route C", 4, 3, new ArrayList<>(coordinates3)).persist();
        }
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

    /**
     * Click handler for the record route FAB.
     */
    @Click(R.id.record_action_button)
    protected void recordRouteButtonClick() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.route_frame_layout, mRecordRouteFragment, "mRecordRouteFragment")
                .addToBackStack(null)
                .commit();
        mRecordActionButton.hide();
    }
}
