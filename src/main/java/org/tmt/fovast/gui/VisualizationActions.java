/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.gui;

import org.jdesktop.application.Action;

/**
 *
 * @author vivekananda_moosani
 */
public class VisualizationActions {

    private VisualizationPanel visualizationPanel;

    public VisualizationActions(VisualizationPanel vp) {
        this.visualizationPanel = vp;
    }

    @Action(name = "VisCp.setScienceTarget")
    public void setScienceTarget() {
        //vp.setScienceTarget();
    }

}
