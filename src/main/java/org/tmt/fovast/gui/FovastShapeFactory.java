/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.gui;

import diva.canvas.Figure;
import diva.canvas.connector.CenterTarget;
import diva.canvas.event.LayerAdapter;
import diva.canvas.event.LayerEvent;
import diva.canvas.interactor.BasicGrabHandleFactory;
import diva.canvas.interactor.BoundsManipulator;
import diva.canvas.interactor.CircleGeometry;
import diva.canvas.interactor.CompositeInteractor;
import diva.canvas.interactor.DragInteractor;
import diva.canvas.interactor.Interactor;
import diva.canvas.interactor.PointConstraint;
import diva.canvas.interactor.SelectionInteractor;
import diva.canvas.toolbox.BasicFigure;
import diva.canvas.toolbox.LabelWrapper;
import diva.util.java2d.Polygon2D;
import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.RenderingHints;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.util.HashMap;
import javax.vecmath.Color3b;
import jsky.coords.CoordinateConverter;
import jsky.graphics.CanvasFigure;
import jsky.graphics.CanvasFigureEvent;
import jsky.graphics.CanvasFigureGroup;
import jsky.graphics.CanvasGraphics;
import jsky.image.graphics.DivaImageGraphics;
import jsky.image.graphics.RectangleManipulator;
import jsky.image.graphics.RotatableCanvasFigure;
import jsky.image.graphics.ShapeUtil;
import org.tmt.fovast.instrumentconfig.Config;
import javax.swing.SwingConstants;
import java.awt.Font;
import jsky.graphics.CanvasFigureListener;
import jsky.image.graphics.RectangleGeometry;
import jsky.image.graphics.RoiFigure;

/**
 *
 */
class FovastShapeFactory {

    private final static String FIGURE_LABEL = "figureLabel";
    
    private final static String FIGURE_TYPE = "figureType";

    private final static String FIGURE_TYPE_RECTANGLE = "rectangle";

    private final static String FIGURE_TYPE_CIRCLE = "circle";

    private final static String FIGURE_TYPE_PROBETIP = "probeTip";

    private final static String FIGURE_TYPE_ARC = "arc";

    private final static String ARC_END = "arcEnd";

    private final static int ARC_END_OPEN = Arc2D.OPEN;

    private final static int ARC_END_PIE = Arc2D.PIE;

    private final static int ARC_END_CHORD = Arc2D.CHORD;

    private final static String ARC_START_ANGLE = "startAngle";

    private final static String ARC_ANGLE_EXTENT = "angleExtent";

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

       String label = (String) props.get(FIGURE_LABEL);
       
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
       // roiSelectionInteractor.addInteractor(dragInteractor);

       if(rotatable && moveable) {
           interactor = makeRoiSelectionInteractor(dig);
       } else if(rotatable) {           
           interactor = makeRoiSelectionInteractor(dig);
           //remove drag interactor so that move does not happen.
           ((CompositeInteractor)interactor).removeInteractor(
                   dig.getDragInteractor());
       } else if(moveable) {
           interactor = makeDragInteractor(dig);
       }
       
