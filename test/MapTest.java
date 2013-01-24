import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Hugo
 */
public class MapTest {
    
    public MapTest() {
    }

    /**
     * Test of addToList method, of class Map.
     */
    @Test
    public void testAddToList() {
        System.out.println("addToList");
        LocalizedPhenomenon l = new Thermal(0.0, 0.0);
        Map instance = new Map();
        instance.addToList(l);
        int newSize = instance.getLocalizedPhenomenonList().size();
        assertEquals(1, newSize);
        assertEquals(l, instance.getLocalizedPhenomenonList().get(0));
    }

    /**
     * Test of deleteFromList method, of class Map.
     */
    @Test
    public void testDeleteFromList() {
        System.out.println("deleteFromList");
        LocalizedPhenomenon l = new Thermal(0.0, 0.0);
        Map instance = new Map();
        instance.addToList(l);
        instance.deleteFromList(l);
        int newSize = instance.getLocalizedPhenomenonList().size();
        assertEquals(0, newSize);
    }

    /**
     * Test of translateLocalizedPhenomenon method, of class Map.
     */
    @Test
    public void testTranslateLocalizedPhenomenon() {
        System.out.println("translateLocalizedPhenomenon");
        LocalizedPhenomenon currentPhenomenon = new Thermal(0.0, 0.0);
        double dx = 5.0;
        double dy = 10.0;
        Map instance = new Map();
        instance.translateLocalizedPhenomenon(currentPhenomenon, dx, dy);
        assertEquals(5.0, currentPhenomenon.getX(), 0.0);
        assertEquals(10.0, currentPhenomenon.getY(), 0.0);
    }

    /**
     * Test of modifyRadius method, of class Map.
     */
    @Test
    public void testSetRadius() {
        System.out.println("setRadius");
        LocalizedPhenomenon currentPhenomenon = new Thermal(0.0, 0.0);
        double r = 7.0;
        Map instance = new Map();
        instance.setRadius(currentPhenomenon, r);
        assertEquals(7.0, currentPhenomenon.getRadius(), 0.0);
    }

    /**
     * Test of setMaximumSpeed method, of class Map.
     */
    @Test
    public void testSetMaximumSpeed() {
        System.out.println("setMaximumSpeed");
        Thermal t = new Thermal(0.0, 0.0);
        double m = 3.0;
        Map instance = new Map();
        instance.setMaximumSpeed(t, m);
        assertEquals(3.0, t.getMaximumSpeed(), 0.0);
    }
}
