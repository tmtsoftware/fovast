/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.gui;

import jsky.graphics.CanvasFigure;
import org.tmt.fovast.instrumentconfig.Config;

/**
 *
 */
class FovastShapeFactory {

    private final FovastImageDisplay fovastImageDisplay;

    private Config config;

    FovastShapeFactory(FovastImageDisplay fovastImageDisplay) {
        this.fovastImageDisplay = fovastImageDisplay;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public CanvasFigure makeFigure(String confElementId) {
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

}
