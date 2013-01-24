import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class used to load the SRTM data and used them to determine 
 * the slope of a precise location
 * @author seb
 * @version 1.0
 */
public class SRTM {

    final static double THREE_SECONDS_IN_DEGREE = 1200.0;
    final static File paparazziDirectory = 
            MainCoordinatesManager.getPaparazzi();
    final static String fileSeparator = System.getProperty("file.separator");
    static File srtmDirectory = null;
    
    /* Static block to initialize the path to the srtm directory */
    static {
        if (paparazziDirectory != null)
        srtmDirectory = new File(paparazziDirectory.getAbsolutePath() 
                + fileSeparator + "data" + fileSeparator + "srtm");
    }

    /**
     * Length of a SRTM tile : 1201 = (1°=1200*3'') + 1 
     * (last line overlapping the next one)
     */
    private final int TILE_SIZE = 1201;
    
    /**
     * Buffer used to conserve the SRTM data (+2000 for the initSRTM method)
     */
    private byte[] buffer = new byte[TILE_SIZE*TILE_SIZE*2+2000];
    
    private FileInputStream zipFile = null;

    /**
     * BEWARE : Only opens the FileInputStream ! Needs to be initiated then !
     * @param mainLandMark Main coordinates : indicates the tile to load
     * @throws InputException : If no SRTM directory found, 
     *                          or if the needed file hasn't been found
     */
    public SRTM(Coordinates home) throws InputException {
        
        double left;
        double bottom;
        String leftS;
        String bottomS;
        String fileName;
        
        if (srtmDirectory ==null)
            throw new InputException("Unable to find the SRTM directory");
        left = Math.floor(home.getX());
        bottom = Math.floor(home.getY());
        leftS = String.format("%03d", (int) left);
        bottomS = String.format("%02d", (int) bottom);
        fileName = ((bottom>0)?"N":"S") + bottomS 
                + ((left>0)?"E":"W") + leftS 
                + ".hgt.zip";
        try {
            zipFile = new FileInputStream(srtmDirectory 
                                          +fileSeparator + fileName);
        } catch (FileNotFoundException ex) {
            throw new InputException("Unable to find the SRTM file");
        }
    }

    /**
     * Inits the SRTM class : loads the data into the buffer
     */
    public void initSRTM() {
        
        ZipInputStream zip;
        ZipEntry e;
        int total;
        int counter;
        
        try {
            zip = new ZipInputStream(zipFile);
            e = zip.getNextEntry();
            total = 0;
            counter = 0;
            while((counter = zip.read(buffer, total, 1000))!= -1) {
                total += counter;
            }
            zip.close();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    /**
     * Reads the correspondant altitude in the SRTM file used.
     * 
     * @param x number of 3'' to shift from the integer longitude
     * @param y number of 3'' to shift from the integer latitude
     * @return altitude of the terrain
     */
    private int getAltitude(int x, int y) {
        
        int index;
        byte byte1;
        byte byte2;
        
        /* Index of the two-bytes signed integer to read.
         * The file is like an array which lines 
         * have been put from start to finish.
         * The coordinates naming the file are at the south-west corner, 
         * since the minus before the y.
         */
        index = 2*((TILE_SIZE-y)*TILE_SIZE+x);
        
        /* Two-bytes signed integer, big endian convention */
        byte1 = buffer[index];
        byte2 = buffer[index+1];
        return (256*byte1 + ((byte2>0)?byte2:byte2+256));
    }

    /**
     * Determines the couple of integer used to getAltitude in the srtm buffer.
     * The closest point that have been measured by the SRTM is returned
     * 
     * @param f latitude or longitude
     * @return couple of values locating the related altitude measure in
     *         the "array" of altitudes
     */
    private Coordinates compute3SecondsIndexes (Coordinates coords) {
        
        double left;
        double bottom;
        
        left = Math.floor(coords.getX());
        bottom = Math.floor(coords.getY());
        return new Coordinates(Math.floor((coords.getX()-left)
                                    *THREE_SECONDS_IN_DEGREE+0.5), 
                               Math.floor((coords.getY()-bottom)
                                    *THREE_SECONDS_IN_DEGREE+0.5));
    }

    /**
     * Computes (approximatively) the slope towards North
     * @param coords coordinates of the location
     * @return the slope in radians
     */
    public double getNorthSlope(Coordinates coords) {
        
        /* Indexes in the array (buffer) */
        int x;
        int y;
        
        /* Altitudes used to determine the slope */
        double z1;
        double z2;
        
        x = (int) this.compute3SecondsIndexes(coords).getX();
        y = (int) this.compute3SecondsIndexes(coords).getY();
        
        z1 = (getAltitude(x, y+5));
        z2 = (getAltitude(x, y-5));
        
        /* A litle bit of trigonometry :
         * Opposite : z1-z2,
         * Hypothenus : 10*3'' = 10*3/60' = 10*3/60*ratioMetersMile in meters
         * Hence this calculus.
         */
        return Math.asin((z1-z2) 
                / (10.0*3.0/60.0*MainCoordinatesManager.METERS_IN_1_MILE));
    }
    
    /**
     * Computes (approximatively) the slope towards East
     * @param coords coordinates of the location
     * @return the slope in radians
     */
    public double getEastSlope(Coordinates coords) {
        
        /* Indexes in the array (buffer) */
        int x;
        int y;
        
        /* Altitudes used to determine the slope */
        double z1;
        double z2;
        
        x = (int) this.compute3SecondsIndexes(coords).getX();
        y = (int) this.compute3SecondsIndexes(coords).getY();
        
        z1 = (getAltitude(x+5, y));
        z2 = (getAltitude(x-5, y));
        
        /* A litle bit of trigonometry :
         * Opposite : z1-z2,
         * Hypothenus : 10*3'' = 10*3/60' = 10*3/60*ratioMetersMile in meters
         * Hence this calculus.
         */
        return Math.asin((z1-z2) 
                / (10.0*3.0/60.0*MainCoordinatesManager.METERS_IN_1_MILE));
    }

}
