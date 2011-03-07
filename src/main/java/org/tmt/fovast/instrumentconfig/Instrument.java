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
public class Instrument extends Element {

    public Instrument(String id) {
        super(id);
        setValue(new BooleanValue(false));
    }

    public boolean isSelected() {
        return ((BooleanValue)getValue()).getValue();
    }

    public void setSelected(boolean selected) {
        setValue(new BooleanValue(selected));
    }
}
