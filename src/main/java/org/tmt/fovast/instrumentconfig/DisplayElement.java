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
public class DisplayElement {

    private String id;

    private String label;

    private boolean showByDefault;

    private Element parent;

    public DisplayElement(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isShowByDefault() {
        return showByDefault;
    }

    public void setShowByDefault(boolean showByDefault) {
        this.showByDefault = showByDefault;
    }

    public Element getParent() {
        return parent;
    }

    public void setParent(Element parent) {
        this.parent = parent;
    }
}

