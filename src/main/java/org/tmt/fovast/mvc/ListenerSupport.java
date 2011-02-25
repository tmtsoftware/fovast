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
 * By doing that you can add any object as a Listener which could be a problem.
 * Please donot do such thing.
 *
 *
 * @author vivekananda_moosani
 */
public class ListenerSupport<T1> {

    public static Logger logger = LoggerFactory.getLogger(ListenerSupport.class);

    protected ArrayList genericListeners = new ArrayList();

    public ListenerSupport() {
        //nothing to do
    }

    public ArrayList getGenericListeners() {
        return genericListeners;
    }

    public synchronized <T2 extends T1> void addListener(T2 t2) {
        if (!genericListeners.contains(t2)) {
            genericListeners.add(t2);
        }
    }

    public synchronized <T2 extends T1> void removeListener(T2 t2) {
        genericListeners.remove(t2);
    }

// This implementation is tricky to be done correctly
// args if set to null will create problem.
// Supporting primitives is also tricky as the passed arguments are objects and
// we would not easily be able to check if the arg was intended to be primitive
// or wrapper type
//
//    public synchronized void fireChange(String methodName, Object... args) {
//        for (int i = 0; i < genericListeners.size(); i++) {
//            try {
//                Object listener = genericListeners.get(i);
//                Class clz = listener.getClass();
//                Class[] classesArray = new Class[args.length];
//                for(int j = 0; j < args.length; j++) {
//                    classesArray[j] = args[j].getClass();
//                }
//                Method m = clz.getMethod(methodName, classesArray);
//                m.invoke(listener, args);
//            } catch (Exception ex) {
//                logger.error("Could not call listener method", ex);
//            }
//        }
//    }
}
