/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.instrumentconfig;

/**
 *
 */
public class BooleanValue extends Value {

    private boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return  false;
        else if(obj instanceof BooleanValue) {
            return (value == ((BooleanValue)obj).value);
        }
        return false;
    }

    public int hashCode1() {
        if(value)
            return 1;
        else
            return 2;
    }

}
