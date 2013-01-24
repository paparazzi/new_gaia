import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 
 * This class represents the agregation of all of the meteorological phenomena
 * and the topographic elements. It will gather all of the localized phenomena,
 * the vertical profile and the turbulences, sum each contribution and create a
 * wind vector to send through the IVY bus.
 *
 * @author Hugo
 * @version 1.0
 */
public class Map {

    List<LocalizedPhenomenon> localizedPhenomenonList = new ArrayList(); /* The
    data structure that lists all the localized phenomena of the environment .*/
    Turbulences turbulences = new Turbulences(); // The implemented turbulences.
    VerticalProfile verticalProfile = new VerticalProfile(); /* The implemented
    vertical profile. */
    private int timeScale = 1;

    /** 
     * Adds an object LocalizedPhenomenon to the
     * adapted list.
     *
     * @param l the phenomenon to add
     */
    public void addToList(LocalizedPhenomenon l) {
        this.localizedPhenomenonList.add(l);
    }

    /** 
     * Erases a phenomenon from the list.
     *
     * @param l the phenomenon to erase
     */
    public void deleteFromList(LocalizedPhenomenon l) {
        this.localizedPhenomenonList.remove(l);
    }

    /** 
     * Translates a given localized phenomenon.
     *
     * @param currentPhenomenon the phenomenon to translate.
     * @param dx the x increment
     * @param dy the y increment
     */
    public void translateLocalizedPhenomenon(
            LocalizedPhenomenon currentPhenomenon, double dx, double dy) {
        currentPhenomenon.translate(dx, dy);
    }

    /** 
     * Deletes a given localized phenomenon from the concerned list.
     *
     * @param currentPhenomenon the phenomenon to delete
     */
    public void deleteLocalizedPhenomenon(
            LocalizedPhenomenon currentPhenomenon) {
        this.deleteFromList(currentPhenomenon);
    }

    /** 
     * Changes the upper radius of a given phenomenon thanks to an increment.
     *
     * @param currentPhenomenon the phenomenon whose radius have to be modify
     * @param dr the radius increment
     */
    public void modifyRadius(LocalizedPhenomenon currentPhenomenon, double dr) {
        currentPhenomenon.modifyRadius(dr);
    }

    /** 
     * Gets the radius in meters of a specified localized phenomenon.
     *
     * @param l the localized phenomenon
     * @return the radius in meters
     */
    public double getMRadius(LocalizedPhenomenon l) {
        return l.getMRadius();
    }

    /**
     * Gets the radius in pixels of a specified localized phenomenon.
     * 
     * @param l the localized phenomenon
     * @return the radius in pixels
     */
    public double getRadius(LocalizedPhenomenon l) {
        return l.getRadius();
    }

    /**
     * Sets the radius in pixels of a specified localized phenomenon.
     * 
     * @param l the localized phenomenon
     * @param r the new radius in pixels
     */
    public void setRadius(LocalizedPhenomenon l, double r) {
        l.setRadius(r);
    }

    /** 
     * Gets the x value of the coordinates of a specified localized phenomenon.
     * The origin of the coordinates system is the top left point of the panel.
     *
     * @param l the localized phenomenon
     * @return the x value in pixels
     */
    public double getX(LocalizedPhenomenon l) {
        return l.getX();
    }

    /** 
     * Gets the y value of the coordinates of a specified localized phenomenon.
     * The origin of the coordinates system is the top left point of the panel.
     *
     * @param l the localized phenomenon
     * @return the y value in pixels
     */
    public double getY(LocalizedPhenomenon l) {
        return l.getY();
    }

    /** 
     * Gets the x value of the coordinates of a specified localized phenomenon.
     * The origin of the coordinates system is the Home point.
     *
     * @param l the localized phenomenon
     * @return the x value in meters
     */
    public double getEast(LocalizedPhenomenon l) {
        return l.getEast();
    }

    /** 
     * Gets the y value of the coordinates of a specified localized phenomenon.
     * The origin of the coordinates system is the Home point.
     *
     * @param l the localized phenomenon
     * @return the y value in meters
     */
    public double getNorth(LocalizedPhenomenon l) {
        return l.getNorth();
    }

    /** 
     * Gets the data structure gathering the localized phenomena.
     *
     * @return the data structure
     */
    public java.util.List<LocalizedPhenomenon> getLocalizedPhenomenonList() {
        return this.localizedPhenomenonList;
    }


