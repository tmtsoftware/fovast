/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tmt.fovast.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import jsky.coords.CoordinateConverter;
import jsky.image.graphics.ShapeUtil;
import jsky.util.java2d.ShapeUtilities;

/**
 *
 */

public class FovastTablePlotter{
    private FovastImageDisplay _display;
    private int symbolNumber=0;
    private int colorNumber=0;
    private boolean show=true;
    private HashMap<Catalog,ArrayList> catalogList = new LinkedHashMap<Catalog,ArrayList>();
    private HashMap<Catalog, Boolean> catalogDisplayList = new LinkedHashMap<Catalog, Boolean>();
    private HashMap<Catalog, SymbolTable> catalogSymbolList = new LinkedHashMap<Catalog, SymbolTable>();

    public void setImageDisplay(FovastImageDisplay imageDisplay) {
        _display = imageDisplay;
    }

     /**
     * @return the catalogList
     */
    public HashMap<Catalog, ArrayList> getCatalogList() {
        return catalogList;
    }

    /**
     * @return the catalogDisplayList
     */
    public HashMap<Catalog, Boolean> getCatalogDisplayList() {
        return catalogDisplayList;
    }

    /**
     * @return the catalogSymbolList
     */
    public HashMap<Catalog, SymbolTable> getCatalogSymbolList() {
        return catalogSymbolList;
    }

    public void makeList(Catalog c){
        
        Object[][] data=c.getData();
        SymbolTable st = null;
        if(getCatalogSymbolList().get(c) == null) {
            st = new SymbolTable();
            int symbolSetSize = SymbolTable.getSymbolSetSize();
            st.setSymbol(SymbolTable.getSymbols(symbolNumber%symbolSetSize));
            st.setSymbolColor(SymbolTable.getColours(symbolNumber%SymbolTable.getColorSetSize()));
            st.setRatio(1);
            st.setAngle(0);
            st.setLabel("");
            st.setSize(4);
            st.setUnits("image");
            symbolNumber++;
            catalogSymbolList.put(c, st);
        } else {
            st = getCatalogSymbolList().get(c);
        }

//        double ratio=1;
//        double angle=0;
//        String label="";
//        int symbolSize=4;
//        String units="image";
        
        ArrayList<Shape> figureList = new ArrayList<Shape>();
        for (int i = 0; i < data.length; i++) {
            Point2D.Double pos = new Point2D.Double((Double)data[i][0],(Double)data[i][1]);
            CoordinateConverter coordinateConverter = _display.getCoordinateConverter();

            Point2D.Double wcsCenter = coordinateConverter.getWCSCenter();//image center RA/DEC
          double ra = wcsCenter.x ;    //RA in degrees
          double dec = wcsCenter.y ;    //DEC
          double factor = Math.cos(Math.toRadians(dec)); //less than 1

          Point2D.Double pt = new Point2D.Double(ra, dec);
          coordinateConverter.worldToScreenCoords(pt, false);
          ra = pt.x;     //in pixels now
          dec = pt.y;
          //double factor = Math.cos(Math.toRadians(pos.y)); //less than 1





            coordinateConverter.convertCoords(pos, CoordinateConverter.WORLD, CoordinateConverter.USER, false);
            // clip to image bounds
            double w = coordinateConverter.getWidth();
            double h = coordinateConverter.getHeight();
//            if (pos.x < 0. || pos.y < 0. || pos.x >= w || pos.y >= h) {
//                continue;
//            }
            coordinateConverter.convertCoords(pos, CoordinateConverter.USER, CoordinateConverter.SCREEN, false);

            double x_offset=(ra - pos.x)*factor;
            pos.x = ra-x_offset;           //corrected x position (in pixels)





            Point2D.Double size = new Point2D.Double(st.getSize(), st.getSize());
            int sizeType = getCoordType(st.getUnits());
            coordinateConverter.convertCoords(size, sizeType, CoordinateConverter.SCREEN, true);
            // get the Shape object for the symbol
            Shape shape = makeShape(st.getSymbol(),
                    pos.x, pos.y, Math.max(size.x, size.y), st.getRatio(), st.getAngle());
            figureList.add(shape);         
        }
        if(getCatalogDisplayList().get(c) == null) {
             getCatalogDisplayList().put(c, Boolean.TRUE);
        }
        getCatalogList().put(c, figureList);
    }

