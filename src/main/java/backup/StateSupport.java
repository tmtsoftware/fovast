/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package backup;

import org.tmt.fovast.mvc.ListenerSupport;

/**
 *
 * @author vivekananda_moosani
 */
public class StateSupport<T1> {

    protected ListenerSupport<T1> listenerSupport = new ListenerSupport<T1>();

    public <T2 extends T1> void addListener(T2 t2) {
        listenerSupport.addListener(t2);
    }

    public synchronized <T2 extends T1> void removeListener(T2 t2) {
        listenerSupport.removeListener(t2);
    }
}
