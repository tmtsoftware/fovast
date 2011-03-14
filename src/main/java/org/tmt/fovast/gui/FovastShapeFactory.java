/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.gui;

import diva.canvas.Figure;
import diva.canvas.event.LayerAdapter;
import diva.canvas.event.LayerEvent;
import diva.canvas.interactor.BasicGrabHandleFactory;
import diva.canvas.interactor.BoundsManipulator;
import diva.canvas.interactor.CircleGeometry;
import diva.canvas.interactor.CompositeInteractor;
import diva.canvas.interactor.Interactor;
import diva.canvas.interactor.SelectionInteractor;
import diva.util.java2d.Polygon2D;
import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.util.HashMap;
import javax.vecmath.Color3b;
import jsky.coords.CoordinateConverter;
import jsky.graphics.CanvasFigure;
import jsky.graphics.CanvasFigureGroup;
import jsky.graphics.CanvasGraphics;
import jsky.image.graphics.DivaImageGraphics;
import jsky.image.graphics.RectangleManipulator;
import jsky.image.graphics.RotatableCanvasFigure;
import jsky.image.graphics.ShapeUtil;
import org.tmt.fovast.instrumentconfig.Config;

/**
 *
 */
class FovastShapeFactory {

    private final static String FIGURE_TYPE = "figureType";

    private final static String FIGURE_TYPE_RECTANGLE = "rectangle";

    private final static String FIGURE_TYPE_CIRCLE = "circle";

    private final static String FIGURE_TYPE_NFIRAOS_ASTERISM = "nfiraos.lsgasterism";

    private final static String FIGURE_TYPE_OIWFS_PROBE = "iris.oiwfs.probe";

    private final static String ROTATABLE = "rotatable";

    private final static String MOVEABLE = "moveable";

    private final static String DRAW_OUTLINE = "drawOutline";

    private final static boolean DRAW_OUTLINE_YES = true;

    private final static boolean DRAW_OUTLINE_NO = false;

    private final static String OUTLINE_WIDTH = "outlineWidth";
    
    private final static String FILL = "fillOutline";

    private final static boolean FILL_OUTLINE_YES = true;

    private final static boolean FILL_OUTLINE_NO = false;

    private final static String CENTER_OFFSET_X = "centerOffsetX";

    private final static String CENTER_OFFSET_Y = "centerOffsetY";

    private final static String WIDTH_X = "widthX";

    private final static String WIDTH_Y = "widthY";

    private final static String RADIUS = "radius";

    private final static String OUTLINE_COLOR = "outlineColor";

    private final static String FILL_COLOR = "fillColor";

    private final static String MAJORAXIS_X = "majorAxisX";

    private final static String MAJORAXIS_Y = "majorAxisY";

    private final static String START_ANGLE = "startAngle";

    private final static String STOP_ANGLE = "stopAngle";

    private final FovastImageDisplay fovastImageDisplay;

    private Config config;

