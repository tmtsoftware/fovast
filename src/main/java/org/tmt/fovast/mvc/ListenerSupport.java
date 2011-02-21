/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */

package org.tmt.fovast.mvc;

import java.util.ArrayList;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note this class allows you do some thing like
 *  private ListenerSupport support = new ListenerSupport();
 * By doing that you can add object as a Listener which could be a problem. Please
 * donot do such thing. 
 *
 * @author vivekananda_moosani
 */
public class ListenerSupport<T1> {

    public static Logger logger = LoggerFactory.getLogger(ListenerSupport.class);

    private ArrayList genericListeners = new ArrayList();

    public ListenerSupport() {
        //nothing to do
    }


    public synchronized <T2 extends T1> void addChangeListener(T2 t2) {
        if (!genericListeners.contains(t2)) {
            genericListeners.add(t2);
        }
    }

    public synchronized <T2 extends T1> void removeChangeListener(T2 t2) {
        genericListeners.remove(t2);
    }

    public synchronized void fireChange(String methodName, Object... args) {
        for (int i = 0; i < genericListeners.size(); i++) {
            try {
                Object listener = genericListeners.get(i);
                Class clz = listener.getClass();
                Class[] classesArray = new Class[args.length];
                for(int j = 0; i < args.length; i++) {
                    classesArray[i] = args[i].getClass();
                }
                Method m = clz.getMethod(methodName, classesArray);
                m.invoke(listener, args);
            } catch (Exception ex) {
                logger.error("Could not call listener method", ex);
            }
        }
    }
}
