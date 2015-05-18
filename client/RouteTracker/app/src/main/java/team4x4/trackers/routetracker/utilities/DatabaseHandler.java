package team4x4.trackers.routetracker.utilities;

import com.activeandroid.query.Select;

import java.util.List;

import team4x4.trackers.routetracker.models.Route;

/**
 * Class that provides static methods for database access.
 */
public class DatabaseHandler {

    /**
     * Gets a list of all routes.
     *
     * @return List of all routes.
     */
    public static List<Route> getRouteList() {
        return new Select().from(Route.class).execute();
    }

    /**
     * Gets a route with the given ID.
     *
     * @param id ID of the route to fetch.
     * @return Required route.
     */
    public static Route getRouteById(int id) {
        return Route.load(Route.class, id);
    }
}
