import fr.lri.swingstates.canvas.*;
import fr.lri.swingstates.canvas.transitions.LeaveOnShape;
import fr.lri.swingstates.canvas.transitions.PressOnShape;
import fr.lri.swingstates.debug.StateMachineVisualization;
import fr.lri.swingstates.sm.State;
import fr.lri.swingstates.sm.Transition;
import fr.lri.swingstates.sm.transitions.Drag;
import fr.lri.swingstates.sm.transitions.Press;
import fr.lri.swingstates.sm.transitions.Release;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.geom.Point2D;
import java.util.*;
import javax.swing.JPanel;

/** 
 * The MVC Controler for the vertical profile properties editor. It uses the
 * swingstates library.
 *
 * @author Hugo
 * @version 1.2
 */
public class VerticalProfileControler {

    private Canvas canvas;
    private JPanel panel;
    private Map map;

    private final double xLength;
    private final double yLength;

    static final double PI = Math.PI;

    private java.util.Map<CShape, Level> associationListLevel
            = new HashMap();
    private java.util.Map<CShape, List<CShape>> associationListShapeGroup
            = new HashMap();
    private java.util.List<CShape> notToMoveList = new ArrayList();

    /** 
     * Calculates the x value in pixels from a speed value.
     *
     * @param axisLength the x axis length
     * @param speed the speed
     * @return the calculated x value in pixels
     */
    public static double calculateX(double axisLength, double speed) {
        return axisLength*speed/VerticalProfile.MAXIMUM_SPEED;
    }

    /** 
     * Calculates the speed from a x value.
     *
     * @param axisLength the x axis length
     * @param x the x value in pixels
     * @return the calculated speed
     */
    public static double calculateSpeed(double axisLength, double x) {
        return x*VerticalProfile.MAXIMUM_SPEED/axisLength;
    }

    /** 
     * Calculates the y value in pixels from a height value.
     *
     * @param axisLength the y axis length
     * @param height the given height
     * @param maximumHeight the current maximum height
     * @return the calculated y value in pixels
     */
    public static double calculateY(double axisLength, double height,
            double maximumHeight) {
        return axisLength*(maximumHeight-height)/maximumHeight;
    }

    /** 
     * Calculates the height from a given y value.
     *
     * @param axisLength the y axis length
     * @param y the given y value
     * @param maximumHeight the current maximum height
     * @return the calculated height
     */
    public static double calculateHeight(double axisLength, double y,
            double maximumHeight) {
        return maximumHeight-y*maximumHeight/axisLength;
    }