       if(figType.equals(FIGURE_TYPE_RECTANGLE)) {
//           double width = (Double)props.get(WIDTH_X);
//           double height = (Double)props.get(WIDTH_Y);
//           double x = (Double)props.get(CENTER_OFFSET_X);
//           double y = (Double)props.get(CENTER_OFFSET_Y);
//           Point2D.Double wcsCenter = cc.getWCSCenter();
//           //axis goes from east <- west
//           x = wcsCenter.x + x;
//           y = wcsCenter.y + y;
//           Point2D.Double pt = new Point2D.Double(x, y);
//           cc.worldToScreenCoords(pt, false);
//           x = pt.x;
//           y = pt.y;
//           pt = new Point2D.Double(width, height);
//           cc.worldToScreenCoords(pt, true);
//           width = pt.x;
//           height = pt.y;
//           x = x - width/2;
//           y = y - height/2;
//
//           Rectangle2D.Double rect = new Rectangle2D.Double(x, y, width, height);

           double width = (Double)props.get(WIDTH_X); //in degrees
           double height = (Double)props.get(WIDTH_Y);
           double x_off = (Double)props.get(CENTER_OFFSET_X); //in degrees
           double y_off = (Double)props.get(CENTER_OFFSET_Y);
           double flag_x=1;
           double flag_y=1;

           if(x_off < 0.0) {
               flag_x=-1;
           }
           if(y_off < 0.0) {
               flag_y=-1;
           }
           Point2D.Double wcsCenter = cc.getWCSCenter();
           double ra = wcsCenter.x ;    //RA in degrees
           double dec = wcsCenter.y ;    //DEC
           double factor = Math.cos(Math.toRadians(dec)); //less than 1
           Point2D.Double pt = new Point2D.Double(ra, dec);
           cc.worldToScreenCoords(pt, false);
           ra = pt.x;     //in pixels now
           dec = pt.y;
           pt = new Point2D.Double(x_off, y_off); //works only for positive offsets
           cc.worldToScreenCoords(pt, true);
           x_off = pt.x;     //in pixels now
           y_off = pt.y;
         //axis goes from east <-- west
           ra = ra + x_off*flag_x;    //RA
           dec = dec + y_off*flag_y;    //DEC
           pt = new Point2D.Double(width*factor, height);
           cc.worldToScreenCoords(pt, true);
           width = pt.x;   //in pixels now
           height = pt.y;
           ra = ra - width/2;           //go to top left corner
           dec = dec - height/2;
           Rectangle2D.Double rect = new Rectangle2D.Double(ra, dec, width, height);

           fig = dig.makeFigure(rect, fillColor, outlineColor, outlineWidth,
                   interactor);
           CanvasFigure labelFig = null;
           if(label != null) {
               Font labelFont = new Font("arial", Font.PLAIN, 2);
               Point2D.Double pos = new Point2D.Double(
                       rect.getX(), rect.getY() + rect.height);
               labelFig = dig.makeLabel(pos, "TWFS", outlineColor, labelFont);
               dig.add(labelFig);
               labelFig.setVisible(false);
               fig.addSlave(labelFig);
           }

//           } else {
//               Font labelFont = new Font("arial", Font.PLAIN, 10);
//               fig = dig.makeLabeledFigure(rect, fillColor, outlineColor, outlineWidth,
//                       label, SwingConstants.NORTH,
//                       outlineColor, labelFont, interactor);
//           }
           //turns off resizing
           if(fig instanceof RotatableCanvasFigure) {
                ((RotatableCanvasFigure)fig).setResizable(false);
           }
           dig.add(fig);
           fig.setVisible(false);
           if(label == null) {
                return new CanvasFigure[]{fig};
           } else {
                return new CanvasFigure[]{fig, labelFig};
           }


       } else if(figType.equals(FIGURE_TYPE_CIRCLE)) {
           double radius = (Double)props.get(RADIUS);
           double x_off = (Double)props.get(CENTER_OFFSET_X); //in degrees
           double y_off = (Double)props.get(CENTER_OFFSET_Y);
           double flag_x=1;
           double flag_y=1;

           if(x_off < 0.0) {
               flag_x=-1;
           }
           if(y_off < 0.0) {
               flag_y=-1;
           }
           Point2D.Double wcsCenter = cc.getWCSCenter();
           double ra = wcsCenter.x ;    //RA in degrees
           double dec = wcsCenter.y ;    //DEC
           double factor = Math.cos(Math.toRadians(dec)); //less than 1
           Point2D.Double pt = new Point2D.Double(ra, dec);
           cc.worldToScreenCoords(pt, false);
           ra = pt.x;     //in pixels now
           dec = pt.y;
           pt = new Point2D.Double(x_off, y_off); //works only for positive offsets
           cc.worldToScreenCoords(pt, true);
           x_off = pt.x;     //in pixels now
           y_off = pt.y;
         //axis goes from east <-- west
           ra = ra + x_off*flag_x;    //RA
           dec = dec + y_off*flag_y;    //DEC
           pt = new Point2D.Double(radius*factor, radius);
           cc.worldToScreenCoords(pt, true);
           radius = pt.x;
           ra = ra - radius ;
           dec = dec - radius ;
           Ellipse2D.Double ell = new Ellipse2D.Double(ra, dec, 2*radius, 2*radius);
//           double x = (Double)props.get(CENTER_OFFSET_X);
//           double y = (Double)props.get(CENTER_OFFSET_Y);
//           Point2D.Double wcsCenter = cc.getWCSCenter();
//           x = wcsCenter.x + x;
//           y = wcsCenter.y + y;
//           Point2D.Double pt = new Point2D.Double(x, y);
//           cc.worldToScreenCoords(pt, false);
//           x = pt.x;
//           y = pt.y;
//           pt = new Point2D.Double(radius, radius);
//           cc.worldToScreenCoords(pt, true);
//           radius = pt.x;
//           x = x - radius;
//           y = y - radius;
//           Ellipse2D.Double ell = new Ellipse2D.Double(x, y, 2*radius, 2*radius);

           fig = dig.makeFigure(ell, fillColor, outlineColor, outlineWidth,
                   interactor);
           //turns off resizing
           if(fig instanceof RotatableCanvasFigure) {
                ((RotatableCanvasFigure)fig).setResizable(false);
           }
           dig.add(fig);
           fig.setVisible(false);
           return new CanvasFigure[]{fig};

       } else if(figType.equals(FIGURE_TYPE_ARC)) {
           double radius = (Double)props.get(RADIUS);
           double x_off = (Double)props.get(CENTER_OFFSET_X); //in degrees
           double y_off = (Double)props.get(CENTER_OFFSET_Y);
           double flag_x=1;
           double flag_y=1;

           if(x_off < 0.0) {
               flag_x=-1;
           }
           if(y_off < 0.0) {
               flag_y=-1;
           }
           Point2D.Double wcsCenter = cc.getWCSCenter();
           double ra = wcsCenter.x ;    //RA in degrees
           double dec = wcsCenter.y ;    //DEC
           double factor = Math.cos(Math.toRadians(dec)); //less than 1
           Point2D.Double pt = new Point2D.Double(ra, dec);
           cc.worldToScreenCoords(pt, false);
           ra = pt.x;     //in pixels now
           dec = pt.y;
           pt = new Point2D.Double(x_off, y_off); //works only for positive offsets
           cc.worldToScreenCoords(pt, true);
           x_off = pt.x;     //in pixels now
           y_off = pt.y;
         //axis goes from east <-- west
           ra = ra + x_off*flag_x;    //RA
           dec = dec + y_off*flag_y;    //DEC
           pt = new Point2D.Double(radius*factor, radius);
           cc.worldToScreenCoords(pt, true);
           radius = pt.x;
           ra = ra - radius ;
           dec = dec - radius ;
//           double x = (Double)props.get(CENTER_OFFSET_X);
//           double y = (Double)props.get(CENTER_OFFSET_Y);
//           Point2D.Double wcsCenter = cc.getWCSCenter();
//           //axis goes from east <- west
//           x = wcsCenter.x + x;
//           y = wcsCenter.y + y;
//           Point2D.Double pt = new Point2D.Double(x, y);
//           cc.worldToScreenCoords(pt, false);
//           x = pt.x;
//           y = pt.y;
//           pt = new Point2D.Double(radius, radius);
//           cc.worldToScreenCoords(pt, true);
//           radius = pt.x;
//           x = x - radius;
//           y = y - radius;

           int type = (Integer)props.get(ARC_END);
           //axis goes from east -> west
           double startAngle = -1.0 * (Double)props.get(ARC_START_ANGLE); //(Double)props.get(ARC_START_ANGLE);
           double angleExtent = (Double)props.get(ARC_ANGLE_EXTENT);//(Double)props.get(ARC_ANGLE_EXTENT);          
           Arc2D.Double arc = new Arc2D.Double(ra, dec, 2*radius, 2*radius, startAngle,
                   angleExtent, type);

           fig = dig.makeFigure(arc, fillColor, outlineColor, outlineWidth,
                   interactor);
           //turns off resizing
           if(fig instanceof RotatableCanvasFigure) {
                ((RotatableCanvasFigure)fig).setResizable(false);
           }
           dig.add(fig);
           fig.setVisible(false);
           return new CanvasFigure[]{fig};

       }
       else if(figType.equals(FIGURE_TYPE_NFIRAOS_ASTERISM)) {
           //we have to draw 6 cricles with one at center and others on a pentagon
           //of radius 35 arc sec
           //double pentagonRadius = props.get(RADIUS);
           double radius = (Double)props.get(RADIUS);
           double x = (Double)props.get(CENTER_OFFSET_X);
           double y = (Double)props.get(CENTER_OFFSET_X);
           Point2D.Double wcsCenter = cc.getWCSCenter();
           //axis goes from east <- west
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
       else if(figType.equals(FIGURE_TYPE_PROBETIP)) {
           double width = (Double)props.get(WIDTH_X);//in degrees
           double height = (Double)props.get(WIDTH_Y);
           double radius = (Double)props.get(RADIUS);
           CanvasFigure[] probe = new CanvasFigure[2];
           for(int i=0; i<probe.length; i++)  {              
               double x_off = (Double)props.get(CENTER_OFFSET_X); //in degrees
               double y_off = (Double)props.get(CENTER_OFFSET_Y);
               double flag_x=1;
               double flag_y=1;

               if(x_off < 0.0) {
                   flag_x=-1;
               }
               if(y_off < 0.0) {
                   flag_y=-1;
               }
               Point2D.Double wcsCenter = cc.getWCSCenter();
               double ra = wcsCenter.x ;    //RA in degrees
               double dec = wcsCenter.y ;    //DEC
               double factor = Math.cos(Math.toRadians(dec)); //less than 1
               Point2D.Double pt = new Point2D.Double(ra, dec);
               cc.worldToScreenCoords(pt, false);
               ra = pt.x;     //in pixels now
               dec = pt.y;
               pt = new Point2D.Double(x_off, y_off); //works only for positive offsets
               cc.worldToScreenCoords(pt, true);
               x_off = pt.x;     //in pixels now
               y_off = pt.y;
             //axis goes from east <-- west
               ra = ra + x_off*flag_x;    //RA
               dec = dec + y_off*flag_y;    //DEC
//               double x = (Double)props.get(CENTER_OFFSET_X);
//               double y = (Double)props.get(CENTER_OFFSET_Y);
//               Point2D.Double wcsCenter = cc.getWCSCenter();
//               x = wcsCenter.x + x;
//               y = wcsCenter.y + y;
//               Point2D.Double pt = new Point2D.Double(x, y);
//               cc.worldToScreenCoords(pt, false);
//               x = pt.x;
//               y = pt.y;
               if(i == 1)
               {
                   pt = new Point2D.Double(radius*factor, radius);
                   cc.worldToScreenCoords(pt, true);
                   radius = pt.x;
                   ra = ra - radius ;
                   dec = dec - radius ;
                   Ellipse2D.Double ell = new Ellipse2D.Double(ra, dec, 2*radius, 2*radius);
//                   pt = new Point2D.Double(radius, radius);
//                   cc.worldToScreenCoords(pt, true);
//                   radius = pt.x;
//                   x = x - radius;
//                   y = y - radius;
//                   Ellipse2D.Double ell = new Ellipse2D.Double(x, y, 2*radius, 2*radius);

                   probe[i] = dig.makeFigure(ell, fillColor, outlineColor, outlineWidth,interactor);
                   //turns off resizing
                   if(probe[i] instanceof RotatableCanvasFigure) {
                        ((RotatableCanvasFigure)probe[i]).setResizable(false);
                   }
                   dig.add(probe[i]);
                   probe[i].setVisible(false);
               }
               else if(i == 0){
                   pt = new Point2D.Double(width*factor, height);
                   cc.worldToScreenCoords(pt, true);
                   width = pt.x;   //in pixels now
                   height = pt.y;
                   ra = ra - width/2;           //go to top left corner
                   dec = dec - height/2;
                   Rectangle2D.Double rect = new Rectangle2D.Double(ra, dec, width, height);
//                   pt = new Point2D.Double(width, height);
//                   cc.worldToScreenCoords(pt, true);
//                   width = pt.x;
//                   height = pt.y;
//                   x = x - width/2;
//                   y = y - height/2;
//                   Rectangle2D.Double rect = new Rectangle2D.Double(x, y, width, height);
                   probe[i] = dig.makeFigure(rect, fillColor, outlineColor, outlineWidth,null);
                   //turns off resizing
                   if(probe[i] instanceof RotatableCanvasFigure) {
                        ((RotatableCanvasFigure)probe[i]).setResizable(false);
                   }
                   dig.add(probe[i]);
                   probe[i].setVisible(false);
                }
           }
           probe[1].addSlave(probe[0]);
           return probe;
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

        final HashMap<String, CanvasFigure[]> map = new HashMap<String, CanvasFigure[]>();

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
        props.put(RADIUS, 1/60d);
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

       /* props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 10/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.WHITE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] nfiraosAcqusitionCameraLimits = makeFigure(props);
        map.put("nfiraos.acqusitionCameraLimits", nfiraosAcqusitionCameraLimits);
        */
//        DragInteractor dragInteractor =
//                (DragInteractor)nfiraosAcqusitionCameraLimits[0].getInteractor();
//        dragInteractor.appendConstraint(new PointConstraint() {
//
//            Point2D prevPt = null;
//
//            @Override
//            public void constrain(Point2D pt) {
//                double x = pt.getX();
//                double y = pt.getY();
//
//                CanvasFigure[] figs = map.get("nfiraos.limits");
//                CanvasFigure nfiraosLimitsFig = figs[0];
//
//                Shape shape2 = nfiraosLimitsFig.getShape();
//                Point2D.Double nFigCenter = new Point2D.Double(
//                        nfiraosAcqusitionCameraLimits[0].getBounds().getCenterX(),
//                        nfiraosAcqusitionCameraLimits[0].getBounds().getCenterY());
//
//                CoordinateConverter cc = fovastImageDisplay.getCoordinateConverter();
//                //acq cam fov is 20 arc sec
//                Point2D.Double radius = new Point2D.Double(10/3600d, 10/3600d);
//                cc.worldToScreenCoords(radius, true);
//
//                //nfiraos FOV is 2 arc min ..
//                Point2D.Double nFov = new Point2D.Double(1/60d, 1/60d);
//                cc.worldToScreenCoords(nFov, true);
//
//                Point2D.Double imageCenter = (Point2D.Double)cc.getWCSCenter().clone();
//                cc.worldToScreenCoords(imageCenter, false);
//
//                //if(shape2.contains(pt)) {
//                //if(imageCenter.distance(nFigCenter) < (nFov.getX() - radius.getX())) {
//                if(imageCenter.distance(nFigCenter) < (radius.getX())) {
//                    prevPt = pt;
//                    //leave pt as is
//                } else {
//                    pt.setLocation(prevPt.getX(), prevPt.getY());
//                }
//            }
//
//            /**
//             * As of now never snaps
//             *
//             */
//            @Override
//            public boolean snapped() {
//                return false;
//            }
//        });
//

        //iris.ifuimager.lenslet
        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_RECTANGLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        //max fov is 1.01" x 1.15"
        //18" of axis .. so 18" + 1.01/2
        props.put(CENTER_OFFSET_X, -(18.0/3600d)); // + 1.01/2)/3600d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(WIDTH_X, 1.01/3600d);
        props.put(WIDTH_Y, 1.15/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] lensletFigs = makeFigure(props);
        //cfgIris.add(fig);
        map.put("iris.ifuimager.lenslet", lensletFigs);

        //iris.ifuimager.slicer
        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_RECTANGLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        //max fov is 4.4" x 2.25"
        //18" of axis .. so 18" + 4.4/2
        props.put(CENTER_OFFSET_X, -(18.0/3600d));// + 4.4/2)/3600d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(WIDTH_X, 4.4/3600d);
        props.put(WIDTH_Y, 2.25/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] slicerFigs = makeFigure(props);
        //cfgIris.add(fig);
        map.put("iris.ifuimager.slicer", slicerFigs);

//        //note basic units are in arc-sec ..
//        props = new HashMap<String, Object>();
//        props.put(FIGURE_TYPE, FIGURE_TYPE_RECTANGLE);
//        props.put(ROTATABLE, false);
//        props.put(MOVEABLE, true);
//        props.put(CENTER_OFFSET_X, 0d);
//        props.put(CENTER_OFFSET_Y, 0d);
//        props.put(WIDTH_X, 17/3600d);
//        props.put(WIDTH_Y, 17/3600d);
//        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
//        props.put(OUTLINE_COLOR, Color.GREEN);
//        props.put(FILL, FILL_OUTLINE_NO);
//        props.put(OUTLINE_WIDTH, 1.0f);
//        CanvasFigure[] probeFigs = makeFigure(props);
//        //cfgIris.add(fig);
//        map.put("iris.oiwfs.probe1", irisDetectorFig);
//        for(int i=0; i<probeFigs.length; i++) {
//            irisDetectorFig[0].addSlave((CanvasFigure) probeFigs[i]);
//        }
//        probeFigs = makeFigure(props);
//        map.put("iris.oiwfs.probe2", irisDetectorFig);
//        for(int i=0; i<probeFigs.length; i++) {
//            irisDetectorFig[0].addSlave((CanvasFigure) probeFigs[i]);
//        }
//        probeFigs = makeFigure(props);
//        map.put("iris.oiwfs.probe3", irisDetectorFig);
//        for(int i=0; i<probeFigs.length; i++) {
//            irisDetectorFig[0].addSlave((CanvasFigure) probeFigs[i]);
//        }



        //Group2 starts - mobie related elements

        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 7.5/60d); //7.5'
        props.put(ARC_END, ARC_END_CHORD);
        props.put(ARC_START_ANGLE, 90d);
        props.put(ARC_ANGLE_EXTENT, 180d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.BLUE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] mobieVignettingFigures = makeFigure(props);
        map.put("mobie.vignettingstart", mobieVignettingFigures);

        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, -8.9/60d); //8.9'
        props.put(ARC_END, ARC_END_CHORD);
        props.put(ARC_START_ANGLE, 90d);
        props.put(ARC_ANGLE_EXTENT, 180d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.BLUE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] mobieEdgeOfFiledFigs = makeFigure(props);
        map.put("mobie.edgeoffield", mobieEdgeOfFiledFigs);

        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_RECTANGLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        //MOBIE DIMENSIONS ARE 4.2 x 9.6
        props.put(CENTER_OFFSET_X, -(5.4)/60d); // 5.4' offet-x
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(WIDTH_X, 4.2/60d);
        props.put(WIDTH_Y, 9.6/60d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.BLUE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] mobieDetectorFigs = makeFigure(props);
        //cfgIris.add(fig);
        map.put("mobie.detector", mobieDetectorFigs);
        ((DragInteractor)mobieDetectorFigs[0].getInteractor()).appendConstraint(
                new MobieDetectorConstraint(mobieDetectorFigs[0], fovastImageDisplay));

