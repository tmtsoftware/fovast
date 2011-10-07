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
import diva.canvas.interactor.DragInteractor;
import diva.canvas.interactor.Interactor;
import diva.canvas.interactor.PointConstraint;
import diva.canvas.interactor.SelectionInteractor;
import diva.util.java2d.ShapeUtilities;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import jsky.coords.CoordinateConverter;
import jsky.graphics.CanvasFigure;
import jsky.graphics.CanvasFigureEvent;
import jsky.graphics.CanvasGraphics;
import jsky.image.graphics.DivaImageGraphics;
import jsky.image.graphics.RectangleManipulator;
import jsky.image.graphics.RotatableCanvasFigure;
import org.tmt.fovast.instrumentconfig.Config;
import java.awt.Font;
import java.awt.geom.Line2D;
import jsky.graphics.CanvasFigureListener;
import jsky.image.graphics.ShapeUtil;
import org.tmt.fovast.astro.util.DegreeCoverter;

class FovastShapeFactory {

    private String FIGURE_LABEL = "figureLabel";
    
    private String FIGURE_TYPE = "figureType";

    private String FIGURE_TYPE_RECTANGLE = "rectangle";

    private String FIGURE_TYPE_CROSS = "cross";

    private String FIGURE_TYPE_CIRCLE = "circle";

    private String FIGURE_TYPE_MOBIE_DETECTOR = "mobieDetector";

    private String FIGURE_TYPE_MOBIE_GUIDER = "mobieGuider";

    private String FIGURE_TYPE_PROBETIP = "probeTip";

    private String FIGURE_TYPE_FOCUS_PROBETIP = "focusProbeTip";

    private String FIGURE_TYPE_DOUBLE_ARC = "doubleArc";

    private String FIGURE_TYPE_DOUBLE_CIRCLE = "doubleCircle";

    private String FIGURE_TYPE_ARC = "arc";

    private String ARC_END = "arcEnd";

    private int ARC_END_OPEN = Arc2D.OPEN;

    private int ARC_END_PIE = Arc2D.PIE;

    private int ARC_END_CHORD = Arc2D.CHORD;

    private String ARC_START_ANGLE = "startAngle";

    private String ARC_ANGLE_EXTENT = "angleExtent";

    private String FIGURE_TYPE_NFIRAOS_ASTERISM = "nfiraos.lsgasterism";

    private String ROTATABLE = "rotatable";

    private String MOVEABLE = "moveable";

    private String DRAW_OUTLINE = "drawOutline";

    private boolean DRAW_OUTLINE_YES = true;

    private boolean DRAW_OUTLINE_NO = false;

    private String OUTLINE_WIDTH = "outlineWidth";
    
    private String FILL = "fillOutline";

    private boolean FILL_OUTLINE_YES = true;

    private boolean FILL_OUTLINE_NO = false;

    private String CENTER_OFFSET_X = "centerOffsetX";

    private String CENTER_OFFSET_Y = "centerOffsetY";

    private String ARM_Line1_TOP_X = "line1TopX";

    private String ARM_Line1_BOTTOM_X = "line1BottomX";

    private String ARM_Line1_TOP_Y = "line1TopY";

    private String ARM_Line1_BOTTOM_Y = "line1BottomY";

    private String WIDTH_X = "widthX";

    private String WIDTH_Y = "widthY";

    private String RADIUS = "radius";

    private String OUTLINE_COLOR = "outlineColor";

    private String FILL_COLOR = "fillColor";

    private String CENTER_OFFSET_X1 = "centerOffsetX1";

    private String CENTER_OFFSET_Y1 = "centerOffsetY1";

    private String RADIUS1 = "radius1";

    private String RADIUS2 = "radius2";

    private String ARC_END1 = "arcEnd1";

    private String ARC_ANGLE_EXTENT1 = "angleExtent1";

    private String ARC_START_ANGLE1 = "startAngle1";

    private FovastImageDisplay fovastImageDisplay;

    private Config config;

    HashMap<String, CanvasFigure[]> map = new HashMap<String, CanvasFigure[]>();

//    CoordinateConverter cc1 = null;

    //DO NOT CHANGE INDEX POSITIONS OF FOLLOWING ITEMS
    private int PROBETIP_RECTANGLE_INDEX = 0;
    private int PROBETIP_CIRCLE_INDEX = 1;
    private int ARM_LINE_INDEX = 2;
    //radius of circle. Is used to reduced height of rectanglur arm of IRIS
    private int CIRCLE_RADIUS = 5;	//is 5 arc sec

    DivaImageGraphics dig ;

    FovastShapeFactory(FovastImageDisplay fovastImageDisplay) {
        this.fovastImageDisplay = fovastImageDisplay;
//        CoordinateConverter cc1 = fovastImageDisplay.getCoordinateConverter();
    }

    public void setConfig(Config config) {
        this.config = config;
    }

//    public CanvasFigure updateArmLines(DivaImageGraphics dig,
//            Point2D.Double arm_line_top_pos,
//            Point2D.Double arm_line_bottom_pos,
//            Interactor interactor,
//            Color color) {
//    	float linewidth = 1.0f;
//    	CanvasFigure cfg = null;
//        Line2D.Double newline = new Line2D.Double(arm_line_top_pos, arm_line_bottom_pos);
//        cfg = dig.makeFigure(newline, color, color, linewidth, interactor);
//        cfg.setVisible(true);
//    	return cfg;
//	}
    
	public CanvasFigure updateArmRectangles(DivaImageGraphics dig,
																			Point2D.Double topPt1,
																			Point2D.Double bottomPt2,
																			Color fillColor,
																			Color outlineColor,
																			double widthInPixel,
																			float outlineWidth,
																			double radianTheta,
																			Interactor interactor) {
    	CanvasFigure cfg = null;
		//height = slope formula => sq. root([(y2 - y1)*(y2 - y1)] + [(x2 - x1)*(x2 - x1)])
		double height = Math.sqrt(((bottomPt2.getY() - topPt1.getY())*(bottomPt2.getY() - topPt1.getY())) 
										+ ((bottomPt2.getX() - topPt1.getX())*(bottomPt2.getX() - topPt1.getX())));
		double radius = CIRCLE_RADIUS / 3600d;
		
		Point2D.Double wcsCenter = fovastImageDisplay.getCoordinateConverter().getWCSCenter();
		double factor = Math.cos(Math.toRadians(wcsCenter.y)); //dec, less than 1
		Point2D.Double pt = new Point2D.Double(radius * factor, radius);
		fovastImageDisplay.getCoordinateConverter().worldToScreenCoords(pt, true);
		radius = pt.x;
		height = height - radius;
		
        double recra = topPt1.getX() - (widthInPixel / 2);
        double recdec = topPt1.getY();
        Rectangle2D.Double rect = new Rectangle2D.Double(recra, recdec, widthInPixel, height);
        cfg = dig.makeFigure(rect, fillColor, outlineColor, outlineWidth, interactor);
        cfg.setVisible(true);
		
		//rotate the figure by angle theta
        AffineTransform rotation = 
        	AffineTransform.getRotateInstance(radianTheta, topPt1.getX(), topPt1.getY());
        cfg.transform(rotation);
    	return cfg;
	}

	private double calculateArmAngle(double topX1,
																double topY1,
																double bottomX2,
																double bottomY2) {
		/**
         * theta = tan-1 [(y2 - y1) / (x2 - x1)]
         */
		double rotationangle = Math.atan((bottomY2 - topY1) / (bottomX2 - topX1));
        
        //as tan -1 goes from -90 to 90, required
        if((bottomY2 - topY1) > 0 && (bottomX2 - topX1) > 0)	//1st quadrant
        {
        	rotationangle = rotationangle + Math.toRadians(90);
        }
        else if((bottomY2 - topY1) > 0 && (bottomX2 - topX1) < 0)	//2nd quadrant
        {
        	rotationangle = rotationangle + Math.toRadians(270);
        }
        else if((bottomY2 - topY1) < 0 && (bottomX2 - topX1) < 0)	//3rd quadrant
        {
        	rotationangle = rotationangle + Math.toRadians(270);
        }
        else if((bottomY2 - topY1) < 0 && (bottomX2 - topX1) > 0)	//4th quadrant
        {
        	rotationangle = rotationangle + Math.toRadians(90);
        }
		return rotationangle;
	}

    public CanvasFigure[] makeFigure(HashMap<String, Object> props) {
       String figType = (String) props.get(FIGURE_TYPE);
       CoordinateConverter cc = fovastImageDisplay.getCoordinateConverter();
       CanvasGraphics cg = fovastImageDisplay.getCanvasGraphics();
       dig = (DivaImageGraphics)cg;
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
       
       if(rotatable && moveable) {
           interactor = makeRoiSelectionInteractor(dig,true);
       } else if(rotatable) {           
           interactor = makeRoiSelectionInteractor(dig,false);
           //remove drag interactor so that move does not happen.
//           ((CompositeInteractor)interactor).removeInteractor(
//                   dig.getDragInteractor());
       } else if(moveable) {
           interactor = makeDragInteractor(dig);
       }
       
       if(figType.equals(FIGURE_TYPE_RECTANGLE)) {

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
           pt = new Point2D.Double(x_off*factor, y_off); //works only for positive offsets
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

       }

        else if(figType.equals(FIGURE_TYPE_CROSS)){
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
           pt = new Point2D.Double(x_off*factor, y_off); //works only for positive offsets
           cc.worldToScreenCoords(pt, true);
           x_off = pt.x;     //in pixels now
           y_off = pt.y;
         //axis goes from east <-- west
           ra = ra + x_off*flag_x;    //RA
           dec = dec + y_off*flag_y;    //DEC
           pt = new Point2D.Double(radius*factor, radius);
           cc.worldToScreenCoords(pt, true);
           radius = pt.x;
//           ra = ra - radius ;
//           dec = dec - radius ;
           Shape shape = ShapeUtil.makeCross(ra, dec, radius);
           fig = dig.makeFigure(shape, fillColor, outlineColor, outlineWidth,
                   interactor);
           //turns off resizing
           if(fig instanceof RotatableCanvasFigure) {
                ((RotatableCanvasFigure)fig).setResizable(false);
           }
           dig.add(fig);
           fig.setVisible(false);
           return new CanvasFigure[] {fig};
        }
       else if(figType.equals(FIGURE_TYPE_MOBIE_DETECTOR)){
           double width = (Double)props.get(WIDTH_X); //in degrees
           double height = (Double)props.get(WIDTH_Y);
           double x_off = (Double)props.get(CENTER_OFFSET_X); //in degrees
           double y_off = (Double)props.get(CENTER_OFFSET_Y);
           double x_off1 = (Double)props.get(CENTER_OFFSET_X1); //in degrees
           double y_off1 = (Double)props.get(CENTER_OFFSET_Y1);
           //array length modified
           CanvasFigure[] compFig = new CanvasFigure[1];
           Point2D.Double wcsCenter = cc.getWCSCenter();
           double ra = wcsCenter.x ;    //RA in degrees
           double dec = wcsCenter.y ;    //DEC
           double factor = Math.cos(Math.toRadians(dec)); //less than 1           
           Point2D.Double pt = new Point2D.Double(width*factor, height);
           cc.worldToScreenCoords(pt, true);
           width = pt.x;   //in pixels now
           height = pt.y;                     
           for(int i=0;i<compFig.length;i++)
           {             
               if(i == 0){
            	   //right rectangle
                   pt = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, x_off, y_off);
                   ra = pt.x;
                   dec = pt.y;
                   ra = ra - width/2;           //go to top left corner
                   dec = dec - height/2;
                   Rectangle2D.Double rect = new Rectangle2D.Double(ra, dec, width, height);
                   compFig[i] = dig.makeFigure(rect, fillColor, outlineColor, outlineWidth, interactor);
               }
               if(i == 1){
            	   //left rectangle
//                   pt = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, x_off1, y_off1);
//                   ra = pt.x;
//                   dec = pt.y;
//                   ra = ra - width/2;           //go to top left corner
//                   dec = dec - height/2;
//                   Rectangle2D.Double rect = new Rectangle2D.Double(ra, dec, width, height);
//                   compFig[i] = dig.makeFigure(rect, fillColor, outlineColor, outlineWidth,
//                           interactor);
            	   
//            	   //added line code
//            	   Point2D.Double pt2 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, x_off, y_off);
//                   Point2D.Double pt1 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, x_off1, y_off1);
//                   Line2D.Double line1 = new Line2D.Double(pt1, pt2);
//                   pt1 = new  Point2D.Double(line1.getBounds2D().getCenterX(), line1.getBounds2D().getY());
//                   line1 = new Line2D.Double(pt1, pt2);
//                   compFig[i] = dig.makeFigure(line1, fillColor, outlineColor, (outlineWidth + 4), null);
            	   
            	   		//small rectangle
						Point2D.Double pt2 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, x_off, y_off);
						Point2D.Double pt1 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, x_off1, y_off1);
						Line2D.Double line1 = new Line2D.Double(pt1, pt2);
						pt1 = new  Point2D.Double(line1.getBounds2D().getCenterX(), line1.getBounds2D().getY());
						Rectangle2D.Double rect = new Rectangle2D.Double(pt1.getX(), pt1.getY(), (pt2.getX() - pt1.getX()), 50);
						compFig[i] = dig.makeFigure(rect, fillColor, outlineColor, outlineWidth, interactor);
               }
               if(i == 2){
            	   //line
                    Point2D.Double pt2 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, x_off, y_off);
                    Point2D.Double pt1 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, x_off1, y_off1);
                    Line2D.Double line1 = new Line2D.Double(pt1, pt2) ;
                    compFig[i] = dig.makeFigure(line1, fillColor, outlineColor, outlineWidth,null);
               }               
               if(compFig[i] instanceof RotatableCanvasFigure) {
                    ((RotatableCanvasFigure)compFig[i]).setResizable(false);
               }
               dig.add(compFig[i]);
               compFig[i].setVisible(false);
           }
