/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */

package org.tmt.fovast.instrumentconfig;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ConfigHelper implements Config.ConfigListener{

    private static Logger logger = LoggerFactory.getLogger(ConfigHelper.class);
    
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
                logger.debug("Enabling .. config element " + configOption.getId());
                config.enableConfig(configOption.getId(), true);
                checkDependants(configOption.getId(), true);
            }

            if(configOption.isSelectByDefault()) {
                config.enableConfig(configOption.getId(), true);
                config.setConfigElementValue(configOption.getId(),
                        configOption.getPossibleValues().getDefaultValue());
                checkDependants(configOption.getId(), false);
            }
        }

    }

    public void addConfigListener(Config.ConfigListener l) {
        config.addConfigListener(l);
    }

    public void setConfig(String confElementId, Value value) {
        if(value == null && config.getConfig(confElementId) == null) {
            //do nothing
            return;
        }
        if(value == null || !value.equals(config.getConfig(confElementId))) {
            Object obj = config.configElementMap.get(confElementId);
//            if(obj instanceof DisplayElement) {
//                if(value == null && !((DisplayElement)obj).isVisible()) {
//                    //NOTE: do nothing .. we treat null = false for display elements
//                    return;
//                }
//            }

            logger.debug("Setting .. config element " + confElementId);
            config.setConfigElementValue(confElementId, value);
            //checkDependants(confElementId, false);
        }
    }

    private void checkDependants(String confElementId, boolean calledOnEnable) {
        logger.debug("Checking dependents for .. config element " + confElementId);
        //get a list of dependants
        Object objWhoseDepsAreBeingEvaled =
                config.configElementMap.get(confElementId);
        ArrayList dependants = (ArrayList) config.configOptionDependentsMap.get(
                objWhoseDepsAreBeingEvaled);
        if(dependants == null)
            return;
        //for each dependant
        for(int i=0; i<dependants.size(); i++) {            
            Object obj = dependants.get(i);
            if(obj instanceof ConfigOption) {
                ConfigOption co = (ConfigOption)obj;
                String depConfElementId = co.getId();
                logger.debug("Checking config element " + depConfElementId);
                ConfigOption parent = co.getParent();

                boolean parentEnabled = false;
                boolean parentSelected = false;
                if(parent == null) { // for main configNodes
                    parentEnabled = true;
                    parentSelected = true;
                }
                else {
                    parentEnabled = parent.isEnabled() ;
                    if(parent.getType().equals(Config.TYPE_ATTRIBUTE_VALUE_BOOLEAN))
                        parentSelected = (parent.getValue() != null) &&
                                (parent.getValue().equals(new BooleanValue(true)));
                    else
                        parentSelected = (parent.getValue() != null);
                }

                boolean shouldBeEnabled = parentEnabled && parentSelected &&
                        (co.getEnableConditions().getCondition().isTrue());

                if(shouldBeEnabled != co.isEnabled()) {
                    config.enableConfig(depConfElementId, shouldBeEnabled);
                    if(shouldBeEnabled) {
                        if(co.isSelectOnEnable()) {
                            config.setConfigElementValue(co.getId(), co.getPossibleValues().getDefaultValue());
                        } 
                        //if the dependant is a direct child .. and the parent was selected
                        //now check for selection conditions
                        else if((!calledOnEnable &&
                                ((parent == null) || (parent == objWhoseDepsAreBeingEvaled)) ) ) {

                            if(co.isPrevValueSet()) {
                                config.setConfigElementValue(co.getId(), co.getPrevValue());
                            } else if(co.isSelectByDefault()) {
                                config.setConfigElementValue(co.getId(), co.getPossibleValues().getDefaultValue());
                            }
                        }
                    } else {
                        if(co.getType().equals(Config.TYPE_ATTRIBUTE_VALUE_BOOLEAN)) {
                            config.setConfigElementValue(co.getId(), new BooleanValue(false));
                        } else {
                            config.setConfigElementValue(co.getId(), null);
                        }
                    }
                } else {
                    //config.enableConfig(depConfElementId, false);
                    //config.setConfigElementValue(depConfElementId, null);
                }
                
            } else if(obj instanceof DisplayElement) {
                DisplayElement de = (DisplayElement)obj;
                String depConfElementId = de.getId();
                ConfigOption parent = de.getParent();
                logger.debug("Checking config element " + depConfElementId);
                
                boolean parentEnabled = false;
                boolean parentSelected = false;
                if(parent == null) { // for main configNodes
                    parentEnabled = true;
                    parentSelected = true;
                }
                else {
                    parentEnabled = parent.isEnabled() ;
                    if(parent.getType().equals(Config.TYPE_ATTRIBUTE_VALUE_BOOLEAN))
                        parentSelected = (parent.getValue() != null) &&
                                (parent.getValue().equals(new BooleanValue(true)));
                    else
                        parentSelected = (parent.getValue() != null);
                }
                
                //note that display elements are not main level config elements ..
                //so no need to check if tehy are null.
                boolean shouldBeEnabled = parentEnabled && parentSelected &&
                        (de.getEnableConditions().getCondition().isTrue());                    


                if(shouldBeEnabled != de.isEnabled()) {
                    config.enableConfig(depConfElementId, shouldBeEnabled);

                    if(shouldBeEnabled) {
                        if(de.isShowOnEnable()) {
                            config.setConfigElementValue(de.getId(), new BooleanValue(true));
                        }
                        else if(!calledOnEnable &&
                                (de.getParent() == objWhoseDepsAreBeingEvaled)) {
                            if(de.isPrevVisibleSet()) {
                                config.setConfigElementValue(de.getId(), de.isPrevVisible());
                            } else if(de.isShowByDefault()) {
                                config.setConfigElementValue(de.getId(), new BooleanValue(true));
                            }
                        }
                    } else { // when being disabled set value to null 
                        config.setConfigElementValue(de.getId(), null);
                    }
                } else {
                    //config.enableConfig(depConfElementId, false);
                    //config.setConfigElementValue(depConfElementId, new BooleanValue(false));
                }

            } else {
                assert false : "checking dependent elements ..." +
                        " found unwanted element of class " + obj.getClass();
            }
        }
    }

    @Override
    public void updateConfigElementValue(String confElementId, Value value, boolean isDisplayElement) {
//        if(value == null && config.getConfig(confElementId) == null) {
//            //do nothing
//        }
//        if(value == null || !value.equals(config.getConfig(confElementId))) {
//            Object obj = config.configElementMap.get(confElementId);
//            if(obj instanceof DisplayElement) {
//                if(value == null && !((DisplayElement)obj).isVisible()) {
//                    //NOTE: do nothing .. we treat null = false for display elements
//                }
//            }
//
//            logger.debug("CheckDependents being called in listener");
//            checkDependants(confElementId, false);
//        }
        checkDependants(confElementId, false);
    }

    @Override
    public void enableConfig(String confElementId, boolean enable, boolean isDisplayElement) {
//        if(config.isEnabled(confElementId) == enable) {
//            checkDependants(confElementId, true);
//        }
        checkDependants(confElementId, true);
    }

    @Override
    public void updateConfigElementProperty(String confElementId, String propKey, String propValue) {
        //nothing to do
    }
}
