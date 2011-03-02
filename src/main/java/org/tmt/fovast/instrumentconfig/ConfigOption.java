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
public class ConfigOption extends Element {

    private String label;

    private String type;

    private Values possibleValues;

    private Value value;

    private EnableConditions enableConditions;

    public ConfigOption(String id) {
        super(id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Values getPossibleValues() {
        return possibleValues;
    }

    public void getPossibleValues(Values values) {
        this.possibleValues = values;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}

