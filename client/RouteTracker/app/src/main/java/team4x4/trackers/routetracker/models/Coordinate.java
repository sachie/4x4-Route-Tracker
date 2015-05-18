package team4x4.trackers.routetracker.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Model class for route coordinates.
 */
@Table(name = "Coordinates")
public class Coordinate extends Model {

    /**
     * The route that the coordinates belong to.
     */
    @Column(name = "Route", onDelete = Column.ForeignKeyAction.CASCADE)
    private Route mRoute;

    /**
     * Latitude of the coordinate.
     */
    @Column(name = "Latitude")
    private double mLatitude;

    /**
     * Longitude of the coordinate.
     */
    @Column(name = "Longitude")
    private double mLongitude;

    /**
     * @param mLatitude  Latitude of the coordinate.
     * @param mLongitude Longitude of the coordinate.
     */
    public Coordinate(double mLatitude, double mLongitude) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    /**
     * Empty default constructor. (Required by Sugar ORM)
     */
    public Coordinate() {
        super();
    }

    /**
     * Sets the route for the coordinate.
     *
     * @param mRoute Route for the coodinate.
     */
    public void setRoute(Route mRoute) {
        this.mRoute = mRoute;
    }

    /**
     * Gets the latitude of the coordinate.
     *
     * @return Latitude of the coordinate.
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Sets the latitude of the coordinate.
     *
     * @param mLatitude Latitude of the coordinate.
     */
    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    /**
     * Gets the longitude of the coordinate.
     *
     * @return Longitude of the coordinate.
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * Sets the longitude of the coordinate.
     *
     * @param mLongitude Longitude of the coordinate.
     */
    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }
}
