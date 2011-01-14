/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.mvc;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Support class to add / remove change listeners and fire change events ..
 * @author vivekananda_moosani
 */
public class ChangeSupport {

    /**
     * Stores generic listeners
     */
    private ArrayList<ChangeListener> genericListeners;

    /**
     *  Stores listeners for specific events .. 
     */
    private HashMap<String, ArrayList<ChangeListener>> listenerMap;

    public ChangeSupport() {
        genericListeners = new ArrayList<ChangeListener>();
        listenerMap = new HashMap<String, ArrayList<ChangeListener>>();
    }

    public synchronized void addChangeListener(ChangeListener cl) {
        if (!genericListeners.contains(cl)) {
            genericListeners.add(cl);
        }
    }

    public synchronized void removeChangeListener(ChangeListener cl) {
        genericListeners.remove(cl);
    }

    public synchronized void addChangeListener(String eventKey, ChangeListener cl) {
        ArrayList<ChangeListener> al = listenerMap.get(eventKey);
        if (al == null) {
            al = new ArrayList<ChangeListener>();
            listenerMap.put(eventKey, al);
        }
        if (!al.contains(cl)) {
            al.add(cl);
        }
    }

    public synchronized void removeChangeListener(String eventKey, ChangeListener cl) {
        ArrayList<ChangeListener> al = listenerMap.get(eventKey);
        if (al != null) {
            al.remove(cl);
        }
    }

    public synchronized void fireChange(Object source, String eventKey, HashMap<String, Object> args) {
        for (int i = 0; i < genericListeners.size(); i++) {
            genericListeners.get(i).update(source, eventKey, args);
        }

        //look for listeners of only this event
        ArrayList<ChangeListener> al = listenerMap.get(eventKey);
        if (al != null) {
            for (int i = 0; i < al.size(); i++) {
                al.get(i).update(source, eventKey, args);
            }
        }
    }

}
