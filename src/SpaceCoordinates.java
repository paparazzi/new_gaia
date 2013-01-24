/** 
 * This class represents a set of three coordinates. It extends the Coordinates
 * class.
 *
 * @author Hugo
 * @version 1.0
 */
public class SpaceCoordinates extends Coordinates {

    /** This attribute represents a value along the z axis. It can be seen as
     * an altitude. */
    private double z;

    /** A default value is given to the z value of a new set of coordinates. */
    public static final double DEFAULT_Z = 0.0;

    /** This first constructor permits one to create a set of coordinates by
     * instantiating its parameters with default values. */
    public SpaceCoordinates() {
        super();
        this.z = DEFAULT_Z;
    }

    /** 
     * This second constructor permits one to create a set of coordinates by
     * instantiating its parameters with given values.
     *
     * @param newX the new x value
     * @param newY the new y value
     * @param newZ the new z value
     */
    public SpaceCoordinates(double newX, double newY, double newZ) {
        super(newX, newY);
        this.z = newZ;
    }

    /** 
     * Gets the z value.
     *
     * @return the z value
     */
    public double getZ() {
        return this.z;
    }

    /** 
     * Sets a new z value.
     *
     * @param newZ the new z value
     */
    public void setZ(double newZ) {
        this.z = newZ;
    }

    /** 
     * Instantiates both parameters with given values.
     *
     * @param newX the new x value
     * @param newY the new y value
     * @param newZ the new z value
     */
    public void setCoordinates(double newX, double newY, double newZ) {
        super.setCoordinates(newX, newY);
        this.setZ(newZ);
    }

    /**
     * Translates the coordinates.
     * 
     * @param dx the x increment
     * @param dy the y increment
     * @param dz the z increment
     */
    public void translate(double dx, double dy, double dz) {
        super.translate(dx, dy);
        this.setZ(this.getZ()+dz);
    }

    @Override
    public String toString() {
        String NEW_LINE = System.getProperty("line.separator");
        StringBuilder res = new StringBuilder(super.toString());
        res.append(NEW_LINE);
        res.append("z = ");
        res.append(this.getZ());
        return res.toString();
    }

    @Override
    public SpaceCoordinates clone() {
        return new SpaceCoordinates(this.getX(), this.getY(), this.getZ());
    }
}
