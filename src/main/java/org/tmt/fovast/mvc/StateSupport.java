/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.mvc;

/**
 *
 * @author vivekananda_moosani
 */
public class StateSupport {

    protected ChangeSupport changeSupport = new ChangeSupport();

    public void addChangeListener(ChangeListener cl) {
        changeSupport.addChangeListener(cl);
    }

    public void removeChangeListener(ChangeListener cl) {
        changeSupport.removeChangeListener(cl);
    }

    public void addChangeListener(String eventKey, ChangeListener cl) {
        changeSupport.addChangeListener(eventKey, cl);
    }

    public void removeChangeListener(String eventKey, ChangeListener cl) {
        changeSupport.removeChangeListener(eventKey, cl);
    }

}
