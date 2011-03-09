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
public class PossibleValues {

    public static int SELECT_MODE_MUTUALLY_EXCLUSIVE = 1;

    private ArrayList<Value> values = new ArrayList<Value>();

    private Value defaultValue;

    private int selectMode;

    public int size() {
        return values.size();
    }

    public Value getValue(int i) {
        return values.get(i);
    }

    public void addValue(Value value) {
        values.add(value);
    }

    public Value getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Value defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }
}