    public void showHide(Catalog c,boolean state){
           if(state == true){
               getCatalogDisplayList().put(c, Boolean.TRUE);
           }else{
               getCatalogDisplayList().put(c, Boolean.FALSE);
           }
    }

    public void remove(Catalog c){
        getCatalogList().remove(c);
        getCatalogDisplayList().remove(c);
        getCatalogSymbolList().remove(c);
    }
    
    public void paintSymbols(Graphics2D g, Rectangle2D region){      
        BasicStroke s1 = new BasicStroke();
        g.setPaintMode();
        Iterator iter = catalogList.keySet().iterator();
        int j=0;
        while(iter.hasNext()){
            Catalog c = (Catalog)iter.next();
            SymbolTable st = getCatalogSymbolList().get(c);
            Color color = st.getSymbolColor();
            if((catalogDisplayList.get(c)) == true){
                for(int i = 0;i < (catalogList.get(c)).size();i++){
                    g.setColor(color);
                    g.setStroke(s1);
                    g.draw((Shape)catalogList.get(c).get(i));
                }
            }
         }
     }
        


    protected int getCoordType(String name) {
        if (name != null && name.length() != 0) {
            if (name.startsWith("deg")) {
                return CoordinateConverter.WORLD;
            }
            if (name.equals("image")) {
                return CoordinateConverter.IMAGE;
            }
            if (name.equals("screen")) {
                return CoordinateConverter.SCREEN;
            }
            if (name.equals("canvas")) {
                return CoordinateConverter.CANVAS;
            }
            if (name.equals("user")) {
                return CoordinateConverter.USER;
            }
        }
        return CoordinateConverter.IMAGE;
    }

protected Shape makeShape(String symbol, double x, double y, double size,
                              double ratio, double angle) {
         int shape = 0;
         if(symbol.equalsIgnoreCase("square")){
             shape = 1;
         }else if(symbol.equalsIgnoreCase("circle")){
             shape = 2;
         }else if(symbol.equalsIgnoreCase("plus")){
             shape = 3;
         }else if(symbol.equalsIgnoreCase("cross")){
             shape = 4;
         }else if(symbol.equalsIgnoreCase("triangle")){
             shape = 5;
         }else if(symbol.equalsIgnoreCase("diamond")){
             shape = 6;
         }else if(symbol.equalsIgnoreCase("ellipse")){
             shape = 7;
         }else if(symbol.equalsIgnoreCase("compass")){
             shape = 8;
         }else if(symbol.equalsIgnoreCase("line")){
             shape = 9;
         }else if(symbol.equalsIgnoreCase("arrow")){
             shape = 10;
         }

         // get center, north and east in screen coords
        Point2D.Double center = new Point2D.Double(x, y);
        Point2D.Double north = new Point2D.Double(x, y - size);
        Point2D.Double east = new Point2D.Double(x - size, y);

        getNorthAndEast(center, size, ratio, angle, north, east);

        switch (shape) {
            case 1:
                return new Rectangle2D.Double(x - size, y - size, size * 2, size * 2);

            case 2:
                return new Ellipse2D.Double(x - size, y - size, size * 2, size * 2);

            case 3:
                return ShapeUtil.makePlus(center, north, east);

            case 4:
                return ShapeUtil.makeCross(x, y, size);

            case 5:
                return ShapeUtil.makeTriangle(x, y, size);

            case 6:
                return ShapeUtil.makeDiamond(x, y, size);

            case 7:
                return ShapeUtil.makeEllipse(center, north, east);

            case 8:
                return ShapeUtil.makeCompass(center, north, east);

            case 9:
                return ShapeUtil.makeLine(center, north);

            case 10:

                return ShapeUtil.makeArrow(center, north);
        }
        throw new RuntimeException("Unknown plot symbol shape: " + symbol);
    }

