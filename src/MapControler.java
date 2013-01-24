import fr.lri.swingstates.canvas.*;
import fr.lri.swingstates.canvas.Canvas;
import fr.lri.swingstates.canvas.transitions.LeaveOnShape;
import fr.lri.swingstates.canvas.transitions.PressOnShape;
import fr.lri.swingstates.debug.StateMachineVisualization;
import fr.lri.swingstates.sm.State;
import fr.lri.swingstates.sm.Transition;
import fr.lri.swingstates.sm.transitions.Drag;
import fr.lri.swingstates.sm.transitions.Move;
import fr.lri.swingstates.sm.transitions.Press;
import fr.lri.swingstates.sm.transitions.Release;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

/** 
 * The MVC Controler for the map section. It uses the Swingstates library.
 *
 * @author Hugo
 * @version 1.2
 */
public class MapControler {

    private Canvas canvasMap;
    private CardLayout cardL;
    private Map map;
    private JPanel mapPanel;
    private CText mapCoordinates;

    private Canvas canvasTherm;
    private JPanel thermPanel;
    private JPanel thermViewPanel;
    private JSlider thermMaxSpeedSlider;
    private JLabel thermMaxSpeedLabel;
    private JLabel thermCoordinatesLabel;
    private JLabel thermCoordinatesGeoLabel;
    private JToggleButton toggleThermal;
    private Thermal currentThermal;
    private CShape currentThermalShape;
    private Color currentThermalColor = Color.WHITE;

    private java.util.Map<Integer, List<CShape>> uavList = new HashMap();

    private final double xLength;
    private final double yLength;

    static final double PI = Math.PI;

    private static Point2D originP = new Point2D.Double(0.0, 0.0);

    private static final Color[] colorTab = {Color.WHITE, Color.RED,
        Color.ORANGE, Color.CYAN, Color.BLUE, Color.GREEN, Color.MAGENTA,
        Color.YELLOW};
    private static int colorCursor = 0;

    private java.util.Map<CShape, LocalizedPhenomenon> associationListPhenomenon
            = new HashMap();
    private java.util.Map<CShape, List<CShape>> associationListShapeGroup
            = new HashMap();
    private java.util.Map<CShape, List<CShape>> associationListShapeGroupView
            = new HashMap();
    private java.util.List<CShape> listHandle = new ArrayList();
    private java.util.List<CShape> listLine = new ArrayList();

    private java.util.List<CShape> listGoogle = new ArrayList();
    
    private String homePath = null;
    
    /**
     * 
     * @param newpath
     */
    public void setHomePath(String newpath) {
        homePath = newpath;
    }

    /**
     * 
     * @return
     */
    public String getHomePath() {
        return homePath;
    }
    
    /** 
     * Calculates the x value in pixels from the radius value.
     *
     * @param axisLength the length of the x axis
     * @param radius the radius value
     * @return the calculated x value in pixels
     */
    public static double calculateX(double axisLength, double radius) {
        return axisLength*radius/Thermal.MAXIMUM_MRADIUS;
    }

    /** 
     * Calculates the y value in pixels from the height value.
     *
     * @param axisLength the length of the y value
     * @param height the height value
     * @param maximumHeight the maximum height value
     * @return the calculated y value in pixels
     */
    public static double calculateY(double axisLength, double height,
            double maximumHeight) {
        return axisLength*(maximumHeight-height)/maximumHeight;
    }