    /** 
     * This method represents a controler for the properties editor section.
     * These are the events that are treated :
     * - a left click over a free space will create a new level,
     * - a right click on a level will erease it (except for the highest and
     * lowest levels),
     * - an horizontal drag and drop on a level will move it and change its
     * speed value,
     * - a vertical drag and drop on the highest level will change the maximum
     * height value,
     * - a drag and drop on an arrow will rotate it around its level and change
     * the direction value (it will rotate only clockwise).
     *
     * @param p the properties editor panel
     * @param m the model
     */
    public VerticalProfileControler(JPanel p, Map m) {

        panel = p;
        map = m;

        canvas = new Canvas(panel.getWidth(), panel.getHeight());

        Point2D zero = new Point2D.Double(55.0, panel.getHeight()-70.0);
        Point2D xEnd = new Point2D.Double(panel.getWidth()-50.0, 
                panel.getHeight()-70.0);
        Point2D yEnd = new Point2D.Double(55.0, 5.0);

        CPolyLine yAxis = new CPolyLine(zero);
        yAxis.lineTo(yEnd);
        CPolyLine yArrow = yAxis.getArrow(PI/6, 10);
        yArrow.setTransparencyFill(0);
        canvas.addShape(yAxis);
        canvas.addShape(yArrow);
        yAxis.moveTo(zero);
        CPolyLine xAxis = new CPolyLine(zero);
        xAxis.lineTo(xEnd);
        CPolyLine xArrow = xAxis.getArrow(PI/6, 10);
        xArrow.setTransparencyFill(0);
        canvas.addShape(xAxis);
        canvas.addShape(xArrow);

        final double xAxisLength = xEnd.getX()-65.0;
        final double yAxisLength = zero.getY()-20;

        xLength = xAxisLength;
        yLength = yAxisLength;

        Level upperLevel = map.getLevel(map.getMaximumHeight());
        Level lowerLevel = map.getLevel(VerticalProfile.DEFAULT_MINIMUM_HEIGHT);

        CRectangle upperRect = new CRectangle(
                VerticalProfileControler.calculateX(xAxisLength, 1)+
                55.0+2.5, 20.0+2.5, 5.0, 5.0);
        upperRect.setFillPaint(Color.WHITE);
        upperRect.setOutlinePaint(Color.BLUE);

        CRectangle lowerRect = new CRectangle(
                VerticalProfileControler.calculateX(xAxisLength, 1)+55.0+2.5,
                VerticalProfileControler.calculateY(yAxisLength,
                VerticalProfile.DEFAULT_MINIMUM_HEIGHT,
                map.getMaximumHeight())+2.5+20.0, 5.0, 5.0);
        lowerRect.setFillPaint(Color.WHITE);
        lowerRect.setOutlinePaint(Color.BLUE);

        associationListLevel.put(lowerRect, lowerLevel);
        associationListLevel.put(upperRect, upperLevel);

        CPolyLine curve = new CPolyLine(lowerRect.getCenterX(),
                lowerRect.getCenterY());
        for(CShape shape : associationListLevel.keySet()) {
            if(!shape.equals(lowerRect)) {
                curve.lineTo(shape.getCenterX(), shape.getCenterY());
            }
        }
        curve.lineTo(upperRect.getCenterX(),
                upperRect.getCenterY()-20);
        curve.moveTo(55.0, panel.getHeight()-70.0);
        curve.curveTo((lowerRect.getCenterX()-55.0)/2+55.0,
                (panel.getHeight()-70.0-lowerRect.getCenterY())*
                4/5+lowerRect.getCenterY(), (lowerRect.getCenterX()-55.0)*
                2/3+55.0, (panel.getHeight()-70.0-lowerRect.getCenterY())*
                2/5+lowerRect.getCenterY(), lowerRect.getCenterX(),
                lowerRect.getCenterY());
        curve.setOutlinePaint(Color.RED);
        curve.setTransparencyFill(1);
        canvas.addShape(curve);

        associationListShapeGroup.put(curve, null);

        canvas.addShape(upperRect);
        canvas.addShape(lowerRect);

        CPolyLine upperLine = new CPolyLine(55.0, upperRect.getCenterY());
        CPolyLine lowerLine = new CPolyLine(55.0, lowerRect.getCenterY());
        upperLine.lineTo(55.0+xAxisLength, upperRect.getCenterY());
        lowerLine.lineTo(55.0+xAxisLength, lowerRect.getCenterY());

        canvas.addShape(upperLine);
        canvas.addShape(lowerLine);

        upperRect.above(upperLine);
        lowerRect.above(lowerLine);

        CText upperText = new CText(new Point2D.Double(5.0,
                upperRect.getCenterY()-5.0),
                ((Double)map.getMaximumHeight()).toString(),
                new Font("verdana", Font.PLAIN, 12));

        CText upperSpeedText = new CText(new Point2D.Double(
                upperRect.getCenterX(),
                panel.getHeight()-70.0),
                ((Double)Level.DEFAULT_SPEED).toString(),
                new Font("verdana", Font.PLAIN, 12));

        CText upperDirectionText = new CText(new Point2D.Double(upperRect.
                getCenterX()+15.0, upperRect.getCenterY()+15.0),
                ((Double)map.getDirection(upperLevel)).toString(),
                new Font("verdana", Font.PLAIN, 12));

        CText lowerText = new CText(new Point2D.Double(5.0,
                lowerRect.getCenterY()-5.0),
                ((Double)VerticalProfile.DEFAULT_MINIMUM_HEIGHT).toString(),
                new Font("verdana", Font.PLAIN, 12));

        CText lowerSpeedText = new CText(new Point2D.Double(
                lowerRect.getCenterX(),
                panel.getHeight()-70.0),
                ((Double)Level.DEFAULT_SPEED).toString(),
                new Font("verdana", Font.PLAIN, 12));

        CText lowerDirectionText = new CText(new Point2D.Double(lowerRect.
                getCenterX()+15.0, lowerRect.getCenterY()+15.0),
                ((Double)map.getDirection(lowerLevel)).toString(),
                new Font("verdana", Font.PLAIN, 12));

        canvas.addShape(upperText);
        canvas.addShape(lowerText);

        CText yLegend = new CText(new Point2D.Double(0.0, -2.0), "Height m",
                new Font("verdana", Font.PLAIN, 12));

        CText xLegend = new CText(new Point2D.Double(panel.getWidth()-110.0,
                panel.getHeight()-90.0), "Speed m/s",
                new Font("verdana", Font.PLAIN, 12));

        canvas.addShape(yLegend);
        canvas.addShape(xLegend);

        CPolyLine upperCompass = new CPolyLine(upperRect.getCenterX(),
                upperRect.getCenterY()-5.0);
        upperCompass.lineTo(upperRect.getCenterX(),
                upperRect.getCenterY()-20.0);
        CPolyLine upperArrow = upperCompass.getArrow(PI/6, 10);
        upperArrow.setFillPaint(Color.MAGENTA);

        CPolyLine lowerCompass = new CPolyLine(lowerRect.getCenterX(),
                lowerRect.getCenterY()-5.0);
        lowerCompass.lineTo(lowerRect.getCenterX(),
                lowerRect.getCenterY()-20.0);
        CPolyLine lowerArrow = lowerCompass.getArrow(PI/6, 10);
        lowerArrow.setFillPaint(Color.MAGENTA);

        canvas.addShape(upperCompass);
        canvas.addShape(lowerCompass);
        canvas.addShape(upperArrow);
        canvas.addShape(lowerArrow);

        List<CShape> upperList = new ArrayList();
        upperList.add(upperLine);
        upperList.add(upperText);
        upperList.add(upperSpeedText);
        upperList.add(upperDirectionText);
        upperList.add(upperCompass);
        upperList.add(upperArrow);
        associationListShapeGroup.put(upperRect, upperList);

        List<CShape> lowerList = new ArrayList();
        lowerList.add(lowerLine);
        lowerList.add(lowerText);
        lowerList.add(lowerSpeedText);
        lowerList.add(lowerDirectionText);
        lowerList.add(lowerCompass);
        lowerList.add(lowerArrow);
        associationListShapeGroup.put(lowerRect, lowerList);

        notToMoveList.add(xAxis);
        notToMoveList.add(yAxis);
        notToMoveList.add(upperLine);
        notToMoveList.add(upperText);
        notToMoveList.add(upperSpeedText);
        notToMoveList.add(upperDirectionText);
        notToMoveList.add(lowerLine);
        notToMoveList.add(lowerText);
        notToMoveList.add(lowerSpeedText);
        notToMoveList.add(lowerDirectionText);

        CStateMachine sm = new CStateMachine(canvas) {

            CElement toAct = null;
            Point2D lastPoint = null;
            CEllipse hystShape = null;
            Level newElement = null;
            CText toActText = null;
            CText toActText2 = null;
            CPolyLine toActCompass = null;
            CPolyLine toActArrow = null;

            /** The idle state */
            public State start = new State() {

                /** The click on an arrow. */
                Transition clickedCompass = new PressOnShape(
                        BUTTON1, ">> dragCompass") {

                            @Override
                            public boolean guard() {
                                return !associationListLevel.
                                        containsKey(getShape()) &&
                                        !associationListShapeGroup.
                                        containsKey(getShape()) &&
                                        !notToMoveList.contains(getShape());
                            }

                            @Override
                            public void action() {
                                CShape temp = getShape();
                                for(CShape shape : associationListShapeGroup.
                                        keySet()) {
                                    if(!(shape instanceof CPolyLine)) {
                                        if(associationListShapeGroup.get(shape).
                                                contains(temp)) {
                                            toActArrow = (CPolyLine)
                                                    associationListShapeGroup.
                                                    get(shape)
                                                    .get(
                                                    associationListShapeGroup
                                                    .get(shape).size()-1);
                                            toActCompass = (CPolyLine)
                                                    associationListShapeGroup.
                                                    get(shape)
                                                    .get(
                                                    associationListShapeGroup
                                                    .get(shape).size()-2);
                                            toActText = (CText)
                                                    associationListShapeGroup.
                                                    get(shape)
                                                    .get(
                                                    associationListShapeGroup
                                                    .get(shape).size()-3);
                                            toAct = (CRectangle)shape;
                                        }
                                    }
                                }
                                lastPoint = getPoint();
                            }
                        };

                /** A click over a free space. */
                Transition createLevel = new Press(
                        BUTTON1, ">> clickedLevel") {

                            @Override
                            public void action() {
                                lastPoint = getPoint();
                            }

                        };

                /** This transition is in charge of a click over the curve that
                 * join all the levels. */
                Transition createLevel2 = new PressOnShape(
                        BUTTON1, ">> clickedLevel") {

                            @Override
                            public boolean guard() {
                                return getShape() instanceof CPolyLine;
                            }

                            @Override
                            public void action() {
                                lastPoint = getPoint();
                            }

                        };

                /** A right click on a level. */
                Transition remove = new PressOnShape(BUTTON3) {

                    @Override
                    public boolean guard() {
                        return associationListLevel.
                                containsKey(getShape()) &&
                                getShape() instanceof CRectangle;
                    }

                    @Override
                    public void action() {
                        toAct = getShape();
                        double toDeleteAlt = associationListLevel.get(
                                (CShape)toAct).getHeight();
                        if(toDeleteAlt !=
                                map.getMaximumHeight()
                                && toDeleteAlt !=
                                VerticalProfile.DEFAULT_MINIMUM_HEIGHT) {
                            map.deleteLevel(toDeleteAlt);
                            associationListLevel.remove((CShape)toAct);
                            canvas.removeShape((CShape)toAct);
                            for(CShape element :
                                    associationListShapeGroup.get(
                                    (CShape)toAct)) {
                                if(notToMoveList.contains(element)) {
                                    notToMoveList.remove(element);
                                }
                                canvas.removeShape(element);
                            }
                            associationListShapeGroup.remove((CShape)toAct);
                        }

                        CShape getUpperRect = null;
                        CShape getLowerRect = null;

                        for(CShape shape : associationListLevel.keySet()) {
                            if(associationListLevel.get((CShape)shape).
                                    getHeight() == map.getMaximumHeight()) {
                                getUpperRect = shape;
                            }
                            else if(associationListLevel.get((CShape)shape).
                                    getHeight() == VerticalProfile.
                                    DEFAULT_MINIMUM_HEIGHT) {
                                getLowerRect = shape;
                            }
                        }

                        CPolyLine getCurve = null;

                        for(CShape shape : associationListShapeGroup.keySet()) {
                            if(shape instanceof CPolyLine) {
                                getCurve = (CPolyLine)shape;
                            }
                        }

                        double nextAlt;
                        double alt;
                        CShape nextShape = getUpperRect;

                        Set<CShape> set = new HashSet(associationListLevel.
                                keySet());

                        getCurve.reset(getLowerRect.getCenterX(),
                                getLowerRect.getCenterY());

                        do {
                            nextAlt = map.getMaximumHeight();
                            for(CShape shape : set) {
                                alt = map.getHeight(associationListLevel.
                                        get(shape));
                                if(alt < nextAlt && alt != VerticalProfile.
                                        DEFAULT_MINIMUM_HEIGHT && alt !=
                                        map.getMaximumHeight()) {
                                    nextAlt = alt;
                                    nextShape = shape;
                                }
                            }
                            getCurve.lineTo(nextShape.getCenterX(),
                                    nextShape.getCenterY());
                            set.remove(nextShape);
                        } while(nextAlt != map.getMaximumHeight());
                        getCurve.lineTo(getUpperRect.getCenterX(),
                                getUpperRect.getCenterY());
                        getCurve.lineTo(getUpperRect.getCenterX(),
                                getUpperRect.getCenterY()-20);
                        getCurve.moveTo(55.0, panel.getHeight()-70.0);
                        getCurve.curveTo((getLowerRect.getCenterX()-55.0)/
                                2+55.0, (panel.getHeight()-70.0-getLowerRect.
                                getCenterY())*4/5+getLowerRect.getCenterY(),
                                (getLowerRect.getCenterX()-55.0)*2/3+55.0,
                                (panel.getHeight()-70.0-getLowerRect.
                                getCenterY())*2/5+getLowerRect.getCenterY(),
                                getLowerRect.getCenterX(),
                                getLowerRect.getCenterY());
                    }

                };

                /** A left click on a level. */
                Transition press = new PressOnShape(BUTTON1, ">> waitHyst") {

                    @Override
                    public boolean guard() {
                        return !(getShape() instanceof CPolyLine);
                    }

                    @Override
                    public void action() {
                        toAct = getShape();
                        lastPoint = getPoint();
                        hystShape = new CEllipse(lastPoint.getX()-5,
                                lastPoint.getY()-5, 10, 10);
                        hystShape.setTransparencyFill(0);
                        hystShape.setTransparencyOutline(0);
                        canvas.addShape(hystShape);
                    }

                };

            };

            /** A left click then a drag and drop over an arrow. */
            public State dragCompass = new State() {

                @Override
                public void enter() {
                    toActCompass.setOutlinePaint(Color.ORANGE);
                    toActArrow.setFillPaint(Color.ORANGE);
                    canvas.addShape(toActText);
                    Cursor cursor = new Cursor(Cursor.HAND_CURSOR);
                    panel.setCursor(cursor);
                }

                @Override
                public void leave() {
                    toActCompass.setOutlinePaint(Color.BLACK);
                    toActArrow.setFillPaint(Color.MAGENTA);
                    canvas.removeShape(toActText);
                    Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    panel.setCursor(cursor);
                }

                Transition stop = new Release(BUTTON1, ">> start") {

                };

                Transition move = new Drag(BUTTON1) {

                    @Override
                    public void action() {
                        Point2D newPoint = getPoint();

                        double dx = newPoint.getX() -
                                (toAct.getCenterX() - 2.5);
                        double dy = newPoint.getY() -
                                (toAct.getCenterY() - 2.5);

                        double newDistance = Math.sqrt(Math.pow(dx, 2) +
                                Math.pow(dy, 2));
                        if(newDistance > 0) {
                            double lastDistance = Math.sqrt(
                                    Math.pow(lastPoint.getX()-
                                    (toAct.getCenterX()-2.5), 2) +
                                    Math.pow(lastPoint.getY()-
                                    (toAct.getCenterY()-2.5), 2));
                            double distance = Math.sqrt(
                                    Math.pow(newPoint.getX()-
                                    lastPoint.getX(), 2) +
                                    Math.pow(newPoint.getY()-
                                    lastPoint.getY(), 2));

                            double ds = Math.acos((Math.pow(newDistance, 2) +
                                    Math.pow(lastDistance, 2) -
                                    Math.pow(distance, 2))/
                                    (2*newDistance*lastDistance));

                            toActArrow.translateTo(toAct.getCenterX(),
                                    toAct.getCenterY());
                            toActCompass.translateTo(toAct.getCenterX(),
                                    toAct.getCenterY());

                            map.modifyDirection(associationListLevel.
                                    get((CShape)toAct), ds*180/PI);
                            double newDirection = map.getDirection(
                                    associationListLevel.
                                    get((CShape)toAct))*PI/180;

                            toActArrow.rotateTo(newDirection);
                            toActCompass.rotateTo(newDirection);

                            toActArrow.translateBy(15.0*Math.sin(newDirection),
                                    -15.0*Math.cos(newDirection));
                            toActCompass.translateBy(5.0*Math.sin(newDirection),
                                    -5.0*Math.cos(newDirection));

                            toAct.above(toActCompass);

                            String longDirection = ((Double)map.getDirection(
                                    associationListLevel.get(
                                    (CShape)toAct))).toString();
                            String printedDirection = longDirection;
                            int index = longDirection.indexOf(".");
                            if (index != -1) {
                                printedDirection = longDirection.substring(0,
                                        index+2);
                            }
                            toActText.setText(printedDirection + "°");
                        }

                        lastPoint = getPoint();
                    }

                };

            };

            /** The creation of a new level. */
            public State clickedLevel = new State() {

                Transition release = new Release(BUTTON1, ">> start") {

                    @Override
                    public void action() {
                        lastPoint = getPoint();
                        double newAlt = VerticalProfileControler.
                                calculateHeight(
                                yAxisLength, lastPoint.getY()-20.0-2.5,
                                map.getMaximumHeight());
                        double newSpeed = VerticalProfileControler.
                                calculateSpeed(
                                xAxisLength, lastPoint.getX()-55.0-2.5);
                        boolean available = true;
                        for(CShape shape : associationListLevel.keySet()) {
                            if(lastPoint.getY()-
                                    (shape.getCenterY()-2.5) < 15.0 &&
                                    lastPoint.getY()-(shape.getCenterY()-2.5) >
                                    -15.0) {
                                available = false;
                            }
                        }
                        if(newAlt <= map.getMaximumHeight() &&
                                newAlt >=
                                VerticalProfile.DEFAULT_MINIMUM_HEIGHT &&
                                newSpeed <= VerticalProfile.MAXIMUM_SPEED &&
                                newSpeed >= 0 && available) {
                            newElement = map.createLevel(newAlt, newSpeed);

                            CRectangle newRect = new CRectangle(
                                    lastPoint.getX()-2.5, lastPoint.getY()-2.5,
                                    5.0, 5.0);
                            newRect.setFillPaint(Color.WHITE);
                            newRect.setOutlinePaint(Color.RED);

                            CPolyLine newLine = new CPolyLine(55.0,
                                    newRect.getCenterY());
                            newLine.lineTo(55.0+xAxisLength,
                                    newRect.getCenterY());

                            canvas.addShape(newRect);
                            canvas.addShape(newLine);

                            newRect.above(newLine);

                            String longAlt = Double.toString(newAlt);
                            String printedAlt = longAlt;
                            int index = longAlt.indexOf(".");
                            if (index != -1) {
                                printedAlt = longAlt.substring(0, index+2);
                            }
                            CText altText = new CText(new Point2D.Double(5.0,
                                    newRect.getCenterY()-5.0),
                                    printedAlt,
                                    new Font("verdana", Font.PLAIN, 12));
                            canvas.addShape(altText);

                            CText speedText = new CText(new Point2D.Double(
                                    newRect.getCenterX(),
                                    panel.getHeight()-70.0),
                                    Double.toString(newSpeed),
                                    new Font("verdana", Font.PLAIN, 12));

                            associationListLevel.put(newRect, newElement);
                            List<CShape> groupShape = new ArrayList();
                            groupShape.add(newLine);
                            groupShape.add(altText);
                            groupShape.add(speedText);

                            CText directionText = new CText(new Point2D.Double(
                                    newRect.getCenterX()+15.0,
                                    newRect.getCenterY()+15.0),
                                    ((Double)map.getDirection(
                                    associationListLevel.get((CShape)newRect))).
                                    toString(), new Font("verdana",
                                    Font.PLAIN, 12));
                            groupShape.add(directionText);
                            associationListShapeGroup.put(newRect, groupShape);

                            notToMoveList.add(newLine);
                            notToMoveList.add(altText);
                            notToMoveList.add(speedText);
                            notToMoveList.add(directionText);

                            CShape getUpperRect = null;
                            CShape getLowerRect = null;

                            for(CShape shape : associationListLevel.keySet()) {
                                if(associationListLevel.get((CShape)shape).
                                        getHeight() == map.getMaximumHeight()) {
                                    getUpperRect = shape;
                                }
                                else if(associationListLevel.get((CShape)shape).
                                        getHeight() == VerticalProfile.
                                        DEFAULT_MINIMUM_HEIGHT) {
                                    getLowerRect = shape;
                                }
                            }

                            CPolyLine getCurve = null;

                            for(CShape shape : associationListShapeGroup.
                                    keySet()) {
                                if(shape instanceof CPolyLine) {
                                    getCurve = (CPolyLine)shape;
                                }
                            }

                            double nextAlt;
                            double alt;
                            CShape nextShape = getUpperRect;

                            Set<CShape> set = new HashSet(associationListLevel.
                                    keySet());

                            getCurve.reset(getLowerRect.getCenterX(),
                                    getLowerRect.getCenterY());

                            do {
                                nextAlt = map.getMaximumHeight();
                                for(CShape shape : set) {
                                    alt = map.getHeight(associationListLevel.
                                            get(shape));
                                    if(alt < nextAlt && alt != VerticalProfile.
                                            DEFAULT_MINIMUM_HEIGHT && alt !=
                                            map.getMaximumHeight()) {
                                        nextAlt = alt;
                                        nextShape = shape;
                                    }
                                }
                                getCurve.lineTo(nextShape.getCenterX(),
                                        nextShape.getCenterY());
                                set.remove(nextShape);
                            } while(nextAlt != map.getMaximumHeight());
                            getCurve.lineTo(getUpperRect.getCenterX(),
                                    getUpperRect.getCenterY());
                            getCurve.lineTo(getUpperRect.getCenterX(),
                                    getUpperRect.getCenterY()-20);
                            getCurve.moveTo(55.0, panel.getHeight()-70.0);
                            getCurve.curveTo((getLowerRect.getCenterX()-55.0)/
                                    2+55.0, (panel.getHeight()-70.0-
                                    getLowerRect.getCenterY())*4/5+
                                    getLowerRect.getCenterY(),
                                    (getLowerRect.getCenterX()-55.0)*2/3+55.0,
                                    (panel.getHeight()-70.0-getLowerRect.
                                    getCenterY())*2/5+getLowerRect.
                                    getCenterY(), getLowerRect.getCenterX(),
                                    getLowerRect.getCenterY());

                            CPolyLine newCompass = new CPolyLine(newRect.
                                    getCenterX(), newRect.getCenterY()-5.0);
                            newCompass.lineTo(newRect.getCenterX(),
                                    newRect.getCenterY()-20.0);
                            CPolyLine newArrow = newCompass.getArrow(3.14/6,
                                    10);
                            newArrow.setFillPaint(Color.MAGENTA);

                            associationListShapeGroup.get(newRect).
                                    add(newCompass);
                            associationListShapeGroup.get(newRect).
                                    add(newArrow);

                            canvas.addShape(newCompass);
                            canvas.addShape(newArrow);
                        }
                    }

                };

            };

            /** The hysteresis state. */
            public State waitHyst = new State() {

                Transition dragHandle = new LeaveOnShape(
                        ">> dragHandle") {

                    @Override
                    public boolean guard() {
                        return getShape() == hystShape
                                && associationListLevel.containsKey(
                                (CShape)toAct);
                    }

                    @Override
                    public void action() {
                        boolean result = false;
                        if(getShape() instanceof CRectangle) {
                            if(map.getHeight(associationListLevel.get(
                                    getShape())) == map.
                                    getMaximumHeight()) {
                                result = true;
                            }
                        }
                        Point2D newPoint = getPoint();
                        double dx = newPoint.getX()-lastPoint.getX();
                        double newSpeed = VerticalProfileControler.
                                calculateSpeed(xAxisLength,
                                newPoint.getX()-55.0-2.5);
                        if(newSpeed <= VerticalProfile.MAXIMUM_SPEED &&
                                newSpeed >= 0) {
                            List<CShape> list = new ArrayList(
                                    associationListShapeGroup.
                                    get((CShape)toAct));
                            for(CShape shape : list) {
                                if(shape instanceof CPolyLine) {
                                    toActArrow = (CPolyLine)shape;
                                }
                            }
                            list.remove(toActArrow);
                            for(CShape shape : list) {
                                if(shape instanceof CPolyLine) {
                                    toActCompass = (CPolyLine)shape;
                                }
                            }
                            toAct.translateBy(dx, 0.0);
                            toActCompass.translateBy(dx, 0.0);
                            toActArrow.translateBy(dx, 0.0);
                            map.setSpeed(
                                    associationListLevel.get(
                                    (CShape)toAct), newSpeed);

                            for(CShape shape :
                                    associationListShapeGroup.
                                    get((CShape)toAct)) {
                                if(shape instanceof CText &&
                                        shape.getCenterY() >=
                                        panel.getHeight()-70.0) {
                                    toActText = (CText)shape;
                                }
                            }

                            String longSpeed = Double.toString(
                                    newSpeed);
                            String printedSpeed = longSpeed;
                            int index = longSpeed.indexOf(".");
                            if (index != -1) {
                                printedSpeed = longSpeed.substring(0,
                                        index+2);
                            }
                            toActText.setText(printedSpeed);
                            toActText.translateBy(dx, 0.0);

                            toActText2 = (CText)
                                    associationListShapeGroup.
                                    get((CShape)toAct).get(
                                    associationListShapeGroup.
                                    get((CShape)toAct).size()-3);
                            toActText2.translateBy(dx, 0.0);
                        }


                        if(result) {
                            double oldMaximumHeight = map.
                                    getMaximumHeight();
                            double dh = calculateHeight(yAxisLength,
                                    newPoint.getY(), oldMaximumHeight)-
                                    calculateHeight(yAxisLength,
                                    lastPoint.getY(), oldMaximumHeight);
                            double newHeight = map.
                                    getMaximumHeight()+dh;
                            CShape minShape = null;
                            for(CShape shape : associationListLevel.
                                    keySet()) {
                                if(associationListLevel.get(shape).
                                        getHeight() == VerticalProfile.
                                        DEFAULT_MINIMUM_HEIGHT) {
                                    minShape = shape;
                                }
                            }
                            for(CShape shape : associationListLevel.
                                    keySet()) {
                                if(associationListLevel.get(shape).
                                        getHeight() >
                                        associationListLevel.
                                        get(minShape).getHeight() &&
                                        associationListLevel.get(shape).
                                        getHeight() != map.
                                        getMaximumHeight()) {
                                    minShape = shape;
                                }
                            }
                            if(newHeight <= VerticalProfile.
                                    DEFAULT_MAXIMUM_HEIGHT &&
                                    newHeight >= associationListLevel.
                                    get(minShape).getHeight()+30) {
                                map.modifyMaximumHeight(dh);
                                associationListLevel.get((CShape)toAct).
                                        setHeight(map.
                                        getMaximumHeight());

                                String longAlt = Double.toString(map.
                                        getHeight(associationListLevel.
                                        get((CShape)toAct)));
                                String printedAlt = longAlt;
                                int index = longAlt.indexOf(".");
                                if (index != -1) {
                                    printedAlt = longAlt.substring(0,
                                            index+2);
                                }
                                ((CText)associationListShapeGroup.
                                        get((CShape)toAct).
                                        get(associationListShapeGroup.
                                        get((CShape)toAct).size()-5)).
                                        setText(printedAlt);
                                for(CShape shape :
                                        associationListShapeGroup.
                                        keySet()) {
                                    if(!toAct.equals(shape) && shape
                                            instanceof CRectangle) {
                                        shape.translateTo(60.0+
                                                calculateX(xAxisLength,
                                                map.getSpeed(
                                                associationListLevel.
                                                get(shape))),
                                                calculateY(yAxisLength,
                                                map.getHeight(
                                                associationListLevel.
                                                get(shape)), newHeight)
                                                +2.5+20.0);
                                        associationListShapeGroup.
                                                get(shape).
                                                get(
                                                associationListShapeGroup.
                                                get(shape).size()-6).
                                                translateTo(55.0+
                                                xAxisLength/2,
                                                calculateY(yAxisLength,
                                                map.getHeight(
                                                associationListLevel.
                                                get(shape)),
                                                newHeight)+20.0+2.5);
                                        associationListShapeGroup.
                                                get(shape).
                                                get(
                                                associationListShapeGroup.
                                                get(shape).size()-5).
                                                translateTo(23.0,
                                                calculateY(yAxisLength,
                                                map.getHeight(
                                                associationListLevel.
                                                get(shape)), newHeight)
                                                +4+20.0);
                                        associationListShapeGroup.
                                                get(shape).
                                                get(
                                                associationListShapeGroup.
                                                get(shape).size()-3).
                                                translateTo(shape.
                                                getCenterX()+15.0,
                                                calculateY(yAxisLength,
                                                map.getHeight(
                                                associationListLevel.
                                                get(shape)), newHeight)
                                                +15+20.0);
                                        associationListShapeGroup.
                                                get(shape).
                                                get(
                                                associationListShapeGroup.
                                                get(shape).size()-2).
                                                translateTo(shape.
                                                getCenterX(),
                                                calculateY(yAxisLength,
                                                map.getHeight(
                                                associationListLevel.
                                                get(shape)), newHeight)
                                                -5.0+20.0);
                                        associationListShapeGroup.
                                                get(shape).
                                                get(
                                                associationListShapeGroup.
                                                get(shape).size()-1).
                                                translateTo(shape.
                                                getCenterX(),
                                                calculateY(yAxisLength,
                                                map.getHeight(
                                                associationListLevel.
                                                get(shape)), newHeight)
                                                -15.0+20.0);

                                        associationListShapeGroup.
                                                get(shape).
                                                get(
                                                associationListShapeGroup.
                                                get(shape).size()-1).
                                                translateTo(shape.
                                                getCenterX(),
                                                shape.getCenterY());
                                        associationListShapeGroup.
                                                get(shape).
                                                get(
                                                associationListShapeGroup.
                                                get(shape).size()-2).
                                                translateTo(shape.
                                                getCenterX(),
                                                shape.getCenterY());

                                        double newDirection = map.
                                                getDirection(
                                                associationListLevel.
                                                get(shape))*PI/180;

                                        associationListShapeGroup.
                                                get(shape).
                                                get(
                                                associationListShapeGroup.
                                                get(shape).size()-1).
                                                rotateTo(newDirection);
                                        associationListShapeGroup.
                                                get(shape).
                                                get(
                                                associationListShapeGroup.
                                                get(shape).size()-2).
                                                rotateTo(newDirection);

                                        associationListShapeGroup.
                                                get(shape).
                                                get(
                                                associationListShapeGroup.
                                                get(shape).size()-1).
                                                translateBy(15.0*
                                                Math.sin(newDirection),
                                                -15.0*Math.cos(
                                                newDirection));
                                        associationListShapeGroup.
                                                get(shape).
                                                get(
                                                associationListShapeGroup.
                                                get(shape).size()-2).
                                                translateBy(5.0*
                                                Math.sin(newDirection),
                                                -5.0*Math.cos(
                                                newDirection));

                                        shape.above(
                                                associationListShapeGroup.
                                                get(shape).
                                                get(
                                                associationListShapeGroup.
                                                get(shape).size()-2));
                                    }
                                }

                                CShape getLowerRect = null;
                                CShape getUpperRect = (CShape)toAct;

                                for(CShape shape : associationListLevel.
                                        keySet()) {
                                    if(associationListLevel.
                                            get((CShape)shape).
                                            getHeight() ==
                                            VerticalProfile.
                                            DEFAULT_MINIMUM_HEIGHT) {
                                        getLowerRect = shape;
                                    }
                                }

                                CPolyLine getCurve = null;

                                for(CShape shape :
                                        associationListShapeGroup.
                                        keySet()) {
                                    if(shape instanceof CPolyLine) {
                                        getCurve = (CPolyLine)shape;
                                    }
                                }

                                double nextAlt;
                                double alt;
                                CShape nextShape = getUpperRect;

                                Set<CShape> set = new HashSet(
                                        associationListLevel.
                                        keySet());

                                getCurve.reset(getLowerRect.
                                        getCenterX(),
                                        getLowerRect.getCenterY());

                                do {
                                    nextAlt = map.getMaximumHeight();
                                    for(CShape shape : set) {
                                        alt = map.getHeight(
                                                associationListLevel.
                                                get(shape));
                                        if(alt < nextAlt && alt !=
                                                VerticalProfile.
                                                DEFAULT_MINIMUM_HEIGHT
                                                && alt !=
                                                map.getMaximumHeight())
                                        {
                                            nextAlt = alt;
                                            nextShape = shape;
                                        }
                                    }
                                    getCurve.lineTo(
                                            nextShape.getCenterX(),
                                            nextShape.getCenterY());
                                    set.remove(nextShape);
                                } while(nextAlt != map.
                                        getMaximumHeight());
                                getCurve.lineTo(getUpperRect.
                                        getCenterX(),
                                        getUpperRect.getCenterY());
                                getCurve.lineTo(getUpperRect.
                                        getCenterX(),
                                        getUpperRect.getCenterY()-20);
                                getCurve.moveTo(55.0, panel.
                                        getHeight()-70.0);
                                getCurve.curveTo((getLowerRect.
                                        getCenterX()-55.0)/
                                        2+55.0, (panel.getHeight()-70.0-
                                        getLowerRect.getCenterY())*4/5+
                                        getLowerRect.getCenterY(),
                                        (getLowerRect.
                                        getCenterX()-55.0)*2/3+55.0,
                                        (panel.getHeight()-70.0-
                                        getLowerRect.
                                        getCenterY())*2/5+getLowerRect.
                                        getCenterY(), getLowerRect.
                                        getCenterX(),
                                        getLowerRect.getCenterY());
                            }
                        }
                        lastPoint = getPoint();
                        canvas.removeShape(hystShape);

                    }

                };

                Transition release = new Release(BUTTON1, ">> start") {

                    @Override
                    public void action() {
                        canvas.removeShape(hystShape);
                    }

                };

            };

            /** The move of a level. */
            public State dragHandle = new State() {

                @Override
                public void enter() {
                    toAct.setOutlinePaint(Color.ORANGE);
                    canvas.addShape(toActText);
                }

                @Override
                public void leave() {
                    if(associationListLevel.get((CShape)toAct).getHeight() !=
                            map.getMaximumHeight() &&
                            associationListLevel.get((CShape)toAct).
                            getHeight() !=
                            VerticalProfile.DEFAULT_MINIMUM_HEIGHT) {
                        toAct.setOutlinePaint(Color.RED);
                    }
                    else {
                        toAct.setOutlinePaint(Color.BLUE);
                    }
                    canvas.removeShape(toActText);
                }

                Transition stop = new Release(BUTTON1, ">> start") {

                    @Override
                    public void action() {
                        canvas.removeShape(hystShape);
                        Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
                        panel.setCursor(cursor);
                    }

                };

                Transition move = new Drag(BUTTON1) {

                    @Override
                    public void action() {
                        boolean result = false;
                        if(toAct instanceof CRectangle) {
                            if(map.getHeight(associationListLevel.
                                    get((CShape)toAct)) == map.
                                    getMaximumHeight()) {
                                result = true;
                            }
                        }
                        if(!result) {
                            Cursor cursor = new Cursor(Cursor.E_RESIZE_CURSOR);
                            panel.setCursor(cursor);
                        }
                        else {
                            Cursor cursor = new Cursor(Cursor.MOVE_CURSOR);
                            panel.setCursor(cursor);
                        }
                        Point2D newPoint = getPoint();
                        double dx = newPoint.getX()-lastPoint.getX();
                        double newSpeed = VerticalProfileControler.
                                calculateSpeed(xAxisLength,
                                newPoint.getX()-55.0-2.5);
                        if(newSpeed <= VerticalProfile.MAXIMUM_SPEED &&
                                newSpeed >= 0) {
                            toAct.translateBy(dx, 0.0);
                            toActArrow.translateBy(dx, 0.0);
                            toActCompass.translateBy(dx, 0.0);
                            map.setSpeed(
                                    associationListLevel.get((CShape)toAct),
                                    newSpeed);

                            CShape getUpperRect = null;
                            CShape getLowerRect = null;

                            for(CShape shape : associationListLevel.keySet()) {
                                if(associationListLevel.get((CShape)shape).
                                        getHeight() == map.getMaximumHeight()) {
                                    getUpperRect = shape;
                                }
                                else if(associationListLevel.get((CShape)shape).
                                        getHeight() == VerticalProfile.
                                        DEFAULT_MINIMUM_HEIGHT) {
                                    getLowerRect = shape;
                                }
                            }

                            CPolyLine getCurve = null;

                            for(CShape shape : associationListShapeGroup.
                                    keySet()) {
                                if(shape instanceof CPolyLine) {
                                    getCurve = (CPolyLine)shape;
                                }
                            }

                            double nextAlt;
                            double alt;
                            CShape nextShape = getUpperRect;

                            Set<CShape> set = new HashSet(associationListLevel.
                                    keySet());

                            getCurve.reset(getLowerRect.getCenterX(),
                                    getLowerRect.getCenterY());

                            do {
                                nextAlt = map.getMaximumHeight();
                                for(CShape shape : set) {
                                    alt = map.getHeight(associationListLevel.
                                            get(shape));
                                    if(alt < nextAlt && alt != VerticalProfile.
                                            DEFAULT_MINIMUM_HEIGHT && alt !=
                                            map.getMaximumHeight()) {
                                        nextAlt = alt;
                                        nextShape = shape;
                                    }
                                }
                                getCurve.lineTo(nextShape.getCenterX(),
                                        nextShape.getCenterY());
                                set.remove(nextShape);
                            } while(nextAlt != map.getMaximumHeight());
                            getCurve.lineTo(getUpperRect.getCenterX(),
                                    getUpperRect.getCenterY());
                            getCurve.lineTo(getUpperRect.getCenterX(),
                                    getUpperRect.getCenterY()-20);
                            getCurve.moveTo(55.0, panel.getHeight()-70.0);
                            getCurve.curveTo((getLowerRect.getCenterX()-55.0)/
                                    2+55.0, (panel.getHeight()-70.0-
                                    getLowerRect.getCenterY())*4/5+
                                    getLowerRect.getCenterY(),
                                    (getLowerRect.getCenterX()-55.0)*2/3+55.0,
                                    (panel.getHeight()-70.0-getLowerRect.
                                    getCenterY())*2/5+getLowerRect.
                                    getCenterY(), getLowerRect.getCenterX(),
                                    getLowerRect.getCenterY());

                            String longSpeed = Double.toString(newSpeed);
                            String printedSpeed = longSpeed;
                            int index = longSpeed.indexOf(".");
                            if (index != -1) {
                                printedSpeed = longSpeed.substring(0, index+2);
                            }
                            toActText.setText(printedSpeed);
                            toActText.translateBy(dx, 0.0);

                            toActText2 = (CText)associationListShapeGroup.get(
                                    (CShape)toAct).get(
                                    associationListShapeGroup.
                                    get((CShape)toAct).size()-3);
                            toActText2.translateBy(dx, 0.0);
                        }

                        if(result) {
                            double oldMaximumHeight = map.getMaximumHeight();
                            double dh = calculateHeight(yAxisLength, newPoint.
                                    getY(), oldMaximumHeight)-
                                    calculateHeight(yAxisLength, lastPoint.
                                    getY(), oldMaximumHeight);
                            double newHeight = map.getMaximumHeight()+dh;

                            CShape minShape = null;
                            for(CShape shape : associationListLevel.keySet()) {
                                if(associationListLevel.get(shape).getHeight()
                                        == VerticalProfile.
                                        DEFAULT_MINIMUM_HEIGHT) {
                                    minShape = shape;
                                }
                            }
                            for(CShape shape : associationListLevel.keySet()) {
                                if(associationListLevel.get(shape).getHeight()
                                        > associationListLevel.get(minShape).
                                        getHeight() && associationListLevel.
                                        get(shape).getHeight() != map.
                                        getMaximumHeight()) {
                                    minShape = shape;
                                }
                            }
                            if(calculateY(yAxisLength,
                                    map.getHeight(
                                    associationListLevel.
                                    get(minShape)),
                                    newHeight)+2.5+20.0-toAct.getCenterY() 
                                    > 15.0 && newHeight <= VerticalProfile.
                                    DEFAULT_MAXIMUM_HEIGHT) {
                                map.modifyMaximumHeight(dh);
                                associationListLevel.get((CShape)toAct).
                                        setHeight(map.getMaximumHeight());

                                String longAlt = Double.toString(map.
                                        getHeight(associationListLevel.
                                        get((CShape)toAct)));
                                String printedAlt = longAlt;
                                int index = longAlt.indexOf(".");
                                if (index != -1) {
                                    printedAlt = longAlt.substring(0, index+2);
                                }
                                ((CText)associationListShapeGroup.
                                        get((CShape)toAct).
                                        get(associationListShapeGroup.
                                        get((CShape)toAct).size()-5)).
                                        setText(printedAlt);
                                for(CShape shape : associationListShapeGroup.
                                        keySet()) {
                                    if(!toAct.equals(shape) && shape
                                            instanceof CRectangle) {
                                        shape.translateTo(60.0+
                                                calculateX(xAxisLength,
                                                map.getSpeed(
                                                associationListLevel.
                                                get(shape))),
                                                calculateY(yAxisLength,
                                                map.getHeight(
                                                associationListLevel.
                                                get(shape)),
                                                newHeight)+2.5+20.0);
                                        associationListShapeGroup.get(shape).
                                                get(associationListShapeGroup.
                                                get(shape).size()-6).
                                                translateTo(55.0+xAxisLength/2,
                                                calculateY(yAxisLength,
                                                map.getHeight(
                                                associationListLevel.
                                                get(shape)),
                                                newHeight)+20.0+2.5);
                                        associationListShapeGroup.get(shape).
                                                get(associationListShapeGroup.
                                                get(shape).size()-5).
                                                translateTo(23.0,
                                                calculateY(yAxisLength,
                                                map.getHeight(
                                                associationListLevel.
                                                get(shape)),
                                                newHeight)+4+20.0);
                                        associationListShapeGroup.get(shape).
                                                get(associationListShapeGroup.
                                                get(shape).size()-3).
                                                translateTo(shape.
                                                getCenterX()+15.0,
                                                calculateY(yAxisLength,
                                                map.getHeight(
                                                associationListLevel.
                                                get(shape)),
                                                newHeight)+15+20.0);
                                        associationListShapeGroup.get(shape).
                                                get(associationListShapeGroup.
                                                get(shape).size()-2).
                                                translateTo(shape.getCenterX(),
                                                calculateY(yAxisLength,
                                                map.getHeight(
                                                associationListLevel.
                                                get(shape)),
                                                newHeight)-5.0+20.0);
                                        associationListShapeGroup.get(shape).
                                                get(associationListShapeGroup.
                                                get(shape).size()-1).
                                                translateTo(shape.getCenterX(),
                                                calculateY(yAxisLength,
                                                map.getHeight(
                                                associationListLevel.
                                                get(shape)),
                                                newHeight)-15.0+20.0);

                                        associationListShapeGroup.get(shape).
                                                get(associationListShapeGroup.
                                                get(shape).size()-1).
                                                translateTo(shape.getCenterX(),
                                                shape.getCenterY());
                                        associationListShapeGroup.get(shape).
                                                get(associationListShapeGroup.
                                                get(shape).size()-2).
                                                translateTo(shape.getCenterX(),
                                                shape.getCenterY());

                                        double newDirection = map.getDirection(
                                                associationListLevel.
                                                get(shape))*PI/180;

                                        associationListShapeGroup.get(shape).
                                                get(associationListShapeGroup.
                                                get(shape).size()-1).
                                                rotateTo(newDirection);
                                        associationListShapeGroup.get(shape).
                                                get(associationListShapeGroup.
                                                get(shape).size()-2).
                                                rotateTo(newDirection);

                                        associationListShapeGroup.get(shape).
                                                get(associationListShapeGroup.
                                                get(shape).size()-1).
                                                translateBy(15.0*
                                                Math.sin(newDirection),
                                                -15.0*Math.cos(newDirection));
                                        associationListShapeGroup.get(shape).
                                                get(associationListShapeGroup.
                                                get(shape).size()-2).
                                                translateBy(5.0*
                                                Math.sin(newDirection),
                                                -5.0*Math.cos(newDirection));

                                        shape.above(associationListShapeGroup.
                                                get(shape).
                                                get(associationListShapeGroup.
                                                get(shape).size()-2));
                                    }
                                }

                                CShape getLowerRect = null;
                                CShape getUpperRect = (CShape)toAct;

                                for(CShape shape : associationListLevel.
                                        keySet()) {
                                    if(associationListLevel.get((CShape)shape).
                                            getHeight() == VerticalProfile.
                                            DEFAULT_MINIMUM_HEIGHT) {
                                        getLowerRect = shape;
                                    }
                                }

                                CPolyLine getCurve = null;

                                for(CShape shape : associationListShapeGroup.
                                        keySet()) {
                                    if(shape instanceof CPolyLine) {
                                        getCurve = (CPolyLine)shape;
                                    }
                                }

                                double nextAlt;
                                double alt;
                                CShape nextShape = getUpperRect;

                                Set<CShape> set = new HashSet(
                                        associationListLevel.keySet());

                                getCurve.reset(getLowerRect.getCenterX(),
                                        getLowerRect.getCenterY());

                                do {
                                    nextAlt = map.getMaximumHeight();
                                    for(CShape shape : set) {
                                        alt = map.getHeight(
                                                associationListLevel.
                                                get(shape));
                                        if(alt < nextAlt && alt !=
                                                VerticalProfile.
                                                DEFAULT_MINIMUM_HEIGHT && alt !=
                                                map.getMaximumHeight()) {
                                            nextAlt = alt;
                                            nextShape = shape;
                                        }
                                    }
                                    getCurve.lineTo(nextShape.getCenterX(),
                                            nextShape.getCenterY());
                                    set.remove(nextShape);
                                } while(nextAlt != map.getMaximumHeight());

                                getCurve.lineTo(getUpperRect.getCenterX(),
                                        getUpperRect.getCenterY());
                                getCurve.lineTo(getUpperRect.getCenterX(),
                                        getUpperRect.getCenterY()-20);
                                getCurve.moveTo(55.0, panel.getHeight()-70.0);
                                getCurve.curveTo((getLowerRect.getCenterX()-
                                        55.0)/2+55.0, (panel.getHeight()-70.0-
                                        getLowerRect.getCenterY())*4/5+
                                        getLowerRect.getCenterY(),
                                        (getLowerRect.getCenterX()-55.0)*2/3+
                                        55.0, (panel.getHeight()-70.0-
                                        getLowerRect.getCenterY())*2/5+
                                        getLowerRect.getCenterY(), 
                                        getLowerRect.getCenterX(),
                                        getLowerRect.getCenterY());
                            }

                        }
                        lastPoint = getPoint();
                        canvas.removeShape(hystShape);

                    }

                };

            };

        };

        final StateMachineVisualization smv =
                new StateMachineVisualization(sm);

        panel.setLayout(new GridLayout(1,1));
        panel.add(canvas);

    }

