/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.instrumentconfig;

import java.util.ArrayList;

/**
 *
 */
public class FovastConfigHelper {

    public FovastConfigHelper() {
        
    }

    public void setConfig(String confElementId, Value value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Value getConfig(String confElementId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static interface ConfigListener {
        
        boolean updateConfig(String confElementId, Value value);

        boolean batchUpdateConfig(ArrayList<String> confElementIds,
                ArrayList<Value> values);
    }
}
