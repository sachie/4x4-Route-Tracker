package team4x4.trackers.routetracker.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Model class for a 4x4 route.
 */
@Table(name = "Routes")
public class Route extends Model {

    /**
     * Title of the route.
     */
    @Column(name = "Title")
    private String mTitle;

    /**
     * Difficulty rating for the route.
     */
    @Column(name = "DifficultyRating")
    private int mDifficultyRating;

    /**
     * Distance of the route.
     */
    @Column(name = "Distance")
    private double mDistance;

    /**
     * Coordinates of the route.
     */
    private List<Coordinate> mCoordinates;

    /**
     * Complete constructor.
     *
     * @param mTitle            Title of the route.
     * @param mDifficultyRating Difficulty rating for the route.
     * @param mDistance         Distance of the route.
     * @param mCoordinates      Coordinates of the route.
     */
    public Route(String mTitle, int mDifficultyRating, float mDistance, List<Coordinate> mCoordinates) {
        this.mTitle = mTitle;
        this.mDifficultyRating = mDifficultyRating;
        this.mDistance = mDistance;
        this.mCoordinates = mCoordinates;
    }

    /**
     * Empty default constructor. (Required by Sugar ORM)
     */
    public Route() {
        super();
    }

    /**
     * Saves the route and its coordinates to the database.
     *
     * @return Id of the saved route.
     */
    public Long persist() {
        long id = super.save();
        for (Coordinate coordinate : mCoordinates) {
            coordinate.setRoute(this);
            coordinate.save();
        }
        return id;
    }

    /**
     * Gets the title of the route.
     *
     * @return Title of the route.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Sets the title of the route.
     *
     * @param mTitle Title of the route.
     */
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    /**
     * Gets the difficulty rating for the route.
     *
     * @return Difficulty rating for the route.
     */
    public int getDifficultyRating() {
        return mDifficultyRating;
    }

    /**
     * Sets the difficulty rating for the route.
     *
     * @param mDifficultyRating Difficulty rating for the route.
     */
    public void setDifficultyRating(int mDifficultyRating) {
        this.mDifficultyRating = mDifficultyRating;
    }

    /**
     * Gets the distance of the route.
     *
     * @return Distance of the route.
     */
    public double getDistance() {
        return mDistance;
    }

    /**
     * Sets the distance of the route.
     *
     * @param mDistance Distance of the route.
     */
    public void setDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    /**
     * Gets a list route coordinates.
     *
     * @return Coordinates of the route as a list.
     */
    public List<Coordinate> getCoordinates() {
        return getMany(Coordinate.class, "Route");
    }

    /**
     * Sets the coordinates of the route.
     *
     * @param mCoordinates Coordinates of the route.
     */
    public void setCoordinates(List<Coordinate> mCoordinates) {
        this.mCoordinates = mCoordinates;
    }
}