    FovastShapeFactory(FovastImageDisplay fovastImageDisplay) {
        this.fovastImageDisplay = fovastImageDisplay;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public CanvasFigure[] makeFigure(HashMap<String, Object> props) {
       String figType = (String) props.get(FIGURE_TYPE);
       CoordinateConverter cc = fovastImageDisplay.getCoordinateConverter();
       CanvasGraphics cg = fovastImageDisplay.getCanvasGraphics();
       DivaImageGraphics dig = (DivaImageGraphics)cg;
       CanvasFigure fig = null;
       
       float outlineWidth = (Float)props.get(OUTLINE_WIDTH);
       boolean drawOutline = (Boolean)props.get(DRAW_OUTLINE);
       Color outlineColor = null;
       if(drawOutline)
           outlineColor = (Color)props.get(OUTLINE_COLOR);
       boolean fill = (Boolean)props.get(FILL);
       Color fillColor = null;
       if(fill)
           fillColor = (Color)props.get(FILL_COLOR);

       Boolean rotatable = (Boolean) props.get(ROTATABLE);
       Boolean moveable = (Boolean) props.get(MOVEABLE);
       if(rotatable == null)
           rotatable = false;
       if(moveable == null)
           moveable = false;
       Interactor interactor = null;
//        RectangleManipulator rectangleManipulator = new RectangleManipulator(new BasicGrabHandleFactory());
//        rectangleManipulator.getHandleInteractor().addLayerListener(new LayerAdapter() {
//
//            public void mouseReleased(LayerEvent e) {
//                Figure fig = e.getFigureSource();
//                if (fig instanceof CanvasFigure) {
//                    ((CanvasFigure) fig).fireCanvasFigureEvent(CanvasFigure.RESIZED);
//                }
//            }
//        });
//
//        // Create a movable, resizable selection interactor
//        SelectionInteractor roiSelectionInteractor = new SelectionInteractor();
//        roiSelectionInteractor.setPrototypeDecorator(rectangleManipulator);
//        // connect the different selection models
//        roiSelectionInteractor.setSelectionModel(dig.getSelectionInteractor().getSelectionModel());
//        interactor = roiSelectionInteractor;
       // roiSelectionInteractor.addInteractor(_dragInteractor);

       if(rotatable && moveable) {
           interactor = makeRoiSelectionInteractor(dig);
       } else if(rotatable) {           
           interactor = makeRoiSelectionInteractor(dig);
           //remove drag interactor so that move does not happen.
           ((CompositeInteractor)interactor).removeInteractor(
                   dig.getDragInteractor());
       } else if(moveable) {
           interactor = dig.getDragInteractor();
       }
       
       if(figType.equals(FIGURE_TYPE_RECTANGLE)) {
           double width = (Double)props.get(WIDTH_X);
           double height = (Double)props.get(WIDTH_Y);
           double x = (Double)props.get(CENTER_OFFSET_X);
           double y = (Double)props.get(CENTER_OFFSET_X);
           Point2D.Double wcsCenter = cc.getWCSCenter();
           x = wcsCenter.x + x;           
           y = wcsCenter.y + y;
           Point2D.Double pt = new Point2D.Double(x, y);
           cc.worldToScreenCoords(pt, false);
           x = pt.x;
           y = pt.y;
           pt = new Point2D.Double(width, height);
           cc.worldToScreenCoords(pt, true);
           width = pt.x;
           height = pt.y;
           x = x - width/2;
           y = y - height/2;
           
           Rectangle2D.Double rect = new Rectangle2D.Double(x, y, width, height);

           fig = dig.makeFigure(rect, fillColor, outlineColor, outlineWidth,
                   interactor);
           //turns off resizing
           if(fig instanceof RotatableCanvasFigure) {
                ((RotatableCanvasFigure)fig).setResizable(false);
           }
           dig.add(fig);
           fig.setVisible(false);
           return new CanvasFigure[]{fig};


       } else if(figType.equals(FIGURE_TYPE_CIRCLE)) {
           double radius = (Double)props.get(RADIUS);
           double x = (Double)props.get(CENTER_OFFSET_X);
           double y = (Double)props.get(CENTER_OFFSET_X);
           Point2D.Double wcsCenter = cc.getWCSCenter();
           x = wcsCenter.x + x;
           y = wcsCenter.y + y;
           Point2D.Double pt = new Point2D.Double(x, y);
           cc.worldToScreenCoords(pt, false);
           x = pt.x;
           y = pt.y;
           pt = new Point2D.Double(radius, radius);
           cc.worldToScreenCoords(pt, true);
           radius = pt.x;
           x = x - radius/2;
           y = y - radius/2;
           Ellipse2D.Double ell = new Ellipse2D.Double(x, y, radius, radius);

           fig = dig.makeFigure(ell, fillColor, outlineColor, outlineWidth,
                   interactor);
           //turns off resizing
           if(fig instanceof RotatableCanvasFigure) {
                ((RotatableCanvasFigure)fig).setResizable(false);
           }
           dig.add(fig);
           fig.setVisible(false);
           return new CanvasFigure[]{fig};

       } else if(figType.equals(FIGURE_TYPE_NFIRAOS_ASTERISM)) {
           //we have to draw 6 cricles with one at center and others on a pentagon
           //of radius 35 arc sec
           //double pentagonRadius = props.get(RADIUS);
           double radius = (Double)props.get(RADIUS);
           double x = (Double)props.get(CENTER_OFFSET_X);
           double y = (Double)props.get(CENTER_OFFSET_X);
           Point2D.Double wcsCenter = cc.getWCSCenter();
           x = wcsCenter.x + x;
           y = wcsCenter.y + y;
           Point2D.Double pt = new Point2D.Double(x, y);
           cc.worldToScreenCoords(pt, false);
           x = pt.x;
           y = pt.y;

           pt = new Point2D.Double(radius, radius);
           cc.worldToScreenCoords(pt, true);
           radius = pt.x;
           
           double triangleRadius1 = 10/3600d; //10 arc sec
           pt = new Point2D.Double(triangleRadius1, triangleRadius1);
           cc.worldToScreenCoords(pt, true);
           triangleRadius1 = pt.x;
           
           double triangleRadius2 = 4/3600d; //10 arc sec
           pt = new Point2D.Double(triangleRadius2, triangleRadius2);
           cc.worldToScreenCoords(pt, true);
           triangleRadius2 = pt.x;


           CanvasFigure[] stars = new CanvasFigure[6];
           //CanvasFigureGroup starGroup = dig.makeFigureGroup();
           //assuming pentagon points north ..
           //starts are at 18, 90, 162, 264, 306 degrees from x+ve axis.
           for(int i=0; i<stars.length; i++)  {
               double tmpX = 0;
               double tmpY = 0;
               if(i==0) { //center star
                  tmpX = x;
                  tmpY = y;
               } else { //other stars on pentagon
                  double angle = Math.toRadians(18 + 72*(i-1));
                  tmpX = x + radius * Math.cos(angle);
                  //- as swing origin is at left north
                  tmpY = y - radius * Math.sin(angle);
               }

               //now draw a star at tmpX. tmpY
               //start would have 6 vertices
               int vertexCount = 5;
               double[] resX = new double[vertexCount*2];
               double[] resY = new double[vertexCount*2];
               double addAngle=2*Math.PI/vertexCount;
               double startAngle = Math.PI/2; // we want one vertex pointed up ..
               double angle=startAngle;
               double innerAngle=startAngle+Math.PI/vertexCount;
               for (int vi=0; vi<vertexCount; vi++) {
                    resX[vi*2] = tmpX + triangleRadius1*Math.cos(angle);
                    resY[vi*2] = tmpY - triangleRadius1*Math.sin(angle) ;
                    angle+=addAngle;
                    resX[vi*2+1] = tmpX + triangleRadius2*Math.cos(innerAngle);
                    resY[vi*2+1] = tmpY - triangleRadius2*Math.sin(innerAngle);
                    innerAngle+=addAngle;
               }
               double[] res = new double[vertexCount*4];
               for(int vi=0,viNew=0; vi<vertexCount*2; vi++,viNew=viNew+2) {
                   res[viNew]=resX[vi];
                   res[viNew+1]=resY[vi];
               }

               Polygon2D.Double polygon = new Polygon2D.Double(res);
               stars[i] = dig.makeFigure(polygon, fillColor,
                       outlineColor, outlineWidth, interactor);
               if(stars[i] instanceof RotatableCanvasFigure) {
                   ((RotatableCanvasFigure)stars[i]).setResizable(false);
               }
               dig.add(stars[i]);
               stars[i].setVisible(false);
               //starGroup.add(stars[i]);
           }

           //dig.add(starGroup);
           //starGroup.setVisible(false);
           return stars;

       }
       else {
           return null;
       }
    }

    public void clearFigures() {
       // throw new UnsupportedOperationException("Not yet implemented");
    }

    public HashMap<String, CanvasFigure[]> makeFigures() {
        //Group1
        //------------
        //nfiraos.limits
        //nfiraos.lsgasterism (always fixed)
        //nfiraos.twfs (moveable & constrained ..)
        //nfiraos.acqusitionCameraLimits
        //
        //iris.sciencedetector
        //iris.ifuimager.lenslet
        //iris.ifuimager.slicer
        //iris.oiwfs.probex.arm  (moveable & constrained ..)
        //iris.oiwfs.probex.limits
        //iris.oiwfs.probex.vignettingLimits
        //
        //Group2
        //----------------
        //mobie.detector
        //mobie.vignettingstart
        //mobie.edgeoffield
        //
        //US-82 and above will be left over

        HashMap<String, CanvasFigure[]> map = new HashMap<String, CanvasFigure[]>();

        //make fig-grp1
        DivaImageGraphics dig = (DivaImageGraphics) fovastImageDisplay.getCanvasGraphics();
        CanvasFigureGroup cfgIris = dig.makeFigureGroup();

        //nfiraos.limits
        HashMap<String, Object> props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 2/60d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.WHITE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] nfiraosLimitsFigs = makeFigure(props);
        map.put("nfiraos.limits", nfiraosLimitsFigs);

        //nfiraos.lsgasterism (always fixed)
        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_NFIRAOS_ASTERISM);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 35/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        Color astColor = new Color(59, 81, 171);
        props.put(OUTLINE_COLOR, astColor);
        props.put(FILL, FILL_OUTLINE_YES);
        props.put(FILL_COLOR, astColor);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] lsgFigures = makeFigure(props);
        map.put("nfiraos.lsgasterism", lsgFigures);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 20/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.WHITE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] nfiraosAcqusitionCameraLimits = makeFigure(props);
        map.put("nfiraos.acqusitionCameraLimits", nfiraosAcqusitionCameraLimits);

        //note basic units are in arc-sec .. 
        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_RECTANGLE);
        props.put(ROTATABLE, true);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(WIDTH_X, 17/3600d);
        props.put(WIDTH_Y, 17/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] irisDetectorFig = makeFigure(props);
        //cfgIris.add(fig);
        map.put("iris.sciencedetector", irisDetectorFig);
        irisDetectorFig[0].addSlave((CanvasFigure) nfiraosLimitsFigs[0]);

        return map;
    }

    /** Copied from DivaImageGraphics */
    private Interactor makeRoiSelectionInteractor(DivaImageGraphics dig) {
        RectangleManipulator rectangleManipulator = new RectangleManipulator();
        rectangleManipulator.getHandleInteractor().addLayerListener(new LayerAdapter() {

            public void mouseReleased(LayerEvent e) {
                Figure fig = e.getFigureSource();
                if (fig instanceof CanvasFigure) {
                    ((CanvasFigure) fig).fireCanvasFigureEvent(CanvasFigure.RESIZED);
                }
            }
        });

        // Create a movable, resizable selection interactor
        SelectionInteractor roiSelectionInteractor = new SelectionInteractor();
        roiSelectionInteractor.setPrototypeDecorator(rectangleManipulator);
        // connect the different selection models
        roiSelectionInteractor.setSelectionModel(
                dig.getSelectionInteractor().getSelectionModel());
        roiSelectionInteractor.addInteractor(dig.getDragInteractor());

        return roiSelectionInteractor;
    }

}
