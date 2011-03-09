/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */

package org.tmt.fovast.instrumentconfig;

import java.util.ArrayList;
import org.tmt.fovast.gui.FovastInstrumentTree;

/**
 *
 * @author vivekananda_moosani
 */
public class ConfigHelper implements Config.ConfigListener{

    public Config config;

    public ConfigHelper(Config config) {
        this.config = config;
        config.addConfigListener(this);
    }


    /**
     * to be used by control panel .. 
     */
    public void fireInitialEvents() {
        // We have to do this so that tree components are enabled and disabled
        // as per the initial state.
        ArrayList<ConfigOption> firstLevelConfig = config.getChildren();
        for(int i=0; i<firstLevelConfig.size(); i++) {
            ConfigOption configOption = firstLevelConfig.get(i);
            //independent of other elements 
            if(configOption.getEnableConditions() == null
                    || configOption.getEnableConditions().getCondition() == null
                    || configOption.getEnableConditions().getCondition(
                            ) instanceof AlwaysTrueCondition) {
                config.enableConfig(configOption.getId(), true);
                checkDependants(configOption.getId());
            }
        }

    }

    public void addConfigListener(Config.ConfigListener l) {
        config.addConfigListener(l);
    }

    public void setConfig(String confElementId, Value value) {
        if(value == null && config.getConfig(confElementId) == null) {
            //do nothing
        }
        if(value == null || !value.equals(config.getConfig(confElementId))) {
            Object obj = config.configElementMap.get(confElementId);
            if(obj instanceof DisplayElement) {
                if(value == null && !((DisplayElement)obj).isVisible()) {
                    //NOTE: do nothing .. we treat null = false for display elements
                }
            }

            config.setConfig(confElementId, value);
            checkDependants(confElementId);
        }
    }

    private void checkDependants(String confElementId) {
//        if(configOption.isSelectByDefault()) {
//            if(configOption.getType().equals(Config.TYPE_ATTRIBUTE_VALUE_BOOLEAN)) {
//                config.setConfig(configOption.getId(), null);
//            }
//        }

        //TODO: 
        //1. get a list of dependants
        //2. for each dependant 
        //3.    if the dep is ConfigOption then run enableConditions, 
        //4.        if it is true and option is not enabled .. then enable it ..
        //5.        else if option is not disabled .. then disable it.
        //6.    if dep is DependencyElement do
        //7.        do some thing similar to 3
        //8.    now check for selection conditions
        //9.    check if selectByDefault or showByDefault is true then
        //10.       if not selected already select/show it

    }

    @Override
    public void updateConfig(String confElementId, Value value) {
        if(value == null && config.getConfig(confElementId) == null) {
            //do nothing
        }
        if(value == null || !value.equals(config.getConfig(confElementId))) {
            Object obj = config.configElementMap.get(confElementId);
            if(obj instanceof DisplayElement) {
                if(value == null && !((DisplayElement)obj).isVisible()) {
                    //NOTE: do nothing .. we treat null = false for display elements
                }
            }
            
            checkDependants(confElementId);
        }
    }

    @Override
    public void batchUpdateConfig(ArrayList<String> confElementIds, ArrayList<Value> values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void enableConfig(String confElementId, boolean enable) {
        if(config.isEnabled(confElementId) == enable) {
            checkDependants(confElementId);
        }
    }
}