    /** 
     * This method represents a controler for the map section. These are the
     * events that are treated :
     * - a left click while the thermal toggle button is selected will create
     * a view of a thermal,
     * - a drag and drop on a thermal view will move the thermal,
     * - a drag and drop on a thermal handle will resize the thermal,
     * - a drag and drop over a free space will move all the created shape,
     * - a right click on a thermal will erase it,
     * - a left click on a thermal will open its properties editor and select it
     * as the current thermal.
     *
     * @param mp the map panel
     * @param tp the thermal properties editor panel
     * @param therm the thermal toggle button
     * @param tvp the thermal properties editor panel part for the thermal view
     * @param slider the thermal maximum speed slider
     * @param m the model
     * @param labelM the thermal maximum speed label
     * @param labelG 
     * @param labelC the thermal coordinates label
     */
    public MapControler(JPanel mp, JPanel tp, JPanel tvp,
            JToggleButton therm, JSlider slider, JLabel labelM,
            JLabel labelC, JLabel labelG, Map m)  {

        mapPanel = mp;
        map = m;
        canvasMap = new Canvas(mapPanel.getWidth(), mapPanel.getHeight());
        canvasMap.setBackground(Color.BLACK);

        thermPanel = tp;
        thermViewPanel = tvp;
        thermMaxSpeedSlider = slider;
        thermMaxSpeedLabel = labelM;
        thermCoordinatesLabel = labelC;
        thermCoordinatesGeoLabel = labelG;
        toggleThermal = therm;
        cardL = (CardLayout)thermPanel.getLayout();
        canvasTherm = new Canvas(thermViewPanel.getWidth(),
                thermViewPanel.getHeight());


        CPolyLine yAxis = new CPolyLine(55.0, thermViewPanel.getHeight()-30.0);
        yAxis.lineTo(55.0, 5.0);
        CPolyLine yArrow = yAxis.getArrow(PI/6, 10);
        yArrow.setTransparencyFill(0);
        canvasTherm.addShape(yAxis);
        canvasTherm.addShape(yArrow);
        yAxis.moveTo(55.0, thermViewPanel.getHeight()-30.0);
        CPolyLine xAxis = new CPolyLine(55.0, thermViewPanel.getHeight()-30.0);
        xAxis.lineTo(thermViewPanel.getWidth()-50.0, thermViewPanel.
                getHeight()-30.0);
        CPolyLine xArrow = xAxis.getArrow(PI/6, 10);
        xArrow.setTransparencyFill(0);
        canvasTherm.addShape(xAxis);
        canvasTherm.addShape(xArrow);

        CText yLegend = new CText(new Point2D.Double(0.0, -2.0), "Height m",
                new Font("verdana", Font.PLAIN, 12));

        CText xLegend = new CText(new Point2D.Double(thermViewPanel.
                getWidth()-60.0,
                thermViewPanel.getHeight()-50.0), "Radius m",
                new Font("verdana", Font.PLAIN, 12));

        final double xAxisLength = thermViewPanel.getWidth()-115.0;
        final double yAxisLength = thermViewPanel.getHeight()-90.0;

        xLength = xAxisLength;
        yLength = yAxisLength;

        canvasTherm.addShape(xLegend);
        canvasTherm.addShape(yLegend);

        originP = new Point2D.Double(0.0, 0.0);

        CEllipse origin = new CEllipse(originP, 10.0, 10.0);
        origin.translateBy(-origin.getWidth()/2, -origin.getHeight()/2);
        origin.setFillPaint(Color.RED);
        CText originName = new CText(new Point2D.Double(origin.getCenterX()-20, 
                origin.getCenterY()-20), "Origin", 
                new Font("verdana", Font.PLAIN, 12));
        originName.setFillPaint(Color.RED);
        canvasMap.addShape(origin);
        canvasMap.addShape(originName);

        mapCoordinates = new CText(new Point2D.Double(0.0, 0.0), "(0.0, 0.0)",
                new Font("verdana", Font.PLAIN, 12));
        mapCoordinates.setFillPaint(Color.WHITE);

        canvasMap.addShape(mapCoordinates);

        CStateMachine sm = new CStateMachine(canvasMap) {


            CElement toAct = null;
            Point2D lastPoint = null;
            CEllipse hystShape = null;
            LocalizedPhenomenon newElement = null;
            LocalizedPhenomenon toActPhenomenon = null;
            CShape toActShape = null;
            CPolyLine toActLine = null;
            CPolyLine toActLineView = null;
            CText toActText = null;
            CText toActTextAltView = null;
            CText toActTextRadView = null;
            CPolyLine toActCurveView = null;
            CPolyLine toActExternalCurveView = null;

            /** This state represents the idle state. */
            public State start = new State() {

                /** A left click on the map while the thermal toggle button is
                 * selected will automatically create a thermal with both
                 * coordinates X and Y. It will lead to the clickedThermal
                 * state. */
                Transition createThermal = new Press(
                        BUTTON1, ">> clickedThermal") {

                    @Override
                    public void action() {
                        lastPoint = getPoint();
                        hystShape = new CEllipse(lastPoint.getX()-5,
                                lastPoint.getY()-5, 10, 10);
                        hystShape.setTransparencyFill(0);
                        hystShape.setTransparencyOutline(0);
                        canvasMap.addShape(hystShape);
                    }

                };

                /*
                 * This state is the same as above, but it is used when
                 * a gogle element is clicked.
                 */
                Transition createThermal2 = new PressOnShape(
                        BUTTON1, ">> clickedThermal") {

                    @Override
                    public boolean guard() {
                        return listGoogle.contains(getShape());
                    }

                    @Override
                    public void action() {
                        lastPoint = getPoint();
                        hystShape = new CEllipse(lastPoint.getX()-5,
                                lastPoint.getY()-5, 10, 10);
                        hystShape.setTransparencyFill(0);
                        hystShape.setTransparencyOutline(0);
                        canvasMap.addShape(hystShape);
                    }

                };


                /** A right click on a localized phenomenon will erase it. */
                Transition remove = new PressOnShape(BUTTON3) {

                    @Override
                    public boolean guard() {
                        return associationListPhenomenon.
                                containsKey(getShape()) ||
                                getShape() instanceof CPolyLine;
                    }

                    @Override
                    public void action() {
                        if(getShape() instanceof CPolyLine) {
                            for(CShape shape : associationListShapeGroup.
                                    keySet()) {
                                if(associationListShapeGroup.get(shape).
                                        contains(getShape())) {
                                    toAct = shape;
                                }
                            }
                        }
                        else {
                            toAct = getShape();
                        }
                        map.deleteLocalizedPhenomenon(
                                associationListPhenomenon.get(
                                (CShape)toAct));
                        associationListPhenomenon.remove((CShape)toAct);
                        canvasMap.removeShape((CShape)toAct);
                        for(CShape element :
                                associationListShapeGroup.get((CShape)toAct)) {
                            canvasMap.removeShape(element);
                            if(listHandle.contains(element)) {
                                listHandle.remove(element);
                            }
                        }
                        for(CShape element :
                                associationListShapeGroupView.get(
                                (CShape)toAct)) {
                            canvasTherm.removeShape(element);
                        }
                        associationListShapeGroup.remove((CShape)toAct);
                        associationListShapeGroupView.remove((CShape)toAct);

                        boolean doExist = false;
                        for(CShape shape : associationListShapeGroup.keySet()) {
                            if(associationListPhenomenon.get(shape) instanceof
                                    Thermal) {
                                currentThermal = (Thermal)
                                        associationListPhenomenon.get(shape);
                                currentThermalShape = shape;
                                doExist = true;
                            }
                        }
                        if(doExist) {
                            thermMaxSpeedSlider.setValue((int)map.
                                    getMaximumSpeed(currentThermal));
                            thermMaxSpeedLabel.setText(
                                    "Maximum Vertical Speed : " +
                                    (int)map.getMaximumSpeed(currentThermal) +
                                    " cm/s");

                            String longEast = Double.toString(map.
                                    getEast(currentThermal));
                            String longNorth = Double.toString(map.
                                    getNorth(currentThermal));
                            String printedEast = longEast;
                            String printedNorth = longNorth;
                            int indexEast = longEast.indexOf(".");
                            int indexNorth = longNorth.indexOf(".");
                            try {
                                if (indexEast != -1) {
                                    printedEast = longEast.substring(0,
                                            indexEast+6);
                                }
                                if (indexNorth != -1) {
                                    printedNorth = longNorth.substring(0,
                                            indexNorth+6);
                                }
                            } catch(StringIndexOutOfBoundsException e) {
                                if (indexEast != -1) {
                                    printedEast = longEast.substring(0,
                                            indexEast+2);
                                }
                                if (indexNorth != -1) {
                                    printedNorth = longNorth.substring(0,
                                            indexNorth+2);
                                }

                            }
                            thermCoordinatesLabel.setText(
                                    "(" +
                                    printedEast + ", " + printedNorth + ")");

                            double x = map.getEast(currentThermal);
                            double y = map.getNorth(currentThermal);
                            Coordinates newGeoCoord = 
                                    MainCoordinatesManager.
                                    mCoordToGeoCoord(x, y);
                            String longLong = 
                                    Double.toString(newGeoCoord.getX());
                            String longLat = 
                                    Double.toString(newGeoCoord.getY());
                            String printedLong = longLong;
                            String printedLat = longLat;
                            int indexLong = longLong.indexOf(".");
                            int indexLat = longLat.indexOf(".");
                            try {
                                if (indexLong != -1) {
                                    printedLong = longLong.substring(0,
                                            indexLong+7);
                                }
                                if (indexLat != -1) {
                                    printedLat = longLat.substring(
                                            0, indexLat+7);
                                }
                            } catch(StringIndexOutOfBoundsException e) {
                                if (indexLong != -1) {
                                    printedLong = longLong.substring(0,
                                            indexLong+2);
                                }
                                if (indexLat != -1) {
                                    printedLat = longLat.substring(
                                            0, indexLat+2);
                                }
                            }
                            thermCoordinatesGeoLabel.setText(
                                    "(" + printedLong + ", " + printedLat +")");

                            associationListShapeGroup.get(currentThermalShape).
                                    get(associationListShapeGroup.
                                    get(currentThermalShape).size()-1).
                                    translateTo(currentThermalShape.
                                    getCenterX(),
                                    currentThermalShape.getCenterY());
                            canvasMap.addShape(associationListShapeGroup.
                                    get(currentThermalShape).
                                    get(associationListShapeGroup.
                                    get(currentThermalShape).size()-1));
                            for(CShape shape : associationListShapeGroupView.
                                    get(currentThermalShape)) {
                                canvasTherm.addShape(shape);
                            }
                        }
                        else {
                            thermMaxSpeedSlider.setValue(5);
                            thermMaxSpeedLabel.setText(
                                    "Maximum Vertical Speed : " + 5 + " cm/s");
                            thermCoordinatesLabel.setText(
                                    "(" +
                                    0 + ", " +
                                    0 + ")");
                            cardL.show(thermPanel, "cardThermalColumnEmpty");
                        }
                    }

                };

                /** A left click on a localized phenomenon or on a handle will
                 * lead to the hysteresis state. */
                Transition press = new PressOnShape(BUTTON1, ">> waitHyst") {

                    @Override
                    public boolean guard() {
                        boolean result = false;
                        for(CShape shape : associationListShapeGroup.keySet()) {
                            for(CShape form : 
                                    associationListShapeGroup.get(shape)) {
                                if(form.equals(getShape())) {
                                    result = true;
                                }
                            }
                        }
                        return associationListPhenomenon.
                                containsKey(getShape()) ||
                                result;
                    }

                    @Override
                    public void action() {
                        toAct = getShape();
                        try {
                            canvasMap.removeShape(associationListShapeGroup.
                                    get(currentThermalShape).get(
                                    associationListShapeGroup.
                                    get(currentThermalShape).size()-1));
                            for(CShape shape : associationListShapeGroupView.
                                    get(currentThermalShape)) {
                                canvasTherm.removeShape(shape);
                            }
                            if(associationListPhenomenon.containsKey(
                                    (CShape)toAct) &&
                                    associationListPhenomenon.get(
                                    (CShape)toAct) instanceof Thermal) {
                                currentThermal = (Thermal)
                                        associationListPhenomenon.
                                        get((CShape)toAct);
                                currentThermalShape = (CShape)toAct;
                                associationListShapeGroup.get(
                                        currentThermalShape).
                                        get(associationListShapeGroup.
                                        get(currentThermalShape).size()-1).
                                        translateTo(currentThermalShape.
                                        getCenterX(), currentThermalShape.
                                        getCenterY());
                                canvasMap.addShape(associationListShapeGroup.
                                        get(currentThermalShape).get(
                                        associationListShapeGroup.
                                        get(currentThermalShape).size()-1));
                                for(CShape shape :
                                        associationListShapeGroupView.
                                        get(currentThermalShape)) {
                                    canvasTherm.addShape(shape);
                                }
                                thermMaxSpeedSlider.setValue(
                                        (int)map.getMaximumSpeed(
                                        currentThermal));
                                thermMaxSpeedLabel.setText(
                                        "Maximum Vertical Speed : " +
                                        (int)map.getMaximumSpeed(
                                        currentThermal) + " cm/s");

                                String longEast = Double.toString(map.
                                        getEast(currentThermal));
                                String longNorth = Double.toString(map.
                                        getNorth(currentThermal));
                                String printedEast = longEast;
                                String printedNorth = longNorth;
                                int indexEast = longEast.indexOf(".");
                                int indexNorth = longNorth.indexOf(".");
                                try {
                                    if (indexEast != -1) {
                                        printedEast = longEast.substring(0,
                                                indexEast+6);
                                    }
                                    if (indexNorth != -1) {
                                        printedNorth = longNorth.substring(0,
                                                indexNorth+6);
                                    }
                                } catch(StringIndexOutOfBoundsException e) {
                                    if (indexEast != -1) {
                                        printedEast = longEast.substring(0,
                                                indexEast+2);
                                    }
                                    if (indexNorth != -1) {
                                        printedNorth = longNorth.substring(0,
                                                indexNorth+2);
                                    }

                                }
                                thermCoordinatesLabel.setText(
                                        "(" +
                                        printedEast + ", " + printedNorth +
                                        ")");

                                double x = map.getEast(currentThermal);
                                double y = map.getNorth(currentThermal);
                                Coordinates newGeoCoord = 
                                        MainCoordinatesManager.
                                        mCoordToGeoCoord(x, y);
                                String longLong = 
                                        Double.toString(newGeoCoord.getX());
                                String longLat = 
                                        Double.toString(newGeoCoord.getY());
                                String printedLong = longLong;
                                String printedLat = longLat;
                                int indexLong = longLong.indexOf(".");
                                int indexLat = longLat.indexOf(".");
                                try {
                                    if (indexLong != -1) {
                                        printedLong = longLong.substring(0,
                                                indexLong+7);
                                    }
                                    if (indexLat != -1) {
                                        printedLat = longLat.substring(
                                                0, indexLat+7);
                                    }
                                } catch(StringIndexOutOfBoundsException e) {
                                    if (indexLong != -1) {
                                        printedLong = longLong.substring(0,
                                                indexLong+2);
                                    }
                                    if (indexLat != -1) {
                                        printedLat = longLat.substring(
                                                0, indexLat+2);
                                    }
                                }
                                thermCoordinatesGeoLabel.setText(
                                        "(" + printedLong + ", " + 
                                        printedLat +")");

                                toActCurveView = (CPolyLine)
                                        associationListShapeGroupView.
                                        get(currentThermalShape).
                                        get(associationListShapeGroupView.
                                        get(currentThermalShape).size()-1);
                                toActExternalCurveView = (CPolyLine)
                                        associationListShapeGroupView.
                                        get(currentThermalShape).
                                        get(associationListShapeGroupView.
                                        get(currentThermalShape).size()-5);
                            }
                            else if(toAct instanceof CPolyLine || toAct
                                    instanceof CRectangle || (toAct instanceof
                                    CEllipse && !associationListPhenomenon.
                                    containsKey((CShape)toAct))) {
                                CShape temp = null;
                                for(CShape shape : associationListShapeGroup.
                                        keySet()) {
                                    if(associationListShapeGroup.get(shape).
                                            contains(getShape())) {
                                        temp = shape;
                                    }
                                }
                                if(associationListPhenomenon.get((CShape)temp)
                                        instanceof Thermal) {
                                    currentThermal = (Thermal)
                                            associationListPhenomenon.
                                            get((CShape)temp);
                                    currentThermalShape = (CShape)temp;
                                    associationListShapeGroup.get(
                                            currentThermalShape).
                                            get(associationListShapeGroup.
                                            get(currentThermalShape).size()-1).
                                            translateTo(currentThermalShape.
                                            getCenterX(), currentThermalShape.
                                            getCenterY());
                                    canvasMap.addShape(
                                            associationListShapeGroup.
                                            get(currentThermalShape).get(
                                            associationListShapeGroup.
                                            get(currentThermalShape).size()-1));
                                    for(CShape shape :
                                            associationListShapeGroupView.
                                            get(currentThermalShape)) {
                                        canvasTherm.addShape(shape);
                                    }
                                    thermMaxSpeedSlider.setValue(
                                            (int)map.getMaximumSpeed(
                                            currentThermal));
                                    thermMaxSpeedLabel.setText(
                                            "Maximum Vertical Speed : " +
                                            (int)map.getMaximumSpeed(
                                            currentThermal) + " cm/s");

                                    String longEast = Double.toString(map.
                                            getEast(currentThermal));
                                    String longNorth = Double.toString(map.
                                            getNorth(currentThermal));
                                    String printedEast = longEast;
                                    String printedNorth = longNorth;
                                    int indexEast = longEast.indexOf(".");
                                    int indexNorth = longNorth.indexOf(".");
                                    try {
                                        if (indexEast != -1) {
                                            printedEast = longEast.substring(0,
                                                    indexEast+6);
                                        }
                                        if (indexNorth != -1) {
                                            printedNorth = longNorth.substring(
                                                    0, indexNorth+6);
                                        }
                                    } catch(StringIndexOutOfBoundsException e) {
                                        if (indexEast != -1) {
                                            printedEast = longEast.substring(0,
                                                    indexEast+2);
                                        }
                                        if (indexNorth != -1) {
                                            printedNorth = longNorth.substring(
                                                    0, indexNorth+2);
                                        }
                                    }
                                    thermCoordinatesLabel.setText(
                                            "(" +
                                            printedEast + ", " + printedNorth +
                                            ")");

                                    double x = map.getEast(currentThermal);
                                    double y = map.getNorth(currentThermal);
                                    Coordinates newGeoCoord = 
                                            MainCoordinatesManager.
                                            mCoordToGeoCoord(x, y);
                                    String longLong = 
                                            Double.toString(newGeoCoord.getX());
                                    String longLat = 
                                            Double.toString(newGeoCoord.getY());
                                    String printedLong = longLong;
                                    String printedLat = longLat;
                                    int indexLong = longLong.indexOf(".");
                                    int indexLat = longLat.indexOf(".");
                                    try {
                                        if (indexLong != -1) {
                                            printedLong = longLong.substring(0,
                                                    indexLong+7);
                                        }
                                        if (indexLat != -1) {
                                            printedLat = longLat.substring(
                                                    0, indexLat+7);
                                        }
                                    } catch(StringIndexOutOfBoundsException e) {
                                        if (indexLong != -1) {
                                            printedLong = longLong.substring(0,
                                                    indexLong+2);
                                        }
                                        if (indexLat != -1) {
                                            printedLat = longLat.substring(
                                                    0, indexLat+2);
                                        }
                                    }
                                    thermCoordinatesGeoLabel.setText(
                                            "(" + printedLong + ", " + 
                                            printedLat +")");

                                    toActCurveView = (CPolyLine)
                                            associationListShapeGroupView.
                                            get(currentThermalShape).
                                            get(associationListShapeGroupView.
                                            get(currentThermalShape).size()-1);
                                    toActExternalCurveView = (CPolyLine)
                                            associationListShapeGroupView.
                                            get(currentThermalShape).
                                            get(associationListShapeGroupView.
                                            get(currentThermalShape).size()-5);
                                }
                            }
                        } catch(NullPointerException e) {
                        }
                        lastPoint = getPoint();
                        hystShape = new CEllipse(lastPoint.getX()-5,
                                lastPoint.getY()-5, 10, 10);
                        hystShape.setTransparencyFill(0);
                        hystShape.setTransparencyOutline(0);
                        canvasMap.addShape(hystShape);
                    }

                };

                Transition mouseMove = new Move(">> start") {

                    @Override
                    public void action() {
                        double x = getPoint().getX() - getOrigin().getX();
                        double y = getPoint().getY() - getOrigin().getY();
                        Coordinates newGeoCoord = 
                                MainCoordinatesManager.mCoordToGeoCoord(x, -y);

                        String longLong = Double.toString(newGeoCoord.getX());
                        String longLat = Double.toString(newGeoCoord.getY());
                        String printedLong = longLong;
                        String printedLat = longLat;
                        int indexLong = longLong.indexOf(".");
                        int indexLat = longLat.indexOf(".");
                        try {
                            if (indexLong != -1) {
                                printedLong = longLong.substring(0,
                                        indexLong + 7);
                            }
                            if (indexLat != -1) {
                                printedLat = longLat.substring(
                                        0, indexLat+7);
                            }
                        } catch(StringIndexOutOfBoundsException e) {
                            if (indexLong != -1) {
                                printedLong = longLong.substring(0,
                                        indexLong+2);
                            }
                            if (indexLat != -1) {
                                printedLat = longLat.substring(
                                        0, indexLat+2);
                            }
                        }
                        mapCoordinates.setText(
                                "(" + printedLong + ", " + printedLat +")");
                    }

                };

            };

            /** This state is in charge of the creation and the move of a
             * thermal view. */
            public State clickedThermal = new State() {

                /** The thermal view is created. */
                Transition release = new Release(BUTTON1, ">> start") {

                    @Override
                    public boolean guard() {
                        return toggleThermal.isSelected();
                    }

                    @Override
                    public void action() {
                        cardL.show(thermPanel, "cardThermalColumnProperties");
                        lastPoint = getPoint();
                        newElement = map.createThermal(lastPoint.getX(),
                                lastPoint.getY());
                        boolean doExist = false;
                        for(CShape shape : associationListPhenomenon.keySet()) {
                            if(associationListPhenomenon.get(shape)
                                    instanceof Thermal) {
                                doExist = true;
                            }
                        }
                        if(doExist) {
                            canvasMap.removeShape(associationListShapeGroup.
                                    get(currentThermalShape).get(
                                    associationListShapeGroup.
                                    get(currentThermalShape).size()-1));
                            for(CShape shape : associationListShapeGroupView.
                                    get(currentThermalShape)) {
                                canvasTherm.removeShape(shape);
                            }
                        }
                        currentThermal = (Thermal)newElement;
                        thermMaxSpeedSlider.setValue(
                                (int)Thermal.DEFAULT_MAXIMUM_SPEED);
                        thermMaxSpeedLabel.setText("Maximum Vertical Speed : " +
                                (int)map.getMaximumSpeed(currentThermal) +
                                " cm/s");

                        String longEast = Double.toString(map.
                                getEast(currentThermal));
                        String longNorth = Double.toString(map.
                                getNorth(currentThermal));
                        String printedEast = longEast;
                        String printedNorth = longNorth;
                        int indexEast = longEast.indexOf(".");
                        int indexNorth = longNorth.indexOf(".");
                        try {
                            if (indexEast != -1) {
                                printedEast = longEast.substring(0,
                                        indexEast+6);
                            }
                            if (indexNorth != -1) {
                                printedNorth = longNorth.substring(0,
                                        indexNorth+6);
                            }
                        } catch(StringIndexOutOfBoundsException e) {
                            if (indexEast != -1) {
                                printedEast = longEast.substring(0,
                                        indexEast+2);
                            }
                            if (indexNorth != -1) {
                                printedNorth = longNorth.substring(0,
                                        indexNorth+2);
                            }

                        }
                        thermCoordinatesLabel.setText(
                                "(" +
                                printedEast + ", " + printedNorth + ")");

                        double x = map.getEast(currentThermal);
                        double y = map.getNorth(currentThermal);
                        Coordinates newGeoCoord = 
                                MainCoordinatesManager.mCoordToGeoCoord(x, y);
                        String longLong = Double.toString(newGeoCoord.getX());
                        String longLat = Double.toString(newGeoCoord.getY());
                        String printedLong = longLong;
                        String printedLat = longLat;
                        int indexLong = longLong.indexOf(".");
                        int indexLat = longLat.indexOf(".");
                        try {
                            if (indexLong != -1) {
                                printedLong = longLong.substring(0,
                                        indexLong+7);
                            }
                            if (indexLat != -1) {
                                printedLat = longLat.substring(
                                        0, indexLat+7);
                            }
                        } catch(StringIndexOutOfBoundsException e) {
                            if (indexLong != -1) {
                                printedLong = longLong.substring(0,
                                        indexLong+2);
                            }
                            if (indexLat != -1) {
                                printedLat = longLat.substring(
                                        0, indexLat+2);
                            }
                        }
                        thermCoordinatesGeoLabel.setText(
                                "(" + printedLong + ", " + printedLat +")");

                        double radius = newElement.getRadius();
                        CEllipse therm = new CEllipse(
                                lastPoint.getX()-radius,
                                lastPoint.getY()-radius,
                                radius*2, radius*2);
                        therm.setOutlinePaint(currentThermalColor);
                        therm.setTransparencyFill(0);
                        currentThermalShape = therm;

                        CEllipse center = new CEllipse(
                                lastPoint.getX()-2.5,
                                lastPoint.getY()-2.5,
                                2.5*2, 2.5*2);
                        center.setTransparencyFill(0);
                        center.setOutlinePaint(Color.RED);

                        CRectangle rect = new CRectangle(
                                lastPoint.getX()-radius-2.5,
                                lastPoint.getY()-2.5,
                                5, 5);
                        rect.setFillPaint(Color.WHITE);
                        rect.setOutlinePaint(Color.RED);

                        CPolyLine line = new CPolyLine(
                                lastPoint.getX(),
                                lastPoint.getY());
                        line.lineTo(lastPoint.getX()-radius+2.5,
                                lastPoint.getY());
                        line.setOutlinePaint(currentThermalColor);

                        String longRad = Double.toString(map.
                                getMRadius(newElement));
                        String printedRad = longRad;
                        int indexRad = longRad.indexOf(".");
                        if (indexRad != -1) {
                            printedRad = longRad.substring(0, indexRad+2);
                        }
                        CText label = new CText(new Point2D.Double(
                                therm.getCenterX()-((3/2)*radius),
                                therm.getCenterY()),
                                printedRad,
                                new Font("verdana", Font.PLAIN, 12));
                        label.setFillPaint(currentThermalColor);

                        rect.above(label);

                        CPolyLine lineAlt = new CPolyLine(55.0,
                                calculateY(yAxisLength, map.
                                getHeight((Thermal)newElement), Thermal.
                                MAXIMUM_HEIGHT)+30.0+7.0);
                        lineAlt.lineTo(55.0+xAxisLength,
                                calculateY(yAxisLength, map.
                                getHeight((Thermal)newElement), Thermal.
                                MAXIMUM_HEIGHT)+30.0+7.0);

                        String longAlt = Double.toString(map.
                                getHeight((Thermal)newElement));
                        String printedAlt = longAlt;
                        int indexAlt = longAlt.indexOf(".");
                        if (indexAlt != -1) {
                            printedAlt = longAlt.substring(0, indexAlt+2);
                        }
                        CText labelAlt = new CText(new Point2D.Double(5.0,
                                calculateY(yAxisLength, map.
                                getHeight((Thermal)newElement), Thermal.
                                MAXIMUM_HEIGHT)+30.0),
                                printedAlt,
                                new Font("verdana", Font.PLAIN, 12));

                        CPolyLine curve = new CPolyLine(55.0, thermViewPanel.
                                getHeight()-30.0);
                        curve.curveTo(
                                calculateX(xAxisLength, map.
                                getMRadius((Thermal)newElement)*
                                (1.0/2.0)/Math.sqrt(2)+55.0),
                                calculateY(yAxisLength, map.
                                getHeight((Thermal)newElement)*
                                1.0/100.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                                calculateX(xAxisLength, map.
                                getMRadius((Thermal)newElement)*
                                (3.0/4.0)/Math.sqrt(2)+55.0),
                                calculateY(yAxisLength, map.
                                getHeight((Thermal)newElement)*
                                1.0/10.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                                calculateX(xAxisLength, map.
                                getMRadius((Thermal)newElement)/
                                Math.sqrt(2)+55.0),
                                calculateY(yAxisLength, map.
                                getHeight((Thermal)newElement),
                                Thermal.MAXIMUM_HEIGHT)+30.0+7.0);
                        curve.lineTo(55.0,
                                calculateY(yAxisLength,
                                map.getHeight((Thermal)newElement),
                                Thermal.MAXIMUM_HEIGHT)+30.0+7.0);
                        curve.lineTo(55.0, thermViewPanel.
                                getHeight()-30.0);

                        CPolyLine externalCurve = new CPolyLine(55.0,
                                thermViewPanel.getHeight()-30.0);
                        externalCurve.curveTo(
                                calculateX(xAxisLength, map.
                                getMRadius((Thermal)newElement)*1.0/2.0+55.0),
                                calculateY(yAxisLength, map.
                                getHeight((Thermal)newElement)*
                                1.0/100.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                                calculateX(xAxisLength, map.
                                getMRadius((Thermal)newElement)*
                                3.0/4.0+55.0),
                                calculateY(yAxisLength, map.
                                getHeight((Thermal)newElement)*
                                1.0/10.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                                calculateX(xAxisLength, map.
                                getMRadius((Thermal)newElement)+55.0),
                                calculateY(yAxisLength, map.
                                getHeight((Thermal)newElement),
                                Thermal.MAXIMUM_HEIGHT)+30.0+7.0);
                        externalCurve.setOutlinePaint(Color.BLUE);
                        externalCurve.setTransparencyFill(0);

                        toActCurveView = curve;
                        toActExternalCurveView = externalCurve;

                        CText labelRad = new CText(new Point2D.Double(
                                calculateX(xAxisLength, map.
                                getMRadius((Thermal)newElement))/
                                Math.sqrt(2)+55.0,
                                calculateY(yAxisLength, map.
                                getHeight((Thermal)newElement),
                                Thermal.MAXIMUM_HEIGHT)+20.0),
                                printedRad,
                                new Font("verdana", Font.PLAIN, 12));

                        canvasMap.addShape(label);
                        canvasMap.addShape(therm);
                        canvasMap.addShape(rect);
                        canvasMap.addShape(line);
                        canvasMap.addShape(center);

                        canvasTherm.addShape(externalCurve);
                        canvasTherm.addShape(lineAlt);
                        canvasTherm.addShape(labelAlt);
                        canvasTherm.addShape(curve);
                        canvasTherm.addShape(labelRad);

                        associationListPhenomenon.put(therm, newElement);
                        List<CShape> groupShape = new ArrayList();
                        groupShape.add(rect);
                        groupShape.add(line);
                        groupShape.add(label);
                        groupShape.add(center);
                        associationListShapeGroup.put(therm, groupShape);
                        listHandle.add(rect);
                        listLine.add(line);

                        List<CShape> groupShapeView = new ArrayList();
                        groupShapeView.add(externalCurve);
                        groupShapeView.add(labelRad);
                        groupShapeView.add(lineAlt);
                        groupShapeView.add(labelAlt);
                        groupShapeView.add(curve);
                        associationListShapeGroupView.put(therm,
                                groupShapeView);
                    }

                };

                /** All the shapes will be moved as soon as the hysteresis shape
                 * is left. */
                Transition moveOnMap = new LeaveOnShape(">> move") {

                    @Override
                    public boolean guard() {
                        return getShape() == hystShape;
                    }

                    @Override
                    public void action() {
                        Point2D newPoint = getPoint();
                        double dx = (newPoint.getX()-lastPoint.getX());
                        double dy = (newPoint.getY()-lastPoint.getY());
                        for(CShape key : canvasMap.getDisplayList()) {
                            key.translateBy(dx, dy);
                        }
                        mapCoordinates.translateBy(-dx, -dy);
                        originP.setLocation(
                                originP.getX()+dx, originP.getY()+dy);
                        lastPoint = getPoint();
                        canvasMap.removeShape(hystShape);

                    }

                };

            };

            /** This state represents the move of all the shapes on the
             * canvas. */
            public State move = new State() {

                /** If the hysteresis shape is not left, this transition leads
                 * to the start state.
                 */
                Transition stop = new Release(BUTTON1, ">> start") {

                    @Override
                    public void action() {
                        canvasMap.removeShape(hystShape);
                    }

                };

                /** All the shapes are moved. */
                Transition move = new Drag(BUTTON1) {

                    @Override
                    public void action() {
                        Point2D newPoint = getPoint();
                        double dx = (newPoint.getX()-lastPoint.getX());
                        double dy = (newPoint.getY()-lastPoint.getY());
                        for(CShape key : canvasMap.getDisplayList()) {
                            key.translateBy(dx, dy);
                        }
                        mapCoordinates.translateBy(-dx, -dy);
                        originP.setLocation(
                                originP.getX()+dx, originP.getY()+dy);
                        lastPoint = getPoint();
                    }

                };

            };

            /** The mouse cursor is still over the hysteresis shape. */
            public State waitHyst = new State() {

                @Override
                public void enter() {
                    cardL.show(thermPanel, "cardThermalColumnProperties");
                }

                Transition dragLine = new LeaveOnShape(">> dragLine") {

                    @Override
                    public boolean guard() {
                        return getShape() == hystShape
                                && listLine.contains((CShape)toAct);
                    }

                    @Override
                    public void action() {
                        Point2D newPoint = getPoint();
                        double dx = newPoint.getX()-lastPoint.getX();
                        double dy = newPoint.getY()-lastPoint.getY();
                        for(CShape element :
                                associationListShapeGroup.keySet()) {
                            if(associationListShapeGroup.
                                    get(element).contains((CShape)toAct)) {
                                toActShape = element;
                            }
                        }
                        toActShape.translateBy(dx, dy);
                        for(CShape element : associationListShapeGroup.
                                get(toActShape)) {
                            element.translateBy(dx, dy);
                        }
                        map.translateLocalizedPhenomenon(
                                associationListPhenomenon.get(toActShape),
                                dx, dy);
                        lastPoint = getPoint();
                        canvasMap.removeShape(hystShape);
                    }

                };

                /** As soon as the hysteresis shape is left, the concerned
                 * phenomenon is moved.
                 */
                Transition dragPhenomenon = new LeaveOnShape(
                        ">> dragPhenomenon") {

                    @Override
                    public boolean guard() {
                        return getShape() == hystShape
                                && associationListPhenomenon.containsKey(
                                (CShape)toAct);
                    }

                    @Override
                    public void action() {
                        Point2D newPoint = getPoint();
                        double dx = newPoint.getX()-lastPoint.getX();
                        double dy = newPoint.getY()-lastPoint.getY();
                        toAct.translateBy(dx, dy);
                        for(CShape element :
                                associationListShapeGroup.get((CShape)toAct)) {
                            element.translateBy(dx, dy);
                        }
                        map.translateLocalizedPhenomenon(
                                associationListPhenomenon.get((CShape)toAct),
                                dx, dy);
                        lastPoint = getPoint();
                        canvasMap.removeShape(hystShape);
                    }

                };

                /** As soon as the hysteresis shape is left, the concerned
                 * handle is moved.
                 */
                Transition dragHandle = new LeaveOnShape(">> dragHandle") {

                    @Override
                    public boolean guard() {
                        return getShape() == hystShape
                                && listHandle.contains((CShape)toAct);
                    }

                    @Override
                    public void action() {
                        double dx = getPoint().getX()-lastPoint.getX();
                        for(CShape element :
                                associationListShapeGroup.keySet()) {
                            for(CShape shape :
                                    associationListShapeGroup.get(element)) {
                                if(((CShape)toAct).equals(shape)) {
                                    toActShape = element;
                                    for(CShape form :
                                            associationListShapeGroup.
                                            get(element)) {
                                        if(form instanceof CPolyLine) {
                                            toActLine = (CPolyLine)form;
                                        }
                                        else if(form instanceof CText) {
                                            toActText = (CText)form;
                                        }
                                    }
                                }
                            }
                        }
                        toActTextAltView = (CText)
                                associationListShapeGroupView.
                                get(toActShape).
                                get(associationListShapeGroupView.
                                get(toActShape).size()-2);
                        toActLineView = (CPolyLine)
                                associationListShapeGroupView.
                                get(toActShape).
                                get(associationListShapeGroupView.
                                get(toActShape).size()-3);
                        toActCurveView = (CPolyLine)
                                associationListShapeGroupView.
                                get(toActShape).
                                get(associationListShapeGroupView.
                                get(toActShape).size()-1);
                        toActExternalCurveView = (CPolyLine)
                                associationListShapeGroupView.
                                get(toActShape).
                                get(associationListShapeGroupView.
                                get(toActShape).size()-5);
                        toActTextRadView = (CText)
                                associationListShapeGroupView.
                                get(toActShape).
                                get(associationListShapeGroupView.
                                get(toActShape).size()-4);
                        double oldWidth = toActShape.getWidth()/2;
                        if(oldWidth-dx >= Thermal.MINIMUM_RADIUS &&
                                oldWidth-dx <= Thermal.MAXIMUM_RADIUS) {
                            Point2D currentPointLine = (Point2D)toActLine.
                                    getCurrentPoint().clone();
                            toActLine.removeLastSegment();
                            toAct.translateBy(dx, 0.0);
                            toActText.translateBy(dx/2, 0.0);
                            toActPhenomenon = associationListPhenomenon.get(
                                    toActShape);
                            double currentHeight = toActShape.getHeight();
                            double currentWidth = toActShape.getWidth();
                            double currentX = new Double(toActShape.
                                    getCenterX()-toActShape.getWidth()/2);
                            double currentY = new Double(toActShape.
                                    getCenterY()-toActShape.getWidth()/2);
                            ((CRectangularShape)toActShape).setHeight(
                                    currentHeight-2*dx);
                            ((CRectangularShape)toActShape).setWidth(
                                    currentWidth-2*dx);
                            toActShape.translateBy(-(toActShape.
                                    getCenterX()-toActShape.
                                    getWidth()/2-currentX)+dx,
                                    -(toActShape.
                                    getCenterY()-toActShape.
                                    getWidth()/2-currentY)+dx);
                            toActLine.lineTo(currentPointLine.getX()+dx,
                                    currentPointLine.getY());
                            double oldAlt = map.
                                    getHeight((Thermal)toActPhenomenon);
                            double oldRad = map.
                                    getMRadius((Thermal)toActPhenomenon);
                            map.modifyRadius(toActPhenomenon, -dx);
                            toActLineView.translateBy(0.0,
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon),
                                    Thermal.MAXIMUM_HEIGHT)-
                                    calculateY(yAxisLength, oldAlt,
                                    Thermal.MAXIMUM_HEIGHT));
                            toActTextAltView.translateBy(0.0,
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon),
                                    Thermal.MAXIMUM_HEIGHT)-
                                    calculateY(yAxisLength, oldAlt,
                                    Thermal.MAXIMUM_HEIGHT));
                            toActTextRadView.translateBy(
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)/
                                    Math.sqrt(2))-
                                    calculateX(xAxisLength, oldRad/
                                    Math.sqrt(2)),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon),
                                    Thermal.MAXIMUM_HEIGHT)-
                                    calculateY(yAxisLength, oldAlt,
                                    Thermal.MAXIMUM_HEIGHT));
                            toActCurveView.reset(55.0, thermViewPanel.
                                    getHeight()-30.0);
                            toActCurveView.curveTo(
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)*
                                    (1.0/2.0)/Math.sqrt(2)+55.0),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon)*
                                    1.0/100.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)*
                                    (3.0/4.0)/Math.sqrt(2)+55.0),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon)*
                                    1.0/10.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)/
                                    Math.sqrt(2)+55.0),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon),
                                    Thermal.MAXIMUM_HEIGHT)+30.0+7.0);
                            toActCurveView.lineTo(55.0,
                                    calculateY(yAxisLength,
                                    map.getHeight((Thermal)toActPhenomenon),
                                    Thermal.MAXIMUM_HEIGHT)+30.0+7.0);
                            toActCurveView.lineTo(55.0, thermViewPanel.
                                    getHeight()-30.0);
                            toActExternalCurveView.reset(55.0, thermViewPanel.
                                    getHeight()-30.0);
                            toActExternalCurveView.curveTo(
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)*
                                    1.0/2.0+55.0),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon)*
                                    1.0/100.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)*
                                    3.0/4.0+55.0),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon)*
                                    1.0/10.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)+55.0),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon),
                                    Thermal.MAXIMUM_HEIGHT)+30.0+7.0);

                            String longRad = Double.toString(toActPhenomenon.
                                    getMRadius());
                            String printedRad = longRad;
                            int indexEast = longRad.indexOf(".");
                            if (indexEast != -1) {
                                printedRad = longRad.substring(0, indexEast+2);
                            }
                            toActText.setText(printedRad);
                            toActTextRadView.setText(printedRad);

                            String longAlt = Double.toString(map.
                                    getHeight((Thermal)toActPhenomenon));
                            String printedAlt = longAlt;
                            int index = longAlt.indexOf(".");
                            if (index != -1) {
                                printedAlt = longAlt.substring(0, index+2);
                            }
                            toActTextAltView.setText(printedAlt);

                        }
                        lastPoint = getPoint();
                        canvasMap.removeShape(hystShape);
                    }

                };

                /** If the hysteresis is not left and the mouse button is
                 * released, it will lead to the start state. */
                Transition release = new Release(BUTTON1, ">> start") {

                    @Override
                    public void action() {
                        canvasMap.removeShape(hystShape);
                        Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
                        mapPanel.setCursor(cursor);
                    }

                };

            };

            /** This state is in charge of the deplacement of the handles. */
            public State dragHandle = new State() {

                @Override
                public void enter() {
                    toAct.setOutlinePaint(Color.ORANGE);
                    Cursor cursor = new Cursor(Cursor.W_RESIZE_CURSOR);
                    mapPanel.setCursor(cursor);
                }

                @Override
                public void leave() {
                    toAct.setOutlinePaint(Color.RED);
                }

                /** When the move is stopped, the new state is the start
                 * state. */
                Transition stop = new Release(BUTTON1, ">> start") {

                    @Override
                    public void action() {
                        canvasMap.removeShape(hystShape);
                        Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
                        mapPanel.setCursor(cursor);
                    }

                };

                Transition move = new Drag(BUTTON1) {

                    @Override
                    public void action() {
                        double dx = getPoint().getX()-lastPoint.getX();
                        double oldWidth = toActShape.getWidth()/2;
                        if(oldWidth-dx >= Thermal.MINIMUM_RADIUS &&
                                oldWidth-dx <= Thermal.MAXIMUM_RADIUS) {
                            Point2D currentPointLine = (Point2D)toActLine.
                                    getCurrentPoint().clone();
                            toActLine.removeLastSegment();
                            toAct.translateBy(dx, 0.0);
                            toActText.translateBy(dx/2, 0.0);
                            double currentHeight = toActShape.getHeight();
                            double currentWidth = toActShape.getWidth();
                            double currentX = new Double(toActShape.
                                    getCenterX()-toActShape.getWidth()/2);
                            double currentY = new Double(toActShape.
                                    getCenterY()-toActShape.getWidth()/2);
                            ((CRectangularShape)toActShape).setHeight(
                                    currentHeight-2*dx);
                            ((CRectangularShape)toActShape).setWidth(
                                    currentWidth-2*dx);
                            toActShape.translateBy(-(toActShape.getCenterX()-
                                    toActShape.getWidth()/2-currentX)+dx,
                                    -(toActShape.getCenterY()-
                                    toActShape.getWidth()/2-currentY)+dx);
                            toActLine.lineTo(currentPointLine.getX()+dx,
                                    currentPointLine.getY());
                            double oldAlt = map.
                                    getHeight((Thermal)toActPhenomenon);
                            double oldRad = map.
                                    getMRadius((Thermal)toActPhenomenon);
                            map.modifyRadius(toActPhenomenon, -dx);
                            toActLineView.translateBy(0.0,
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon),
                                    Thermal.MAXIMUM_HEIGHT)-
                                    calculateY(yAxisLength, oldAlt,
                                    Thermal.MAXIMUM_HEIGHT));
                            toActTextAltView.translateBy(0.0,
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon),
                                    Thermal.MAXIMUM_HEIGHT)-
                                    calculateY(yAxisLength, oldAlt,
                                    Thermal.MAXIMUM_HEIGHT));
                            toActTextRadView.translateBy(
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)/
                                    Math.sqrt(2))-
                                    calculateX(xAxisLength, oldRad/
                                    Math.sqrt(2)),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon),
                                    Thermal.MAXIMUM_HEIGHT)-
                                    calculateY(yAxisLength, oldAlt,
                                    Thermal.MAXIMUM_HEIGHT));
                            toActCurveView.reset(55.0, thermViewPanel.
                                    getHeight()-30.0);
                            toActCurveView.curveTo(
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)*
                                    (1.0/2.0)/Math.sqrt(2)+55.0),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon)*
                                    1.0/100.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)*
                                    (3.0/4.0)/Math.sqrt(2)+55.0),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon)*
                                    1.0/10.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)/
                                    Math.sqrt(2)+55.0),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon),
                                    Thermal.MAXIMUM_HEIGHT)+30.0+7.0);
                            toActCurveView.lineTo(55.0,
                                    calculateY(yAxisLength,
                                    map.getHeight((Thermal)toActPhenomenon),
                                    Thermal.MAXIMUM_HEIGHT)+30.0+7.0);
                            toActCurveView.lineTo(55.0, thermViewPanel.
                                    getHeight()-30.0);
                            toActExternalCurveView.reset(55.0, thermViewPanel.
                                    getHeight()-30.0);
                            toActExternalCurveView.curveTo(
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)*
                                    1.0/2.0+55.0),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon)*
                                    1.0/100.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)*
                                    3.0/4.0+55.0),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon)*
                                    1.0/10.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                                    calculateX(xAxisLength, map.
                                    getMRadius((Thermal)toActPhenomenon)+55.0),
                                    calculateY(yAxisLength, map.
                                    getHeight((Thermal)toActPhenomenon),
                                    Thermal.MAXIMUM_HEIGHT)+30.0+7.0);

                            String longRad = Double.toString(toActPhenomenon.
                                    getMRadius());
                            String printedRad = longRad;
                            int indexEast = longRad.indexOf(".");
                            if (indexEast != -1) {
                                printedRad = longRad.substring(0, indexEast+2);
                            }
                            toActText.setText(printedRad);
                            toActTextRadView.setText(printedRad);

                            String longAlt = Double.toString(map.
                                    getHeight((Thermal)toActPhenomenon));
                            String printedAlt = longAlt;
                            int index = longAlt.indexOf(".");
                            if (index != -1) {
                                printedAlt = longAlt.substring(0, index+2);
                            }
                            toActTextAltView.setText(printedAlt);
                        }
                        lastPoint = getPoint();
                    }

                };

            };

            /** This state is in charge of the deplacement of the phenomena. */
            public State dragPhenomenon = new State() {

                @Override
                public void enter() {
                    toAct.setOutlinePaint(Color.ORANGE);
                    for(CShape shape : associationListShapeGroup.
                            get((CShape)toAct)) {
                        shape.setOutlinePaint(Color.ORANGE);
                    }
                    Cursor cursor = new Cursor(Cursor.HAND_CURSOR);
                    mapPanel.setCursor(cursor);
                }

                @Override
                public void leave() {
                    toAct.setOutlinePaint(currentThermalColor);
                    for(CShape shape : associationListShapeGroup.
                            get((CShape)toAct)) {
                        if(shape instanceof CRectangle || shape
                                instanceof CEllipse) {
                            shape.setOutlinePaint(Color.RED);
                        }
                        else {
                            shape.setOutlinePaint(currentThermalColor);
                        }
                    }
                }

                Transition stop = new Release(BUTTON1, ">> start") {

                    @Override
                    public void action() {
                        canvasMap.removeShape(hystShape);
                        Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
                        mapPanel.setCursor(cursor);
                    }

                };

                Transition move = new Drag(BUTTON1) {

                    @Override
                    public void action() {
                        Point2D newPoint = getPoint();
                        double dx = newPoint.getX()-lastPoint.getX();
                        double dy = newPoint.getY()-lastPoint.getY();
                        map.translateLocalizedPhenomenon(
                                associationListPhenomenon.get((CShape)toAct),
                                dx, dy);
                        toAct.translateBy(dx, dy);

                        String longEast = Double.toString(map.
                                getEast(currentThermal));
                        String longNorth = Double.toString(map.
                                getNorth(currentThermal));
                        String printedEast = longEast;
                        String printedNorth = longNorth;
                        int indexEast = longEast.indexOf(".");
                        int indexNorth = longNorth.indexOf(".");
                        try {
                            if (indexEast != -1) {
                                printedEast = longEast.substring(0,
                                        indexEast+6);
                            }
                            if (indexNorth != -1) {
                                printedNorth = longNorth.substring(0,
                                        indexNorth+6);
                            }
                        } catch(StringIndexOutOfBoundsException e) {
                            if (indexEast != -1) {
                                printedEast = longEast.substring(0,
                                        indexEast+2);
                            }
                            if (indexNorth != -1) {
                                printedNorth = longNorth.substring(0,
                                        indexNorth+2);
                            }

                        }
                        thermCoordinatesLabel.setText(
                                "(" +
                                printedEast + ", " + printedNorth + ")");

                        double x = map.getEast(currentThermal);
                        double y = map.getNorth(currentThermal);
                        Coordinates newGeoCoord = 
                                MainCoordinatesManager.mCoordToGeoCoord(x, y);
                        String longLong = Double.toString(newGeoCoord.getX());
                        String longLat = Double.toString(newGeoCoord.getY());
                        String printedLong = longLong;
                        String printedLat = longLat;
                        int indexLong = longLong.indexOf(".");
                        int indexLat = longLat.indexOf(".");
                        try {
                            if (indexLong != -1) {
                                printedLong = longLong.substring(0,
                                        indexLong+7);
                            }
                            if (indexLat != -1) {
                                printedLat = longLat.substring(
                                        0, indexLat+7);
                            }
                        } catch(StringIndexOutOfBoundsException e) {
                            if (indexLong != -1) {
                                printedLong = longLong.substring(0,
                                        indexLong+2);
                            }
                            if (indexLat != -1) {
                                printedLat = longLat.substring(
                                        0, indexLat+2);
                            }
                        }
                        thermCoordinatesGeoLabel.setText(
                                "(" + printedLong + ", " + printedLat +")");

                        for(CShape element :
                                associationListShapeGroup.get((CShape)toAct)) {
                            element.translateBy(dx, dy);
                        }
                        lastPoint = getPoint();
                    }

                };

            };

            /** When the radius line is dragged, the whole thermal is moved. */
            public State dragLine = new State() {

                @Override
                public void enter() {
                    for(CShape element : associationListShapeGroup.keySet()) {
                        if(associationListShapeGroup.get(element).
                                contains((CShape)toAct)) {
                            element.setOutlinePaint(Color.ORANGE);
                            for(CShape shape : associationListShapeGroup.
                                    get(element)) {
                                shape.setOutlinePaint(Color.ORANGE);
                            }
                        }
                    }
                    Cursor cursor = new Cursor(Cursor.HAND_CURSOR);
                    mapPanel.setCursor(cursor);
                }

                @Override
                public void leave() {
                    for(CShape element : associationListShapeGroup.keySet()) {
                        if(associationListShapeGroup.get(element).
                                contains((CShape)toAct)) {
                            element.setOutlinePaint(currentThermalColor);
                            for(CShape shape : associationListShapeGroup.
                                    get(element)) {
                                if(shape instanceof CRectangle || shape
                                        instanceof CEllipse) {
                                    shape.setOutlinePaint(Color.RED);
                                }
                                else {
                                    shape.setOutlinePaint(currentThermalColor);
                                }
                            }
                        }
                    }
                }

                Transition stop = new Release(BUTTON1, ">> start") {

                    @Override
                    public void action() {
                        canvasMap.removeShape(hystShape);
                        Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
                        mapPanel.setCursor(cursor);
                    }

                };

                Transition move = new Drag(BUTTON1) {

                    @Override
                    public void action() {
                        Point2D newPoint = getPoint();
                        double dx = newPoint.getX()-lastPoint.getX();
                        double dy = newPoint.getY()-lastPoint.getY();
                        map.translateLocalizedPhenomenon(
                                associationListPhenomenon.get(toActShape),
                                dx, dy);
                        toActShape.translateBy(dx, dy);

                        String longEast = Double.toString(map.
                                getEast(currentThermal));
                        String longNorth = Double.toString(map.
                                getNorth(currentThermal));
                        String printedEast = longEast;
                        String printedNorth = longNorth;
                        int indexEast = longEast.indexOf(".");
                        int indexNorth = longNorth.indexOf(".");
                        try {
                            if (indexEast != -1) {
                                printedEast = longEast.substring(0,
                                        indexEast+6);
                            }
                            if (indexNorth != -1) {
                                printedNorth = longNorth.substring(0,
                                        indexNorth+6);
                            }
                        } catch(StringIndexOutOfBoundsException e) {
                            if (indexEast != -1) {
                                printedEast = longEast.substring(0,
                                        indexEast+2);
                            }
                            if (indexNorth != -1) {
                                printedNorth = longNorth.substring(0,
                                        indexNorth+2);
                            }

                        }
                        thermCoordinatesLabel.setText(
                                "(" +
                                printedEast + ", " + printedNorth + ")");

                        double x = map.getEast(currentThermal);
                        double y = map.getNorth(currentThermal);
                        Coordinates newGeoCoord = 
                                MainCoordinatesManager.mCoordToGeoCoord(x, y);
                        String longLong = Double.toString(newGeoCoord.getX());
                        String longLat = Double.toString(newGeoCoord.getY());
                        String printedLong = longLong;
                        String printedLat = longLat;
                        int indexLong = longLong.indexOf(".");
                        int indexLat = longLat.indexOf(".");
                        try {
                            if (indexLong != -1) {
                                printedLong = longLong.substring(0,
                                        indexLong+7);
                            }
                            if (indexLat != -1) {
                                printedLat = longLat.substring(
                                        0, indexLat+7);
                            }
                        } catch(StringIndexOutOfBoundsException e) {
                            if (indexLong != -1) {
                                printedLong = longLong.substring(0,
                                        indexLong+2);
                            }
                            if (indexLat != -1) {
                                printedLat = longLat.substring(
                                        0, indexLat+2);
                            }
                        }
                        thermCoordinatesGeoLabel.setText(
                                "(" + printedLong + ", " + printedLat +")");

                        for(CShape element : associationListShapeGroup.
                                get(toActShape)) {
                            element.translateBy(dx, dy);
                        }
                        lastPoint = getPoint();
                    }

                };

            };

        };

        final StateMachineVisualization smv =
                new StateMachineVisualization(sm);

        mapPanel.setLayout(new GridLayout(1, 1));
        mapPanel.add(canvasMap);

        thermViewPanel.setLayout(new GridLayout(1, 1));
        thermViewPanel.add(canvasTherm);

    }

    /** Resets all the thermal views. */
    public void resetThermal() {
        Set<CShape> tempSet = new HashSet(associationListPhenomenon.keySet());
        for(CShape shape : tempSet) {
            associationListPhenomenon.remove(shape);
            canvasMap.removeShape(shape);
            for(CShape element : associationListShapeGroup.get(shape)) {
                canvasMap.removeShape(element);
                if(listHandle.contains(element)) {
                    listHandle.remove(element);
                }
            }
            for(CShape element :
                    associationListShapeGroupView.get(shape)) {
                canvasTherm.removeShape(element);
            }
            associationListShapeGroup.remove(shape);
        }
        map.resetThermal();
        thermMaxSpeedSlider.setValue(5);
        thermMaxSpeedLabel.setText(
                "Maximum Vertical Speed : " + 5 + " cm/s");
        thermCoordinatesLabel.setText(
                "(" +
                0 + ", " +
                0 + ")");
        thermCoordinatesGeoLabel.setText(
                "(" +
                0 + ", " +
                0 + ")");
        cardL.show(thermPanel, "cardThermalColumnEmpty");
    }

    /** 
     * Gets the selected current thermal.
     *
     * @return the current thermal
     */
    public Thermal getCurrentThermal() {
        return currentThermal;
    }

    /**
     * Gets the map canvas.
     * 
     * @return the map canvas
     */
    public Canvas getCanvas() {
        return canvasMap;
    }

    /**
     * Gets the origin point coordinates.
     * 
     * @return the origin point coordinates
     */
    public static Point2D getOrigin () {
        return originP;
    }

    /**
     * Gets the google list.
     * 
     * @return the google list
     */
    public List<CShape> getGoogleList() {
        return listGoogle;
    }

    /**
     * Loads a formerly created environment.
     */
    public void loadView() {
        cardL.show(thermPanel, "cardThermalColumnProperties");
        List<LocalizedPhenomenon> tempList = 
                new ArrayList(map.getLocalizedPhenomenonList());
        for(LocalizedPhenomenon phenomenon : map.getLocalizedPhenomenonList()) {
            if(phenomenon instanceof Thermal) {
                currentThermal = (Thermal)phenomenon;
                tempList.remove(currentThermal);

                String longEast = Double.toString(map.
                        getEast(currentThermal));
                String longNorth = Double.toString(map.
                        getNorth(currentThermal));
                String printedEast = longEast;
                String printedNorth = longNorth;
                int indexEast = longEast.indexOf(".");
                int indexNorth = longNorth.indexOf(".");
                try {
                    if (indexEast != -1) {
                        printedEast = longEast.substring(0,
                                indexEast+6);
                    }
                    if (indexNorth != -1) {
                        printedNorth = longNorth.substring(0,
                                indexNorth+6);
                    }
                } catch(StringIndexOutOfBoundsException e) {
                    if (indexEast != -1) {
                        printedEast = longEast.substring(0,
                                indexEast+2);
                    }
                    if (indexNorth != -1) {
                        printedNorth = longNorth.substring(0,
                                indexNorth+2);
                    }

                }
                thermCoordinatesLabel.setText(
                        "(" +
                        printedEast + ", " + printedNorth + ")");

                double x = map.getEast(currentThermal);
                double y = map.getNorth(currentThermal);
                Coordinates newGeoCoord = 
                        MainCoordinatesManager.mCoordToGeoCoord(x, y);
                String longLong = Double.toString(newGeoCoord.getX());
                String longLat = Double.toString(newGeoCoord.getY());
                String printedLong = longLong;
                String printedLat = longLat;
                int indexLong = longLong.indexOf(".");
                int indexLat = longLat.indexOf(".");
                try {
                    if (indexLong != -1) {
                        printedLong = longLong.substring(0,
                                indexLong+7);
                    }
                    if (indexLat != -1) {
                        printedLat = longLat.substring(
                                0, indexLat+7);
                    }
                } catch(StringIndexOutOfBoundsException e) {
                    if (indexLong != -1) {
                        printedLong = longLong.substring(0,
                                indexLong+2);
                    }
                    if (indexLat != -1) {
                        printedLat = longLat.substring(
                                0, indexLat+2);
                    }
                }
                thermCoordinatesGeoLabel.setText(
                        "(" + printedLong + ", " + printedLat +")");

                double radius = currentThermal.getRadius();
                CEllipse therm = new CEllipse(
                        currentThermal.getX()-radius,
                        currentThermal.getY()-radius,
                        radius*2, radius*2);
                therm.setTransparencyFill(0);
                therm.setOutlinePaint(currentThermalColor);
                currentThermalShape = therm;

                CEllipse center = new CEllipse(
                        currentThermal.getX()-2.5,
                        currentThermal.getY()-2.5,
                        2.5*2, 2.5*2);
                center.setTransparencyFill(0);
                center.setOutlinePaint(Color.RED);

                CRectangle rect = new CRectangle(
                        currentThermal.getX()-radius-2.5,
                        currentThermal.getY()-2.5,
                        5, 5);
                rect.setFillPaint(Color.WHITE);
                rect.setOutlinePaint(Color.RED);

                CPolyLine line = new CPolyLine(
                        currentThermal.getX(),
                        currentThermal.getY());
                line.lineTo(currentThermal.getX()-radius+2.5,
                        currentThermal.getY());
                line.setOutlinePaint(currentThermalColor);

                String longRad = Double.toString(map.
                        getMRadius(currentThermal));
                String printedRad = longRad;
                int indexRad = longRad.indexOf(".");
                if (indexRad != -1) {
                    printedRad = longRad.substring(0, indexRad+2);
                }
                CText label = new CText(new Point2D.Double(
                        therm.getCenterX()-((3/2)*radius),
                        therm.getCenterY()),
                        printedRad,
                        new Font("verdana", Font.PLAIN, 12));
                label.setFillPaint(currentThermalColor);

                rect.above(label);

                CPolyLine lineAlt = new CPolyLine(55.0,
                        calculateY(yLength, map.
                        getHeight(currentThermal), Thermal.
                        MAXIMUM_HEIGHT)+30.0+7.0);
                lineAlt.lineTo(55.0+xLength,
                        calculateY(yLength, map.
                        getHeight(currentThermal), Thermal.
                        MAXIMUM_HEIGHT)+30.0+7.0);

                String longAlt = Double.toString(map.
                        getHeight(currentThermal));
                String printedAlt = longAlt;
                int indexAlt = longAlt.indexOf(".");
                if (indexAlt != -1) {
                    printedAlt = longAlt.substring(0, indexAlt+2);
                }
                CText labelAlt = new CText(new Point2D.Double(5.0,
                        calculateY(yLength, map.
                        getHeight(currentThermal), Thermal.
                        MAXIMUM_HEIGHT)+30.0),
                        printedAlt,
                        new Font("verdana", Font.PLAIN, 12));

                CPolyLine curve = new CPolyLine(55.0, thermViewPanel.
                        getHeight()-30.0);
                curve.curveTo(
                        calculateX(xLength, map.
                        getMRadius(currentThermal)*
                        (1.0/2.0)/Math.sqrt(2)+55.0),
                        calculateY(yLength, map.
                        getHeight(currentThermal)*
                        1.0/100.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                        calculateX(xLength, map.
                        getMRadius(currentThermal)*
                        (3.0/4.0)/Math.sqrt(2)+55.0),
                        calculateY(yLength, map.
                        getHeight(currentThermal)*
                        1.0/10.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                        calculateX(xLength, map.
                        getMRadius(currentThermal)/
                        Math.sqrt(2)+55.0),
                        calculateY(yLength, map.
                        getHeight(currentThermal),
                        Thermal.MAXIMUM_HEIGHT)+30.0+7.0);
                curve.lineTo(55.0,
                        calculateY(yLength,
                        map.getHeight(currentThermal),
                        Thermal.MAXIMUM_HEIGHT)+30.0+7.0);
                curve.lineTo(55.0, thermViewPanel.
                        getHeight()-30.0);

                CPolyLine externalCurve = new CPolyLine(55.0,
                        thermViewPanel.getHeight()-30.0);
                externalCurve.curveTo(
                        calculateX(xLength, map.
                        getMRadius(currentThermal)*1.0/2.0+55.0),
                        calculateY(yLength, map.
                        getHeight(currentThermal)*
                        1.0/100.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                        calculateX(xLength, map.
                        getMRadius(currentThermal)*
                        3.0/4.0+55.0),
                        calculateY(yLength, map.
                        getHeight(currentThermal)*
                        1.0/10.0, Thermal.MAXIMUM_HEIGHT)+30.0+7.0,
                        calculateX(xLength, map.
                        getMRadius(currentThermal)+55.0),
                        calculateY(yLength, map.
                        getHeight(currentThermal),
                        Thermal.MAXIMUM_HEIGHT)+30.0+7.0);
                externalCurve.setOutlinePaint(Color.BLUE);
                externalCurve.setTransparencyFill(0);

                CText labelRad = new CText(new Point2D.Double(
                        calculateX(xLength, map.
                        getMRadius(currentThermal))/
                        Math.sqrt(2)+55.0,
                        calculateY(yLength, map.
                        getHeight(currentThermal),
                        Thermal.MAXIMUM_HEIGHT)+20.0),
                        printedRad,
                        new Font("verdana", Font.PLAIN, 12));

                canvasMap.addShape(label);
                canvasMap.addShape(therm);
                canvasMap.addShape(rect);
                canvasMap.addShape(line);

                if(tempList.isEmpty()) {
                    canvasMap.addShape(center);

                    canvasTherm.addShape(externalCurve);
                    canvasTherm.addShape(lineAlt);
                    canvasTherm.addShape(labelAlt);
                    canvasTherm.addShape(curve);
                    canvasTherm.addShape(labelRad);
                }

                associationListPhenomenon.put(therm, currentThermal);
                List<CShape> groupShape = new ArrayList();
                groupShape.add(rect);
                groupShape.add(line);
                groupShape.add(label);
                groupShape.add(center);
                associationListShapeGroup.put(therm, groupShape);
                listHandle.add(rect);
                listLine.add(line);

                List<CShape> groupShapeView = new ArrayList();
                groupShapeView.add(externalCurve);
                groupShapeView.add(labelRad);
                groupShapeView.add(lineAlt);
                groupShapeView.add(labelAlt);
                groupShapeView.add(curve);
                associationListShapeGroupView.put(therm,
                        groupShapeView);

                thermMaxSpeedSlider.setValue(
                        (int)Thermal.DEFAULT_MAXIMUM_SPEED);
                thermMaxSpeedLabel.setText("Maximum Vertical Speed : " +
                        (int)map.getMaximumSpeed(currentThermal) +
                        " cm/s");
            }
        }
    }

    /**
     * Changes the current thermal color.
     * 
     * @param c the new thermal color
     */
    public void setThermalColor(Color c) {
        this.currentThermalColor = c;
        for(CShape shape : associationListShapeGroup.keySet()) {
            shape.setOutlinePaint(currentThermalColor);
            for(CShape form : associationListShapeGroup.
                    get(shape)) {
                if(!(form instanceof CRectangle) && !(form
                        instanceof CEllipse)) {
                    if(form instanceof CText) {
                        ((CText)form).setFillPaint(currentThermalColor);
                    }
                    else {
                        form.setOutlinePaint(currentThermalColor);
                    }
                }
            }
        }
    }

    /**
     * Gets the current thermal color.
     * 
     * @return the current thermal color
     */
    public Color getThermalColor() {
        return this.currentThermalColor;
    }

    /**
     * Adds a new UAV to the UAV list.
     * 
     * @param n the UAV number
     * @param firstCoordinates the first coordinates of the new UAV
     */
    public void newUAV(int n, SpaceCoordinates firstCoordinates) {
        if(!this.uavList.containsKey(n)) {
            CRectangle uavShape = new CRectangle(firstCoordinates.getX()-5.0, 
                    firstCoordinates.getY()-5.0, 10.0, 10.0);
            uavShape.setFillPaint(colorTab[colorCursor]);
            uavShape.setOutlinePaint(Color.WHITE);

            CText uavLabel = new CText(new Point2D.Double(
                    uavShape.getCenterX()-10.0, 
                    uavShape.getCenterY()-10.0), "uav " + n,
                new Font("verdana", Font.PLAIN, 12));
            uavLabel.setFillPaint(colorTab[colorCursor]);


            if(colorCursor == colorTab.length-1) {
                colorCursor = 0;
            }
            else {
                colorCursor += 1;
            }

            CEllipse point1 = new CEllipse(firstCoordinates.getX()-2.5, 
                    firstCoordinates.getY()-2.5, 5.0, 5.0);
            CEllipse point2 = new CEllipse(firstCoordinates.getX()-2, 
                    firstCoordinates.getY()-2, 4.0, 4.0);
            CEllipse point3 = new CEllipse(firstCoordinates.getX()-1.5, 
                    firstCoordinates.getY()-1.5, 3.0, 3.0);
            CEllipse point4 = new CEllipse(firstCoordinates.getX()-1, 
                    firstCoordinates.getY()-1, 2.0, 2.0);
            CEllipse point5 = new CEllipse(firstCoordinates.getX()-0.5, 
                    firstCoordinates.getY()-0.5, 1.0, 1.0);

            List<CShape> groupShape = new ArrayList();
            groupShape.add(uavShape);
            groupShape.add(point1);
            groupShape.add(point2);
            groupShape.add(point3);
            groupShape.add(point4);
            groupShape.add(point5);
            groupShape.add(uavLabel);

            this.uavList.put(n, groupShape);

            canvasMap.addShape(uavShape);
            canvasMap.addShape(point1);
            canvasMap.addShape(point2);
            canvasMap.addShape(point3);
            canvasMap.addShape(point4);
            canvasMap.addShape(point5);
            canvasMap.addShape(uavLabel);
            point5.aboveAll();
            point4.aboveAll();
            point3.aboveAll();
            point2.aboveAll();
            point1.aboveAll();
            uavLabel.aboveAll();
            uavShape.aboveAll();
        }
    }

    /**
     * Changes the coordinates of an UAV.
     * 
     * @param n the UAV number
     * @param newCoordinates the new coordinates
     */
    public void setUAVCoordinates(int n, SpaceCoordinates newCoordinates) {
        List<CShape> groupShape = this.uavList.get(n);

        CShape uav = groupShape.get(0);
        CShape point1 = groupShape.get(1);
        CShape point2 = groupShape.get(2);
        CShape point3 = groupShape.get(3);
        CShape point4 = groupShape.get(4);
        CShape point5 = groupShape.get(5);
        CShape uavLabel = groupShape.get(6);

        point5.translateTo(point4.getCenterX(), point4.getCenterY());
        point4.translateTo(point3.getCenterX(), point3.getCenterY());
        point3.translateTo(point2.getCenterX(), point2.getCenterY());
        point2.translateTo(point1.getCenterX(), point1.getCenterY());
        point1.translateTo(uav.getCenterX(), uav.getCenterY());

        uav.translateTo(MainCoordinatesManager.meterToPixel(
                newCoordinates.getX())+getOrigin().getX(), 
                -MainCoordinatesManager.meterToPixel(newCoordinates.getY()) +
                getOrigin().getY());
        uavLabel.translateTo(uav.getCenterX()-10.0, uav.getCenterY()-10.0);
    }
}