//           compFig[2].addSlave(compFig[0]);
//           compFig[2].addSlave(compFig[1]);
           return compFig;
       }
       else if(figType.equals(FIGURE_TYPE_MOBIE_GUIDER)){
           double radius = (Double)props.get(RADIUS);
           double x_off = (Double)props.get(CENTER_OFFSET_X); //in degrees
           double y_off = (Double)props.get(CENTER_OFFSET_Y);
           double radius1 = (Double)props.get(RADIUS1);
           double flag_x=1;
           double flag_y=1;
//           double flag_x1=1;
//           double flag_y1=1;
           Arc2D.Double arc = null;
           CanvasFigure[] doubleArc = new CanvasFigure[4];
           if(x_off < 0.0) {
               flag_x=-1;
           }
           if(y_off < 0.0) {
               flag_y=-1;
           }
           for(int i=0;i<doubleArc.length;i++){
               Point2D.Double wcsCenter = cc.getWCSCenter();
               double ra = wcsCenter.x ;    //RA in degrees
               double dec = wcsCenter.y ;    //DEC
               double factor = Math.cos(Math.toRadians(dec)); //less than 1
               Point2D.Double pt = new Point2D.Double(ra, dec);
               cc.worldToScreenCoords(pt, false);
               ra = pt.x;     //in pixels now
               dec = pt.y;              
               if(i==0){
                   pt = new Point2D.Double(x_off*factor, y_off); //works only for positive offsets
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
                   int type = (Integer)props.get(ARC_END);
                   //axis goes from east -> west
                   double startAngle = -1.0 * (Double)props.get(ARC_START_ANGLE); //(Double)props.get(ARC_START_ANGLE);
                   double angleExtent = (Double)props.get(ARC_ANGLE_EXTENT);//(Double)props.get(ARC_ANGLE_EXTENT);
                   arc = new Arc2D.Double(ra, dec, 2*radius, 2*radius, startAngle,
                       angleExtent, type);
                   doubleArc[i] = dig.makeFigure(arc, fillColor, outlineColor, outlineWidth,
                       interactor);
               }
               //second arc
               if(i==1){
                   pt = new Point2D.Double(x_off*factor, y_off); //works only for positive offsets
                   cc.worldToScreenCoords(pt, true);
                   x_off = pt.x;     //in pixels now
                   y_off = pt.y;
                 //axis goes from east <-- west
                   ra = ra + x_off*flag_x;    //RA
                   dec = dec + y_off*flag_y;    //DEC
                   pt = new Point2D.Double(radius1*factor, radius1);
                   cc.worldToScreenCoords(pt, true);
                   radius1 = pt.x;
                   ra = ra - radius1 ;
                   dec = dec - radius1 ;
                   int type = (Integer)props.get(ARC_END);
                   //axis goes from east -> west
                   double startAngle = -1.0 * (Double)props.get(ARC_START_ANGLE); //(Double)props.get(ARC_START_ANGLE);
                   double angleExtent = (Double)props.get(ARC_ANGLE_EXTENT);//(Double)props.get(ARC_ANGLE_EXTENT);
                   arc = new Arc2D.Double(ra, dec, 2*radius1, 2*radius1, startAngle,
                       angleExtent, type);
                   doubleArc[i] = dig.makeFigure(arc, fillColor, outlineColor, outlineWidth,
                       interactor);
               }
               if(i==2){
                    double topXline1 = 0.015834d;
                    double topYline1 = -0.027424d;
                    double bottomXline1 = 0.005834;
                    double bottomYline1 = -0.010104;
                    Point2D.Double pt2 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, bottomXline1, bottomYline1);
                    Point2D.Double pt1 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, topXline1, topYline1);
                    Line2D.Double line1 = new Line2D.Double(pt1, pt2) ;
                   doubleArc[i] = dig.makeFigure(line1, fillColor, outlineColor, outlineWidth,null);
               }
               if(i==3){
                    double topXline1 = 0.015834d;
                    double topYline1 = 0.027424d;
                    double bottomXline1 = 0.005834;
                    double bottomYline1 = 0.010104;
                    Point2D.Double pt2 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, bottomXline1, bottomYline1);
                    Point2D.Double pt1 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, topXline1, topYline1);
                    Line2D.Double line1 = new Line2D.Double(pt1, pt2) ;
                   doubleArc[i] = dig.makeFigure(line1, fillColor, outlineColor, outlineWidth,null);
               }
               //turns off resizing
               if(doubleArc[i] instanceof RotatableCanvasFigure) {
                    ((RotatableCanvasFigure)doubleArc[0]).setResizable(false);
               }
               dig.add(doubleArc[i]);
               doubleArc[i].setVisible(false);
           }
           return doubleArc;
       }
       else if(figType.equals(FIGURE_TYPE_CIRCLE)) {
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
           pt = new Point2D.Double(x_off*factor, y_off); //works only for positive offsets
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

           fig = dig.makeFigure(ell, fillColor, outlineColor, outlineWidth,
                   interactor);
           //turns off resizing
           if(fig instanceof RotatableCanvasFigure) {
                ((RotatableCanvasFigure)fig).setResizable(false);
           }
           dig.add(fig);
           fig.setVisible(false);
           return new CanvasFigure[]{fig};

       }else if(figType.equals(FIGURE_TYPE_DOUBLE_CIRCLE)){
           CanvasFigure[] doubleCircle = new CanvasFigure[2];
           Ellipse2D.Double ell=null;
           double radius = (Double)props.get(RADIUS);
           double radius1 = (Double)props.get(RADIUS1);
           for(int i=0;i<doubleCircle.length;i++){
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
               if(i == 1){
                   pt = new Point2D.Double(x_off*factor, y_off); //works only for positive offsets
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
                   ell = new Ellipse2D.Double(ra, dec, 2*radius, 2*radius);
               }
               if(i == 0){
                   pt = new Point2D.Double(x_off*factor, y_off); //works only for positive offsets
                   cc.worldToScreenCoords(pt, true);
                   x_off = pt.x;     //in pixels now
                   y_off = pt.y;
                 //axis goes from east <-- west
                   ra = ra + x_off*flag_x;    //RA
                   dec = dec + y_off*flag_y;    //DEC
                   pt = new Point2D.Double(radius1*factor, radius1);
                   cc.worldToScreenCoords(pt, true);
                   radius1 = pt.x;
                   ra = ra - radius1 ;
                   dec = dec - radius1 ;
                   ell = new Ellipse2D.Double(ra, dec, 2*radius1, 2*radius1);
               }
               doubleCircle[i] = dig.makeFigure(ell, fillColor, outlineColor, outlineWidth,
                   interactor);
               //turns off resizing
               if(doubleCircle[i] instanceof RotatableCanvasFigure) {
                    ((RotatableCanvasFigure)doubleCircle[i]).setResizable(false);
               }
               dig.add(doubleCircle[i]);
               doubleCircle[i].setVisible(false);
           }
           doubleCircle[1].addSlave(doubleCircle[0]);
           return doubleCircle;
       }
       else if(figType.equals(FIGURE_TYPE_ARC)) {
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
           pt = new Point2D.Double(x_off*factor, y_off); //works only for positive offsets
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
       else if(figType.equals(FIGURE_TYPE_DOUBLE_ARC)) {
           double radius = (Double)props.get(RADIUS);
           double x_off = (Double)props.get(CENTER_OFFSET_X); //in degrees
           double y_off = (Double)props.get(CENTER_OFFSET_Y);
           double radius1 = (Double)props.get(RADIUS1);
           double x_off1 = (Double)props.get(CENTER_OFFSET_X1); //in degrees
           double y_off1 = (Double)props.get(CENTER_OFFSET_Y1);
           double flag_x=1;
           double flag_y=1;
           double flag_x1=1;
           double flag_y1=1;
           Arc2D.Double arc = null;

           CanvasFigure[] doubleArc = new CanvasFigure[2];
           if(x_off < 0.0) {
               flag_x=-1;
           }
           if(y_off < 0.0) {
               flag_y=-1;
           }
           if(x_off1 < 0.0) {
               flag_x1=-1;
           }
           if(y_off1 < 0.0) {
               flag_y1=-1;
           }
           for(int i=0;i<doubleArc.length;i++){
               Point2D.Double wcsCenter = cc.getWCSCenter();
               double ra = wcsCenter.x ;    //RA in degrees
               double dec = wcsCenter.y ;    //DEC
               double factor = Math.cos(Math.toRadians(dec)); //less than 1
               Point2D.Double pt = new Point2D.Double(ra, dec);
               cc.worldToScreenCoords(pt, false);
               ra = pt.x;     //in pixels now
               dec = pt.y;
               if(i==0){
                   pt = new Point2D.Double(x_off*factor, y_off); //works only for positive offsets
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
                   int type = (Integer)props.get(ARC_END);
                   //axis goes from east -> west
                   double startAngle = -1.0 * (Double)props.get(ARC_START_ANGLE); //(Double)props.get(ARC_START_ANGLE);
                   double angleExtent = (Double)props.get(ARC_ANGLE_EXTENT);//(Double)props.get(ARC_ANGLE_EXTENT);
                   arc = new Arc2D.Double(ra, dec, 2*radius, 2*radius, startAngle,
                       angleExtent, type);
               }
               //second arc
               if(i==1){
                   pt = new Point2D.Double(x_off1*factor, y_off1); //works only for positive offsets
                   cc.worldToScreenCoords(pt, true);
                   x_off1 = pt.x;     //in pixels now
                   y_off1 = pt.y;
                 //axis goes from east <-- west
                   ra = ra + x_off1*flag_x1;    //RA
                   dec = dec + y_off1*flag_y1;    //DEC
                   pt = new Point2D.Double(radius1*factor, radius1);
                   cc.worldToScreenCoords(pt, true);
                   radius1 = pt.x;
                   ra = ra - radius1 ;
                   dec = dec - radius1 ;
                   int type = (Integer)props.get(ARC_END1);
                   //axis goes from east -> west
                   double startAngle = -1.0 * (Double)props.get(ARC_START_ANGLE1); //(Double)props.get(ARC_START_ANGLE);
                   double angleExtent = (Double)props.get(ARC_ANGLE_EXTENT1);//(Double)props.get(ARC_ANGLE_EXTENT);
                   arc = new Arc2D.Double(ra, dec, 2*radius1, 2*radius1, startAngle,
                       angleExtent, type);
               }              
               doubleArc[i] = dig.makeFigure(arc, fillColor, outlineColor, outlineWidth,
                       interactor);
               //turns off resizing
               if(doubleArc[i] instanceof RotatableCanvasFigure) {
                    ((RotatableCanvasFigure)doubleArc[0]).setResizable(false);
               }
               dig.add(doubleArc[i]);
               doubleArc[i].setVisible(false);
           }
           return doubleArc;
       }

        else if(figType.equals(FIGURE_TYPE_NFIRAOS_ASTERISM)) {
           //we have to draw 6 cricles with one at center and others on a pentagon
           //of radius 35 arc sec
           //double pentagonRadius = props.get(RADIUS);
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
           pt = new Point2D.Double(x_off*factor, y_off); //works only for positive offsets
           cc.worldToScreenCoords(pt, true);
           x_off = pt.x;     //in pixels now
           y_off = pt.y;
         //axis goes from east <-- west
           ra = ra + x_off*flag_x;    //RA
           dec = dec + y_off*flag_y;    //DEC
           pt = new Point2D.Double(radius*factor, radius);
           cc.worldToScreenCoords(pt, true);
           radius = pt.x;

           double radius1 = 0.325/3600d; //10 arc sec
           pt = new Point2D.Double(radius1*factor, radius1);
           cc.worldToScreenCoords(pt, true);
           radius1 = pt.x;

           CanvasFigure[] stars = new CanvasFigure[6];
           //CanvasFigureGroup starGroup = dig.makeFigureGroup();
           //assuming pentagon points north ..
           //starts are at 18, 90, 162, 264, 306 degrees from x+ve axis.
           for(int i=0; i<stars.length; i++)  {
               double tmpX = 0;
               double tmpY = 0;
               if(i==0) { //center star
                    tmpX = ra;
                    tmpY = dec;
               } else { //other stars on pentagon
                  double angle = Math.toRadians(18 + 72*(i-1));
                  tmpX = ra + radius * Math.cos(angle);
                  //- as swing origin is at left north
                  tmpY = dec - radius * Math.sin(angle);
               }
                tmpX = tmpX - radius1 ;
                tmpY = tmpY - radius1 ;
                Ellipse2D.Double ell = new Ellipse2D.Double(tmpX, tmpY, 2*radius1, 2*radius1);


               stars[i] = dig.makeFigure(ell, fillColor,
                       outlineColor, outlineWidth, interactor);
               if(stars[i] instanceof RotatableCanvasFigure) {
                   ((RotatableCanvasFigure)stars[i]).setResizable(false);
               }
               dig.add(stars[i]);
               stars[i].setVisible(false);
           }
           return stars;
       }
       else if(figType.equals(FIGURE_TYPE_PROBETIP)) {         
           double width = (Double)props.get(WIDTH_X);//in degrees
           double height = (Double)props.get(WIDTH_Y);
           double radius = (Double)props.get(RADIUS);
           CanvasFigure[] probe = new CanvasFigure[3];
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
               
               pt = new Point2D.Double(x_off*factor, y_off); //works only for positive offsets
               cc.worldToScreenCoords(pt, true);
               x_off = pt.x;     //in pixels now
               y_off = pt.y;
             //axis goes from east <-- west
               ra = ra + x_off*flag_x;    //RA
               dec = dec + y_off*flag_y;    //DEC

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
                   
                   probe[i] = dig.makeFigure(rect, fillColor, outlineColor, outlineWidth,null);
                   //turns off resizing
                   if(probe[i] instanceof RotatableCanvasFigure) {
                        ((RotatableCanvasFigure)probe[i]).setResizable(false);
                   }
                   dig.add(probe[i]);
                   probe[i].setVisible(false);
                }
                else if(i == 2){
//                    double topXline1 = (Double)props.get(ARM_Line1_TOP_X);
//                    double topYline1 = (Double)props.get(ARM_Line1_TOP_Y);
//                    double bottomXline1 = (Double)props.get(ARM_Line1_BOTTOM_X);
//                    double bottomYline1 = (Double)props.get(ARM_Line1_BOTTOM_Y);
//                    Point2D.Double pt2 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, bottomXline1, bottomYline1);
//                    Point2D.Double pt1 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, topXline1, topYline1);
//                    Line2D.Double line1 = new Line2D.Double(pt1, pt2) ;
//                    probe[i] = dig.makeFigure(line1, fillColor, outlineColor, outlineWidth,null);
                	
                	//draw rectangle instead of lines
                    double topXline1 = (Double)props.get(ARM_Line1_TOP_X);
                    double topYline1 = (Double)props.get(ARM_Line1_TOP_Y);
                    double bottomXline1 = (Double)props.get(ARM_Line1_BOTTOM_X);
                    double bottomYline1 = (Double)props.get(ARM_Line1_BOTTOM_Y);
					Point2D.Double pt1 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, topXline1, topYline1);
                    Point2D.Double pt2 = DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, bottomXline1, bottomYline1);
					//to calculate theta
		            double rotationangle = calculateArmAngle(topXline1, topYline1, bottomXline1, bottomYline1);
					//add 180
		            rotationangle = rotationangle + Math.toRadians(180);
		            
		          //calculate width of all three rectangles
		          //width in degrees
		          double rectanleWidth = 3.6377 / 3600d;
		          double newfactor = Math.cos(Math.toRadians(wcsCenter.y)); //less than 1
		          Point2D.Double temppt = new Point2D.Double(rectanleWidth * newfactor, rectanleWidth);
		          fovastImageDisplay.getCoordinateConverter().worldToScreenCoords(temppt, true);
		          //width in pixels
		          rectanleWidth = temppt.getX();
		          
