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
public class Values {

    public static int SELECT_MODE_MUTUALLY_EXCLUSIVE = 1;

    private ArrayList<Value> values = new ArrayList<Value>();

    private Value defaultValue;

    private int selectMode;

    public int size() {
        return values.size();
    }

}