    /** Resets the vertical profile. */
    public void resetVerticalProfile() {
        Set<CShape> tempSet = new HashSet(associationListLevel.keySet());
        for(CShape shape : tempSet) {
            double toDeleteAlt = associationListLevel.get(
                    (CShape)shape).getHeight();
            if(toDeleteAlt !=
                    map.getMaximumHeight()
                    && toDeleteAlt !=
                    VerticalProfile.DEFAULT_MINIMUM_HEIGHT) {
                associationListLevel.remove((CShape)shape);
                canvas.removeShape((CShape)shape);
                for(CShape element :
                        associationListShapeGroup.get(
                        (CShape)shape)) {
                    if(notToMoveList.contains(element)) {
                        notToMoveList.remove(element);
                    }
                    canvas.removeShape(element);
                }
                associationListShapeGroup.remove((CShape)shape);
            }
        }
        map.resetVerticalProfile();

        CShape getUpperRect = null;
        CShape getLowerRect = null;

        for(CShape form : associationListLevel.keySet()) {
            if(associationListLevel.get((CShape)form).
                    getHeight() == map.getMaximumHeight()) {
                getUpperRect = form;
            }
            else if(associationListLevel.get((CShape)form).
                    getHeight() == VerticalProfile.
                    DEFAULT_MINIMUM_HEIGHT) {
                getLowerRect = form;
            }
        }

        map.setMaximumHeight(VerticalProfile.DEFAULT_MAXIMUM_HEIGHT);
        map.setHeight(associationListLevel.get(getUpperRect), VerticalProfile.
                DEFAULT_MAXIMUM_HEIGHT);
        map.setDirection(associationListLevel.get(getUpperRect), 
                Level.DEFAULT_DIRECTION);
        map.setSpeed(associationListLevel.get(getUpperRect), 
                Level.DEFAULT_SPEED);

        map.setHeight(associationListLevel.get(getLowerRect), 
                VerticalProfile.DEFAULT_MINIMUM_HEIGHT);
        map.setDirection(associationListLevel.get(getLowerRect), 
                Level.DEFAULT_DIRECTION);
        map.setSpeed(associationListLevel.get(getLowerRect), 
                Level.DEFAULT_SPEED);


        String longAlt = Double.toString(map.getHeight(associationListLevel.
                get(getUpperRect)));
        String printedAlt = longAlt;
        int index = longAlt.indexOf(".");
        if (index != -1) {
            printedAlt = longAlt.substring(0, index+2);
        }
        ((CText)associationListShapeGroup.get(getUpperRect).
                get(associationListShapeGroup.
                get(getUpperRect).size()-5)).setText(printedAlt);

        getUpperRect.translateTo(
                VerticalProfileControler.calculateX(xLength, 1)+
                60.0, 20.0+5);
        associationListShapeGroup.get(getUpperRect).
                get(associationListShapeGroup.get(getUpperRect).size()-3).
                translateTo(getUpperRect.getCenterX()+28.0,
                getUpperRect.getCenterY()+25.0);
        associationListShapeGroup.get(getUpperRect).
                get(associationListShapeGroup.get(getUpperRect).size()-4).
                translateTo(
                getUpperRect.getCenterX()+12.0,
                panel.getHeight()-63.0);

        getLowerRect.translateTo(
                VerticalProfileControler.calculateX(xLength, 1)+60.0,
                VerticalProfileControler.calculateY(yLength,
                VerticalProfile.DEFAULT_MINIMUM_HEIGHT,
                map.getMaximumHeight())+5+20.0);
        associationListShapeGroup.get(getLowerRect).
                get(associationListShapeGroup.get(getLowerRect).size()-6).
                translateTo(55.0+xLength/2, getLowerRect.getCenterY());
        associationListShapeGroup.get(getLowerRect).
                get(associationListShapeGroup.get(getLowerRect).size()-5).
                translateTo(23.0, getLowerRect.getCenterY()+2.0);
        associationListShapeGroup.get(getLowerRect).
                get(associationListShapeGroup.get(getLowerRect).size()-3).
                translateTo(getLowerRect.getCenterX()+28.0,
                getLowerRect.getCenterY()+25.0);
        associationListShapeGroup.get(getLowerRect).
                get(associationListShapeGroup.get(getLowerRect).size()-4).
                translateTo(
                getLowerRect.getCenterX()+12.0,
                panel.getHeight()-63.0);

        CPolyLine getCurve = null;

        for(CShape form : associationListShapeGroup.keySet()) {
            if(form instanceof CPolyLine) {
                getCurve = (CPolyLine)form;
            }
        }

        double nextAlt;
        double alt;
        CShape nextShape = getUpperRect;

        Set<CShape> set = new HashSet(associationListLevel.keySet());

        getCurve.reset(getLowerRect.getCenterX(),
                getLowerRect.getCenterY());

        do {
            nextAlt = map.getMaximumHeight();
            for(CShape form : set) {
                alt = map.getHeight(associationListLevel.
                        get(form));
                if(alt < nextAlt && alt != VerticalProfile.
                        DEFAULT_MINIMUM_HEIGHT && alt !=
                        map.getMaximumHeight()) {
                    nextAlt = alt;
                    nextShape = form;
                }
            }
            getCurve.lineTo(nextShape.getCenterX(),
                    nextShape.getCenterY());
            set.remove(nextShape);
        } while(nextAlt != map.getMaximumHeight());

        getCurve.lineTo(getUpperRect.getCenterX(),
                getUpperRect.getCenterY());
        getCurve.lineTo(getUpperRect.getCenterX(),
                getUpperRect.getCenterY()-20);
        getCurve.moveTo(55.0, panel.getHeight()-70.0);
        getCurve.curveTo((getLowerRect.getCenterX()-55.0)/
                2+55.0, (panel.getHeight()-70.0-getLowerRect.
                getCenterY())*4/5+getLowerRect.getCenterY(),
                (getLowerRect.getCenterX()-55.0)*2/3+55.0,
                (panel.getHeight()-70.0-getLowerRect.
                getCenterY())*2/5+getLowerRect.getCenterY(),
                getLowerRect.getCenterX(),
                getLowerRect.getCenterY());

        CPolyLine toActUpperArrow = (CPolyLine)
                associationListShapeGroup.get(getUpperRect)
                .get(associationListShapeGroup
                .get(getUpperRect).size()-1);
        CPolyLine toActUpperCompass = (CPolyLine)
                associationListShapeGroup.get(getUpperRect)
                .get(associationListShapeGroup
                .get(getUpperRect).size()-2);

        CPolyLine toActLowerArrow = (CPolyLine)
                associationListShapeGroup.get(getLowerRect)
                .get(associationListShapeGroup
                .get(getLowerRect).size()-1);
        CPolyLine toActLowerCompass = (CPolyLine)
                associationListShapeGroup.get(getLowerRect)
                .get(associationListShapeGroup
                .get(getLowerRect).size()-2);

        CText toActUpperText = (CText)
                associationListShapeGroup.get(getUpperRect)
                .get(associationListShapeGroup
                .get(getUpperRect).size()-3);
        CText toActLowerText = (CText)
                associationListShapeGroup.get(getLowerRect)
                .get(associationListShapeGroup
                .get(getLowerRect).size()-3);

        toActUpperArrow.translateTo(getUpperRect.getCenterX(),
                getUpperRect.getCenterY());
        toActUpperCompass.translateTo(getUpperRect.getCenterX(),
                getUpperRect.getCenterY());

        toActUpperArrow.rotateTo(0.0);
        toActUpperCompass.rotateTo(0.0);

        toActUpperArrow.translateBy(0.0, -15.0);
        toActUpperCompass.translateBy(0.0, -5.0);

        toActLowerArrow.translateTo(getLowerRect.getCenterX(),
                getLowerRect.getCenterY());
        toActLowerCompass.translateTo(getLowerRect.getCenterX(),
                getLowerRect.getCenterY());

        toActLowerArrow.rotateTo(0.0);
        toActLowerCompass.rotateTo(0.0);

        toActLowerArrow.translateBy(0.0, -15.0);
        toActLowerCompass.translateBy(0.0, -5.0);

        toActUpperText.setText(((Double)0.0).toString());
        toActLowerText.setText(((Double)0.0).toString());

        map.setDirection(associationListLevel.get(getUpperRect), 0.0);
        map.setDirection(associationListLevel.get(getLowerRect), 0.0);

        getUpperRect.above(toActUpperCompass);
        getLowerRect.above(toActLowerCompass);
    }