//		          probe[i] = updateArmRectangles(dig, pt1, pt2, fillColor, outlineColor, rectanleWidth, outlineWidth, rotationangle, null);
		          probe[i] = updateArmRectangles(dig, pt1, pt2, outlineColor, outlineColor, rectanleWidth, outlineWidth, rotationangle, null);
					
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
       else if(figType.equals(FIGURE_TYPE_FOCUS_PROBETIP)){
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

//               double ra1 = ra;
//               double dec1 = dec;
               pt = new Point2D.Double(x_off*factor, y_off); //works only for positive offsets
               cc.worldToScreenCoords(pt, true);
               x_off = pt.x;     //in pixels now
               y_off = pt.y;
             //axis goes from east <-- west
               ra = ra + x_off*flag_x;    //RA
               dec = dec + y_off*flag_y;    //DEC
               
               if(i == 1)
               {
                   pt = new Point2D.Double(radius*factor, radius);
                   cc.worldToScreenCoords(pt, true);
                   radius = pt.x;
                   ra = ra - radius ;
                   dec = dec - radius ;
                   Ellipse2D.Double ell = new Ellipse2D.Double(ra, dec, 2*radius, 2*radius);

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

        //final HashMap<String, CanvasFigure[]> map = new HashMap<String, CanvasFigure[]>();

        //make fig-grp1
//        DivaImageGraphics dig = (DivaImageGraphics) fovastImageDisplay.getCanvasGraphics();
//        CanvasFigureGroup cfgIris = dig.makeFigureGroup();

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

        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 0.92/60d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_NO);
        props.put(OUTLINE_COLOR, Color.WHITE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 0f);
        CanvasFigure[] nfiraosLimitsFigs1 = makeFigure(props);
        map.put("nfiraos.limits1", nfiraosLimitsFigs1);
        
        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 0.83333338/60d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_NO);
        props.put(OUTLINE_COLOR, Color.WHITE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 0f);
        CanvasFigure[] nfiraosLimitsFigs2 = makeFigure(props);
        map.put("nfiraos.limits2", nfiraosLimitsFigs2);

        //nfiraos.lsgasterism (always fixed)
        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_NFIRAOS_ASTERISM);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS,35/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.YELLOW);
        props.put(FILL, FILL_OUTLINE_YES);
        props.put(FILL_COLOR,  Color.YELLOW);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] lsgFigures = makeFigure(props);
        map.put("nfiraos.lsgasterism", lsgFigures);

        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_CROSS);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 1/3600d);
        props.put(WIDTH_Y, 1/3600d);
        props.put(WIDTH_X, 1/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] sweetSpotImagingModeFigs = makeFigure(props);
        map.put("iris.sweetspot.imaging", sweetSpotImagingModeFigs);

        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_CROSS);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, (18.0/3600d));
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 1/3600d);
        props.put(WIDTH_Y, 1/3600d);
        props.put(WIDTH_X, 1/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] sweetSpotIfuModeFigs = makeFigure(props);
        map.put("iris.sweetspot.ifu", sweetSpotIfuModeFigs);

        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_CROSS);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0.0025d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 1/3600d);
        props.put(WIDTH_Y, 1/3600d);
        props.put(WIDTH_X, 1/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] sweetSpotBothFigs = makeFigure(props);
        map.put("iris.sweetspot.both", sweetSpotBothFigs);


        //iris.ifuimager.lenslet
        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_RECTANGLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        //max fov is 1.01" x 1.15"
        //18" of axis .. so 18" + 1.01/2
        props.put(CENTER_OFFSET_X, (18.0/3600d)); // + 1.01/2)/3600d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(WIDTH_Y, 1.01/3600d);
        props.put(WIDTH_X, 1.15/3600d);
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
        props.put(CENTER_OFFSET_X, (18.0/3600d));// + 4.4/2)/3600d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(WIDTH_Y, 4.4/3600d);
        props.put(WIDTH_X, 2.25/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] slicerFigs = makeFigure(props);
        //cfgIris.add(fig);
        map.put("iris.ifuimager.slicer", slicerFigs);
        
        //mobie
        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, (5.4)/60d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 5.239275/60d);
        props.put(ARC_END, ARC_END_CHORD);
        props.put(ARC_START_ANGLE, 90d);
        props.put(ARC_ANGLE_EXTENT, 180d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.RED);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] mobieWavefrontSensorFigs = makeFigure(props);
        map.put("mobie.wavefrontSensorFOV", mobieWavefrontSensorFigs);

        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_MOBIE_GUIDER);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(CENTER_OFFSET_X1,1.3/60d);
        props.put(CENTER_OFFSET_Y1, 0d);
        props.put(RADIUS, 1.9/60d);
        props.put(RADIUS1, 0.7/60d);
        props.put(RADIUS2, 10.5/3600d);
        props.put(ARC_ANGLE_EXTENT, 120.0d);
        props.put(ARC_END,ARC_END_OPEN);
        props.put(ARC_START_ANGLE,60.0d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.RED);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] mobieGuiderLimitsFigs = makeFigure(props);
        map.put("mobie.guider.limits", mobieGuiderLimitsFigs);

        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_ARC);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 0.0145867d);
        props.put(ARC_ANGLE_EXTENT, 120.0d);
        props.put(ARC_END,ARC_END_PIE);
        props.put(ARC_START_ANGLE,60.0d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.RED);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] mobieGuiderLimitsSmallFigs = makeFigure(props);
        map.put("mobie.guider.limits.small", mobieGuiderLimitsSmallFigs);

        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_ARC);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 0.0287534d);
        props.put(ARC_ANGLE_EXTENT, 100.0d);
        props.put(ARC_END,ARC_END_PIE);
        props.put(ARC_START_ANGLE,50.0d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.RED);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] mobieGuiderLimitsLargeFigs = makeFigure(props);
        map.put("mobie.guider.limits.large", mobieGuiderLimitsLargeFigs);

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
        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 5.4/60d);
        props.put(ARC_END, ARC_END_CHORD);
        props.put(ARC_START_ANGLE, 90d);
        props.put(ARC_ANGLE_EXTENT, 180d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.WHITE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] mobieFigures2 = makeFigure(props);
        map.put("mobie.limits", mobieFigures2);     

