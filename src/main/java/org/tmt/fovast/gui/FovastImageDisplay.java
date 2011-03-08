/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.gui;

import java.awt.geom.AffineTransform;
import jsky.image.gui.DivaMainImageDisplay;
import jsky.navigator.NavigatorPane;

/**
 * Mimicing NavigatorImageDisplay as needed ..
 * 
 */
class FovastImageDisplay extends DivaMainImageDisplay{

    private FovastNavigatorPane _navigatorPane;

    public FovastImageDisplay() {
        super(new FovastNavigatorPane());
        _navigatorPane = (FovastNavigatorPane) getCanvasPane();
        _navigatorPane.setImageDisplay(this);
    }

    /**
     * Display or hide the WCS grid
     */
    public void toggleGrid() {
        setGridVisible(!isGridVisible());
    }

    /**
     * @return true if the WCS grid is visible
     */
    public boolean isGridVisible() {
        return _navigatorPane.getGridLayer().isVisible();
    }

    /**
     * Sets the visibility of the WCS grid
     *
     * @param visible set to true to show the grid
     */
    public void setGridVisible(boolean visible) {
        if (visible != isGridVisible()) {
            _navigatorPane.getGridLayer().setVisible(visible);
            repaint();
        }
    }

    public FovastNavigatorPane getNavigatorPane() {
        return _navigatorPane;
    }

    public FovastSymbolLayer getSymbolLayer() {
        return _navigatorPane.getSymbolLayer();
    }

    /**
     * Transform the image graphics using the given AffineTransform.
     */
    protected void transformGraphics(AffineTransform trans) {
        super.transformGraphics(trans);
        if (_navigatorPane != null) {
            FovastTablePlotter plotter = _navigatorPane.getSymbolLayer().getPlotter();
            if (plotter != null) {
                plotter.transformGraphics(trans);
                _navigatorPane.getSymbolLayer().repaint();
            }
        }
    }


}
