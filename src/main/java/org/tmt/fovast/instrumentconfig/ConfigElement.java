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
public class ConfigElement {

    private String id;

    private String label;

    private Value value;

    private String selectByDefault;

    private EnableConditions enableConditions = new EnableConditions();

    private ArrayList<ConfigElement> elements = new ArrayList<ConfigElement>();

    private ArrayList<DisplayElement> displayElements = new ArrayList<DisplayElement>();

    private ConfigElement parent;

    public ConfigElement(String id) {
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSelectByDefault() {
        return selectByDefault;
    }

    public void setSelectByDefault(String selectByDefault) {
        this.selectByDefault = selectByDefault;
    }

    public ArrayList<DisplayElement> getDisplayElements() {
        return displayElements;
    }

    public void addDisplayElement(DisplayElement displayElement) {
        this.displayElements.add(displayElement);
    }

    public ArrayList<ConfigElement> getElements() {
        return elements;
    }

    public void addElement(ConfigElement element) {
        this.elements.add(element);
    }

    public ConfigElement getParent() {
        return parent;
    }

    public void setParent(ConfigElement parent) {
        this.parent = parent;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