//        props = new HashMap<String, Object>();
//        props.put(FIGURE_TYPE, FIGURE_TYPE_RECTANGLE);
//        props.put(ROTATABLE, false);
//        props.put(MOVEABLE, true);
//        //MOBIE DIMENSIONS ARE 4.2 x 9.6
//        props.put(CENTER_OFFSET_X, (5.4)/60d); // 5.4' offet-x
//        props.put(CENTER_OFFSET_Y, 0d);
//        props.put(WIDTH_X, 4.2/60d);
//        props.put(WIDTH_Y, 9.6/60d);
//        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
//        props.put(OUTLINE_COLOR, Color.BLUE);
//        props.put(FILL, FILL_OUTLINE_NO);
//        props.put(OUTLINE_WIDTH, 1.0f);
//        CanvasFigure[] mobieDetectorFigs = makeFigure(props);
//        map.put("mobie.detector", mobieDetectorFigs);
//        
//        //TUSHAR
//        MobieDetectorConstraint mdc = new MobieDetectorConstraint(mobieDetectorFigs[0], fovastImageDisplay, map); 
//        ((DragInteractor)mobieDetectorFigs[0].getInteractor()).appendConstraint(mdc);        

        props = new HashMap<String, Object>();
         props.put(FIGURE_TYPE, FIGURE_TYPE_MOBIE_DETECTOR);
         props.put(ROTATABLE, false);
         props.put(MOVEABLE, false);
         //MOBIE DIMENSIONS ARE 4.2 x 9.6
         props.put(CENTER_OFFSET_X, (5.4)/60d); // 5.4' offet-x
         props.put(CENTER_OFFSET_Y, 0d);
         props.put(CENTER_OFFSET_X1,-(5.4)/60d); // -5.4' offet-x
         props.put(CENTER_OFFSET_Y1, 0d);
         props.put(WIDTH_X, 4.2/60d);
         props.put(WIDTH_Y, 9.6/60d);
         props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
         props.put(OUTLINE_COLOR, Color.RED);
         props.put(FILL, FILL_OUTLINE_NO);
         props.put(OUTLINE_WIDTH, 1.0f);
         //store in temp values, need to add sciencedetector to this only.
         CanvasFigure[] mobieDetectorFigs = makeFigure(props);
         map.put("mobie.detector", mobieDetectorFigs);

         //now add dragger
         props = new HashMap<String, Object>();
         props.put(FIGURE_TYPE, FIGURE_TYPE_RECTANGLE);
         props.put(ROTATABLE, false);
         props.put(MOVEABLE, true);
//        props.put(CENTER_OFFSET_X, 0d);
         props.put(CENTER_OFFSET_X, (5.4)/108d);
         props.put(CENTER_OFFSET_Y, 0d);
         props.put(WIDTH_X, 16.4/3600d); //16.4 arcsec
         props.put(WIDTH_Y, 16.4/3600d);
         props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
         props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_YES);
        props.put(FILL_COLOR,  Color.GREEN);
         props.put(OUTLINE_WIDTH, 1.0f);
         CanvasFigure[] mobieDragFig = makeFigure(props);
         map.put("mobie.drag", mobieDragFig);

//        //add CanvasFigureListener
//        mobieDragFig[0].addCanvasFigureListener(new MobieDetectorFigListener());
         if(((Boolean) props.get(ROTATABLE)).booleanValue() == false)
         {
             MobieDetectorConstraint mdc = new MobieDetectorConstraint(mobieDragFig[0], fovastImageDisplay, map);
             ((DragInteractor)mobieDragFig[0].getInteractor()).appendConstraint(mdc);
         }
     
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
        props.put(RADIUS, 2.29266474/60d);
        props.put(ARC_ANGLE_EXTENT, 51.72);
        props.put(ARC_END,ARC_END_PIE);
        props.put(ARC_START_ANGLE,115.86);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.WHITE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] tempIrisProbeLimits1 = makeFigure(props);
        map.put("iris.oiwfs.probe1.limits1", tempIrisProbeLimits1);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_ARC);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, -0.033091765d);
        props.put(CENTER_OFFSET_Y, 0.019105539d);
        props.put(RADIUS,2.29266474/60d);
        props.put(ARC_ANGLE_EXTENT, 51.72);
        props.put(ARC_END,ARC_END_PIE);
        props.put(ARC_START_ANGLE,355.86);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.WHITE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] tempIrisProbeLimits2 = makeFigure(props);
        map.put("iris.oiwfs.probe2.limits1", tempIrisProbeLimits2);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_ARC);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0.033091765d);
        props.put(CENTER_OFFSET_Y, 0.019105539d);
        props.put(RADIUS, 2.29266474/60d);
        props.put(ARC_ANGLE_EXTENT, 51.72);
        props.put(ARC_END,ARC_END_PIE);
        props.put(ARC_START_ANGLE,235.86);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.WHITE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] tempIrisProbeLimits3 = makeFigure(props);
        map.put("iris.oiwfs.probe3.limits1", tempIrisProbeLimits3);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_DOUBLE_ARC);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, -2.292664743/60d);
        props.put(RADIUS, 2.375998076/60d);
        props.put(ARC_ANGLE_EXTENT, 51.72);
        props.put(ARC_END,ARC_END_PIE);
        props.put(ARC_START_ANGLE,115.86);
        props.put(CENTER_OFFSET_X1, 0d);
        props.put(CENTER_OFFSET_Y1, 0d);
        props.put(RADIUS1, 1/60d);
        props.put(ARC_ANGLE_EXTENT1, 163.99);
        props.put(ARC_END1,ARC_END_OPEN);
        props.put(ARC_START_ANGLE1,-8.0);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.RED);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisProbeLimits1 = makeFigure(props);
        map.put("iris.oiwfs.probe1.limits", irisProbeLimits1);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_DOUBLE_ARC);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, -0.033091765d);
        props.put(CENTER_OFFSET_Y, 0.019105539d);
        props.put(RADIUS,2.375998076/60d);
        props.put(ARC_ANGLE_EXTENT, 51.72);
        props.put(ARC_END,ARC_END_PIE);
        props.put(ARC_START_ANGLE,355.86);
        props.put(CENTER_OFFSET_X1, 0d);
        props.put(CENTER_OFFSET_Y1, 0d);
        props.put(RADIUS1, 1/60d);
        props.put(ARC_ANGLE_EXTENT1, 163.99);
        props.put(ARC_END1,ARC_END_OPEN);
        props.put(ARC_START_ANGLE1,-128.0);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisProbeLimits2 = makeFigure(props);
        map.put("iris.oiwfs.probe2.limits", irisProbeLimits2);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_DOUBLE_ARC);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, false);
        props.put(CENTER_OFFSET_X, 0.033091765d);
        props.put(CENTER_OFFSET_Y, 0.019105539d);
        props.put(RADIUS, 2.375998076/60d);
        props.put(ARC_ANGLE_EXTENT, 51.72);
        props.put(ARC_END,ARC_END_PIE);
        props.put(ARC_START_ANGLE,235.86);
        props.put(CENTER_OFFSET_X1, 0d);
        props.put(CENTER_OFFSET_Y1, 0d);
        props.put(RADIUS1, 1/60d);
        props.put(ARC_ANGLE_EXTENT1, 163.99);
        props.put(ARC_END1,ARC_END_OPEN);
        props.put(ARC_START_ANGLE1,-248.0);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.BLUE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisProbeLimits3 = makeFigure(props);
        map.put("iris.oiwfs.probe3.limits", irisProbeLimits3);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_Y, -0.013d);
        props.put(RADIUS, 10 / 3600d);
        props.put(WIDTH_X, 4/3600d); //1 arcsec
        props.put(WIDTH_Y, 4/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.RED);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 3.0f);
        final CanvasFigure[] irisProbeContraint1 = makeFigure(props);
        map.put("iris.oiwfs.probe1.contraint", irisProbeContraint1);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_FOCUS_PROBETIP);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, 1/3600d);
        props.put(CENTER_OFFSET_Y, -0.013d);
        props.put(ARM_Line1_TOP_X, 0d);
        props.put(ARM_Line1_TOP_Y, -2.292664743/60d);
//        props.put(ARM_Line2_TOP_X, 0d);
//        props.put(ARM_Line2_TOP_Y, -2.1/60d);
        props.put(ARM_Line1_BOTTOM_X, 0d);
        props.put(ARM_Line1_BOTTOM_Y, -0.013d);
//        props.put(ARM_Line2_BOTTOM_X, 0d);
//        props.put(ARM_Line2_BOTTOM_Y, 0d);
        props.put(RADIUS,  CIRCLE_RADIUS / 3600d);
        props.put(WIDTH_X, 4/3600d); //1 arcsec
        props.put(WIDTH_Y, 4/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.RED);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 3.0f);
        final CanvasFigure[] irisProbeFocus1 = makeFigure(props);
        map.put("iris.oiwfs.probe1.focus", irisProbeFocus1);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_PROBETIP);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
