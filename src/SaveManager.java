import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


/**
 * Implements method to save and load xml files thanks to jdom.
 *
 * @author Sebastien
 * @version 1.0
 */
public class SaveManager {
    
    private SaveManager () {}
    
    /**
     * Saves in a xml file informations needed to load the current situation
     * later.
     *
     * @param map the used map
     * @param fileName name of the file used to save
     */
    public static void save (Map map, String fileName) {
        
        /* Element used to create a JDom tree */
        Element root;
        Element referencePoint;
        Element turbulence;
        Element verticalProfile;
        Element localizedPhenomenonListElement;
        
        /* Attribute of some elements */
        Attribute longitude; //referencePoint
        Attribute latitude; //referencePoint
        Attribute maximumHeight; //verticaleProfile
        Attribute turbIntensity; //turbulence
        
        
        /* All JDom documents start with a root eelment. */
        root = new Element("Environment");
        org.jdom2.Document jdomDocSave = new Document(root);
        
        /* Builds the JDom file, thanks to the given map. */
        referencePoint = new Element("CoordinatesOrigin");
        root.addContent(referencePoint);
        longitude = new Attribute("lon0",
                Double.toString(MainCoordinatesManager.getMainLandMark().getX()));
        referencePoint.setAttribute(longitude);
        latitude = new Attribute("lat0",
                Double.toString(MainCoordinatesManager.getMainLandMark().getY()));
        referencePoint.setAttribute(latitude);
        
        turbulence = new Element("Turbulences");
        root.addContent(turbulence);
        turbIntensity = new Attribute("Intensity",
                Integer.toString(map.getIntensity()));
        turbulence.setAttribute(turbIntensity);
        
        verticalProfile = new Element("VerticalProfile");
        root.addContent(verticalProfile);
        maximumHeight = new Attribute("MaximumHeight",
                Double.toString(map.getMaximumHeight()));
        verticalProfile.setAttribute(maximumHeight);
        
        localizedPhenomenonListElement = new Element("LocalizedPhenomenonList");
        root.addContent(localizedPhenomenonListElement);
        
        for(double key : map.verticalProfile.getLevelList().keySet()) {
            Element level;
            Attribute levHeight;
            Attribute levDir;
            Attribute levSpeed;
            
            level = new Element("Level");
            verticalProfile.addContent(level);
            levHeight = new Attribute("Height",
                    Double.toString(map.getLevelList().get(key).getHeight()));
            levDir = new Attribute("Direction",
                    Double.toString(map.getLevelList().get(key).getDirection())
                    );
            levSpeed = new Attribute("Speed",
                    Double.toString(map.getLevelList().get(key).getSpeed()));
            level.setAttribute(levHeight);
            level.setAttribute(levDir);
            level.setAttribute(levSpeed);
        }
        
        for(LocalizedPhenomenon localizedPhenomenon 
                : map.localizedPhenomenonList) {
            Element localizedPhonomenonElement;
            Attribute coordinateX;
            Attribute coordinateY;
            Attribute radius;
            Attribute Height;
            Attribute MaximumSpeed;
            
            localizedPhonomenonElement = new Element("LocalizedPhenomenon");
            localizedPhenomenonListElement.addContent(
                    localizedPhonomenonElement);
            coordinateX = new Attribute("EastLocalPosition",
                    Double.toString(localizedPhenomenon.getEast()));
            coordinateY = new Attribute("NorthLocalPosition",
                    Double.toString(localizedPhenomenon.getNorth()));
            radius = new Attribute("Radius",
                    Double.toString(localizedPhenomenon.getMRadius()));
            localizedPhonomenonElement.setAttribute(coordinateX);
            localizedPhonomenonElement.setAttribute(coordinateY);
            localizedPhonomenonElement.setAttribute(radius);
            if(localizedPhenomenon instanceof Thermal) {
                localizedPhonomenonElement.setName("Thermal");
                Height = new Attribute("Height", 
                        Double.toString((
                        (Thermal)localizedPhenomenon).getHeight()));
                MaximumSpeed = new Attribute("MaximumSpeed", 
                        Double.toString((
                        (Thermal)localizedPhenomenon).getMaximumSpeed()));
                localizedPhonomenonElement.setAttribute(Height);
                localizedPhonomenonElement.setAttribute(MaximumSpeed);
            }
        }
        
        /* Saves the xml file. */
        writeFile(fileName, jdomDocSave);
    }
    
