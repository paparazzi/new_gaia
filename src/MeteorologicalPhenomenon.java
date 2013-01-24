/** 
 * This class represents the interface for all the meteorological phenomena.
 *
 * @author Hugo
 * @version 1.0
 */
public interface MeteorologicalPhenomenon {

    /** 
     * Calculates the wind speed of a meteorological phenomenon.
     *
     * @param cUAV the UAV coordinates
     * @return the wind speed on each east, north and up axis
     */
    SpaceCoordinates getWind(SpaceCoordinates cUAV);

}