       /*
     * Set x and y in the north and east parameters in screen
     * coordinates, given the center point and radius in screen
     * coordinates, an optional rotation angle, and an x/y ellipticity
     * ratio.  If the image supports world coordinates, that is taken
     * into account (the calculations are done in RA,DEC before
     * converting to screen coords).  The conversion to screen coords
     * automatically takes the current zoom and rotate settings into
     * account.
     *
     * @param center the center position screen coordinate
     * @param size the radius of the symbol in screen coordinates
     * @param ratio the x/y ratio (ellipticity ratio) of the symbol
     * @param angle the rotation angle
     * @param north on return, contains the screen coordinates of WCS north
     * @param east on return, contains the screen coordinates of WCS east
     */
    protected void getNorthAndEast(Point2D.Double center,
                                   double size,
                                   double ratio,
                                   double angle,
                                   Point2D.Double north,
                                   Point2D.Double east) {
        CoordinateConverter coordinateConverter = _display.getCoordinateConverter();
        if (coordinateConverter.isWCS()) {
            // get center and radius in deg 2000
            Point2D.Double wcsCenter = new Point2D.Double(center.x, center.y);
            coordinateConverter.screenToWorldCoords(wcsCenter, false);
            Point2D.Double wcsRadius = new Point2D.Double(size, size);
            coordinateConverter.screenToWorldCoords(wcsRadius, true);

            // adjust the radius by the ratio
            if (ratio < 1.) {
                wcsRadius.y *= 1.0 / ratio;
            } else if (ratio > 1.) {
                wcsRadius.x *= ratio;
            }

            // set east
            east.x = Math.IEEEremainder(wcsCenter.x + Math.abs(wcsRadius.x) / Math.cos((wcsCenter.y / 180.) * Math.PI), 360.);
            if (east.x < 0.) {
                east.x += 360.;
            }

            east.y = wcsCenter.y;

            // set north
            north.x = wcsCenter.x;

            north.y = wcsCenter.y + Math.abs(wcsRadius.y);
            if (north.y >= 90.) {
                north.y = 180. - north.y;
            } else if (north.y <= -90.) {
                north.y = -180. - north.y;
            }

            // convert back to screen coords
            coordinateConverter.worldToScreenCoords(north, false);
            coordinateConverter.worldToScreenCoords(east, false);
        } else {
            // not using world coords: adjust the radius by the ratio
            double rx = size, ry = size;
            if (ratio < 1.) {
                ry *= 1.0 / ratio;
            } else if (ratio > 1.) {
                rx *= ratio;
            }

            east.x = center.x - rx;
            east.y = center.y;

            north.x = center.x;
            north.y = center.y - ry;
        }

        // rotate by angle
        if (angle != 0.) {
            rotatePoint(north, center, angle);
            rotatePoint(east, center, angle);
        }
    }

    /*
     * Rotate the point p around the center point by the given
     * angle in deg.
     */
    protected void rotatePoint(Point2D.Double p, Point2D.Double center, double angle) {
        p.x -= center.x;
        p.y -= center.y;
        double tmp = p.x;
        double rad = angle * Math.PI / 180.;
        double cosa = Math.cos(rad);
        double sina = Math.sin(rad);
        p.x = p.x * cosa + p.y * sina + center.x;
        p.y = -tmp * sina + p.y * cosa + center.y;
    }

    /**
     * Transform the plot symbols using the given AffineTransform
     * (called when the image is transformed, to keep the plot symbols up to date).
     */
    public void transformGraphics(AffineTransform trans) {
        Iterator iter = catalogList.keySet().iterator();
        int j=0;
        while(iter.hasNext()){
            Catalog c = (Catalog)iter.next();
            SymbolTable st = getCatalogSymbolList().get(c);
            Color color = st.getSymbolColor();
            //if((catalogDisplayList.get(c)) == true){
                ArrayList<Shape> shapeList = catalogList.get(c);
                for(int i = 0;i < shapeList.size();i++){                    
                    Shape oldShape = shapeList.get(i);
                    shapeList.remove(i);
                    shapeList.add(i, ShapeUtilities.transformModify(oldShape, trans));
                }
            //}
        }
    }

}

