import fr.lri.swingstates.canvas.CImage;
import fr.lri.swingstates.canvas.CShape;
import fr.lri.swingstates.canvas.Canvas;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.File;

/**
 *
 * @author seb
 * @version 1.0
 */
public class MapsGoogle {

    /* Geographic coordinates of the HOME point, used to load the correct 
     * tiles. */
    private Coordinates homeGeo = MainCoordinatesManager.getMainLandMark();

    /* Meters to translate all the background tiles to adjust the HOME point on its representation*/
    private Coordinates offSetHomeGeo = new Coordinates (0,0);

    /* Main Tile name : HOME point is on it*/
    private String nameRef = new String("t");

    /* Exemple of a tile to determine the ration pixels/meters */
    private CImage imageRef = null;

    /* Google zoom. */
    final int ZOOM_MAX = 17;
    final int ZOOM_MINI = 0;

    /* (System dependant). */
    final static String fileSeparator = System.getProperty("file.separator");

    static File dirPaparazzi = MainCoordinatesManager.getPaparazzi();
    static final File dirMap = null;

    /* List holding the available tiles. */
    static File[] listeJpg = null;

    /* Fills the list immediately (in case of a load). */
    static {
        if(dirPaparazzi != null) {
            dirPaparazzi = new File(dirPaparazzi.getAbsolutePath() + fileSeparator + "var" + fileSeparator + "maps");
            if (dirPaparazzi != null) {
                listeJpg = dirPaparazzi.listFiles(new JpgFilter());
            }
        }
    }



