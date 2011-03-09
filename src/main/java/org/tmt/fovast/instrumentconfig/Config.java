/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.instrumentconfig;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map.Entry;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.mvc.ListenerSupport;

/**
 * Just a holder of main config objects ..
 */
public class Config {

    private static Logger logger = LoggerFactory.getLogger(Config.class);

    private static final String INSTRUMENT_TAG_NAME = "Instrument";

    private static final String CONFIG_OPTION_TAG_NAME = "ConfigOption";

    private static final String DISPLAY_ELEMENT_TAG_NAME = "DisplayElement";

    private static final String POSSIBLE_VALUES_ELEMENT_TAG_NAME = "PossibleValues";

    private static final String VALUE_ELEMENT_TAG_NAME = "Value";

    private static final String ENABLE_CONDITIONS_TAG_NAME = "EnableConditions";

    private static final String CONDITION_TAG_NAME = "Condition";

    private static final String CONDITION_TYPE_VALUE_OR = "or";

    private static final String CONDITION_TYPE_VALUE_AND = "and";

    private static final String ID_ATTRIBUTE_NAME = "id";

    private static final String ELEMENT_ATTRIBUTE_NAME = "element";

    private static final String SELECT_BY_DEFAULT_ATTRIBUTE_NAME = "selectByDefault";

    private static final String TYPE_ATTRIBUTE_NAME = "type";

    private static final String VALUE_ATTRIBUTE_NAME = "value";

    static final String TYPE_ATTRIBUTE_VALUE_BOOLEAN = "bool";

    static final String TYPE_ATTRIBUTE_VALUE_STRING = "string";

    static final String TYPE_ATTRIBUTE_VALUE_FLOAT = "double";

    static final String TYPE_ATTRIBUTE_VALUE_INT = "long";

    private static final String SHOW_BY_DEFAULT_ATTRIBUTE_NAME = "showByDefault";

    private static final String SELECT_MODE_ATTRIBUTE_NAME = "selectMode";

    private static final String DEFAULT_ATTRIBUTE_NAME = "default";

    private static final String DEFAULT_ATTRIBUTE_VALUE_TRUE = "true";

    private static final String DEFAULT_ATTRIBUTE_VALUE_FALSE = "false";

    private static final String CAPTURED_ATTRIBUTE_NAME = "captured";

    private static final String CAPTURE_ELEMENT_ATTRIBUTE_NAME = "captureElement";
    
    
    private static final String INSTRUMENT_CONFIG_XML =
            "/org/tmt/fovast/gui/resources/InstrumentConfig.xml";

    private ListenerSupport<Config.ConfigListener> listenerSupport
            = new ListenerSupport<Config.ConfigListener>();

    private ArrayList<ConfigOption> children = new ArrayList<ConfigOption>();

    HashMap<String, Object> configElementMap = new HashMap<String, Object>();

    //configOption -> List of dependents
    HashMap configOptionDependentsMap = new HashMap();

    public void addConfigElement(ConfigOption element) {
        children.add(element);
    }

    public ArrayList<ConfigOption> getChildren() {
        return children;
    }

    public boolean setConfig(String confElementId, Value value) {
        Object obj = configElementMap.get(confElementId);
        if(obj instanceof ConfigOption) {
            //TODO: we donot check if the value is of proper type
            ConfigOption co = (ConfigOption)obj;
            if(value == null && co.getValue() == null) {
                //dont raise event
            } else if(value == null || !value.equals(co.getValue())) {
                co.setValue(value);
                fireClUpdateConfigEventForConfigOption(co);
                return true;
            }            
        } else if(obj instanceof DisplayElement) {
            if(value == null)
                value = new BooleanValue(false);
            if(value instanceof BooleanValue) {
                DisplayElement de = ((DisplayElement)obj);
                if(de.isVisible() != ((BooleanValue)value).getValue()) {
                    de.setVisible(((BooleanValue)value).getValue());
                    fireClUpdateConfigEventForDisplayElement(((DisplayElement)obj),
                            value);
                    return true;
                }
            }            
        }
        else {
            logger.error("value changed for element" +
                    "which is neither DisplayElement or ConfigOption ... " + confElementId);
        }
        return false;
    }

    public Value getConfig(String confElementId) {
        Object obj = configElementMap.get(confElementId);
        if(obj instanceof ConfigOption) {
            return ((ConfigOption)obj).getValue();
        } else if(obj instanceof DisplayElement) {
            return new BooleanValue(((DisplayElement)obj).isVisible());
        }
        else {            
            logger.error("value changed for element" +
                    "which is neither DisplayElement or ConfigOption ... " + confElementId);
            return null;
        }
    }

