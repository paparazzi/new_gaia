/** 
 * This class represents the levels which are associations between a wind
 * speed and a direction at a given height on the vertical profile.
 *
 * @author Hugo
 * @version 1.0
 */
public class Level {

    private double height;
    private double speed;
    private double direction;

    /** A standard level is implemented with a default wind speed. */
    public static final double DEFAULT_SPEED = 1.0;

    /** A standard level is implemented with a default direction in degrees. */
    public static final double DEFAULT_DIRECTION = 360;

    /** The default wind direction in radians. */
    public static final double DEFAULT_DIRECTION_RAD = 2*Math.PI;


    /** 
     * This first constructor allows one to create a level only with a given
     * height. The direction and speed paramaters are instantiated with the
     * default values.
     *
     * @param a the height
     */
    public Level(double a) {
        this.height = a;
        this.direction = DEFAULT_DIRECTION;
        this.speed = DEFAULT_SPEED;
    }

    /** 
     * This second constructor allows one to create a level only with its
     * height and the wind speed at this height.
     *
     * @param a the height
     * @param s the wind speed
     */
    public Level(double a, double s) {
        this.height = a;
        this.speed = s;
        this.direction = DEFAULT_DIRECTION;
    }

    /** 
     * This second constructor allows one to create a level with all its
     * parameters.
     *
     * @param a the height
     * @param s the wind speed
     * @param d the wind direction
     */
    public Level(double a, double s, double d) {
        this.height = a;
        this.speed = s;
        this.direction = d;
    }

    /** 
     * Sets the height of a chosen level.
     *
     * @param a the height
     */
    public void setHeight(double a) {
        this.height = a;
    }

    /** 
     * Gets the height of a chosen level.
     *
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /** 
     * Sets the wind speed of a chosen level.
     *
     * @param s the speed
     */
    public void setSpeed(double s) {
        this.speed = s;
    }

    /** 
     * Gets the wind speed of a chosen level.
     *
     * @return the speed
     */
    public double getSpeed() {
        return speed;
    }

    /** 
     * Sets the wind direction of a chosen level.
     *
     * @param d the direction
     */
    public void setDirection(double d) {
        this.direction = d;
        if(this.direction >= 361) {
            this.direction = this.direction-360;
        }
    }

    /** 
     * Changes the wind direction value thanks to an increment in degrees.
     *
     * @param ds the direction increment
     */
    public void modifyDirection(double ds) {
        this.direction = this.direction + ds;
        if(this.direction >= 361) {
            this.direction = this.direction-360;
        }
    }

    /** 
     * Gets the wind direction of a chosen level.
     *
     * @return the direction
     */
    public double getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        String NEW_LINE = System.getProperty("line.separator");
        StringBuilder res = new StringBuilder("Level :");
        res.append(NEW_LINE);
        res.append("height :");
        res.append(this.getHeight());
        res.append(NEW_LINE);
        res.append("speed :");
        res.append(this.getSpeed());
        res.append(NEW_LINE);
        res.append("direction :");
        res.append(this.getDirection());
        return res.toString();
    }

    @Override
    public Level clone () {
        return new Level(this.getHeight(), this.getSpeed(), 
                this.getDirection());
    }

}
