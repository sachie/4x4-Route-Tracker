package team4x4.trackers.routetracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import team4x4.trackers.routetracker.R;
import team4x4.trackers.routetracker.models.Route;
import team4x4.trackers.routetracker.utilities.DatabaseHandler;

/**
 * Recycler view adapter for the route list.
 */
public abstract class RouteRecyclerViewAdapter extends RecyclerView.Adapter<RouteRecyclerViewAdapter.ViewHolder>
        implements Filterable, View.OnClickListener {

    /**
     * List of routes to list.
     */
    private List<Route> mRouteList = new ArrayList<>();

    /**
     * Filter instance for the adapter.
     */
    private RouteFilter mFilter = new RouteFilter();

    /**
     * Default constructor.
     */
    public RouteRecyclerViewAdapter() {
        update();
    }

    /**
     * Updates the list using the filter interface.
     */
    public void update() {
        getFilter().filter(null);
    }

    /**
     * Called when a view holder is to be created.
     *
     * @param parent   Parent view group.
     * @param viewType Type of the view.
     * @return New ViewHolder object.
     */
    @Override
    public RouteRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_list_item, parent, false);
        TextView mTitleTextView = (TextView) view.findViewById(R.id.route_title);
        TextView mDifficultyTextView = (TextView) view.findViewById(R.id.route_difficulty);
        TextView mDistanceTextView = (TextView) view.findViewById(R.id.route_distance);
        return new ViewHolder(view, mTitleTextView, mDifficultyTextView, mDistanceTextView);
    }

    /**
     * Called when data binding is required for a view.
     *
     * @param holder   ViewHolder to bind data to.
     * @param position Position of the item on the list.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Route route = mRouteList.get(position);
        holder.setServerId(route.getServerId());
        holder.mTitleTextView.setText(route.getTitle());
        String difficultyStars = "";
        for (int count = 0; count < route.getDifficultyRating(); count++) {
            difficultyStars += "*";
        }
        holder.mDifficultyTextView.setText(difficultyStars);
        holder.mDistanceTextView.setText("Distance: " + String.valueOf(route.getDistance()) + " KM");
        holder.itemView.setOnClickListener(this);
    }

    /**
     * Gets the item count.
     *
     * @return Route list count.
     */
    @Override
    public int getItemCount() {
        return mRouteList.size();
    }

    /**
     * Gets the filter instance.
     *
     * @return Filter instance.
     */
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    /**
     * View holder class for the adapter.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Id of the route.
         */
        public int mServerId;

        /**
         * Route title text view.
         */
        public TextView mTitleTextView;

        /**
         * Route difficulty rating text view.
         */
        public TextView mDifficultyTextView;

        /**
         * Route distance text view.
         */
        public TextView mDistanceTextView;

        /**
         * Complete constructor.
         *
         * @param itemView            The items view.
         * @param mTitleTextView      Route title text view.
         * @param mDifficultyTextView Route difficulty rating text view.
         * @param mDistanceTextView   Route distance text view.
         */
        public ViewHolder(View itemView, TextView mTitleTextView, TextView mDifficultyTextView, TextView mDistanceTextView) {
            super(itemView);
            this.mTitleTextView = mTitleTextView;
            this.mDifficultyTextView = mDifficultyTextView;
            this.mDistanceTextView = mDistanceTextView;
            itemView.setTag(this);
        }

        /**
         * Sets the route id.
         *
         * @param mServerId Route id.
         */
        public void setServerId(int mServerId) {
            this.mServerId = mServerId;
        }
    }

    /**
     * Filter implementation.
     */
    public class RouteFilter extends Filter {

        /**
         * Performs filtering of the data.
         *
         * @param constraint Constraints for the task.
         * @return Results from filtering.
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<Route> routeList = DatabaseHandler.getRouteList();
            filterResults.count = routeList.size();
            filterResults.values = routeList;
            return filterResults;
        }

        /**
         * Publishes the results to the adapter.
         *
         * @param constraint Constraint for the task.
         * @param results    Results from filtering.
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mRouteList = (List<Route>) results.values;
            notifyDataSetChanged();
        }
    }
}