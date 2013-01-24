/** 
 * This class represents the turbulences phenomenon. It implements the
 * meteorological phenomenon class.
 *
 * @author Ons
 * @author Alexandra
 * @version 1.0
 */

public class Turbulences implements MeteorologicalPhenomenon {

    private int intensity;

    /** The following values are used to define the bounds of the light,
     * moderate and severe turbulences cases. */

    /** The maximum value to get light turbulences. */
    public static final double LIGHT_MAX = 7.0;

    /** The maximum value to get moderate turbulences. */
    public static final double MODERATE_MAX = 14.0;

    /** The maximum value to get severe turbulences. */
    public static final double SEVERE_MAX = 20.0;

    /** A default value is given when Turbulences is instantiated. */
    public static final double DEFAULT_TURBULENCES = 0.0;

    SpaceCoordinates wind = new SpaceCoordinates();

    /** This constructor allows one to instantiated the class with a default
     * value. */
    public Turbulences() {
        this.setDefault();
    }

    /** 
     * Sets the intensity of the turbulences.
     *
     * @param i the new intensity value.
     */
    public void setIntensity(int i) {
        this.intensity = i ;
    }

    /** 
     * Gets the intensity value of the turbulences.
     *
     * @return the intensity value
     */
    public int getIntensity() {
        return this.intensity;
    }

    /** Instantiates turbulences with default values. */
    public final void setDefault() {
        wind.setCoordinates(DEFAULT_TURBULENCES, DEFAULT_TURBULENCES,
                DEFAULT_TURBULENCES);
    }

    /** 
     * Generates a random double between min and max.
     *
     * @param min the minimum value for the random
     * @param max the maximum value for the random
     * @return the generated double
     */
    private static double randomGenerator(double min, double max) {
        return (double)java.lang.Math.random() * (max - min +1) + min;
    }

    /** 
     * Calculates the contribution to the wind vector of the turbulences.
     *
     * @return the wind vector
     */
    @Override
    public SpaceCoordinates getWind(SpaceCoordinates cUAV) {
        int currentIntensity = new Integer(this.intensity);
        if(currentIntensity == 1) {
            wind.setX(randomGenerator(-LIGHT_MAX,LIGHT_MAX));
            wind.setY(randomGenerator(-LIGHT_MAX,LIGHT_MAX));
            wind.setZ(randomGenerator(-LIGHT_MAX,LIGHT_MAX));
        }
        else if(currentIntensity == 2) {
            wind.setX(randomGenerator(-MODERATE_MAX,MODERATE_MAX));
            wind.setY(randomGenerator(-MODERATE_MAX,MODERATE_MAX));
            wind.setZ(randomGenerator(-MODERATE_MAX,MODERATE_MAX));
        }
        else if(currentIntensity == 3) {
            wind.setX(randomGenerator(-SEVERE_MAX,SEVERE_MAX));
            wind.setY(randomGenerator(-SEVERE_MAX,SEVERE_MAX));
            wind.setZ(randomGenerator(-SEVERE_MAX,SEVERE_MAX));
        }
        else {
            wind.setCoordinates(0.0, 0.0, 0.0);
        }
        return wind;
    }
}
