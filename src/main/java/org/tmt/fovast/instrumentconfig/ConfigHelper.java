/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.instrumentconfig;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.mvc.ListenerSupport;

/**
 *
 */
public class ConfigHelper {

    private static Logger logger = LoggerFactory.getLogger(ConfigHelper.class);

    private static final String INSTRUMENT_TAG_NAME = "Instrument";

    private static final String CONFIGOPTION_TAG_NAME = "ConfigOption";

    private static final String DISPLAY_TAG_NAME = "DisplayElement";

    private static final String ID_ATTRIBUTE = "id";

    private static final String INSTRUMENT_CONFIG_XML =
            "/org/tmt/fovast/gui/resources/InstrumentConfig.xml";

    private Config config;

    private ListenerSupport<Config.ConfigListener> listenerSupport
            = new ListenerSupport<Config.ConfigListener>();
    
    public ConfigHelper() {
    }

    public Config getConfig() {
        return config;
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

    public static Config loadDefaultConfig() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        URL configURL = ConfigHelper.class.getResource(INSTRUMENT_CONFIG_XML);
        Document document = builder.build(configURL);
        Element rootElement = document.getRootElement();

        Config config = new Config();
//        List<Element> children = rootElement.getChildren();
//        for(int i=0; i<children.size(); i++) {
//            Element child = children.get(i);
//            config.add((Instrument)makeConfigObject(child));
//        }
        return config;
    }

//    private static Element makeConfigObject(Element element) {
//        String id = element.getAttributeValue(ID_ATTRIBUTE);
//        List<Element> children = element.getChildren();
//
//        if(element.equals(CONFIGOPTION_TAG_NAME)) {
//            Instrument inst = new Instrument(id);
//            for(int = 0; i<children.size(); i++) {
//                Element child = children.get(i);
//                if(child.getName().equals(CONFIGOPTION_TAG_NAME)) {
//                    makeConfigObject(element);
//                } else {
//                    makeConfigObject(children.get(i));
//                }
//
//            }
//            return inst;
//        } else if(element.equals(DISPLAY_TAG_NAME)) {
//            DisplayElement de = new DisplayElement(id);
//
//            return de;
//        } else {
//            assert false;
//        }
//    }
}
