/** 
 * This class represents the thermal columns as a localized meteorological
 * phenomenon. It allows one to create a thermal with default values, to
 * modify its radius, coordinates, maximum speed and height, and then to
 * calculate the wind at a precise location.
 *
 * @author Ons
 * @author Alexandra
 * @author Hugo
 * @version 1.1
 */
public class Thermal extends LocalizedPhenomenon {
    private double maximumSpeed; /* The maximum speed the wind can reach within
    the thermal.Unit cm/s. */

    private double height; /* The calculated height of the termal.Unit m. */
    private static final double ALPHA = 0.1015;
    /** The standard maximum speed in centimeters per second chosen to be
    * implemented when a thermal is created. */
    public static final double DEFAULT_MAXIMUM_SPEED = 200.0; // cm.s-1

    /** The standard radius in meters chosen to be implemented when a thermal is
     * created. */
    public static final double DEFAULT_MRADIUS = 40.0; // 40 m

    /** The standard radius in pixels chosen to be implemented when a thermal is
     * created. */
    public static final double DEFAULT_RADIUS = MainCoordinatesManager.
            meterToPixel(DEFAULT_MRADIUS); // 40 m

    /** The minimum radius in meters a thermal can reach. */
    public static final double MINIMUM_MRADIUS = 3; // 1.5 m

    /** The minimum radius in pixels a thermal can reach. */
    public static final double MINIMUM_RADIUS = MainCoordinatesManager.
            meterToPixel(MINIMUM_MRADIUS);

    /** The maximum radius in meters a thermal can reach. */
    public static final double MAXIMUM_MRADIUS = 200.0; // 200 m

    /** The maximum radius in pixels a thermal can reach. */
    public static final double MAXIMUM_RADIUS = MainCoordinatesManager.
            meterToPixel(MAXIMUM_MRADIUS);

    /** The maximum height in meters a thermal can reach. */
    public static final double MAXIMUM_HEIGHT =
            calculateHeight(MAXIMUM_MRADIUS);

    /** The default height in meters chosen to be implemented when a thermal is
     * created. */
    public static final double DEFAULT_HEIGHT =
            calculateHeight(DEFAULT_MRADIUS);


    /** 
     * Creates a thermal with only its position on the map.
     *
     * @param c the coordinates of the thermal to be created
     */
    public Thermal(Coordinates c) {
        this.coordinates = new Coordinates(c.getX(),c.getY());
        this.mCoordinates = new Coordinates(
                this.coordinates.getX()-origin.getX(),
                -(this.coordinates.getY()-origin.getY())).pCoordsToMCoords();
        this.origin = new Coordinates(
                this.getOffSetOrigin().getX(), 
                this.getOffSetOrigin().getY());
        this.setDefault();
    }

    /** 
     * Creates a thermal with both values x and y from the geographic
     * coordinate system.
     *
     * @param px the x pixel-coordinate
     * @param py the y pixel-coordinate
     */
    public Thermal(double px, double py) {
        this.coordinates.setX(px);
        this.coordinates.setY(py);
        this.origin = new Coordinates(
                this.getOffSetOrigin().getX(), this.getOffSetOrigin().getY());
        this.mCoordinates = new Coordinates(
                this.coordinates.getX()-origin.getX(),
                -(this.coordinates.getY()-origin.getY())).pCoordsToMCoords();
        this.setDefault();
    }

    /** Implements the attributes of the thermal with default values. */
    public final void setDefault() {
        this.radius = DEFAULT_RADIUS;
        this.mRadius = DEFAULT_MRADIUS;
        calculateHeight();
        this.maximumSpeed = DEFAULT_MAXIMUM_SPEED;
    }


    /** 
     * Calculates the radius of the thermal for a given altitude.
     *
     * @param z the altitude of the UAV
     * @return the radius of the thermal for the altitude of the UAV
     */
    private double calculateRadius(double z){
        double currentHeight = new Double(this.height);
        return ALPHA*java.lang.Math.pow(
                (z/currentHeight),(1.0/3.0))*
                (1-(0.25*(z/currentHeight)))*currentHeight;
    }

