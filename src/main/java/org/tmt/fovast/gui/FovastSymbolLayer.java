/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tmt.fovast.gui;

/**
 *
 */


import diva.canvas.CanvasLayer;
import diva.canvas.VisibleComponent;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Set;
import jsky.image.gui.BasicImageDisplay;
import jsky.image.gui.ImageGraphicsHandler;


public class FovastSymbolLayer extends CanvasLayer implements VisibleComponent {//,ImageGraphicsHandler{

/**
 * A CanvasLayer used for drawing catalog symbols.
 */

    /** If true, the layer is visible */
    private boolean _visible = true;

    /** Object used to draw catalog symbols */
    private FovastTablePlotter _fPlotter;

    public FovastSymbolLayer(){
        _fPlotter = new FovastTablePlotter();
    }
    /** Set the object used to draw catalog symbols */
    public void setPlotter(FovastTablePlotter plotter) {
        _fPlotter = plotter;
    }

    public void paint(Graphics2D g) {
        if (_fPlotter != null)
            _fPlotter.paintSymbols(g, null);
    }

    public void paint(Graphics2D g, Rectangle2D region) {
        if (_fPlotter != null)
            _fPlotter.paintSymbols(g, region);
    }

    public boolean isVisible() {
        return _visible;
    }

    public void setVisible(boolean b) {
        _visible = b;
    }
    
    public void setImageDisplay(FovastImageDisplay imageDisplay) {
        _fPlotter.setImageDisplay(imageDisplay);
       // imageDisplay.addImageGraphicsHandler(this);

    }

    public FovastTablePlotter getPlotter(){
        return _fPlotter;
    }

//    @Override
//    public void drawImageGraphics(BasicImageDisplay bid, Graphics2D gd) {
//        Set<Catalog> catalogs=_fPlotter.getCatalogList().keySet();
//        Iterator iter = catalogs.iterator();
//        while(iter.hasNext())
//        {
//            Catalog c = (Catalog)iter.next();
//            _fPlotter.makeList(c);
//        }
//    }

}
