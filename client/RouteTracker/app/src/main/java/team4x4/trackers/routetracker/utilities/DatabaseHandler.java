package team4x4.trackers.routetracker.utilities;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import team4x4.trackers.routetracker.models.Coordinate;
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
    public static Route getRouteByServerId(int id) {
        return new Select().from(Route.class).where("server_id = ?", String.valueOf(id)).executeSingle();
    }

    /**
     * Saves a list of routes to the database.
     *
     * @param routes List of routes to save.
     */
    public static void saveRoutes(List<Route> routes) {
        for (Route route : routes) {
            route.persist();
        }
    }

    /**
     * Clears all tables.
     */
    public static void clearTables() {
        new Delete().from(Route.class).execute();
        new Delete().from(Coordinate.class).execute();
    }
}