    @Override
    public void setRadius(double r) {
        this.radius = r;
        this.mRadius = MainCoordinatesManager.pixelToMeter(this.radius);
        calculateHeight();
    }

    @Override
    public void modifyRadius(double dr) {
        this.setRadius(this.radius + dr);
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public double getMRadius() {
        return mRadius;
    }


    @Override
    public void setCoordinates(Coordinates c) {
        this.coordinates = new Coordinates(c.getX(),c.getY());
        this.mCoordinates = new Coordinates(
                this.coordinates.getX()-origin.getX(),
                -(this.coordinates.getY()-origin.getY())).pCoordsToMCoords();
    }

    @Override
    public void setCoordinates(double px, double py) {
        this.coordinates.setX(px);
        this.coordinates.setY(py);
        this.mCoordinates = new Coordinates(
                this.coordinates.getX()-origin.getX(),
                -(this.coordinates.getY()-origin.getY())).pCoordsToMCoords();
    }



    @Override
    public void translate(double dx, double dy) {
        this.coordinates.setX(dx + this.getX());
        this.coordinates.setY(dy + this.getY());
        this.mCoordinates = new Coordinates(
                this.coordinates.getX()-this.origin.getX(),
                -(this.coordinates.getY()-this.origin.getY())).
                pCoordsToMCoords();
    }

    @Override
    public double getX() {
        return (this.coordinates.getX());
    }

    @Override
    public double getY() {
        return (this.coordinates.getY());
    }

    @Override
    double getNorth() {
        return this.mCoordinates.getY();
    }

    @Override
    double getEast() {
        return this.mCoordinates.getX();
    }


    /** 
     * Calculates the height reached by the thermal.
     *
     * @param r the upper radius of the thermal
     */
    private void calculateHeight() {
        this.height = 4*this.mRadius/(3*ALPHA);
    }

    private static double calculateHeight(double r) {
        return 4*r/(3*ALPHA);
    }

    /** 
     * Gets the value of the thermal height.
     *
     * @return the height of the thermal
     */
    public double getHeight() {
        return height;
    }


    /** 
     * Changes the thermal current maximum speed to a new one.Unit cm/s.
     *
     * @param m the new maximum speed value
     */
    public void setMaximumSpeed(double m) {
        this.maximumSpeed = m;
    }

    /** 
     * Gets the value of the thermal maximum speed.
     *
     * @return the maximum speed of the thermal
     */
    public double getMaximumSpeed() {
        return maximumSpeed;
    }


    /** 
     * Calculates the distance between the UAV and the thermal center.
     *
     * @param cUAV coordinates of the UAV (in meters!!)
     * @return the distance between UAV and the thermal center
     */
    private double calculateDistance(SpaceCoordinates cUAV) {
        return java.lang.Math.sqrt(
                java.lang.Math.pow((cUAV.getX()-this.getEast()),2)+
                java.lang.Math.pow((cUAV.getY()-this.getNorth()),2));
    }


    @Override
    public SpaceCoordinates getWind(SpaceCoordinates cUAV) {
        double currentSpeed_cms = new Double(this.maximumSpeed);
        // conversion of the speed from cm/s to m/s.
        double currentSpeed = currentSpeed_cms/100;
        double r = calculateRadius(cUAV.getZ());
        double R = r * 2.0;
        double d = calculateDistance(cUAV);
        double windSpeed; //the wind speed
        SpaceCoordinates wind = new SpaceCoordinates();
        if (d>0 && d<r) {
            windSpeed = currentSpeed*
                    java.lang.Math.cos((d/r)*(java.lang.Math.PI/2));
            }
        else if (d>r && d< R) {
            windSpeed = -(currentSpeed*1/3*
                    java.lang.Math.cos(java.lang.Math.PI*
                    (d-(3.0/2.0)*r)/r));
        }
        else if (d==0) {
            windSpeed = currentSpeed;
        }
        else {
            windSpeed = 0.0;
            }
        wind.setCoordinates(0.0, 0.0, windSpeed);
        return wind;
    }

    @Override
    public Thermal clone() {
        Thermal result = new Thermal(this.coordinates.clone());
        result.setMaximumSpeed(this.getMaximumSpeed());
        result.setRadius(this.getRadius());
        return result;
    }
}
