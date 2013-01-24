/** 
 * This class represents a set of two coordinates.
 *
 * @author Hugo
 * @version 1.0
 */
public class Coordinates {

    /** This attribute represents a value along the x axis. It can be seen as
     * a longitude for geographical coordinates. */
    private double x;

    /** This attribute represents a value along the y axis. It can be seen as
     * a latitude for geographical coordinates. */
    private double y;

    /** A default value is given to the x value of a new set of coordinates. */
    public static final double DEFAULT_X = 0.0;

    /** A default value is given to the y value of a new set of coordinates. */
    public static final double DEFAULT_Y = 0.0;


    /** This first constructor permits one to create a set of coordinates by
     * instantiating its parameters with default values. */
    public Coordinates() {
        x = DEFAULT_X;
        y = DEFAULT_Y;
    }

    /** This second constructor permits one to create a set of coordinates by
     * instantiating its parameters with given values.
     *
     * @param newX the new x value
     * @param newY the new y value
     */
    public Coordinates(double newX, double newY) {
        this.x = newX;
        this.y = newY;
    }

    /** 
     * Sets a new x value.
     *
     * @param newX the new x value
     */
    public void setX(double newX) {
        this.x = newX;
    }

    /** 
     * Sets a new y value.
     *
     * @param newY the new x value
     */
    public void setY(double newY) {
        this.y = newY;
    }

    /** 
     * Gets the x value.
     *
     * @return the x value
     */
    public double getX() {
        return this.x;
    }

    /** 
     * Gets the y value.
     *
     * @return the y value
     */
    public double getY() {
        return this.y;
    }

    /** 
     * Instantiates both parameters with given
     * values.
     *
     * @param newX the new x value
     * @param newY the new y value
     */
    public void setCoordinates(double newX, double newY) {
        this.setX(newX);
        this.setY(newY);
    }

    /** 
     * Translates coordinates in meters to coordinates in pixels.
     *
     * @return coordinates in pixels
     */
    public Coordinates mCoordsToPCoords() {
        return new Coordinates(MainCoordinatesManager.meterToPixel(this.getX()),
                MainCoordinatesManager.meterToPixel(this.getY()));
    }

    /** 
     * Translates coordinates in pixels to coordinates in meters.
     *
     * @return coordinates in meters
     */
    public Coordinates pCoordsToMCoords() {
        return new Coordinates(MainCoordinatesManager.pixelToMeter(this.getX()),
                MainCoordinatesManager.pixelToMeter(this.getY()));
    }

    @Override
    public String toString() {
        String NEW_LINE = System.getProperty("line.separator");
        StringBuilder res = new StringBuilder("x = ");
        res.append(this.getX());
        res.append(NEW_LINE);
        res.append("y = ");
        res.append(this.getY());
        return res.toString();
    }

    /**
     * Modifies the coordinates in a translation way.
     * 
     * @param dx
     * @param dy
     */
    public void translate(double dx, double dy) {
        this.setCoordinates(this.getX() + dx, this.getY() + dy);
    }

    @Override
    public Coordinates clone() {
        return new Coordinates(this.getX(), this.getY());
    }
}
