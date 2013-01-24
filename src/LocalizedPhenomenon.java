import java.awt.geom.Point2D;

/** 
 * This class represents all the localized phenomena which can be implemented
 * by the user. For the moment, only the following phenomena are treated :
 * - thermal columns.
 *
 * @author Hugo
 * @version 1.0
 */
public abstract class LocalizedPhenomenon implements MeteorologicalPhenomenon {

    /**
     * All localized phenomenon will be located by their distance from the 
     * origin point.
     */
    protected Coordinates origin = new Coordinates(0.0,0.0);
    
    /**
     * All localized phenomenon will be defined by the distance between the 
     * origin point coordinates and the coordinates basis of the panel.
     */
    protected static Point2D offSetOrigin = MapControler.getOrigin();

    /** Each localized phenomenon is characterized by a radius in pixels which
     * can be seen as the radius of an effect area. */
    protected double radius;

    /** Each localized phenomenon is characterized by a radius in meters which
     * can be seen as the radius of an effect area. */
    protected double mRadius;

    /** Each localized phenomenon is characterized by a set of coordinates in
     * pixels. */
    protected Coordinates coordinates = new Coordinates();

    /** Each localized phenomenon is characterized by a set of coordinates in
     * meters. */
    protected Coordinates mCoordinates = new Coordinates();

    /**
     * Gets the origin point coordinates.
     * 
     * @return the origin point coordinates
     */
    public Coordinates getOrigin() {
        return origin;
    }

    /**
     * Gets the distance between the origin point and the coordinates basis of
     * the panel.
     * 
     * @return the distance between the origin point and the coordinates basis
     */
    public Point2D getOffSetOrigin() {
        return offSetOrigin;
    }

    /**
     * Sets the origin point.
     * 
     * @param x the x value
     * @param y the y value
     */
    public void setOrigin (double x, double y) {
        origin.setCoordinates(x, y);
    }

    /**
     * Changes the distance between the origin point and the coordinates basis
     * of the panel.
     * 
     * @param x the x value
     * @param y the y value
     */
    public static void setOffSetOrigin (double x, double y) {
        offSetOrigin.setLocation(x, y);
    }

    /** 
     * Changes the current upper value of the phenomenon upper radius to a new
     * one.
     *
     * @param r the new upper radius value
     */
    abstract void setRadius(double r);

    /** 
     * Changes the current upper radius thanks to an increment.
     *
     * @param dr the radius increment
     */
    abstract void modifyRadius(double dr);

    /** 
     * Gets the value of the phenomenon upper radius.
     *
     * @return the upper radius of the thermal
     */
    abstract double getRadius();

    /** 
     * Gets the value of the phenomenon upper radius in meters.
     *
     * @return the upper radius of the thermal in meters
     */
    abstract double getMRadius();


    /** 
     * Changes the current coordinates of the phenomenon to a new one.
     *
     * @param c the new coordinates
     */
    abstract void setCoordinates(Coordinates c);

    /** 
     * Changes the current coordinates of the phenomenon to a new one.
     *
     * @param px the new x value
     * @param py the new y value
     */
    abstract void setCoordinates(double x, double y);

    /** 
     * Translates the thermal on both axis.
     *
     * @param dx the x axis translation
     * @param dy the y axis translation
     */
    abstract void translate(double dx, double dy);

    /** 
     * Gets the x value of the phenomenon coordinates in pixels. The origin of
     * the coordinates system is the top left point of the panel.
     *
     * @return the x value in pixels
     */
    abstract double getX();

    /** 
     * Gets the y value of the phenomenon coordinates in pixels. The origin of
     * the coordinates system is the top left point of the panel.
     *
     * @return the y value in pixels
     */
    abstract double getY();

    /** 
     * Gets the y value of the phenomenon coordinates in meters. The origin of
     * the coordinates system is the Home point.
     *
     * @return the y value in meters
     */
    abstract double getNorth();

    /** 
     * Gets the x value of the phenomenon coordinates in meters. The origin of
     * the coordinates system is the Home point.
     *
     * @return the x value in meters
     */
    abstract double getEast();

}
