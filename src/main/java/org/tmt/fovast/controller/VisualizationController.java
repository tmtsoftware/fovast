/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.controller;

import java.util.HashMap;
import org.tmt.fovast.mvc.ChangeListener;
import org.tmt.fovast.mvc.ChangeSupport;
import org.tmt.fovast.state.VisualizationState;

/**
 *
 * @author vivekananda_moosani
 */
public class VisualizationController implements ChangeListener {

    private VisualizationState visualization;

    private ChangeSupport cs = new ChangeSupport();

    public void setState(VisualizationState visualization) {
        this.visualization = visualization;
        this.visualization.addChangeListener(this);
    }

    public void setTarget(Double ra, Double dec) {
        visualization.setTarget(ra, dec);
    }

    public void showTarget(boolean show) {
        visualization.showTarget(show);
    }

    public void addChangeListener(ChangeListener cl) {
        cs.addChangeListener(cl);
    }

    public void removeChangeListener(ChangeListener cl) {
        cs.removeChangeListener(cl);
    }

    public void addChangeListener(String eventKey, ChangeListener cl) {
        cs.addChangeListener(eventKey, cl);
    }

    public void removeChangeListener(String eventKey, ChangeListener cl) {
        cs.removeChangeListener(eventKey, cl);
    }

    @Override
    public void update(Object source, String eventKey, HashMap<String, Object> args) {
        cs.fireChange(this, eventKey, args);
    }

}