//        props.put(CENTER_OFFSET_X, 0d);
        props.put(CENTER_OFFSET_X, 1 / 3600d);
        props.put(CENTER_OFFSET_Y, -0.013d);
        props.put(ARM_Line1_TOP_X, 0d);
        props.put(ARM_Line1_TOP_Y, -2.292664743/60d);
//        props.put(ARM_Line2_TOP_X, 0d);
        props.put(ARM_Line1_BOTTOM_X, 1 / 3600d);
        props.put(ARM_Line1_BOTTOM_Y, -0.013d);
//        props.put(ARM_Line2_BOTTOM_X, 0d);
//        props.put(ARM_Line2_BOTTOM_Y, 0d);
        props.put(RADIUS, CIRCLE_RADIUS/3600d);
        props.put(WIDTH_X, 4/3600d); //1 arcsec
        props.put(WIDTH_Y, 4/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.RED);
        props.put(FILL, FILL_OUTLINE_YES);
//        props.put(FILL_COLOR,  Color.RED);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisProbeArm1 = makeFigure(props);
        map.put("iris.oiwfs.probe1.arm", irisProbeArm1);
        irisProbeArm1[1].addSlave(irisProbeFocus1[1]);
        irisProbeArm1[1].addSlave(irisProbeContraint1[0]);
        irisProbeArm1[1].addCanvasFigureListener(new CanvasFigureListener() {

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
                //do nothing
            }

            @Override
            public void figureMoved(CanvasFigureEvent e) {
                CanvasFigure[] figs1 = map.get("iris.oiwfs.probe1.arm");
                CanvasFigure probArmFig = figs1[0];
                Shape shape1 = probArmFig.getShape();
                double centerX = shape1.getBounds2D().getCenterX();
                double centerY = shape1.getBounds2D().getCenterY();
                Point2D.Double centerPt = new Point2D.Double(centerX, centerY);
                CoordinateConverter cc = fovastImageDisplay.getCoordinateConverter();
                cc.screenToWorldCoords(centerPt, false);
                String centerString = centerPt.getX()+","+centerPt.getY();
                config.setConfigElementProperty("iris.oiwfs.probe1.arm", "position", centerString);
                String tempString;
                if(probArmFig.isVisible()){
                    tempString = "true";
                }else{
                    tempString = "false";
                }
                config.setConfigElementProperty("iris.oiwfs.probe1.arm", "isVisible",tempString);
            }
        });
        CanvasFigure probFig1 = irisProbeArm1[1];
        DragInteractor dragInteractor =  (DragInteractor) probFig1.getInteractor();
        
        dragInteractor.appendConstraint(new PointConstraint() {
            Point2D prevPt = null;
            CanvasFigure iris_oiwfs_probe1_line = null;

            @Override
            public void constrain(Point2D pt) {
                CanvasFigure[] figs = map.get("iris.oiwfs.probe1.limits1");
                CanvasFigure probLimitsFig = figs[PROBETIP_RECTANGLE_INDEX];
                Shape shape = probLimitsFig.getShape();

                CanvasFigure[] figs2 = map.get("nfiraos.limits1");
                CanvasFigure nfiraosLimitsFig = figs2[PROBETIP_RECTANGLE_INDEX];
                Shape shape2 = nfiraosLimitsFig.getShape();

                CanvasFigure[] figs1 = map.get("iris.oiwfs.probe1.arm");
                CanvasFigure probArmFig = figs1[PROBETIP_RECTANGLE_INDEX];
                Shape shape1 = probArmFig.getShape();

                CanvasFigure[] figsProbe2 = map.get("iris.oiwfs.probe2.arm");
                CanvasFigure probArm2Fig = figsProbe2[PROBETIP_CIRCLE_INDEX];
                Shape shapeProbe2 = probArm2Fig.getShape();

                CanvasFigure[] figsProbe3 = map.get("iris.oiwfs.probe3.arm");
                CanvasFigure probArm3Fig = figsProbe3[PROBETIP_CIRCLE_INDEX];
                Shape shapeProbe3 = probArm3Fig.getShape();

                double centerX = shape1.getBounds2D().getCenterX();
                double centerY = shape1.getBounds2D().getCenterY();
//                Point2D.Double centerPt = new Point2D.Double(centerX, centerY);

                if(prevPt != null) {
                    Point2D.Double newCenterPt = new Point2D.Double(
                                centerX + (pt.getX() - prevPt.getX()),
                                centerY + (pt.getY() - prevPt.getY())
                    );
                    if(shape.contains(newCenterPt) && shape2.contains(newCenterPt)
                            && !shapeProbe2.contains(newCenterPt) && !shapeProbe3.contains(newCenterPt)){
                        prevPt = pt;
                        //leave pt as is
                    } else {
                        pt.setLocation(prevPt.getX(), prevPt.getY());
                    }
                } else {
                    prevPt = pt;
                }
                
                //tushar
                //below offsets are taken from initialisation code of arm / probe for fig1.
                Point2D.Double arm_line1_top = 
                	DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, 0, -2.292664743/60);
					
                // code to rotate object by some angle.
//	            CanvasFigure linefig = figs1[ARM_LINE_INDEX];
                CanvasFigure circlefig1 = figs1[PROBETIP_CIRCLE_INDEX];
                
                Point2D.Double circle1CenterPt =
                    new Point2D.Double(circlefig1.getShape().getBounds2D().getCenterX(),
                                                            circlefig1.getShape().getBounds2D().getCenterY());
                
				if(iris_oiwfs_probe1_line != null)
				{
					iris_oiwfs_probe1_line.setVisible(false);
					iris_oiwfs_probe1_line = null;
				}
//				//extend the line
//				DivaImageGraphics dig = (DivaImageGraphics) fovastImageDisplay.getCanvasGraphics();
//				iris_oiwfs_probe1_line = updateArmLines(dig, arm_line1_top, circle1CenterPt, null, Color.RED);
//				dig.remove(figs1[ARM_LINE_INDEX]);
//				figs1[ARM_LINE_INDEX] = iris_oiwfs_probe1_line;
//				figs1[ARM_LINE_INDEX].setVisible(true);
//				dig.add(figs1[ARM_LINE_INDEX]);
                
				//extend the rectangle
				float otwidth = 1.0f;				
				double rotationangle = calculateArmAngle(arm_line1_top.getX(), arm_line1_top.getY(), 
																						circle1CenterPt.getX(), circle1CenterPt.getY());
				rotationangle = rotationangle + Math.toRadians(180);
				
				//calculate width of all three rectangles
				//width in degrees
				double rectanleWidth = 3.6377 / 3600d;
				Point2D.Double wcsCenter = fovastImageDisplay.getCoordinateConverter().getWCSCenter();
				double newfactor = Math.cos(Math.toRadians(wcsCenter.y)); //less than 1
				Point2D.Double temppt = new Point2D.Double(rectanleWidth * newfactor, rectanleWidth);
				fovastImageDisplay.getCoordinateConverter().worldToScreenCoords(temppt, true);
				//width in pixels
				rectanleWidth = temppt.getX();
				
				DivaImageGraphics dig = (DivaImageGraphics) fovastImageDisplay.getCanvasGraphics();
				iris_oiwfs_probe1_line = updateArmRectangles(dig, arm_line1_top, circle1CenterPt, 
																								Color.RED, Color.RED, 
																								rectanleWidth, otwidth, rotationangle, null);
				dig.remove(figs1[ARM_LINE_INDEX]);
				figs1[ARM_LINE_INDEX] = iris_oiwfs_probe1_line;
				figs1[ARM_LINE_INDEX].setVisible(true);
				dig.add(figs1[ARM_LINE_INDEX]);
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
        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, -0.011d);
        props.put(CENTER_OFFSET_Y, +0.0065d);
        props.put(RADIUS, 10 / 3600d);
        props.put(WIDTH_X, 4/3600d); //1 arcsec
        props.put(WIDTH_Y, 4/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 3.0f);
        final CanvasFigure[] irisProbeContraint2 = makeFigure(props);
        map.put("iris.oiwfs.probe2.contraint", irisProbeContraint2);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_FOCUS_PROBETIP);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, -0.011d);
        props.put(CENTER_OFFSET_Y, +0.0065d);
        props.put(ARM_Line1_TOP_X, -0.033091765);
        props.put(ARM_Line1_TOP_Y, 0.019105539d);
//        props.put(ARM_Line2_TOP_X, 0d);
//        props.put(ARM_Line2_TOP_Y, 0d);
//        props.put(ARM_Line1_BOTTOM_X, -0.033091765d);
//        props.put(ARM_Line1_BOTTOM_Y, -0.019105539d);
        props.put(ARM_Line1_BOTTOM_X, -0.011d);
        props.put(ARM_Line1_BOTTOM_Y, +0.0065d);
//        props.put(ARM_Line2_BOTTOM_X, 0d);
//        props.put(ARM_Line2_BOTTOM_Y, 0d);
        props.put(RADIUS, CIRCLE_RADIUS / 3600d);
        props.put(WIDTH_X, 4/3600d); //1 arcsec
        props.put(WIDTH_Y, 4/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 3.0f);
        final CanvasFigure[] irisProbeFocus2 = makeFigure(props);
        map.put("iris.oiwfs.probe2.focus", irisProbeFocus2);
        
        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_PROBETIP);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, -0.011d);
        props.put(CENTER_OFFSET_Y, +0.0065d);
        props.put(ARM_Line1_TOP_X, -0.033091765);
        props.put(ARM_Line1_TOP_Y, 0.019105539d);
//        props.put(ARM_Line2_TOP_X, 0d);
//        props.put(ARM_Line2_TOP_Y, 0d);
//        props.put(ARM_Line1_BOTTOM_X, -0.033091765d);
//        props.put(ARM_Line1_BOTTOM_Y, -0.019105539d);
        props.put(ARM_Line1_BOTTOM_X, -0.011d);
        props.put(ARM_Line1_BOTTOM_Y, +0.0065d);
//        props.put(ARM_Line2_BOTTOM_X, 0d);
//        props.put(ARM_Line2_BOTTOM_Y, 0d);
        props.put(RADIUS, CIRCLE_RADIUS / 3600d);
        props.put(WIDTH_X, 4/3600d); //1 arcsec
        props.put(WIDTH_Y, 4/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_YES);
