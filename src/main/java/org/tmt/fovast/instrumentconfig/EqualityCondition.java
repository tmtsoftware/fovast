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

public class EqualityCondition extends Condition {

    private ConfigElement element;

    private String value;

    public EqualityCondition(ConfigElement element, String value) {
        this.element = element;
        this.value = value;
    }

    @Override
    public boolean isTrue() {
        if(element instanceof Instrument) {
            return ((Instrument)element).isSelected();
        }
        else if(element instanceof ConfigOption) {
            ConfigOption co = (ConfigOption)element;
            return value.equals(co.getValue());
        }
        else {
            new RuntimeException("Unknow element type encountered");
        }

        return false;
    }
}