    /**
     * Load a (compliant) xml file.
     * @param map map to add the elements saved
     * @param filePath path of the file to load
     */
    public static void load(Map map, String filePath) {

        Document document;
        SAXBuilder sxb;
        Element root;
        Element referencePoint;
        Element turbulences;
        Element verticaleProfile;
        Element localizedPhenomena;
        
        /* Main coordinates to load */
        double lon0;
        double lat0;
        
        int turbIntensity;
        
        /* Variables to load the verticale profile */
        double maximumHeight;
        List<Element> levelsList;
        Level upperLevel; 
        Level lowerLevel;
        
        List<Element> phenomenonList;
        
        root = null;
        document = new Document(root);
        
        /* Creation of a SAXBuilder instance, which will parse the XML file. */
        sxb = new SAXBuilder();
        try
        {
            /* Reconstruction of a JDOM structure thanks to the saxbuilder. */
            document = sxb.build(new File(filePath));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        
        /* Initialisation of the root element : the parsing of the JDOM
         * structure can begin. */
        root = document.getRootElement();
        
        /**
         * Gets the Geographic reference, sets it and sets also the ration
         * pixel/meters.
         */
        referencePoint = root.getChild("CoordinatesOrigin");
        lon0 = Double.valueOf(referencePoint.getAttributeValue("lon0"));
        lat0 = Double.valueOf(referencePoint.getAttributeValue("lat0"));
        
        /* Initializes the main coordinates */
        MainCoordinatesManager.setMainLandMark(lon0, lat0);
        
        /* Reads the turbulence intensity and sets the value to the map. */
        turbulences = root.getChild("Turbulences");
        turbIntensity = Integer.parseInt(
                turbulences.getAttributeValue("Intensity"));
        map.setIntensity(turbIntensity);
        
        /* Same for the verticale profile. */
        verticaleProfile = root.getChild("VerticalProfile");
        maximumHeight = Double.valueOf(
                verticaleProfile.getAttributeValue("MaximumHeight"));
        map.setMaximumHeight(maximumHeight);
        levelsList = verticaleProfile.getChildren("Level");
        upperLevel = null;
        lowerLevel = null;
        for(Level temp : map.getLevelList().values()) {
            if(map.getHeight(temp) == VerticalProfile.DEFAULT_MAXIMUM_HEIGHT) {
                upperLevel = temp;
            }
            else if(map.getHeight(temp) 
                    == VerticalProfile.DEFAULT_MINIMUM_HEIGHT) {
                lowerLevel = temp;
            }
        }
        for(Element level : levelsList) {
            double height;
            double speed;
            double direction;
            
            if(Double.valueOf(level.getAttributeValue("Height")) 
                    != map.getMaximumHeight() 
               && Double.valueOf(level.getAttributeValue("Height")) 
                    != VerticalProfile.DEFAULT_MINIMUM_HEIGHT) {
                height = Double.valueOf(level.getAttributeValue("Height"));
                speed = Double.valueOf(level.getAttributeValue("Speed"));
                direction = 
                        Double.valueOf(level.getAttributeValue("Direction"));
                map.createLevel(height, speed, direction);
            }
            else if(Double.valueOf(level.getAttributeValue("Height")) 
                    == map.getMaximumHeight()) {
                speed = Double.valueOf(level.getAttributeValue("Speed"));
                direction = 
                        Double.valueOf(level.getAttributeValue("Direction"));
                map.setSpeed(upperLevel, speed);
                map.setDirection(upperLevel, direction);
                map.setHeight(upperLevel, map.getMaximumHeight());
            }
            else if(Double.valueOf(level.getAttributeValue("Height")) 
                    == VerticalProfile.DEFAULT_MINIMUM_HEIGHT) {
                speed = Double.valueOf(level.getAttributeValue("Speed"));
                direction = 
                        Double.valueOf(level.getAttributeValue("Direction"));
                map.setSpeed(lowerLevel, speed);
                map.setDirection(lowerLevel, direction);
            }
        }
        
        /* And finally same for the Localized Phenomena. */
        localizedPhenomena = root.getChild("LocalizedPhenomenonList");
        phenomenonList = localizedPhenomena.getChildren("Thermal");
        for (Element thermal : phenomenonList) {
            Coordinates origin;
            double coordinateX;
            double coordinateY;
            double radius;
            double height;
            double speed;
            Thermal t;
            
            origin = new Coordinates(MapControler.getOrigin().getX(),
                    MapControler.getOrigin().getY());
            coordinateX = 
                    MainCoordinatesManager.meterToPixel(
                    Double.valueOf(
                        thermal.getAttributeValue("EastLocalPosition")))
                    +origin.getX();
            coordinateY = 
                    MainCoordinatesManager.meterToPixel(
                    -Double.valueOf( //- because of the inverted axis for pixels
                        thermal.getAttributeValue("NorthLocalPosition")))
                    +origin.getY();
            radius = Double.valueOf(thermal.getAttributeValue("Radius"));
            height = Double.valueOf(thermal.getAttributeValue("Height"));
            speed = Double.valueOf(thermal.getAttributeValue("MaximumSpeed"));
            t = new Thermal(coordinateX, coordinateY);
            t.setRadius(MainCoordinatesManager.meterToPixel(radius));
            t.setMaximumSpeed(speed);
            map.addToList(t);
        }
        
    }
    
    /**
     * Displays a JDom Document.
     * @param document to be displayed
     */
    static void affiche(org.jdom2.Document document)
    {
        try
        {
            XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
            sortie.output(document, System.out);
        }
        catch (java.io.IOException e){}
    }
    
    /**
     * Writes the xml file.
     * @param fichier name of the file to save
     * @param document JDom document to convert into xml
     */
    private static void writeFile(String fichier, org.jdom2.Document document)
    {
        try
        {
            XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
            sortie.output(document, new FileOutputStream(fichier));
        }
        catch (java.io.IOException e){}
    }
    

}
