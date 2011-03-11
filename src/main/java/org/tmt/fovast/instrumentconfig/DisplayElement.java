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

    private boolean showByDefault;

    private ConfigOption parent;

    /**
     * can be true, false, null
     * this could have been boolean object .. I dont want autoboxing 
     * to hide any mismatches .. so using BooleanValue
     */
    private BooleanValue visible;

    private boolean enabled;

    private BooleanValue prevVisible;

    private boolean prevVisibleSet = false;

    private EnableConditions enableConditions = new EnableConditions();

    private boolean showOnEnable;

    public DisplayElement(String id) {
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

    public boolean isShowByDefault() {
        return showByDefault;
    }

    public void setShowByDefault(boolean showByDefault) {
        this.showByDefault = showByDefault;
    }

    public ConfigOption getParent() {
        return parent;
    }

    public void setParent(ConfigOption parent) {
        this.parent = parent;
    }

    public BooleanValue isVisible() {
        return visible;
    }

    public void setVisible(BooleanValue visible) {
        this.prevVisible = this.visible;
        this.prevVisibleSet = true;
        this.visible = visible;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public BooleanValue isPrevVisible() {
        return prevVisible;
    }

    public void setPrevVisible(BooleanValue prevVisible) {
        this.prevVisible = prevVisible;
    }

    boolean isPrevVisibleSet() {
        return prevVisibleSet;
    }

    public EnableConditions getEnableConditions() {
        return enableConditions;
    }

    public void setEnableConditions(EnableConditions enableConditions) {
        this.enableConditions = enableConditions;
    }

    public boolean isShowOnEnable() {
        return showOnEnable;
    }

    public void setShowOnEnable(boolean showOnEnable) {
        this.showOnEnable = showOnEnable;
    }

}

