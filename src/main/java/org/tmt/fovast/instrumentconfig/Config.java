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
import java.util.HashMap;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
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

    private static final String CONFIGOPTION_TAG_NAME = "ConfigOption";

    private static final String DISPLAYELEMENT_TAG_NAME = "DisplayElement";

    private static final String ID_ATTRIBUTE = "id";

    private static final String INSTRUMENT_CONFIG_XML =
            "/org/tmt/fovast/gui/resources/InstrumentConfig.xml";

    private ListenerSupport<Config.ConfigListener> listenerSupport
            = new ListenerSupport<Config.ConfigListener>();


    private ArrayList<ConfigElement> children = new ArrayList<ConfigElement>();

    private HashMap<String, Object> configElementMap = new HashMap<String, Object>();

    private HashMap<String, Object> dependencyMap = new HashMap<String, Object>();

    public void addConfigElement(ConfigElement element) {
        children.add(element);
    }

    public ArrayList<ConfigElement> getChildren() {
        return children;
    }

    public void setConfig(String confElementId, Value value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Value getConfig(String confElementId) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        List<Element> children = rootElement.getChildren();
        for(int i=0; i<children.size(); i++) {
            Element child = children.get(i);
            if(child.getName().equals(CONFIGOPTION_TAG_NAME)) {
                config.addConfigElement((ConfigElement) makeConfigObject(config, child));
            } else if(child.getName().equals(INSTRUMENT_TAG_NAME)) {
                config.addConfigElement((ConfigElement) makeConfigObject(config, child));
            }
            else {
                assert false;
            }
        }
        return config;
    }

    private static Object makeConfigObject(Config config, Element element) {
        String id = element.getAttributeValue(ID_ATTRIBUTE);
        String elementTagName = element.getName();
        
        ConfigElement configElement = null;

        if(elementTagName.equals(INSTRUMENT_TAG_NAME)) {
            Instrument inst = new Instrument(id);
            configElement = inst;
            config.configElementMap.put(id, inst);
        } else if(elementTagName.equals(CONFIGOPTION_TAG_NAME)) {
            ConfigOption configOption = new ConfigOption(id);
            configElement = configOption;
            config.configElementMap.put(id, configOption);
        } else if(elementTagName.equals(DISPLAYELEMENT_TAG_NAME)) {
            DisplayElement de = new DisplayElement(id);
            config.configElementMap.put(id, de);
            return de;
        } else {
            assert false;
        }

        //you only reach here if its a ConfigElement subclass ..
        
        List<Element> children = element.getChildren();
        if(configElement instanceof ConfigElement) {
            for(int i = 0; i<children.size(); i++) {
                Element child = children.get(i);
                if(child.getName().equals(CONFIGOPTION_TAG_NAME)) {
                    configElement.addElement((ConfigElement) makeConfigObject(
                            config, child));
                } else if(child.getName().equals(INSTRUMENT_TAG_NAME)) {
                    configElement.addElement((ConfigElement) makeConfigObject(
                            config, child));
                } else if(child.getName().equals(DISPLAYELEMENT_TAG_NAME)) {
                    configElement.addDisplayElement((DisplayElement)
                            makeConfigObject(config, child));
                }
                else {
                    assert false;
                }
            }
        }

        return configElement;
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
