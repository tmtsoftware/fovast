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

    private ConfigOption element;

    private Value value;

    public EqualityCondition(ConfigOption element, Value value) {
        this.element = element;
        this.value = value;
    }

    /**
     * Returns true if the configOption has the said value ..
     * If the config option is not enabled then simply returns false
     * @return
     */
    @Override
    public boolean isTrue() {
        if(element instanceof ConfigOption) {
            ConfigOption co = (ConfigOption)element;
            if(co.getType().equals(Config.TYPE_ATTRIBUTE_VALUE_BOOLEAN)) {
                if(co.isEnabled()) {
                    return value.equals(co.getValue());
                }
                else {
                    if( ((BooleanValue)value).getValue() == false) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            } else {
                return co.isEnabled() && value.equals(co.getValue());
            }
        }
        else {
            new RuntimeException("Unknow element type encountered");
        }

        return false;
    }
}