    /** 
     * Creates and add a thermal to the localized
     * phenomena list.
     *
     * @param x the longitude
     * @param y the latitude
     * @return a brand new thermal
     */
    public Thermal createThermal(double x, double y) {
        Thermal t = new Thermal(x, y);
        this.addToList(t);
        return t;
    }

    /** Removes all the thermals from the localized
     * phenomena list. */
    public void resetThermal() {
        Set<LocalizedPhenomenon> tempSet = new HashSet(localizedPhenomenonList);
        for(LocalizedPhenomenon l : tempSet) {
            if(l instanceof Thermal) {
                this.deleteLocalizedPhenomenon(l);
            }
        }
    }

    /** 
     * Sets the maximum speed of a specified thermal.
     *
     * @param t the thermal
     * @param m the new maximum speed value
     */
    public void setMaximumSpeed(Thermal t, double m) {
        t.setMaximumSpeed(m);
    }

    /** 
     * Gets the maximum speed of a specified thermal.
     *
     * @param t the thermal
     * @return the maximum speed in centimeters per second
     */
    public double getMaximumSpeed(Thermal t) {
        return t.getMaximumSpeed();
    }

    /** 
     * Gets the height of a given thermal.
     *
     * @param t the thermal
     * @return the height in meters
     */
    public double getHeight(Thermal t) {
        return t.getHeight();
    }


    /** Resets the intensity value of the turbulences. */
    public void resetTurbulence() {
        this.turbulences.setIntensity(0);
    }

    /** 
     * Sets a new intensity value for the turbulences.
     *
     * @param intensity the new intensity value
     */
    public void setIntensity(int intensity) {
        turbulences.setIntensity(intensity);
    }

    /** 
     * Gets the intensity value for the turbulences.
     *
     * @return the intensity value
     */
    public int getIntensity() {
        return turbulences.getIntensity();
    }


    /** Resets the vertical profile. */
    public void resetVerticalProfile() {
        this.verticalProfile.resetVerticalProfile();
    }

    /** 
     * Sets the maximum height of the vertical
     * profile to a new value.
     *
     * @param h the new maximum height value of the vertical profile
     */
    public void setMaximumHeight(double h) {
        this.verticalProfile.setMaximumHeight(h);
    }

    /** 
     * Changes the value of the maximum height of the vertical profile thanks
     * to an increment.
     *
     * @param dh the maximum height increment
     */
    public void modifyMaximumHeight(double dh) {
        this.verticalProfile.modifyMaximumHeight(dh);
    }

    /** 
     * Gets the maximum height of the vertical
     * profile.
     *
     * @return the maximum height of the vertical profile
     */
    public double getMaximumHeight() {
        return this.verticalProfile.getMaximumHeight();
    }


    /** 
     * Creates a new level for the vertical profile
     * with only its height.
     *
     * @param a the height of the level
     * @return the created level
     */
    public Level createLevel(double a) {
        Level l = new Level(a);
        this.verticalProfile.addToList(l);
        return l;
    }

    /** 
     * Creates a new level for the vertical profile
     * with its height and its wind speed.
     *
     * @param a the height of the level
     * @param s the speed of the level
     * @return the created level
     */
    public Level createLevel(double a, double s) {
        Level l = new Level(a, s);
        this.verticalProfile.addToList(l);
        return l;
    }

    /** 
     * Creates a new level for the vertical profile
     * with its height, its wind speed and its wind direction.
     *
     * @param a the height of the level
     * @param s the speed of the level
     * @param d the direction of the level
     * @return the created level
     */
    public Level createLevel(double a, double s, double d) {
        Level l = new Level(a, s, d);
        this.verticalProfile.addToList(l);
        return l;
    }

    /** 
     * Deletes a level at a given height from the vertical profile.
     *
     * @param a the height of the level to delete
     */
    public void deleteLevel(double a) {
        this.verticalProfile.deleteFromList(a);
    }

    /** 
     * Sets the wind speed of a specified level.
     *
     * @param l the level
     * @param s the new wind speed value
     */
    public void setSpeed(Level l, double s) {
        this.verticalProfile.setSpeed(l, s);
    }

