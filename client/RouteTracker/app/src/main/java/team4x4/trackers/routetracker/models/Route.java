package team4x4.trackers.routetracker.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model class for a 4x4 route.
 */
@Table(name = "Routes")
public class Route extends Model {

    /**
     * Title of the route.
     */
    @Expose
    @Column(name = "server_id")
    @SerializedName("id")
    private int mServerId;

    /**
     * Title of the route.
     */
    @Expose
    @Column(name = "Title")
    @SerializedName("title")
    private String mTitle;

    /**
     * Difficulty rating for the route.
     */
    @Expose
    @Column(name = "DifficultyRating")
    @SerializedName("difficultyRating")
    private int mDifficultyRating;

    /**
     * Distance of the route.
     */
    @Expose
    @Column(name = "Distance")
    @SerializedName("distance")
    private double mDistance;

    /**
     * Coordinates of the route.
     */
    @Expose
    @SerializedName("coordinates")
    private List<Coordinate> mCoordinates;

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
     * Gets the server ID of the route.
     *
     * @return Servier ID of the route.
     */
    public int getServerId() {
        return mServerId;
    }

    /**
     * Sets the server ID of the route.
     *
     * @param mServerId Server ID of the route.
     */
    public void setServerId(int mServerId) {
        this.mServerId = mServerId;
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
