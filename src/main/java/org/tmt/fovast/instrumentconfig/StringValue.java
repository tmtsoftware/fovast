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
public class StringValue extends Value {

    private String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StringValue) {
            if(value == null)
                return false;
            else
                return value.equals(((StringValue)obj).value);
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        if(value != null)
            return value.hashCode();
        else
            return super.hashCode();
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        if(value == null)
            return super.toString();
        else
            return value.toString();
    }


}