    public boolean enableConfig(String confElementId, boolean enable) {
        Object obj = configElementMap.get(confElementId);
        if(obj instanceof ConfigOption) {
            ConfigOption co = ((ConfigOption)obj);
            if(co.isEnabled() != enable) {
                co.setEnabled(enable);
                fireClEnableConfigEvent(confElementId, enable);
                return true;
            }
        } else if(obj instanceof DisplayElement) {
            DisplayElement de = (DisplayElement) obj;
            if(de.isEnabled() != enable) {
                de.setEnabled(enable);
                fireClEnableConfigEvent(confElementId, enable);
                return true;
            }
        }
        else {
            logger.error("value changed for element" +
                    "which is neither DisplayElement or ConfigOption ... " + confElementId);
        }
        return false;
    }

    boolean isEnabled(String confElementId) {
        Object obj = configElementMap.get(confElementId);
        if(obj instanceof ConfigOption) {
            return ((ConfigOption)obj).isEnabled();
        } else if(obj instanceof DisplayElement) {
            return ((DisplayElement)obj).isEnabled();
        }
        else {
            logger.error("value changed for element" +
                    "which is neither DisplayElement or ConfigOption ... " + confElementId);
            return false;
        }
    }

    public void addConfigListener(Config.ConfigListener l) {
        listenerSupport.addListener(l);
    }

    public void removeConfigListener(Config.ConfigListener l) {
        listenerSupport.removeListener(l);
    }

    //TODO: this method should be in some other class.
    //There should be a configHelper class which is created in control panel
    //Tree listens to configHelper and calls methods on configHelper
    //configHelper calls methods on config in addition to taking decisions
    //on what should be enabled and disabled.

    public static Config loadDefaultConfig() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        URL configURL = Config.class.getResource(INSTRUMENT_CONFIG_XML);
        Document document = builder.build(configURL);
        Element rootElement = document.getRootElement();

        Config config = new Config();
        HashMap<Element, ConfigOption> enableConditionElements =
                new HashMap<Element, ConfigOption>();
        
