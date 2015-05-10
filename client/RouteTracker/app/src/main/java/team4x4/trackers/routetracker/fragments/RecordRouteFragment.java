package team4x4.trackers.routetracker.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team4x4.trackers.routetracker.R;

/**
 * Fragment to display details of a route.
 */
public class RecordRouteFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_record_route, container, false);
    }
}