//        props.put(FILL_COLOR,  Color.GREEN);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisProbeArm2 = makeFigure(props);
        map.put("iris.oiwfs.probe2.arm", irisProbeArm2);
        irisProbeArm2[1].addSlave(irisProbeFocus2[1]);
        irisProbeArm2[1].addSlave(irisProbeContraint2[0]);
        irisProbeArm2[1].addCanvasFigureListener(new CanvasFigureListener() {

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
                //do nothing
            }

            @Override
            public void figureMoved(CanvasFigureEvent e) {
                CanvasFigure[] figs1 = map.get("iris.oiwfs.probe2.arm");
                CanvasFigure probArmFig = figs1[0];
                Shape shape1 = probArmFig.getShape();
                double centerX = shape1.getBounds2D().getCenterX();
                double centerY = shape1.getBounds2D().getCenterY();
                Point2D.Double centerPt = new Point2D.Double(centerX, centerY);
                CoordinateConverter cc = fovastImageDisplay.getCoordinateConverter();
                cc.screenToWorldCoords(centerPt, false);
                String centerString = centerPt.getX()+","+centerPt.getY();
                config.setConfigElementProperty("iris.oiwfs.probe2.arm", "position", centerString);
//                BasicFigure fig = new BasicFigure(irisProbeArm1[0].getShape());
//                fig.setStrokePaint(Color.YELLOW);
                //map.put("iris.oiwfs.probe2.arm",);
                String tempString;
                if(probArmFig.isVisible()){
                    tempString = "true";
                }else{
                    tempString = "false";
                }
                config.setConfigElementProperty("iris.oiwfs.probe2.arm", "isVisible",tempString);
            }
        });
        CanvasFigure probFig2 = irisProbeArm2[1];
        dragInteractor =  (DragInteractor) probFig2.getInteractor();
        dragInteractor.appendConstraint(new PointConstraint() {

            Point2D prevPt = null;
            CanvasFigure iris_oiwfs_probe2_line = null;
            @Override
            public void constrain(Point2D pt) {
            	CanvasFigure[] figs = map.get("iris.oiwfs.probe2.limits1");
                CanvasFigure probLimitsFig = figs[PROBETIP_RECTANGLE_INDEX];
                Shape shape = probLimitsFig.getShape();

                CanvasFigure[] figs2 = map.get("nfiraos.limits1");
                CanvasFigure nfiraosLimitsFig = figs2[PROBETIP_RECTANGLE_INDEX];
                Shape shape2 = nfiraosLimitsFig.getShape();

                CanvasFigure[] figs1 = map.get("iris.oiwfs.probe2.arm");
                CanvasFigure probArmFig = figs1[PROBETIP_RECTANGLE_INDEX];
                Shape shape1 = probArmFig.getShape();

                CanvasFigure[] figsProbe1 = map.get("iris.oiwfs.probe1.contraint");
                CanvasFigure probArm1Fig = figsProbe1[0];
                Shape shapeProbe1 = probArm1Fig.getShape();

                CanvasFigure[] figsProbe3 = map.get("iris.oiwfs.probe3.contraint");
                CanvasFigure probArm3Fig = figsProbe3[0];
                Shape shapeProbe3 = probArm3Fig.getShape();

                double centerX = shape1.getBounds2D().getCenterX();
                double centerY = shape1.getBounds2D().getCenterY();
//                Point2D.Double centerPt = new Point2D.Double(centerX, centerY);

                if(prevPt != null) {
                    Point2D.Double newCenterPt = new Point2D.Double(
                                centerX + (pt.getX() - prevPt.getX()),
                                centerY + (pt.getY() - prevPt.getY())
                    );
                    if(shape.contains(newCenterPt) && shape2.contains(newCenterPt) &&
                            !shapeProbe1.contains(newCenterPt) && !shapeProbe3.contains(newCenterPt)){
                        prevPt = pt;
                        //leave pt as is
                    } else {
                        pt.setLocation(prevPt.getX(), prevPt.getY());
                    }
                } else {
                    prevPt = pt;
                }

                //tushar
//                CanvasFigure linefig = figs1[ARM_LINE_INDEX];
	            CanvasFigure circlefig2 = figs1[PROBETIP_CIRCLE_INDEX];
	            
	           //below offsets are taken from initialisation code of arm / probe for fig2.
	            Point2D.Double arm_line2_top = 
	            	DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, -0.033091765, 0.019105539);
	            
	            Point2D.Double circle2CenterPt =
	                	new Point2D.Double(circlefig2.getShape().getBounds2D().getCenterX(),
	                											circlefig2.getShape().getBounds2D().getCenterY());
	            
				if(iris_oiwfs_probe2_line != null)
				{
					iris_oiwfs_probe2_line.setVisible(false);
					iris_oiwfs_probe2_line = null;
				}
//				//extend the line
//				DivaImageGraphics dig = (DivaImageGraphics) fovastImageDisplay.getCanvasGraphics();
//				iris_oiwfs_probe2_line = updateArmLines(dig, arm_line2_top, circle2CenterPt, null, Color.GREEN);
//				dig.remove(figs1[ARM_LINE_INDEX]);
//				figs1[ARM_LINE_INDEX] = iris_oiwfs_probe2_line;
//				figs1[ARM_LINE_INDEX].setVisible(true);
//				dig.add(figs1[ARM_LINE_INDEX]);
				
				//extend the rectangle
				float otwidth = 1.0f;
				double rotationangle = calculateArmAngle(arm_line2_top.getX(), arm_line2_top.getY(), 
																						circle2CenterPt.getX(), circle2CenterPt.getY());
				rotationangle = rotationangle + Math.toRadians(180);
				
				//calculate width of all three rectangles
				//width in degrees
				double rectanleWidth = 3.6377 / 3600d;
				Point2D.Double wcsCenter = fovastImageDisplay.getCoordinateConverter().getWCSCenter();
				double newfactor = Math.cos(Math.toRadians(wcsCenter.y)); //less than 1
				Point2D.Double temppt = new Point2D.Double(rectanleWidth * newfactor, rectanleWidth);
				fovastImageDisplay.getCoordinateConverter().worldToScreenCoords(temppt, true);
				// width in pixels
				rectanleWidth = temppt.getX();
		          
				DivaImageGraphics dig = (DivaImageGraphics) fovastImageDisplay.getCanvasGraphics();
				iris_oiwfs_probe2_line = updateArmRectangles(dig, arm_line2_top, circle2CenterPt, 
																								Color.GREEN, Color.GREEN, 
																								rectanleWidth, otwidth, rotationangle, null);
				dig.remove(figs1[ARM_LINE_INDEX]);
				figs1[ARM_LINE_INDEX] = iris_oiwfs_probe2_line;
				figs1[ARM_LINE_INDEX].setVisible(true);
				dig.add(figs1[ARM_LINE_INDEX]);
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
        props.put(FIGURE_TYPE, FIGURE_TYPE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, +0.011d);
        props.put(CENTER_OFFSET_Y, +0.0065d);
        props.put(RADIUS, 10 / 3600d);
        props.put(WIDTH_X, 4/3600d); //1 arcsec
        props.put(WIDTH_Y, 4/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.BLUE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 3.0f);
        final CanvasFigure[] irisProbeContraint3 = makeFigure(props);
        map.put("iris.oiwfs.probe3.contraint", irisProbeContraint3);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_FOCUS_PROBETIP);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, +0.011d);
        props.put(CENTER_OFFSET_Y, +0.0065d);
        props.put(ARM_Line1_TOP_X, 0.033091765d);
        props.put(ARM_Line1_TOP_Y, 0.019105539d);
//        props.put(ARM_Line2_TOP_X, 0d);
//        props.put(ARM_Line2_TOP_Y, 0d);
//        props.put(ARM_Line1_BOTTOM_X,0.033091765d);
//        props.put(ARM_Line1_BOTTOM_Y,-0.019105539d);
        props.put(ARM_Line1_BOTTOM_X,+0.011d);
        props.put(ARM_Line1_BOTTOM_Y,+0.0065d);
//        props.put(ARM_Line2_BOTTOM_X, 0d);
//        props.put(ARM_Line2_BOTTOM_Y, 0d);
        props.put(RADIUS, CIRCLE_RADIUS / 3600d);
        props.put(WIDTH_X, 4/3600d); //1 arcsec
        props.put(WIDTH_Y, 4/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.BLUE);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 3.0f);
        final CanvasFigure[] irisProbeFocus3 = makeFigure(props);
        map.put("iris.oiwfs.probe3.focus", irisProbeFocus3);

        props  = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_PROBETIP);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, +0.011d);
        props.put(CENTER_OFFSET_Y, +0.0065d);
        props.put(ARM_Line1_TOP_X, 0.033091765d);
        props.put(ARM_Line1_TOP_Y, 0.019105539d);
//        props.put(ARM_Line2_TOP_X, 0d);
//        props.put(ARM_Line2_TOP_Y, 0d);
//        props.put(ARM_Line1_BOTTOM_X,0.033091765d);
//        props.put(ARM_Line1_BOTTOM_Y,-0.019105539d);
        props.put(ARM_Line1_BOTTOM_X,+0.011d);
        props.put(ARM_Line1_BOTTOM_Y,+0.0065d);
//        props.put(ARM_Line2_BOTTOM_X, 0d);
//        props.put(ARM_Line2_BOTTOM_Y, 0d);
        props.put(RADIUS, CIRCLE_RADIUS / 3600d);
        props.put(WIDTH_X, 4/3600d); //1 arcsec
        props.put(WIDTH_Y, 4/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.BLUE);
        props.put(FILL, FILL_OUTLINE_YES);
