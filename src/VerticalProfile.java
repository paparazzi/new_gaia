import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/** 
 * This class represents the vertical profile as a meteorological phenomenon. A
 * vertical profile is composed of a set of levels.
 *
 * @author Alexandra
 * @author Ons
 * @author Hugo
 * @version 1.0
 */
public class VerticalProfile implements MeteorologicalPhenomenon {

    private double maximumHeight;
    private java.util.Map<Double, Level> levelList = new HashMap();

    /** The maximum height default value chosen to be implemented when the
     * vertical profile is created, in meters. */
    public static final double DEFAULT_MAXIMUM_HEIGHT = 1500.0;

    /** The minimum height default value chosen to be implemented when the
     * vertical profile is created, in meters. */
    public static final double DEFAULT_MINIMUM_HEIGHT = 150.0;

    /** The maximum speed the wind can reached at any height.Unit km/h. */
    public static final double MAXIMUM_SPEED = 15.0;//->15m/s

    private static final double CST_KARMAN =0.4;
    private static final double ROUGHNESS_GRASSLAND = 0.01;

    /** Creates the vertical profile with default values. */
    public VerticalProfile() {
        this.maximumHeight = DEFAULT_MAXIMUM_HEIGHT;
        this.levelList.put(DEFAULT_MINIMUM_HEIGHT,
                new Level(DEFAULT_MINIMUM_HEIGHT));
        this.levelList.put(this.maximumHeight, new Level(this.maximumHeight));
    }


    /** 
     * Sets a new maximum height value.
     *
     * @param h the new maximum height value
     */
    public void setMaximumHeight(double h) {
        this.maximumHeight = h;
    }

    /** 
     * Changes the maximum height value of the vertical profile thanks to an
     * increment.
     *
     * @param dh the height increment
     */
    public void modifyMaximumHeight(double dh) {
        this.maximumHeight += dh;
    }

    /** 
     * Gets the maximum height value.
     *
     * @return the maximum height value in meters
     */
    public double getMaximumHeight() {
        return this.maximumHeight;
    }


    /** 
     * Adds a new level to the level list.
     *
     * @param l the level
     */
    public void addToList(Level l) {
        this.levelList.put(l.getHeight(), l);
    }

    /** 
     * Deletes a level from the list.
     *
     * @param a the height of the level
     */
    public void deleteFromList(double a) {
        this.levelList.remove(a);
    }

    /** Resets the vertical profile. */
    public void resetVerticalProfile() {
        Set<Double> tempSet = new HashSet(levelList.keySet());
        for(double d : tempSet) {
            if(levelList.get(d).getHeight() < this.maximumHeight &&
                    levelList.get(d).getHeight() > DEFAULT_MINIMUM_HEIGHT) {
                this.levelList.remove(d);
            }
        }
    }

    /** 
     * Gets the data structure gathering the levels.
     *
     * @return the data structure
     */
    public java.util.Map<Double, Level> getLevelList() {
        return this.levelList;
    }


    /** 
     * Gets the level at a specified height.
     *
     * @param a the height
     * @return the level
     */
    public Level getLevel(double a) {
        return levelList.get(a);
    }


    /** 
     * Sets the speed value in km/h for a given level.
     *
     * @param l the level
     * @param s the new speed
     */
    public void setSpeed(Level l, double s) {
        l.setSpeed(s);
    }

    /** 
     * Gets the speed value of a given level in km/h.
     *
     * @param l the level
     * @return the speed value
     */
    public double getSpeed(Level l) {
        return l.getSpeed();
    }


    /** 
     * Sets the height value of a given level.
     *
     * @param l the level
     * @param h the new height value
     */
    public void setHeight(Level l, double h) {
        l.setHeight(h);
    }

    /** 
     * Gets the height value of a specified level.
     *
     * @param l the level
     * @return the height
     */
    public double getHeight(Level l) {
        return l.getHeight();
    }


    /** 
     * Sets the wind direction value of a given level.
     *
     * @param l the level
     * @param d the new wind direction value
     */
    public void setDirection(Level l, double d) {
        l.setDirection(d);
    }

    /** 
     * Changes the wind direction value thanks to an increment.
     *
     * @param l the level
     * @param ds the direction increment
     */
    public void modifyDirection(Level l, double ds) {
        l.modifyDirection(ds);
    }