    /**
     * Construtor : displays the tiles if possible.
     * 
     * @param C Canvas used
     * @param L Image list used
     * @param origin HOME point, in case it has been moved by a scrolling on the background
     * @param homePath 
     */
    public MapsGoogle(Canvas C, java.util.List<CShape> L, Point2D origin, String homePath) {
        try {
            if (homePath==null) {
                throw new InputException("No flight plan currently used");
            }
            if (listeJpg==null) {
                throw new InputException("Couldn't find paparazzi directory or no tiles to display : unable to load the background map");
            }
            homeGeo = MainCoordinatesManager.getMainLandMark();

            /* Initializes the main tile name. */
            System.out.println(MainCoordinatesManager.getMainLandMark());
            nameRef = GoogleTileUtils.getTileRef(homeGeo.getX(), homeGeo.getY(),1);
            String reference = nameRef.concat(".jpg");

            imageRef = null; //?

            /* Looks for the main tile jpg file. */
            for (File image : listeJpg)
                if(0==reference.compareTo(image.getName())) {
                    imageRef = new CImage(image.getAbsolutePath(),new Point2D.Double(0.0,0.0));

                    /* Sets the meters/pixel ratio. */
                    computesUnitePixel();

                    /* Computes the slight offset to apply to HOME point to 
                     * display it on the correct location on the main tile. */
                    offSetHomeGeo = computesOffset(homeGeo);
                }

            if (imageRef == null) {
                throw new InputException("No suitable Google tile found : if not done, load the suitable tiles for display");
            }

            /*  Computes the Width and Height applied to translate the tiles to 
             * their coorect location. */
            double maxWidth = imageRef.getWidth()*Math.pow(2, (ZOOM_MAX-ZOOM_MINI));
            double maxHeight = imageRef.getHeight()*Math.pow(2, (ZOOM_MAX-ZOOM_MINI));

            /* Translates the tile image to the good location. */
            for(File f : listeJpg) {
                double xOffset = 0;
                double yOffset = 0;
                double currentWidth = maxWidth;
                double currentHeight = maxHeight;
                String name = f.getName();
                int zoom;

                /* Processes the name to find the location. */
                for(zoom=ZOOM_MINI;zoom<=ZOOM_MAX;zoom++) {
                    char niemeRef = reference.charAt(zoom);
                    char nieme = name.charAt(zoom);
                    if (niemeRef==nieme) {
                        /* nothing */
                    }
                    /* Adjusting the y and y offsets, according to the chars 
                     * difference. */
                    else {
                        if(niemeRef=='q') {
                            if(nieme=='r') {
                                xOffset += currentWidth;
                            }
                            if(nieme=='t') {
                                yOffset += currentHeight;
                            }
                            if(nieme=='s') {
                                xOffset += currentWidth;
                                yOffset += currentHeight;
                            }
                        }
                        if(niemeRef=='r') {
                            if(nieme=='q') {
                                xOffset -= currentWidth;
                            }
                            if(nieme=='t') {
                                xOffset -= currentWidth;
                                yOffset += currentHeight;
                            }
                            if(nieme=='s') {
                                yOffset += currentHeight;
                            }
                        }
                        if(niemeRef=='t') {
                            if(nieme=='q') {
                                yOffset -= currentHeight;
                            }
                            if(nieme=='r') {
                                xOffset += currentWidth;
                                yOffset -= currentHeight;
                            }
                            if(nieme=='s') {
                                xOffset += currentWidth;
                            }
                        }
                        if(niemeRef=='s') {
                            if(nieme=='q') {
                                yOffset -= currentHeight;
                                xOffset -= currentWidth;
                            }
                            if(nieme=='r') {
                                yOffset -= currentHeight;
                            }
                            if(nieme=='t') {
                                xOffset -= currentWidth;
                            }
                        }
                    }
                    currentWidth/=2;
                    currentHeight/=2;
                }

                CImage image = new CImage(f.getAbsolutePath(),new Point2D.Double(xOffset+offSetHomeGeo.getX()+origin.getX(),yOffset+offSetHomeGeo.getY()+origin.getY()));
                image.setOutlined(false);
                C.addShape(image);
                image.belowAll();
                L.add(image);
            }
        }
        catch (InputException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Processes the given geographic coordinates to determine the offset in 
     * meters relatively to the north-west corner of the main tile.
     * 
     * @param coord the precise cordinates
     * @return the offset offset value (in pixels)
     */
    private Coordinates computesOffset (Coordinates coord) {
        Double NWCornerCoordinates = GoogleTileUtils.getLatLong(nameRef);

        /* First : calculates the corresponding y-offset meters. */
        double lat1 = NWCornerCoordinates.getMaxY();
        double lat2 = NWCornerCoordinates.getMaxY(); //same lat (determines x)
        double deltalong = NWCornerCoordinates.getMinX()-coord.getX();

        /* Calculates the x-projection. */
        double x = 1852*60*180/Math.PI
                * Math.acos((Math.sin(lat1/180*Math.PI)*Math.sin(lat2/180*Math.PI))
                + (Math.cos(lat1/180*Math.PI)*Math.cos(lat2/180*Math.PI)*Math.cos(deltalong/180*Math.PI)));
        if (NWCornerCoordinates.x<coord.getX()) {
            x=-x;
        }

        /* Second : calculates the corresponding y-offset meters. */
        lat2 = coord.getY();
        deltalong = 0;
        /* Calculates the y-projection  */
        double y = 1852*60*180/Math.PI
                * Math.acos((Math.sin(lat1/180*Math.PI)*Math.sin(lat2/180*Math.PI))
                + (Math.cos(lat1/180*Math.PI)*Math.cos(lat2/180*Math.PI)*Math.cos(deltalong/180*Math.PI)));
        if (NWCornerCoordinates.y<coord.getY()) {
            y=-y;
        }
        return new Coordinates(x, y).mCoordsToPCoords();
    }

    /**
     * Determines the meters/pixel ratio, based on a width measure.
     */
    public final void computesUnitePixel () {

        /* Uses the width of the tile in meters. */
        Double NWCornerCoordinates = GoogleTileUtils.getLatLong(nameRef);
        double lat1 = NWCornerCoordinates.getCenterX();
        double lat2 = NWCornerCoordinates.getCenterX();
        double deltalong = NWCornerCoordinates.getHeight();
        double dist = 1852*60*180/Math.PI
                * Math.acos((Math.sin(lat1/180*Math.PI)*Math.sin(lat2/180*Math.PI))
                + (Math.cos(lat1/180*Math.PI)*Math.cos(lat2/180*Math.PI)*Math.cos(deltalong/180*Math.PI)));

        /* Uses the width of the image created in pixels. */
        double distPx = imageRef.getHeight();

        MainCoordinatesManager.setUnitePixel(dist/distPx);
    }


    /**
     * Sets the geographic coordinates of HOME.
     * 
     * @param x Longitude of the HOME point
     * @param y Latitude of the HOME point
     */
    public void setHome(double x, double y) {
        homeGeo.setCoordinates(x, y);
    }


}