    /** 
     * Sets the height of a specified level.
     *
     * @param l the level
     * @param h the new height value
     */
    public void setHeight(Level l, double h) {
        this.verticalProfile.setHeight(l, h);
    }

    /** 
     * Sets the wind direction of a specified level.
     *
     * @param l the level
     * @param d the new direction value
     */
    public void setDirection(Level l, double d) {
        verticalProfile.setDirection(l, d);
    }

    /** 
     * Changes the wind direction of a specified level thanks to an increment.
     *
     * @param l the level
     * @param ds the direction increment in degres
     */
    public void modifyDirection(Level l, double ds) {
        verticalProfile.modifyDirection(l, ds);
    }

    /** 
     * Gets the level at a specified height.
     *
     * @param a the height of the level
     * @return the level at the specified height
     */
    public Level getLevel(double a) {
        return this.verticalProfile.getLevel(a);
    }

    /** 
     * Gets the wind speed of a specified level.
     *
     * @param l the level
     * @return the wind speed
     */
    public double getSpeed(Level l) {
        return this.verticalProfile.getSpeed(l);
    }

    /** 
     * Gets the height of a specified level.
     *
     * @param l the level
     * @return the height in meters
     */
    public double getHeight(Level l) {
        return this.verticalProfile.getHeight(l);
    }

    /** 
     * Gets the wind direction of a specified level.
     *
     * @param l the level
     * @return the direction in degres
     */
    public double getDirection(Level l) {
        return verticalProfile.getDirection(l);
    }

    /** 
     * Gets the data structure gathering the levels of the vertical profile.
     *
     * @return the data structure
     */
    public java.util.Map<Double, Level> getLevelList() {
        return this.verticalProfile.getLevelList();
    }

    /** 
     * Translates the UAV Coordonnates before the calculation of the thermal
     * wind.
     *
     * @param cUAV UAV coordinates
     * @param wind wind coordinates
     * @return the new UAV coordinates
     */
    public SpaceCoordinates translateDueToWind(SpaceCoordinates cUAV, 
            SpaceCoordinates wind) {
        SpaceCoordinates newcUAV = new SpaceCoordinates();
        newcUAV.setX(cUAV.getX() - wind.getX());
        newcUAV.setY(cUAV.getY() - wind.getY());
        newcUAV.setZ(cUAV.getZ() - wind.getZ());
        return newcUAV;
    }

    /** 
     * Calculates the sum of the winds for the UAV coordinates.
     *
     * @param cUAV UAV coordinates
     * @return the total wind coordinates
     */
    public SpaceCoordinates getWind(SpaceCoordinates cUAV) {
        SpaceCoordinates wind1 = turbulences.getWind(cUAV);
        SpaceCoordinates wind2 = verticalProfile.getWind(cUAV);
        SpaceCoordinates windResult = new SpaceCoordinates(0.0, 0.0, 0.0) ;
        SpaceCoordinates newcUAV = translateDueToWind(cUAV, wind2);

        ArrayList<LocalizedPhenomenon> currentLocalizedPhenomenonList = 
                this.cloneLocalizedPhenomenonList();
        for (LocalizedPhenomenon element : currentLocalizedPhenomenonList) {
            SpaceCoordinates windlocalized = element.getWind(newcUAV);
            windResult.translate(windlocalized.getX(), windlocalized.getY(), 
                    windlocalized.getZ());
        }
        windResult.translate(wind2.getX(), wind2.getY(), wind2.getZ());
        windResult.translate(wind1.getX(), wind1.getY(), wind1.getZ());
        return windResult;
    }

    /**
     * Copies a localized phenomenon.
     * 
     * @return the newly created localized phenomenon
     */
    public ArrayList<LocalizedPhenomenon> cloneLocalizedPhenomenonList() {
        ArrayList<LocalizedPhenomenon> newLocalizedPhenomenonList = 
                new ArrayList();
        for (LocalizedPhenomenon element: localizedPhenomenonList){
            newLocalizedPhenomenonList.add(element);
        }
        return newLocalizedPhenomenonList;
    }

    /**
     * Gets the time scale of the current simulation.
     * 
     * @return the time scale
     */
    public int getTimeScale() {
        return this.timeScale;
    }

    /**
     * Changes the time scale of the current simulation.
     * 
     * @param s the new time scale
     */
    public void setTimeScale(int s) {
        this.timeScale = s;
    }
}