        List<Element> children = rootElement.getChildren();
        for(int i=0; i<children.size(); i++) {
            Element child = children.get(i);
            if(child.getName().equals(CONFIG_OPTION_TAG_NAME)) {
                config.addConfigElement((ConfigOption) makeConfigObject(config,
                        enableConditionElements, child));
            } else if(child.getName().equals(INSTRUMENT_TAG_NAME)) {
                config.addConfigElement((ConfigOption) makeConfigObject(config,
                        enableConditionElements, child));
            }
            else {
                assert false : "Encountered child.getName()";
            }
        }
        buildingEnableConditions(config, enableConditionElements);
        return config;
    }

    private static Object makeConfigObject(Config config,
            HashMap<Element, ConfigOption> enableConditionElements, Element element) {
        String id = element.getAttributeValue(ID_ATTRIBUTE_NAME);
        boolean selectByDefault = Boolean.parseBoolean(
                element.getAttributeValue(SELECT_BY_DEFAULT_ATTRIBUTE_NAME));
        String type = element.getAttributeValue(TYPE_ATTRIBUTE_NAME);
        boolean captured = Boolean.parseBoolean(
                element.getAttributeValue(CAPTURED_ATTRIBUTE_NAME));
        String captureElementId = element.getAttributeValue(CAPTURE_ELEMENT_ATTRIBUTE_NAME);
        
        //String selectMode = element.getAttributeValue(SELECT_MODE_ATTRIBUTE_NAME);
        //String default = element.getAttributeValue(DEFAULT_ATTRIBUTE_NAME);

        String elementTagName = element.getName();
        
        ConfigOption configOption = null;

        if(elementTagName.equals(INSTRUMENT_TAG_NAME) ||
                elementTagName.equals(CONFIG_OPTION_TAG_NAME)) {
            configOption = new ConfigOption(id);
            configOption.setType(type);
            configOption.setCaptured(captured);
            configOption.setCaptureElementId(captureElementId);
            configOption.setSelectByDefault(selectByDefault);
            if(type == null)
                configOption.setType(TYPE_ATTRIBUTE_VALUE_BOOLEAN);
            else
                configOption.setType(type);            
            
            if(type == null || type.equals(TYPE_ATTRIBUTE_VALUE_BOOLEAN)) {
                PossibleValues possibleValues = new PossibleValues();
                Value trueValue = new BooleanValue(true);
                Value falseValue = new BooleanValue(false);
                possibleValues.addValue(trueValue);
                possibleValues.addValue(falseValue);
                if(selectByDefault)
                    possibleValues.setDefaultValue(trueValue);
                else
                    possibleValues.setDefaultValue(falseValue);
                configOption.setPossibleValues(possibleValues);
            }

            config.configElementMap.put(id, configOption);

            List<Element> children = element.getChildren();
            for(int i = 0; i<children.size(); i++) {
                Element child = children.get(i);
                if(child.getName().equals(CONFIG_OPTION_TAG_NAME)) {
                    ConfigOption childConfigOption = (ConfigOption) makeConfigObject(
                            config, enableConditionElements, child);
                    configOption.addElement(childConfigOption);
                    addDependant(config, configOption, childConfigOption);
                } else if(child.getName().equals(INSTRUMENT_TAG_NAME)) {
                    ConfigOption childConfigOption = (ConfigOption) makeConfigObject(
                            config, enableConditionElements, child);
                    configOption.addElement(childConfigOption);
                    addDependant(config, configOption, childConfigOption);
                } else if(child.getName().equals(DISPLAY_ELEMENT_TAG_NAME)) {
                    DisplayElement childDisplayElement = (DisplayElement)
                            makeConfigObject(config, enableConditionElements, child);
                    configOption.addDisplayElement(childDisplayElement);
                    addDependant(config, configOption, childDisplayElement);
                } else if(child.getName().equals(POSSIBLE_VALUES_ELEMENT_TAG_NAME)) {
                    //ignore boolean types .. possible values does not make sense
                    String configType = configOption.getType();
                    if(!configType.equals(TYPE_ATTRIBUTE_VALUE_BOOLEAN)) {
                        PossibleValues possibleValues = new PossibleValues();                        
                        List<Element> subChildren = child.getChildren();
                        for(int j=0; j<subChildren.size(); j++) {
                            Element subChild = subChildren.get(j);
                            if(subChild.getName().equals(VALUE_ELEMENT_TAG_NAME)) {
                                Value value = null;
                                if(configType.equals(TYPE_ATTRIBUTE_VALUE_STRING)) {
                                    value = new StringValue(subChild.getText());
                                    possibleValues.addValue(value);
                                    if(DEFAULT_ATTRIBUTE_VALUE_TRUE.equals(
                                            subChild.getAttributeValue(DEFAULT_ATTRIBUTE_NAME))) {
                                        possibleValues.setDefaultValue(value);
                                    }
                                } else if(configType.equals(TYPE_ATTRIBUTE_VALUE_FLOAT)) {
                                    assert false : "Float not implemented";
                                } else if(configType.equals(TYPE_ATTRIBUTE_VALUE_INT)) {
                                    assert false : "Int not implemented";
                                } else {
                                    assert false : "Unknown type";
                                }
                                

                            } else {
                                assert false : "Found in Values child with tag "
                                        + subChild.getName();
                            }
                        }
                        configOption.setPossibleValues(possibleValues);
                    }
                }  else if(child.getName().equals(ENABLE_CONDITIONS_TAG_NAME)) {                    
                    enableConditionElements.put(child, configOption);
                } else {
                    assert false : "Found child with tag " + child.getName();
                }
            }           
            
            return configOption;
            
        } else if(elementTagName.equals(DISPLAY_ELEMENT_TAG_NAME)) {
            
            boolean showByDefault =  Boolean.parseBoolean(
                    element.getAttributeValue(SELECT_BY_DEFAULT_ATTRIBUTE_NAME));

            DisplayElement de = new DisplayElement(id);
            de.setShowByDefault(showByDefault);
            
            config.configElementMap.put(id, de);
            return de;
        } else {
            assert false : "Found unknown tag " + elementTagName;
        }
        
        return null;
    }

    private static void addDependant(Config config, ConfigOption configOption,
            Object childObject) {
        ArrayList dependants = (ArrayList) config.configOptionDependentsMap.get(configOption);
        if(dependants == null) {
            dependants = new ArrayList();
            config.configOptionDependentsMap.put(configOption, dependants);
        }
        dependants.add(childObject);
    }


    private static void buildingEnableConditions(Config config,
            HashMap<Element, ConfigOption> enableConditionElements) {
        Iterator<Entry<Element, ConfigOption>> ite =
                enableConditionElements.entrySet().iterator();
        while(ite.hasNext()) {
            
            EnableConditions enableConditions = new EnableConditions();
            Entry<Element, ConfigOption> entry = ite.next();
            Element child = entry.getKey();
            ConfigOption configOption = entry.getValue();
            
            List<Element> subChildren = child.getChildren();
            for(int j=0; j<subChildren.size(); j++) {
                Element subChild = subChildren.get(j);
                Condition condition = makeCondition(config, configOption, subChild);
                enableConditions.setCondition(condition);
            }
            configOption.setEnableConditions(enableConditions);
        }
    }

    private static Condition makeCondition(Config config, ConfigOption parentConfigOption
            , Element element) {

        assert element.getName().equals(CONDITION_TAG_NAME) :
            "Encoundered some other tag in makeCondition" + element.getName();

        String conditionType = element.getAttributeValue(TYPE_ATTRIBUTE_NAME);
        if(CONDITION_TYPE_VALUE_OR.equals(conditionType)) {
            return makeOrCondition(config, parentConfigOption, element);
        } else if(CONDITION_TYPE_VALUE_AND.equals(conditionType)) {
            return makeAndCondition(config, parentConfigOption, element);
        } else if(conditionType == null) {
            //this means its a simple equality condition
            return makeEqualityCondition(config, parentConfigOption, element);
        } else {
            assert false : "Unknown condition type " + conditionType;
        }
        return null;
    }

    private static OrCondition makeOrCondition(Config config,
            ConfigOption parentConfigOption, Element element) {
        List<Element> children = element.getChildren();
        if(children.size() != 2)
            assert false : "incorrect number of params"; // this is for now .. 
        Condition leftChild = makeCondition(config, parentConfigOption, children.get(0));
        Condition rightChild = makeCondition(config, parentConfigOption, children.get(1));
        OrCondition condition = new OrCondition(leftChild, rightChild);
        return condition;
    }

    private static AndCondition makeAndCondition(Config config,
            ConfigOption parentConfigOption, Element element) {
        List<Element> children = element.getChildren();
        if(children.size() != 2)
            assert false : "incorrect number of params"; // this is for now ..
        Condition leftChild = makeCondition(config, parentConfigOption, children.get(0));
        Condition rightChild = makeCondition(config, parentConfigOption, children.get(1));
        AndCondition condition = new AndCondition(leftChild, rightChild);
        return condition;
    }

    private static EqualityCondition makeEqualityCondition(Config config, 
            ConfigOption parentConfigOption, Element element) {
        String id = element.getAttributeValue(ELEMENT_ATTRIBUTE_NAME);
        String valueString = element.getAttributeValue(VALUE_ATTRIBUTE_NAME);

        ConfigOption configOption = (ConfigOption) config.configElementMap.get(id);
        if(configOption == null)
            assert false : "Could not find element " + id;
        Value value = null;
        String attVal = configOption.getType();
        if(attVal == null || attVal.equals(TYPE_ATTRIBUTE_VALUE_BOOLEAN)) {
            value = new BooleanValue(Boolean.parseBoolean(valueString));
        } else if(attVal.equals(TYPE_ATTRIBUTE_VALUE_STRING)) {
            value = new StringValue(valueString);
        } else if(attVal.equals(TYPE_ATTRIBUTE_VALUE_FLOAT)) {
            assert false : "Not supported";
        } else if(attVal.equals(TYPE_ATTRIBUTE_VALUE_INT)) {
            assert false : "Not supported";
        } else {
            assert false : "Not supported";
        }
        
        EqualityCondition condition = new EqualityCondition(configOption, value);

        addDependant(config, configOption, parentConfigOption);

        return condition;
    }

    private void fireClUpdateConfigEventForConfigOption(ConfigOption configOption) {
        ArrayList<ConfigListener> listeners = listenerSupport.getListeners();
        for(int i=0; i<listeners.size(); i++) {
            listeners.get(i).updateConfig(configOption.getId(),
                    configOption.getValue());
        }
    }

    private void fireClUpdateConfigEventForDisplayElement(DisplayElement displayElement,
            Value value) {
        ArrayList<ConfigListener> listeners = listenerSupport.getListeners();
        for(int i=0; i<listeners.size(); i++) {
            listeners.get(i).updateConfig(displayElement.getId(), value);
        }
    }

    private void fireClEnableConfigEvent(String id, boolean enabled) {
        ArrayList<ConfigListener> listeners = listenerSupport.getListeners();
        for(int i=0; i<listeners.size(); i++) {
            listeners.get(i).enableConfig(id, enabled);
        }
    }


    public static interface ConfigListener {

        /**
         * Called when config element values change .. 
         * 
         * @param confElementId - this can be of any element in the XML.
         *                        (DisplayElement / Instrument / ConfigOption)
         * @param value
         */
        public void updateConfig(String confElementId, Value value);

        /** As of now not being used .. */
        public void batchUpdateConfig(ArrayList<String> confElementIds,
                ArrayList<Value> values);

        public void enableConfig(String confElementId, boolean enable);
    }
}