//        props.put(FILL_COLOR,  Color.BLUE);
        props.put(OUTLINE_WIDTH, 1.0f);
        final CanvasFigure[] irisProbeArm3 = makeFigure(props);
        map.put("iris.oiwfs.probe3.arm", irisProbeArm3);
        irisProbeArm3[1].addSlave(irisProbeFocus3[1]);
        irisProbeArm3[1].addSlave(irisProbeContraint3[0]);
        irisProbeArm3[1].addCanvasFigureListener(new CanvasFigureListener() {

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
                //do nothing
            }

            @Override
            public void figureMoved(CanvasFigureEvent e) {
                CanvasFigure[] figs1 = map.get("iris.oiwfs.probe3.arm");
                CanvasFigure probArmFig = figs1[0];
                Shape shape1 = probArmFig.getShape();
                double centerX = shape1.getBounds2D().getCenterX();
                double centerY = shape1.getBounds2D().getCenterY();
                Point2D.Double centerPt = new Point2D.Double(centerX, centerY);
                CoordinateConverter cc = fovastImageDisplay.getCoordinateConverter();
                cc.screenToWorldCoords(centerPt, false);
                String centerString = centerPt.getX()+","+centerPt.getY();
                //System.out.println("center:"+centerString);
                config.setConfigElementProperty("iris.oiwfs.probe3.arm", "position", centerString);
            }
        });
        
        CanvasFigure probFig3 = irisProbeArm3[1];
        dragInteractor =  (DragInteractor) probFig3.getInteractor();
        dragInteractor.appendConstraint(new PointConstraint() {

            Point2D prevPt = null;
            CanvasFigure iris_oiwfs_probe3_line = null;
            @Override
            public void constrain(Point2D pt) {
            	
                CanvasFigure[] figs = map.get("iris.oiwfs.probe3.limits1");
                CanvasFigure probLimitsFig = figs[PROBETIP_RECTANGLE_INDEX];
                Shape shape = probLimitsFig.getShape();

                CanvasFigure[] figs2 = map.get("nfiraos.limits1");
                CanvasFigure nfiraosLimitsFig = figs2[PROBETIP_RECTANGLE_INDEX];
                Shape shape2 = nfiraosLimitsFig.getShape();

                CanvasFigure[] figs1 = map.get("iris.oiwfs.probe3.arm");
                CanvasFigure probArmFig = figs1[PROBETIP_RECTANGLE_INDEX];
                Shape shape1 = probArmFig.getShape();

                CanvasFigure[] figsProbe2 = map.get("iris.oiwfs.probe2.contraint");
                CanvasFigure probArm2Fig = figsProbe2[0];
                Shape shapeProbe2 = probArm2Fig.getShape();

                CanvasFigure[] figsProbe1 = map.get("iris.oiwfs.probe1.contraint");
                CanvasFigure probArm1Fig = figsProbe1[0];
                Shape shapeProbe1 = probArm1Fig.getShape();

                double centerX = shape1.getBounds2D().getCenterX();
                double centerY = shape1.getBounds2D().getCenterY();
//                Point2D.Double centerPt = new Point2D.Double(centerX, centerY);

                if(prevPt != null) {
                    Point2D.Double newCenterPt = new Point2D.Double(
                                centerX + (pt.getX() - prevPt.getX()),
                                centerY + (pt.getY() - prevPt.getY())
                    );
                    if(shape.contains(newCenterPt) && shape2.contains(newCenterPt) &&
                            !shapeProbe1.contains(newCenterPt) && !shapeProbe2.contains(newCenterPt)){
                        prevPt = pt;
                        //leave pt as is
                    } else {
                        pt.setLocation(prevPt.getX(), prevPt.getY());
                    }
                } else {
                    prevPt = pt;
                }

                //tushar
//                CanvasFigure linefig = figs1[ARM_LINE_INDEX];
                CanvasFigure circlefig3 = figs1[PROBETIP_CIRCLE_INDEX];
                
              //below offsets are taken from initialisation code of arm / probe for fig3.
                Point2D.Double arm_line3_top = 
                		DegreeCoverter.correctionUsingOffsets(fovastImageDisplay, 0.033091765, 0.019105539);
                
                Point2D.Double circle3CenterPt =
                	new Point2D.Double(circlefig3.getShape().getBounds2D().getCenterX(),
                											circlefig3.getShape().getBounds2D().getCenterY());
                
					if(iris_oiwfs_probe3_line != null)
					{
						iris_oiwfs_probe3_line.setVisible(false);
						iris_oiwfs_probe3_line = null;
					}
//					//extend the line
//					DivaImageGraphics dig = (DivaImageGraphics) fovastImageDisplay.getCanvasGraphics();
//					iris_oiwfs_probe3_line = updateArmLines(dig, arm_line3_top, circle3CenterPt, null, Color.BLUE);
//					dig.remove(figs1[ARM_LINE_INDEX]);
//					figs1[ARM_LINE_INDEX] = iris_oiwfs_probe3_line;
//					figs1[ARM_LINE_INDEX].setVisible(true);
//					dig.add(figs1[ARM_LINE_INDEX]);
					
					//extend the rectangle
					float otwidth = 1.0f;
					double rotationangle = calculateArmAngle(arm_line3_top.getX(), arm_line3_top.getY(), 
																							circle3CenterPt.getX(), circle3CenterPt.getY());
					rotationangle = rotationangle + Math.toRadians(180);
					
					//calculate width of all three rectangles
					//width in degrees
					double rectanleWidth = 3.6377 / 3600d;
					Point2D.Double wcsCenter = fovastImageDisplay.getCoordinateConverter().getWCSCenter();
					double newfactor = Math.cos(Math.toRadians(wcsCenter.y)); //less than 1
					Point2D.Double temppt = new Point2D.Double(rectanleWidth * newfactor, rectanleWidth);
					fovastImageDisplay.getCoordinateConverter().worldToScreenCoords(temppt, true);
					// width in pixels
					rectanleWidth = temppt.getX();
					
					DivaImageGraphics dig = (DivaImageGraphics) fovastImageDisplay.getCanvasGraphics();
					iris_oiwfs_probe3_line = updateArmRectangles(dig, arm_line3_top, circle3CenterPt, 
																									Color.BLUE, Color.BLUE, 
																									rectanleWidth, otwidth, rotationangle, null);
					dig.remove(figs1[ARM_LINE_INDEX]);
					figs1[ARM_LINE_INDEX] = iris_oiwfs_probe3_line;
					figs1[ARM_LINE_INDEX].setVisible(true);
					dig.add(figs1[ARM_LINE_INDEX]);
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
        
        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_DOUBLE_CIRCLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        props.put(CENTER_OFFSET_X, (1.3)/60d);
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(RADIUS, 10.5/3600d);
        props.put(RADIUS1, 1.6/3600d);
        props.put(ARC_END, ARC_END_CHORD);
        props.put(ARC_START_ANGLE, 90d);
        props.put(ARC_ANGLE_EXTENT, 180d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.RED);
        props.put(FILL, FILL_OUTLINE_NO);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] mobieGuiderFigs = makeFigure(props);
        map.put("mobie.guider.guider", mobieGuiderFigs);
        mobieGuiderFigs[1].addCanvasFigureListener(new CanvasFigureListener() {

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
                //do nothing
            }

            @Override
            public void figureMoved(CanvasFigureEvent e) {
                CanvasFigure[] figs1 = map.get("mobie.guider.guider");
                CanvasFigure mobieGuiderFig = figs1[0];
                Shape shape1 = mobieGuiderFig.getShape();
                double centerX = shape1.getBounds2D().getCenterX();
                double centerY = shape1.getBounds2D().getCenterY();
                Point2D.Double centerPt = new Point2D.Double(centerX, centerY);
                CoordinateConverter cc = fovastImageDisplay.getCoordinateConverter();
                cc.screenToWorldCoords(centerPt, false);
                String centerString = centerPt.getX()+","+centerPt.getY();
                config.setConfigElementProperty("mobie.guider.guider", "position", centerString);
            }
        });
        dragInteractor =  (DragInteractor) mobieGuiderFigs[0].getInteractor();
        dragInteractor.appendConstraint(new PointConstraint() {

            Point2D prevPt = null;
            @Override
            public void constrain(Point2D pt) {

                CanvasFigure[] figs = map.get("mobie.guider.limits.small");
                CanvasFigure mobieLimitsFig = figs[0];
                Shape shape = mobieLimitsFig.getShape();

                CanvasFigure[] figs1 = map.get("mobie.guider.limits.large");
                CanvasFigure mobieLimitsFig1 = figs1[0];
                Shape shape1 = mobieLimitsFig1.getShape();

                CanvasFigure[] figs4 = map.get("mobie.guider.guider");
                CanvasFigure mobieguiderFig = figs4[0];
                Shape shape4 = mobieguiderFig.getShape();

                double centerX = shape4.getBounds2D().getCenterX();
                double centerY = shape4.getBounds2D().getCenterY();
//                Point2D.Double centerPt = new Point2D.Double(centerX, centerY);

                if(prevPt != null) {
                    Point2D.Double newCenterPt = new Point2D.Double(
                                centerX + (pt.getX() - prevPt.getX()),
                                centerY + (pt.getY() - prevPt.getY())
                    );
                    if(!shape.contains(newCenterPt) && shape1.contains(newCenterPt)){
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

        //now add dragger
        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_RECTANGLE);
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
//		props.put(CENTER_OFFSET_X, (5.4)/108d);
        props.put(CENTER_OFFSET_X, (27.0/3600d));
        props.put(CENTER_OFFSET_Y, 0d);
        props.put(WIDTH_Y, 2.0/3600d);
        props.put(WIDTH_X, 2.0/3600d);
        props.put(DRAW_OUTLINE, DRAW_OUTLINE_YES);
        props.put(OUTLINE_COLOR, Color.GREEN);
        props.put(FILL, FILL_OUTLINE_YES);
        props.put(FILL_COLOR,  Color.GREEN);
        props.put(OUTLINE_WIDTH, 1.0f);
        CanvasFigure[] irisDragFig = makeFigure(props);
        map.put("iris.ifuimager.drag", irisDragFig);
        
        if(((Boolean) props.get(ROTATABLE)).booleanValue() == false)
        {
        	IrisDraggerConstraint idc = new IrisDraggerConstraint();
            ((DragInteractor) irisDragFig[0].getInteractor()).appendConstraint(idc);
        }

        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_RECTANGLE);
        props.put(FIGURE_LABEL, "TWFS");
        props.put(ROTATABLE, false);
        props.put(MOVEABLE, true);
        // some arbitrary position outside of science field
        // and inside nfiraos fov > 17" and < 2'
        props.put(CENTER_OFFSET_X, -0.99/60d);
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
        twsFigs[0].addCanvasFigureListener(new CanvasFigureListener() {

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
                //do nothing
            }

            @Override
            public void figureMoved(CanvasFigureEvent e) {
                CanvasFigure[] figs1 = map.get("nfiraos.twfs.detector");
                CanvasFigure probArmFig = figs1[0];
                Shape shape1 = probArmFig.getShape();
                double centerX = shape1.getBounds2D().getCenterX();
                double centerY = shape1.getBounds2D().getCenterY();
                Point2D.Double centerPt = new Point2D.Double(centerX, centerY);
                CoordinateConverter cc = fovastImageDisplay.getCoordinateConverter();
                cc.screenToWorldCoords(centerPt, false);
                String centerString = centerPt.getX()+","+centerPt.getY();
                //System.out.println("center:"+centerString);
                config.setConfigElementProperty("nfiraos.twfs.detector", "position", centerString);
                String tempString;
                if(probArmFig.isVisible()){
                    tempString = "true";
                }else{
                    tempString = "false";
                }
                config.setConfigElementProperty("nfiraos.twfs.detector", "isVisible",tempString);
            }
        });

        
        //put constraints
        //nfiraos.twfs.detector
        CanvasFigure twsFig = twsFigs[0];
        dragInteractor = (DragInteractor) twsFig.getInteractor();
        //now constrain it
        dragInteractor.appendConstraint(new TwfsPointConstraint(twsFig, map));
       
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

        //add start
        //Keeping this after mobie as although mobie arcs are hidden
        //when sciencedetector is shown .. rotation handle goes berserk
        props = new HashMap<String, Object>();
        props.put(FIGURE_TYPE, FIGURE_TYPE_RECTANGLE);
//        props.put(ROTATABLE, true);
        props.put(ROTATABLE, false);
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
        //irisDetectorFigs[0].addSlave((CanvasFigure) nfiraosLimitsFigs[0]);
        //irisDetectorFigs[0].addSlave((CanvasFigure) nfiraosAcqusitionCameraLimits[0]);
        irisDetectorFigs[0].addSlave((CanvasFigure) lensletFigs[0]);
        irisDetectorFigs[0].addSlave((CanvasFigure) slicerFigs[0]);
        //add end

        irisDetectorFigs[0].addSlave((CanvasFigure) twsFigs[0]);
        irisDetectorFigs[0].addSlave((CanvasFigure) twsFigs[1]);
        
        //irisDetectorFigs[0].addSlave((CanvasFigure)nfiraosAcqusitionCameraLimits[0]);
        CanvasFigure cirFig = nfiraosAcqusitionCameraLimits[0];
        dragInteractor =  (DragInteractor) cirFig.getInteractor();
        dragInteractor.appendConstraint(new PointConstraint() {

            Point2D prevPt = null;

            @Override
            public void constrain(Point2D pt) {
            	
                CanvasFigure[] figs = map.get("nfiraos.limits2");
                CanvasFigure nfiraosLimitsFig = figs[0];
                Shape shape = nfiraosLimitsFig.getShape();

                CanvasFigure[] figs1 = map.get("nfiraos.acqusitionCameraLimits");
                CanvasFigure nfiraosLimitsFig1 = figs1[0];
                Shape shape1 = nfiraosLimitsFig1.getShape();
                
                double centerX = shape1.getBounds2D().getCenterX();
                double centerY = shape1.getBounds2D().getCenterY();
//                Point2D.Double centerPt = new Point2D.Double(centerX, centerY);

                if(prevPt != null) {
                    Point2D.Double newCenterPt = new Point2D.Double(
                                centerX + (pt.getX() - prevPt.getX()),
                                centerY + (pt.getY() - prevPt.getY())
                    );
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
    private Interactor makeRoiSelectionInteractor(DivaImageGraphics dig,boolean addDragInteractor ) {
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
        if(addDragInteractor)
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
    
    private class TwfsPointConstraint implements  PointConstraint {
    	
    	Point2D prevPt = null;
        private final HashMap<String, CanvasFigure[]> map;

        public TwfsPointConstraint(CanvasFigure parentFig,
                HashMap<String, CanvasFigure[]> map) {
//            this.parentFig = parentFig;
            this.map = map;
        }

        @Override
        public void constrain(Point2D pt) {

//        	Figure twfsBoxFig = parentFig;
//            Shape twfsBoxShape = twfsBoxFig.getShape();
        	
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

//    private class MobieDetectorFigListener implements CanvasFigureListener {
//    	
//		@Override
//		public void figureDeselected(CanvasFigureEvent arg0) {
//			System.out.println(">>>>>>>> Inside figureDeselected ......");
//		}
//
//		@Override
//		public void figureMoved(CanvasFigureEvent fig) {
//			System.out.println(">>>>>>>> Inside figureMoved ......");
//			CanvasFigure[] figs1 = map.get("mobie.detector");
//			CanvasFigure mobieDetectorFig = figs1[0];
//			CanvasFigure sciencedetectorfig = figs1[1];
//			
//			double sciX = sciencedetectorfig.getShape().getBounds2D().getX();
//			double sciY = sciencedetectorfig.getShape().getBounds2D().getY();
//			System.out.println("sciX = " + sciX + "\t sciY = " + sciY);
//			double mobieX = mobieDetectorFig.getShape().getBounds2D().getX();
//			double mobieY = mobieDetectorFig.getShape().getBounds2D().getY();
//			System.out.println("mobieX = " + mobieX + "\t mobieY = " + mobieY);
//			sciencedetectorfig.translate((mobieX - sciX), (mobieY - sciY));
//			sciX = sciencedetectorfig.getShape().getBounds2D().getX();
//			sciY = sciencedetectorfig.getShape().getBounds2D().getY();
//			System.out.println("after sciX = " + sciX + "\t after sciY = " + sciY);
//		}
//
//		@Override
//		public void figureResized(CanvasFigureEvent arg0) {
//			System.out.println(">>>>>>>> Inside figureResized ......");
//		}
//
//		@Override
//		public void figureSelected(CanvasFigureEvent arg0) {
//			System.out.println(">>>>>>>> Inside figureSelected ......");
//		}
//    }
    
    private class IrisDraggerConstraint  implements PointConstraint {
    	
        private double oldTheta = -1000;
        
        public IrisDraggerConstraint() {}

    	@Override
        public boolean snapped() {
            return false; // no snapping
        }
        
        @Override
        public void constrain(Point2D pt) {
        	CanvasFigure[] lensetfigs = map.get("iris.ifuimager.lenslet");
        	CanvasFigure[] slicerfigs = map.get("iris.ifuimager.slicer");
            CanvasFigure[] detectorfigs = map.get("iris.sciencedetector");
            CanvasFigure draggerFig = map.get("iris.ifuimager.drag")[0];
            
            /**
             * theta = tan-1 [(y2 - y1) / (x2 - x1)]
             */
			//required as point at which fig is to be rotated in not (0, 0)
            CoordinateConverter cc = fovastImageDisplay.getCoordinateConverter();
            
			Point2D.Double center = cc.getWCSCenter();
			Point2D.Double point = new Point2D.Double(center.getX(), center.getY());
			cc.worldToScreenCoords(point, false);
			
            double x = draggerFig.getShape().getBounds2D().getX();
            double y = draggerFig.getShape().getBounds2D().getY();
            
            if(oldTheta == -1000)
            {
            	//will be invoke for first time
            	oldTheta = Math.atan((y - point.getY()) / (x - point.getX()));
            }
            
            double newtheta;
            double rotationangle = Math.atan((y - point.getY()) / (x - point.getX()));
            
            //as tan -1 goes from -90 to 90, required
            if(((y - point.getY()) > 0) && ((x - point.getX()) < 0))
            {
            	rotationangle = rotationangle + Math.toRadians(180);
            }
            else if(((y - point.getY()) < 0) && ((x - point.getX()) < 0))
            {
            	rotationangle = rotationangle + Math.toRadians(180);
            }
            
            newtheta = rotationangle - oldTheta;		//delta theta
            oldTheta = rotationangle;
            
            AffineTransform rotation = AffineTransform.getRotateInstance(newtheta, point.getX(), point.getY());
            lensetfigs[0].transform(rotation);
            slicerfigs[0].transform(rotation);
            detectorfigs[0].transform(rotation);
            
            //newly added. perform rotation for all objects
//            iris.ifuimager.drag		//NOT REQUIRED
//            iris.probes.limits			//PENDING
//            focus correction			//PENDING
            
//            iris.oiwfs.probe1.limits
//            iris.oiwfs.probe2.limits
//            iris.oiwfs.probe3.limits
//
//            iris.oiwfs.probe1.arm
//            iris.oiwfs.probe2.arm
//            iris.oiwfs.probe3.arm
//
//            iris.oiwfs.probe1.limits1
//            iris.oiwfs.probe2.limits1
//            iris.oiwfs.probe3.limits1
//
//            iris.oiwfs.probe1.focus
//            iris.oiwfs.probe2.focus
//            iris.oiwfs.probe3.focus
            
//            	//TUSHAR
//				String prefix = "iris.oiwfs.probe";
//				for(java.util.Map.Entry<String, CanvasFigure[]> entry : map.entrySet()) 
//				{
//					if((entry.getKey()).startsWith(prefix))
//					{
//						CanvasFigure[] figsArr = entry.getValue();
//						for(CanvasFigure fig : figsArr)
//						{
//							fig.transform(rotation);
//						}
//					}
//				}
            }
        }	
    
    private class MobieDetectorConstraint implements PointConstraint {

//        private CanvasFigure mobieFigure;
        private FovastImageDisplay fovastImageDisplay;
        private HashMap<String, CanvasFigure[]> map;
//        Point2D prevPt = null;
        private double oldTheta = -1000; 
        CoordinateConverter cc = null;      
        
        public MobieDetectorConstraint(CanvasFigure mobieFigure, FovastImageDisplay imageDisplay,
                HashMap<String, CanvasFigure[]> map) {
//            this.mobieFigure = mobieFigure;
            this.fovastImageDisplay = imageDisplay;
            this.cc = fovastImageDisplay.getCoordinateConverter();      
            this.map = map;
        }

        @Override
        public boolean snapped() {
            return false; // no snapping
        }
        
        @Override
        public void constrain(Point2D pt) {
        	CanvasFigure[] figs1 = map.get("mobie.detector");
            CanvasFigure mobieDetectorFig = figs1[0];
            CanvasFigure[] figs2 = map.get("mobie.drag");
            CanvasFigure sciencedetectorfig = figs2[0];
            CanvasFigure[] fovfigs = map.get("mobie.wavefrontSensorFOV");
            CanvasFigure[] guiderfig = map.get("mobie.guider.guider");
            CanvasFigure[] guiderlimitfig = map.get("mobie.guider.limits");
            CanvasFigure[] guiderlimitSmallfig = map.get("mobie.guider.limits.small");
            CanvasFigure[] guiderlimitLargefig = map.get("mobie.guider.limits.large");
          
////            CanvasFigure[] figs = map.get("mobie.vignettingstart");
////            CanvasFigure probLimitsFig = figs[0];
////            Shape shape = probLimitsFig.getShape();
////
////            CanvasFigure[] figs2 = map.get("mobie.edgeoffield");
////            CanvasFigure nfiraosLimitsFig = figs2[0];
////            Shape shape2 = nfiraosLimitsFig.getShape();
//
//            CanvasFigure[] figs2 = map.get("mobie.limits");
//            CanvasFigure nfiraosLimitsFig = figs2[0];
//            Shape shape2 = nfiraosLimitsFig.getShape();
//
//            /**
//             * According to new logic,
//             * mobieDetector = 0 and
//             * scienceDetector = 1  
//             */
//            CanvasFigure[] figs1 = map.get("mobie.detector");
//            CanvasFigure mobieDetectorFig = figs1[0];
//            CanvasFigure sciencedetectorfig = figs1[1];
//            Shape shape1 = mobieDetectorFig.getShape();
//
//            double centerX = shape1.getBounds2D().getCenterX();
//            double centerY = shape1.getBounds2D().getCenterY();
//            
//            if(prevPt != null) {
//                    Point2D.Double newCenterPt = new Point2D.Double(
//                                centerX + (pt.getX() - prevPt.getX()),
//                                centerY + (pt.getY() - prevPt.getY())
//                    );
////                    if(shape.contains(newCenterPt) && !shape2.contains(newCenterPt)){
//                    if(shape2.contains(newCenterPt)){
//                        prevPt = pt;
//                        //leave pt as is
//                    } else {
//                        pt.setLocation(prevPt.getX(), prevPt.getY());
//                    }
//                } else {
//                    prevPt = pt;
//                }
            
            //TUSHAR
            /**
             * theta = tan-1 [(y2 - y1) / (x2 - x1)]
             */
			//required as point at which fig is to be rotated in not (0, 0)
			Point2D.Double center = cc.getWCSCenter();
			Point2D.Double point = new Point2D.Double(center.getX(), center.getY());
			cc.worldToScreenCoords(point, false);
			
            double x = sciencedetectorfig.getShape().getBounds2D().getX();
            double y = sciencedetectorfig.getShape().getBounds2D().getY();
            
            if(oldTheta == -1000)
            {
            	//will be invoke for first time
            	oldTheta = Math.atan((y - point.getY()) / (x - point.getX()));
            }
            
            double newtheta;
            double rotationangle = Math.atan((y - point.getY()) / (x - point.getX()));
            
            //as tan -1 goes from -90 to 90, required
            if(((y - point.getY()) > 0) && ((x - point.getX()) < 0))
            {
            	rotationangle = rotationangle + Math.toRadians(180);
            }
            else if(((y - point.getY()) < 0) && ((x - point.getX()) < 0))
            {
            	rotationangle = rotationangle + Math.toRadians(180);
            }
            
            newtheta = rotationangle - oldTheta;		//delta theta
            oldTheta = rotationangle;
            
            AffineTransform rotation = AffineTransform.getRotateInstance(newtheta, point.getX(), point.getY());
            mobieDetectorFig.transform(rotation);
            fovfigs[0].transform(rotation);
            guiderfig[0].transform(rotation);
            guiderfig[1].transform(rotation);
            guiderlimitSmallfig[0].transform(rotation);
            guiderlimitLargefig[0].transform(rotation);
            for(CanvasFigure fig : guiderlimitfig)
            {
            	fig.transform(rotation);
            }
        }
    }
}
