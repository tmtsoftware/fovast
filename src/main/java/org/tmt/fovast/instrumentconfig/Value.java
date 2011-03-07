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
public abstract class Value {

    public static Value createValueFromStringForm(String configOptionValue) {
        //TODO: Need to add support for other types like int, float, position ... 
        return new StringValue(configOptionValue);
    }

}