        //Keeping this after mobie as although mobie arcs are hidden 
        //when sciencedetector is shown .. rotation handle goes berserk
        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_RECTANGLE);
        props.put(ROTATABLE, true);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(WIDTH_X, 16.4/3600d); //16.4 arcsec
        props.put(WIDTH_Y, 16.4/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisDetectorFigs = makeFigure(props);
        //cfgIris.add(fig);
        map.put("iris.sciencedetector", irisDetectorFigs);
        //TODO:this listener .. does not work yet..
        irisDetectorFigs[0].addCanvasFigureListener(new CanvasFigureListener() {

            @Override
            public void figureSelected(CanvasFigureEvent e) {
                //do nothing
            }

            @Override
            public void figureDeselected(CanvasFigureEvent e) {
                //do nothing
            }

            @Override
            public void figureResized(CanvasFigureEvent e) {
                //kick out twfs if it intersects with
                CanvasFigure twfsFig = map.get("nfiraos.twfs.detector")[0];
                //we can do this as twfs does not rotate .. 
                if(irisDetectorFigs[0].getShape().intersects(
                        twfsFig.getShape().getBounds2D())) {
                    CoordinateConverter cc = fovastImageDisplay.getCoordinateConverter();
                    Point2D.Double center = cc.getWCSCenter();
                    Point2D.Double pt = new Point2D.Double(
                            center.getX()+(1/60d), center.getY());
                    cc.worldToScreenCoords(pt, false);
                    twfsFig.translate(pt.x, pt.y);
                    fovastImageDisplay.repaint();
                }                
            }

            @Override
            public void figureMoved(CanvasFigureEvent e) {
                //do nothing
                CanvasFigure twfsFig = map.get("nfiraos.twfs.detector")[0];
                //we can do this as twfs does not rotate ..
                if(irisDetectorFigs[0].getShape().intersects(
                        twfsFig.getShape().getBounds2D())) {
                    CoordinateConverter cc = fovastImageDisplay.getCoordinateConverter();
                    Point2D.Double center = cc.getWCSCenter();
                    Point2D.Double pt = new Point2D.Double(
                            center.getX()+(1/60d), center.getY());
                    cc.worldToScreenCoords(pt, false);
                    twfsFig.translate(pt.x, pt.y);
                    fovastImageDisplay.repaint();
                }    
            }
        });
        irisDetectorFigs[0].addSlave((CanvasFigure) nfiraosLimitsFigs[0]);
        //irisDetectorFigs[0].addSlave((CanvasFigure) nfiraosAcqusitionCameraLimits[0]);
        irisDetectorFigs[0].addSlave((CanvasFigure) lensletFigs[0]);
        irisDetectorFigs[0].addSlave((CanvasFigure) slicerFigs[0]);
        

        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_RECTANGLE);
        props.put(FIGURE_LABEL, "TWFS");
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        // some arbitrary position outside of science field
        // and inside nfiraos fov > 17" and < 2'
        props.put(CENTER_OFFSET_X, 1/60d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(WIDTH_X, 1/3600d); //1 arcsec
        props.put(WIDTH_Y, 1/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.WHITE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] twsFigs = makeFigure(props);
        //cfgIris.add(fig);
        map.put("nfiraos.twfs.detector", twsFigs);
        irisDetectorFigs[0].addSlave((CanvasFigure) twsFigs[0]);
        irisDetectorFigs[0].addSlave((CanvasFigure) twsFigs[1]);
        //put constraints
        //nfiraos.twfs.detector
        CanvasFigure twsFig = twsFigs[0];
        DragInteractor dragInteractor = (DragInteractor) twsFig.getInteractor();
        //now constrain it
        dragInteractor.appendConstraint(new TwfsPointConstraint(twsFig, map));

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 2.292664743/60d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.WHITE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisProbesLimits = makeFigure(props);
        map.put("iris.probes.limits", irisProbesLimits);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_ARC);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, -2.292664743/60d);
        props.put(RADIUS, 2.375998076/60d);
        props.put(ARC_ANGLE_EXTENT, 51.72);
        props.put(ARC_END,ARC_END_PIE);
        props.put(ARC_START_ANGLE,115.86);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.RED);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisProbeLimits1 = makeFigure(props);
        map.put("iris.oiwfs.probe1.limits", irisProbeLimits1);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_ARC);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, -0.033091765d);
        props.put(CENTER_OFFSET_Y, 0.019105539d);
        props.put(RADIUS,2.375998076/60d);
        props.put(ARC_ANGLE_EXTENT, 51.72);
        props.put(ARC_END,ARC_END_PIE);
        props.put(ARC_START_ANGLE,355.86);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisProbeLimits2 = makeFigure(props);
        map.put("iris.oiwfs.probe2.limits", irisProbeLimits2);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_ARC);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0.033091765d);
        props.put(CENTER_OFFSET_Y, 0.019105539d);
        props.put(RADIUS, 2.375998076/60d);
        props.put(ARC_ANGLE_EXTENT, 51.72);
        props.put(ARC_END,ARC_END_PIE);
        props.put(ARC_START_ANGLE,235.86);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.BLUE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisProbeLimits3 = makeFigure(props);
        map.put("iris.oiwfs.probe3.limits", irisProbeLimits3);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_PROBETIP);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, -0.013d);
        props.put(RADIUS, 5/3600d);
        props.put(WIDTH_X, 4/3600d); //1 arcsec
        props.put(WIDTH_Y, 4/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.RED);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisProbeArm1 = makeFigure(props);
        map.put("iris.oiwfs.probe1.arm", irisProbeArm1);
        CanvasFigure probFig1 = irisProbeArm1[1];
        dragInteractor =  (DragInteractor) probFig1.getInteractor();
        dragInteractor.appendConstraint(new PointConstraint() {

            Point2D prevPt = null;

            @Override
            public void constrain(Point2D pt) {
                double x = pt.getX();
                double y = pt.getY();

                CanvasFigure[] figs = map.get("iris.oiwfs.probe1.limits");
                CanvasFigure probLimitsFig = figs[0];
                Shape shape = probLimitsFig.getShape();

                CanvasFigure[] figs1 = map.get("iris.oiwfs.probe1.arm");
                CanvasFigure probArmFig = figs1[0];
                Shape shape1 = probArmFig.getShape();

                double centerX = shape1.getBounds2D().getCenterX();
                double centerY = shape1.getBounds2D().getCenterY();
                Point2D.Double centerPt = new Point2D.Double(centerX, centerY);

                if(prevPt != null) {
                    Point2D.Double newCenterPt = new Point2D.Double(
                                centerX + (pt.getX() - prevPt.getX()),
                                centerY + (pt.getY() - prevPt.getY())
                    );
                    System.out.println("MousePoint " + pt);
                    System.out.println("NewCenter " + newCenterPt);
                    if(shape.contains(newCenterPt)){
                        prevPt = pt;
                        //leave pt as is
                    } else {
                        pt.setLocation(prevPt.getX(), prevPt.getY());
                    }
                } else {
                    prevPt = pt;
                }
            }

            /**
             * As of now never snaps
             *
             */
            @Override
            public boolean snapped() {
                return false;
            }
        });

        
        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_PROBETIP);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, -0.011d);
        props.put(CENTER_OFFSET_Y, +0.0065d);
        props.put(RADIUS, 5/3600d);
        props.put(WIDTH_X, 4/3600d); //1 arcsec
        props.put(WIDTH_Y, 4/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisProbeArm2 = makeFigure(props);
        map.put("iris.oiwfs.probe2.arm", irisProbeArm2);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_PROBETIP);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, +0.011d);
        props.put(CENTER_OFFSET_Y, +0.0065d);
        props.put(RADIUS, 5/3600d);
        props.put(WIDTH_X, 4/3600d); //1 arcsec
        props.put(WIDTH_Y, 4/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.BLUE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisProbeArm3 = makeFigure(props);
        map.put("iris.oiwfs.probe3.arm", irisProbeArm3);
       
        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 10/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.WHITE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] nfiraosAcqusitionCameraLimits = makeFigure(props);
        map.put("nfiraos.acqusitionCameraLimits", nfiraosAcqusitionCameraLimits);
        
        irisDetectorFigs[0].addSlave((CanvasFigure)nfiraosAcqusitionCameraLimits[0]);
        CanvasFigure cirFig = nfiraosAcqusitionCameraLimits[0];
        dragInteractor =  (DragInteractor) cirFig.getInteractor();
        dragInteractor.appendConstraint(new PointConstraint() {

            Point2D prevPt = null;

            @Override
            public void constrain(Point2D pt) {
                double x = pt.getX();
                double y = pt.getY();

                CanvasFigure[] figs = map.get("nfiraos.limits");
                CanvasFigure nfiraosLimitsFig = figs[0];
                Shape shape = nfiraosLimitsFig.getShape();

                CanvasFigure[] figs1 = map.get("nfiraos.acqusitionCameraLimits");
                CanvasFigure nfiraosLimitsFig1 = figs1[0];
                Shape shape1 = nfiraosLimitsFig1.getShape();
                
                double centerX = shape1.getBounds2D().getCenterX();
                double centerY = shape1.getBounds2D().getCenterY();
                Point2D.Double centerPt = new Point2D.Double(centerX, centerY);

                if(prevPt != null) {
                    Point2D.Double newCenterPt = new Point2D.Double(
                                centerX + (pt.getX() - prevPt.getX()),
                                centerY + (pt.getY() - prevPt.getY())
                    );
                    System.out.println("MousePoint " + pt);
                    System.out.println("NewCenter " + newCenterPt);
                    if(shape.contains(newCenterPt)){
                        prevPt = pt;
                        //leave pt as is
                    } else {
                        pt.setLocation(prevPt.getX(), prevPt.getY());
                    }
                } else {
                    prevPt = pt;
                }
            }

            /**
             * As of now never snaps
             *
             */
            @Override
            public boolean snapped() {
                return false;
            }
        });

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
        roiSelectionInteractor.addInteractor(makeDragInteractor(dig));
        roiSelectionInteractor.setConsuming(false);

        return roiSelectionInteractor;
    }

    private Interactor makeDragInteractor(DivaImageGraphics dig) {
        DragInteractor dragInteractor = new DragInteractor();
        dragInteractor.addLayerListener(new LayerAdapter() {

            public void mouseReleased(LayerEvent e) {
                Figure fig = e.getFigureSource();
                if (fig instanceof CanvasFigure) {
                    ((CanvasFigure) fig).fireCanvasFigureEvent(CanvasFigure.MOVED);
                }
            }
        });
        dragInteractor.setConsuming(false);
        return dragInteractor;
    }

    private static class TwfsPointConstraint implements  PointConstraint {
                 Point2D prevPt = null;

        CanvasFigure parentFig = null;

        private final HashMap<String, CanvasFigure[]> map;

        public TwfsPointConstraint(CanvasFigure parentFig,
                HashMap<String, CanvasFigure[]> map) {
            this.parentFig = parentFig;
            this.map = map;
        }

        @Override
        public void constrain(Point2D pt) {


            double x = pt.getX();
            double y = pt.getY();

            Figure twfsBoxFig = parentFig;
            Shape twfsBoxShape = twfsBoxFig.getShape();

            //should be with
            CanvasFigure[] figs =  map.get("iris.sciencedetector");
            CanvasFigure sdFig = figs[0];

            figs = map.get("nfiraos.limits");
            CanvasFigure nfiraosLimitsFig = figs[0];

            Shape sdShape = sdFig.getShape();
            Shape nfiraosLimitsShape = nfiraosLimitsFig.getShape();
            
            if(!sdShape.contains(pt) && nfiraosLimitsShape.contains(pt)) {
            //if(!sdShape.intersects(twfsBoxShape.getBounds2D()) && nfiraosLimitsShape.contains(pt)
            //    && nfiraosLimitsShape.contains(twfsBoxShape.getBounds2D()) ) {
                prevPt = pt;
                //leave pt as is
            } else {
                pt.setLocation(prevPt.getX(), prevPt.getY());
            }
        }

        /**
         * As of now never snaps
         *
         */
        @Override
        public boolean snapped() {
            return false;
        }

    }


    private static class MobieDetectorConstraint implements PointConstraint {

        private CanvasFigure mobieFigure;
        private FovastImageDisplay fovastImageDisplay;

        private Point2D prevPoint;

        public MobieDetectorConstraint(CanvasFigure mobieFigure, FovastImageDisplay imageDisplay) {
            this.mobieFigure = mobieFigure;
            this.fovastImageDisplay = imageDisplay;

            prevPoint = new Point2D.Double(mobieFigure.getBounds().getCenterX(),
                    mobieFigure.getBounds().getCenterY());
        }


        @Override
        public boolean snapped() {
            return false; // no snapping
        }

        @Override
        public void constrain(Point2D pt) {
            double x = mobieFigure.getShape().getBounds2D().getCenterX();
            double y = mobieFigure.getShape().getBounds2D().getCenterY();

            CoordinateConverter cc = fovastImageDisplay.getCoordinateConverter();
            Point2D.Double center = (Point2D.Double) cc.getWCSCenter().clone();
            cc.worldToScreenCoords(center, true);

            //TODO: hard coding mobie value .. mobie is 5.4' off axis
            Point2D.Double distance = new Point2D.Double(5.4/60d, 5.4/60d);
            cc.worldToScreenCoords(distance, false);

            if((center.distance(x, y) - distance.x)/distance.x < Math.pow(10, -15)) {
                prevPoint = pt;
            } else {
                pt.setLocation(prevPoint.getX(), prevPoint.getY());
            }
        }

    }
}