    /** 
     * Gets the direction value of a specified level.
     *
     * @param l the level
     * @return the direction value
     */
    public double getDirection(Level l) {
        return l.getDirection();
    }

    /** Calculates the first coefficient for the wind calculation. */
    private static double calculateCoeffA (double w1 ,double w2,
            double v1 , double v2){
        double a = (w2 -w1)/(v2-v1);
        return a;
    }

    /** Calculates the second coefficient for the wind calculation. */
    private static double calculateCoeffB (double w ,double v, double a){
        double b = w -( a * v);
        return b;
    }

    /** Translates an angle to radians. */
    private static double toRad(double direction){
        return java.lang.Math.abs(direction-360.0)*java.lang.Math.PI/180+
                java.lang.Math.PI/2;
    }

    private static double calculateFriction (double z, double speed ) {
        return (CST_KARMAN * speed)/java.lang.Math.log(z/ROUGHNESS_GRASSLAND);
    }

    private static double calculatePercentEast(double rad){
        return Math.cos(rad) *100.0;
    }

    private Coordinates projectionEast(SpaceCoordinates cUAV){
        Coordinates cUAV2 = new Coordinates (cUAV.getX(),cUAV.getY());
        double eastSlope = MainCoordinatesManager.srtm.
                getEastSlope(MainCoordinatesManager.
                mCoordToGeoCoord(cUAV2.getX(), cUAV2.getY())); //eastSlope
        Coordinates cEast = new Coordinates(Math.cos(eastSlope),
                Math.sin(eastSlope));//(east,up)
        return cEast;
    }

    private Coordinates projectionNorth(SpaceCoordinates cUAV){

        Coordinates cUAV2 = new Coordinates (cUAV.getX(),cUAV.getY());
        double northSlope = MainCoordinatesManager.srtm.
                getNorthSlope(MainCoordinatesManager.
                mCoordToGeoCoord(cUAV2.getX(), cUAV2.getY())); //eastSlope
        Coordinates cNorth = new Coordinates(Math.cos(northSlope),
                Math.sin(northSlope));//(north,up);
        return cNorth;
    }

    private double calculateProjectionUp(
            double east, double north, double percent) {
        double inf ;
        double sup;
        double up;
        if (east <= north) {
            inf = east;
            sup = north;
        }
        else {inf= north; sup=east;}
        double d = sup - inf;
        double dEast = Math.abs(percent*d);
        if (sup == east){
           up=north+ dEast;
        }
        else{
            up=east+(d-dEast);
        }
        return up;
    }

    private SpaceCoordinates calculateTerrain(SpaceCoordinates cUAV,
            double percent){
        Coordinates cEast = this.projectionEast(cUAV);
        Coordinates cNorth = this.projectionNorth( cUAV);
        double up = this.calculateProjectionUp(cEast.getY(), 
                cNorth.getY(), percent);
        SpaceCoordinates terrain = new SpaceCoordinates(cEast.getX(),
                cNorth.getX(), up);
        return terrain;
    }

    private double calculateWindUp(
            SpaceCoordinates cUAV, SpaceCoordinates terrain){
        double d = DEFAULT_MINIMUM_HEIGHT - ROUGHNESS_GRASSLAND;
        double z = cUAV.getZ()-ROUGHNESS_GRASSLAND;
        double percent =(z /d)*100.0;
        double up = (100.0 - percent)*terrain.getZ();
        return up;
    }

    private double calculateWindEast(
            SpaceCoordinates cUAV, SpaceCoordinates terrain, double x){
        double a= calculateCoeffA(
                terrain.getX(), x, ROUGHNESS_GRASSLAND, DEFAULT_MINIMUM_HEIGHT);
        double b= calculateCoeffB(x,DEFAULT_MINIMUM_HEIGHT,a);
        double east=a*cUAV.getZ()+b;
        return east;
    }

    private double calculateWindNorth(
            SpaceCoordinates cUAV, SpaceCoordinates terrain, double y){
        double a= calculateCoeffA(terrain.getY(), y, 
                ROUGHNESS_GRASSLAND,DEFAULT_MINIMUM_HEIGHT);
        double b= calculateCoeffB(y,DEFAULT_MINIMUM_HEIGHT,a);
        double north=a*cUAV.getZ()+b;
        return north;
    }

