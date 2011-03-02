/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */
package backup;

import backup.ChangeSupport;

/**
 *
 */
public class StateSupportUsingChangeSupport {

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
