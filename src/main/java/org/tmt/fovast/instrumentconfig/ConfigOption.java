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
public class ConfigOption extends ConfigElement {

    private String type;

    private PossibleValues possibleValues;

    private boolean captured;

    private String captureElementId;

    public ConfigOption(String id) {
        super(id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPossibleValues(PossibleValues possibleValues) {
        this.possibleValues = possibleValues;
    }

    public PossibleValues getPossibleValues() {
        return possibleValues;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    public boolean isCaptured() {
        return captured;
    }

    void setCaptureElementId(String captureElementId) {
        this.captureElementId = captureElementId;
    }

    public String getCaptureElementId() {
        return captureElementId;
    }
}

