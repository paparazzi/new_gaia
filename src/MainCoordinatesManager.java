import java.io.File;

/**
 *
 * @author seb
 * @version 1.0
 */
public class MainCoordinatesManager {

    /**
     * Commonly used ration Meters/Mile.
     */
    final static double METERS_IN_1_MILE = 1852.0;
    
    /**
     * Main landmark coordinates
     */
    static Coordinates mainLandMark = new Coordinates(0.0, 0.0);
    
    /**
     * Main SRTM tile to use
     */
    public static SRTM srtm;

    /* Default ratio meters/pixel, depends on the screen and location of the 
     * simulation. */
    private static double unitePixel=0.86534265;


    private MainCoordinatesManager() {};

    /**
     * Setter for the main landmark (generally Home)
     * @param lon0 longitude of the landmark
     * @param lat0 latitude of the landmark
     */
    static void setMainLandMark(double lon0, double lat0) {
        mainLandMark.setCoordinates(lon0, lat0);
        try {
            srtm = new SRTM(mainLandMark);
        } catch (InputException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Getter for the main land mark (genrally HOME in Paparazzi)
     * @return the coordinates used as reference
     */
    static Coordinates getMainLandMark () {
        if (mainLandMark!= null) {
        return new Coordinates(mainLandMark.getX(), mainLandMark.getY());
        }
        else {
            return null;
        }
    }
    
    /**
     * 
     * @param x
     * @return 
     */
    static double metersToDegrees(double x) {
        return ((x/METERS_IN_1_MILE)/60.0);
    }
    
    /**
     * Gets the paparazzi directory, scanning it in the parents directories.
     * 
     * @return the paparazzi file
     */
    public static File getPaparazzi () {
        File currentDir = new File(System.getProperty("user.dir"));
        while (currentDir!=null) {
            File[] listFiles = currentDir.listFiles();
            for (File f : listFiles) {
                if (f.isDirectory() && f.getName().compareTo("paparazzi")==0) {
                    return f;
                }
            }
            currentDir = currentDir.getParentFile();
        }
        return null;
    }
    
    /**
     * Conversion from local coordinates (relative to HOME) in meters to 
     * geographic coordinates.
     * WARNING : Not accurate ! Based on a simple conversion from meters to 
     * miles !
     * 
     * @param x x local coordinate (East)
     * @param y y local coordinate (North)
     * @return the corresponding geographic coordinates
     */
    public static Coordinates mCoordToGeoCoord (double x, double y) {
        if (mainLandMark!=null) {
        double longi = mainLandMark.getX()+MainCoordinatesManager.metersToDegrees(x);
        double lat = mainLandMark.getY()+MainCoordinatesManager.metersToDegrees(y);
        return new Coordinates(longi,lat);
        }
        else {
            return new Coordinates(0.0, 0.0);
        }
    }

    /**
     * Gets the corresponding meter length.
     * 
     * @param taillePixel
     * @return
     */
    public static double pixelToMeter(double taillePixel) {
        return taillePixel*unitePixel;
    }

    /**
     * Gets the corresponding pixel length.
     * 
     * @param tailleMeter
     * @return
     */
    public static double meterToPixel(double tailleMeter) {
        return tailleMeter/unitePixel;
    }

    public static void setUnitePixel (double ratio) {
        unitePixel = ratio;
    }


}