    /**
     * Loads a formerly created environment.
     */
    public void loadView() {
        CShape getUpperRect = null;
        CShape getLowerRect = null;

        for(CShape shape : associationListLevel.keySet()) {
            if(associationListLevel.get((CShape)shape).
                    getHeight() == map.getMaximumHeight()) {
                getUpperRect = shape;
            }
            else if(associationListLevel.get((CShape)shape).
                    getHeight() == VerticalProfile.
                    DEFAULT_MINIMUM_HEIGHT) {
                getLowerRect = shape;
            }
        }

        for(Level level : map.getLevelList().values()) {
            double newAlt = map.getHeight(level);
            double newSpeed = map.getSpeed(level);
            if(newAlt < map.getMaximumHeight() &&
                    newAlt >
                    VerticalProfile.DEFAULT_MINIMUM_HEIGHT) {
                CRectangle newRect = new CRectangle(
                        VerticalProfileControler.calculateX(xLength, newSpeed)+
                        60.0,
                        VerticalProfileControler.calculateY(yLength, newAlt, 
                        map.getMaximumHeight())+20.0, 5.0, 5.0);
                newRect.setFillPaint(Color.WHITE);
                newRect.setOutlinePaint(Color.RED);

                CPolyLine newLine = new CPolyLine(55.0,
                        newRect.getCenterY());
                newLine.lineTo(55.0+xLength,
                        newRect.getCenterY());

                canvas.addShape(newRect);
                canvas.addShape(newLine);

                newRect.above(newLine);

                String longAlt = Double.toString(newAlt);
                String printedAlt = longAlt;
                int index = longAlt.indexOf(".");
                if (index != -1) {
                    printedAlt = longAlt.substring(0, index+2);
                }
                CText altText = new CText(new Point2D.Double(5.0,
                        newRect.getCenterY()-5.0),
                        printedAlt,
                        new Font("verdana", Font.PLAIN, 12));
                canvas.addShape(altText);

                CText speedText = new CText(new Point2D.Double(
                        newRect.getCenterX(),
                        panel.getHeight()-70.0),
                        Double.toString(newSpeed),
                        new Font("verdana", Font.PLAIN, 12));

                associationListLevel.put(newRect, level);
                List<CShape> groupShape = new ArrayList();
                groupShape.add(newLine);
                groupShape.add(altText);
                groupShape.add(speedText);

                CText directionText = new CText(new Point2D.Double(
                        newRect.getCenterX()+15.0,
                        newRect.getCenterY()+15.0),
                        ((Double)map.getDirection(level)).
                        toString(), new Font("verdana",
                        Font.PLAIN, 12));
                groupShape.add(directionText);
                associationListShapeGroup.put(newRect, groupShape);

                notToMoveList.add(newLine);
                notToMoveList.add(altText);
                notToMoveList.add(speedText);
                notToMoveList.add(directionText);

                CPolyLine newCompass = new CPolyLine(newRect.
                        getCenterX(), newRect.getCenterY()-5.0);
                newCompass.lineTo(newRect.getCenterX(),
                        newRect.getCenterY()-20.0);
                CPolyLine newArrow = newCompass.getArrow(3.14/6,
                        10);
                newArrow.setFillPaint(Color.MAGENTA);

                associationListShapeGroup.get(newRect).
                        add(newCompass);
                associationListShapeGroup.get(newRect).
                        add(newArrow);

                canvas.addShape(newCompass);
                canvas.addShape(newArrow);

                newArrow.translateTo(newRect.getCenterX(),
                        newRect.getCenterY());
                newCompass.translateTo(newRect.getCenterX(),
                        newRect.getCenterY());

                double newDirection = map.getDirection(level)*PI/180;

                newArrow.rotateTo(newDirection);
                newCompass.rotateTo(newDirection);

                newArrow.translateBy(15.0*Math.sin(newDirection),
                        -15.0*Math.cos(newDirection));
                newCompass.translateBy(5.0*Math.sin(newDirection),
                        -5.0*Math.cos(newDirection));

                newRect.above(newCompass);
            }
            else if(newAlt == VerticalProfile.DEFAULT_MINIMUM_HEIGHT) {
                CPolyLine toActLine = (CPolyLine)associationListShapeGroup.
                        get(getLowerRect).get(associationListShapeGroup.
                        get(getLowerRect).size()-6);
                CText toActSpeedText = (CText)associationListShapeGroup.
                        get(getLowerRect).get(associationListShapeGroup.
                        get(getLowerRect).size()-4);
                CText toActText = (CText)associationListShapeGroup.
                        get(getLowerRect).get(associationListShapeGroup.
                        get(getLowerRect).size()-5);
                CText toActDirectionText = (CText)associationListShapeGroup.
                        get(getLowerRect).get(associationListShapeGroup.
                        get(getLowerRect).size()-3);
                CPolyLine toActCompass = (CPolyLine)associationListShapeGroup.
                        get(getLowerRect).get(associationListShapeGroup.
                        get(getLowerRect).size()-2);
                CPolyLine toActArrow = (CPolyLine)associationListShapeGroup.
                        get(getLowerRect).get(associationListShapeGroup.
                        get(getLowerRect).size()-1);

                getLowerRect.translateTo(
                        VerticalProfileControler.
                        calculateX(xLength, newSpeed)+60.0,
                        VerticalProfileControler.
                        calculateY(yLength, newAlt, 
                        map.getMaximumHeight())+2.5+20.0);

                toActLine.translateTo(55.0+xLength/2, getLowerRect.getCenterY());

                toActDirectionText.translateTo(getLowerRect.getCenterX()+15.0,
                        getLowerRect.getCenterY()+15.0);

                toActText.translateTo(5.0+18.0,
                        getLowerRect.getCenterY());

                toActSpeedText.translateTo(getLowerRect.getCenterX()+12.0, 
                        panel.getHeight()-63.0);
                toActSpeedText.setText(Double.toString(newSpeed));

                toActArrow.translateTo(getLowerRect.getCenterX(),
                        getLowerRect.getCenterY());
                toActCompass.translateTo(getLowerRect.getCenterX(),
                        getLowerRect.getCenterY());

                double newDirection = map.getDirection(level)*PI/180;

                toActArrow.rotateTo(newDirection);
                toActCompass.rotateTo(newDirection);

                toActArrow.translateBy(15.0*Math.sin(newDirection),
                        -15.0*Math.cos(newDirection));
                toActCompass.translateBy(5.0*Math.sin(newDirection),
                        -5.0*Math.cos(newDirection));

                getLowerRect.above(toActCompass);
            }
            else if(newAlt == map.getMaximumHeight()) {
                CText toActSpeedText = (CText)associationListShapeGroup.
                        get(getUpperRect).
                        get(associationListShapeGroup.
                        get(getUpperRect).size()-4);
                CText toActText = (CText)associationListShapeGroup.
                        get(getUpperRect).
                        get(associationListShapeGroup.
                        get(getUpperRect).size()-5);
                CText toActDirectionText = (CText)associationListShapeGroup.
                        get(getUpperRect).
                        get(associationListShapeGroup.
                        get(getUpperRect).size()-3);
                CPolyLine toActCompass = (CPolyLine)associationListShapeGroup.
                        get(getUpperRect).get(associationListShapeGroup.
                        get(getUpperRect).size()-2);
                CPolyLine toActArrow = (CPolyLine)associationListShapeGroup.
                        get(getUpperRect).get(associationListShapeGroup.
                        get(getUpperRect).size()-1);

                getUpperRect.translateTo(
                        VerticalProfileControler.calculateX(
                        xLength, newSpeed)+60.0, 20.0+5.0);

                toActDirectionText.translateTo(getUpperRect.getCenterX()+15.0,
                        getUpperRect.getCenterY()+15.0);

                String longAlt = Double.toString(newAlt);
                String printedAlt = longAlt;
                int index = longAlt.indexOf(".");
                if (index != -1) {
                    printedAlt = longAlt.substring(0, index+2);
                }
                toActText.setText(printedAlt);

                toActSpeedText.translateTo(getUpperRect.getCenterX()+12.0, 
                        panel.getHeight()-63.0);
                toActSpeedText.setText(Double.toString(newSpeed));

                toActArrow.translateTo(getUpperRect.getCenterX(),
                        getUpperRect.getCenterY());
                toActCompass.translateTo(getUpperRect.getCenterX(),
                        getUpperRect.getCenterY());

                double newDirection = map.getDirection(level)*PI/180;

                toActArrow.rotateTo(newDirection);
                toActCompass.rotateTo(newDirection);

                toActArrow.translateBy(15.0*Math.sin(newDirection),
                        -15.0*Math.cos(newDirection));
                toActCompass.translateBy(5.0*Math.sin(newDirection),
                        -5.0*Math.cos(newDirection));

                getUpperRect.above(toActCompass);
            }
        }

        CPolyLine getCurve = null;

        for(CShape shape : associationListShapeGroup.
                keySet()) {
            if(shape instanceof CPolyLine) {
                getCurve = (CPolyLine)shape;
            }
        }

        double nextAlt;
        double alt;
        CShape nextShape = getUpperRect;

        Set<CShape> set = new HashSet(associationListLevel.
                keySet());

        getCurve.reset(getLowerRect.getCenterX(),
                getLowerRect.getCenterY());

        do {
            nextAlt = map.getMaximumHeight();
            for(CShape shape : set) {
                alt = map.getHeight(associationListLevel.
                        get(shape));
                if(alt < nextAlt && alt != VerticalProfile.
                        DEFAULT_MINIMUM_HEIGHT && alt !=
                        map.getMaximumHeight()) {
                    nextAlt = alt;
                    nextShape = shape;
                }
            }
            getCurve.lineTo(nextShape.getCenterX(),
                    nextShape.getCenterY());
            set.remove(nextShape);
        } while(nextAlt != map.getMaximumHeight());
        getCurve.lineTo(getUpperRect.getCenterX(),
                getUpperRect.getCenterY());
        getCurve.lineTo(getUpperRect.getCenterX(),
                getUpperRect.getCenterY()-20);
        getCurve.moveTo(55.0, panel.getHeight()-70.0);
        getCurve.curveTo((getLowerRect.getCenterX()-55.0)/
                2+55.0, (panel.getHeight()-70.0-
                getLowerRect.getCenterY())*4/5+
                getLowerRect.getCenterY(),
                (getLowerRect.getCenterX()-55.0)*2/3+55.0,
                (panel.getHeight()-70.0-getLowerRect.
                getCenterY())*2/5+getLowerRect.
                getCenterY(), getLowerRect.getCenterX(),
                getLowerRect.getCenterY());
    }
}
