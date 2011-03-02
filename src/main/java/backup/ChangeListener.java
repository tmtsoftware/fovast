/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package backup;

import java.util.HashMap;

/**
 * For listening to arbitrary events ..
 * ChangeEvent will have the following structure
 *  ChangeEvent
 *      - source, eventKey, args .. 
 *
 *
 */
public interface ChangeListener {

    public void update(Object source, String eventKey, HashMap<String, Object> args);

}
