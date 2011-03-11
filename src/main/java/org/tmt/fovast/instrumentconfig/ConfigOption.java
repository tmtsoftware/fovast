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
public class ConfigOption {

    private String id;

    private Value value;

    private boolean selectByDefault;

    private EnableConditions enableConditions;

    private ArrayList<ConfigOption> elements = new ArrayList<ConfigOption>();

    private ArrayList<DisplayElement> displayElements = new ArrayList<DisplayElement>();

    private ConfigOption parent;

    private boolean enabled;

    private Value prevValue;

    private boolean prevValueSet = false;

    private String type;

    private PossibleValues possibleValues;

    private boolean captured;

    private String captureElementId;

    private boolean selectOnEnable;

    public ConfigOption(String id) {
        this.id = id;
        enableConditions = new EnableConditions();
        enableConditions.setCondition(new AlwaysTrueCondition());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EnableConditions getEnableConditions() {
        return enableConditions;
    }

    public void setEnableConditions(EnableConditions enableConditions) {
        this.enableConditions = enableConditions;
    }

    public boolean isSelectByDefault() {
        return selectByDefault;
    }

    public void setSelectByDefault(boolean selectByDefault) {
        this.selectByDefault = selectByDefault;
    }

    public ArrayList<DisplayElement> getDisplayElements() {
        return displayElements;
    }

    public void addDisplayElement(DisplayElement displayElement) {
        this.displayElements.add(displayElement);
        displayElement.setParent(this);
    }

    public ArrayList<ConfigOption> getElements() {
        return elements;
    }

    public void addElement(ConfigOption element) {
        this.elements.add(element);
        element.setParent(this);
    }

    public ConfigOption getParent() {
        return parent;
    }

    public void setParent(ConfigOption parent) {
        this.parent = parent;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.prevValue = this.value;
        this.prevValueSet = true;
        this.value = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Value getPrevValue() {
        return prevValue;
    }

//    public void setPrevValue(Value prevValue) {
//        this.prevValue = prevValue;
//    }

    public boolean isPrevValueSet() {
        return prevValueSet;
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

    boolean isSelectOnEnable() {
        return selectOnEnable;
    }

    public void setSelectOnEnable(boolean selectOnEnable) {
        this.selectOnEnable = selectOnEnable;
    }    
}