    @Override
    public SpaceCoordinates getWind(SpaceCoordinates cUAV) {
        HashMap<Double,Level> currentLevelList = this.cloneListLevel();
        Level resultLevel = new Level (cUAV.getZ());
        double lower = DEFAULT_MINIMUM_HEIGHT;
        double upper = this.getMaximumHeight();
        double z   = cUAV.getZ();
        Level h_level;
        Level l_level;

        SpaceCoordinates wind = new SpaceCoordinates();

        //Boundary Layer
        if (z < DEFAULT_MINIMUM_HEIGHT) {
            if (z< ROUGHNESS_GRASSLAND){
                resultLevel.setSpeed(0.0);
            }
            else{
                double speed_boundry_layer =
                        (calculateFriction(DEFAULT_MINIMUM_HEIGHT,
                        currentLevelList.get(DEFAULT_MINIMUM_HEIGHT).
                        getSpeed()) *
                        java.lang.Math.log(z/ROUGHNESS_GRASSLAND)) / CST_KARMAN;
                resultLevel.setSpeed(speed_boundry_layer);
            }
            resultLevel.setDirection(currentLevelList.
                    get(DEFAULT_MINIMUM_HEIGHT).getDirection());
            resultLevel.setHeight(z);
            double rad = toRad(resultLevel.getDirection());

            try {
                double percentEast = calculatePercentEast(rad);
                SpaceCoordinates terrain = 
                        this.calculateTerrain(cUAV, percentEast);
                
                wind.setX(calculateWindEast(
                        cUAV,terrain,Math.cos(rad))* 
                        resultLevel.getSpeed()); //wind_east
                wind.setY(calculateWindNorth(
                        cUAV,terrain,Math.sin(rad))* 
                        resultLevel.getSpeed()); // wind_north
                wind.setZ(calculateWindUp(cUAV,terrain));//wind_up
            }
            catch(NullPointerException e) {
                wind.setX(
                        java.lang.Math.cos(rad)* 
                        resultLevel.getSpeed()); //wind_east
                wind.setY(java.lang.Math.sin(rad)* 
                        resultLevel.getSpeed()); // wind_north
                wind.setZ(0.0);//wind_up
            }
        }

        else {
            if (currentLevelList.containsKey(z)) {
                resultLevel = currentLevelList.get(z);
                resultLevel.setSpeed(currentLevelList.get(z).getSpeed());
            }
            else {
                for (Level element: currentLevelList.values()){
                    if (element.getHeight() > z && element.getHeight() < upper) 
                    {
                        upper = element.getHeight();
                    }
                    if (element.getHeight() <= z && element.getHeight() > lower) 
                    {
                        lower = element.getHeight();
                    }
                }
                h_level = currentLevelList.get(upper);
                l_level = currentLevelList.get(lower);
                if (h_level.getSpeed() == l_level.getSpeed()) {
                    resultLevel.setSpeed(h_level.getSpeed());
                }
                else {
                    double a1  = calculateCoeffA(h_level.getHeight(),
                            l_level.getHeight(), h_level.getSpeed(),
                            l_level.getSpeed());
                    double b1 = calculateCoeffB(h_level.getHeight(), 
                            a1,h_level.getSpeed());
                    double speed = (z - b1) / a1;
                    resultLevel.setSpeed(speed);
                    System.out.println("speed :" + speed);
                }
                if (h_level.getDirection()== l_level.getDirection()) {
                    resultLevel.setDirection(h_level.getDirection());

                }
                else {
                    double a2 = calculateCoeffA (h_level.getDirection(),
                            l_level.getDirection(),h_level.getHeight(),
                            l_level.getHeight()) ;
                    double b2 = calculateCoeffB(h_level.getDirection(), 
                            a2,h_level.getHeight());
                    double direction = (a2*z)+b2;
                    resultLevel.setDirection(direction);
                }
            }
            double rad=toRad(resultLevel.getDirection());
            wind.setX(
                    java.lang.Math.cos(rad)* 
                    resultLevel.getSpeed()); //wind_east
            wind.setY(
                    java.lang.Math.sin(rad)* 
                    resultLevel.getSpeed()); // wind_north
            wind.setZ(0.0);//wind_up
        }
        return wind;
    }

    /**
     * Copies the level list.
     * 
     * @return the newly created level list
     */
    public HashMap<Double,Level> cloneListLevel() {
        HashMap<Double,Level> newLevelList = new HashMap();
        for (Level element: levelList.values()){
            newLevelList.put(element.getHeight(), element.clone());
        }
        return newLevelList;
    }
}
