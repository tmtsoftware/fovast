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
public class Element {

    private String id;

    private String label;

    private Value value;

    private String selectByDefault;

    private EnableConditions enableConditions = new EnableConditions();

    private ArrayList<Element> elements = new ArrayList<Element>();

    private ArrayList<DisplayElement> displayElements = new ArrayList<DisplayElement>();

    private Element parent;

    public Element(String id) {
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

    public ArrayList<Element> getElements() {
        return elements;
    }

    public void addElement(Element element) {
        this.elements.add(element);
    }

    public Element getParent() {
        return parent;
    }

    public void setParent(Element parent) {
        this.parent = parent;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
