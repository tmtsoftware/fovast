/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tmt.fovast.gui;

/**
 *
 */

import diva.canvas.GraphicsPane;
import diva.canvas.CanvasLayer;
import jsky.catalog.gui.TablePlotter;
import jsky.image.graphics.CoordsGridLayer;
import jsky.image.gui.MainImageDisplay;

public class FovastNavigatorPane extends GraphicsPane{

    /**
     * A layer on which to draw catalog symbols
     */
    private FovastSymbolLayer _symbolLayer;

    /**
     * A layer for drawing a WCS grid
     */
    private CoordsGridLayer _gridLayer;

    /**
     * Initialize a new NavigatorPane, which is a Diva GraphicsPane with a layer added
     * for catalog symbols.
     */
    public FovastNavigatorPane() {
        _symbolLayer = new FovastSymbolLayer();
        _initNewLayer(_symbolLayer);
        _gridLayer = new CoordsGridLayer();
        _initNewLayer(_gridLayer);
        _rebuildLayerArray();
    }

    /**
     * @return the layer to use to draw the catalog symbols.
     */
    public FovastSymbolLayer getSymbolLayer() {
        return _symbolLayer;
    }

    /**
     * @return the layer to use to draw the WCS grid
     */
    public CoordsGridLayer getGridLayer() {
        return _gridLayer;
    }

    /**
     * Sets the object used to draw catalog symbols
     * @param plotter the object used to draw catalog symbols
     */
    public void setPlotter(FovastTablePlotter plotter) {
        _symbolLayer.setPlotter(plotter);
    }

    /**
     * Sets the image display to use for the grid
     * @param imageDisplay the image display to use
     */
    public void setImageDisplay(FovastImageDisplay imageDisplay) {
        _gridLayer.setImageDisplay(imageDisplay);
        _symbolLayer.setImageDisplay(imageDisplay);
    }



    /**
     * Rebuild the array of layers for use by iterators.
     * Override superclass to include the new layer.
     */
    protected void _rebuildLayerArray() {
        _layers = new CanvasLayer[7];
        int cursor = 0;
        _layers[cursor++] = _foregroundEventLayer;
        _layers[cursor++] = _overlayLayer;
        _layers[cursor++] = _foregroundLayer;
        _layers[cursor++] = _symbolLayer;
        //_layers[cursor++] = _overlayLayer;
        //_layers[cursor++] = _foregroundLayer;
        _layers[cursor++] = _gridLayer;
        _layers[cursor++] = _backgroundLayer;
        _layers[cursor] = _backgroundEventLayer;
    }
}



