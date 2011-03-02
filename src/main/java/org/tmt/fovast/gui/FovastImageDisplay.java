/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.gui;

import jsky.image.gui.DivaMainImageDisplay;
import jsky.navigator.NavigatorPane;

/**
 * Mimicing NavigatorImageDisplay as needed ..
 * 
 */
class FovastImageDisplay extends DivaMainImageDisplay{

    private NavigatorPane _navigatorPane;

    public FovastImageDisplay() {
        super(new NavigatorPane());
        _navigatorPane = (NavigatorPane) getCanvasPane();
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

}
