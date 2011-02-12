/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JDialog;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import voi.swing.util.DialogCreator;

/**
 * Utility class for app configuration (preferences) load/save and editing
 *
 * @author vivekananda_moosani
 */
public class AppConfiguration {

    public static String MISC_HIDDEN_PROPKEY = "MiscHiddenProperties";
    public static String MISC_FILE_DIALOG_DIR_PROPKEY = "fileDialogPath";
    
    private LinkedHashMap<String, Object> properties =
            new LinkedHashMap<String, Object>();

    private ConfigurationGui configGui =
            new ConfigurationGui(properties);

    private File preferencesFile;
    
    public AppConfiguration(File preferencesFile) throws LoadException{
        this.preferencesFile = preferencesFile;
        if(preferencesFile.exists()) {
            try {
                loadConfiguration();
            } catch(Exception ex) {
                throw new LoadException(ex);
            }
        }
        else {
            LinkedHashMap<String, Object> miscHiddenProps = new LinkedHashMap<String, Object>();
            properties.put(MISC_HIDDEN_PROPKEY, miscHiddenProps);
            miscHiddenProps.put(MISC_FILE_DIALOG_DIR_PROPKEY,
                    new Property(MISC_FILE_DIALOG_DIR_PROPKEY, 
                    System.getProperty("user.home"), true));
        }
    }

    public void saveConfiguration() throws FileNotFoundException, IOException {
        Element root = new Element("Properties");
        makeDomTree(root, properties);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(preferencesFile);
            outputter.output(root, fos);
        } finally {
            if(fos != null)
                fos.close();
        }
    }

    private void makeDomTree(Element element, LinkedHashMap<String, Object> props) {
        Iterator<Map.Entry<String, Object>> ite = props.entrySet().iterator();
        while(ite.hasNext()) {
            Map.Entry<String, Object> entry = ite.next();
            String name = entry.getKey();
            Object value = entry.getValue();
            Element child = new Element("property");
            child.setAttribute("name", name);
            if(value instanceof LinkedHashMap) {
                makeDomTree(child, (LinkedHashMap<String, Object>)value);
                element.addContent(child);
            }
            else {
                Property prop = (Property)value;
                child.setAttribute("value", prop.value.toString());
                child.setAttribute("type", prop.value.getClass().getName());
                element.addContent(child);
            }
        }
    }

    private void loadConfiguration() throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        try {
            //We have an XML config file
            //<property name="" value="" type=""/>
            //property tags with no value acts as section.
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(preferencesFile);
            fillProperties(document.getRootElement(), properties);
        } catch (JDOMException ex) {
            Logger.getLogger(AppConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AppConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fillProperties(Element element, LinkedHashMap<String, Object> props)
            throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<Element> children = element.getChildren();
        for(int i=0; i<children.size(); i++) {
            Element child = children.get(i);
            String name = child.getAttributeValue("name");
            if(child.getChildren().size() > 0) {
                LinkedHashMap<String, Object> newMap = new LinkedHashMap<String, Object>();
                fillProperties(child, newMap);
                props.put(name, newMap);
            } else {
                //Assuming we only have primitive types
                //short, int, long, float, double, String
                String type = child.getAttributeValue("type");
                String valueStr = child.getAttributeValue("value");
                Boolean hidden =
                        Boolean.parseBoolean(child.getAttributeValue("type"));
                Class clz = Class.forName(type);
                if(type.equals(Short.class.toString())) {
                    Method m = clz.getMethod("parseShort");
                    Short value = (Short) m.invoke(null, valueStr);
                    props.put(name, new Property(name, value, hidden));
                } else if(type.equals(Integer.class.toString())) {
                    Method m = clz.getMethod("parseInt");
                    Integer value = (Integer) m.invoke(null, valueStr);
                    props.put(name, new Property(name, value, hidden));
                } else if(type.equals(Long.class.toString())) {
                    Method m = clz.getMethod("parseLong");
                    Long value = (Long) m.invoke(null, valueStr);
                    props.put(name, new Property(name, value, hidden));
                } else if(type.equals(Float.class.toString())) {
                    Method m = clz.getMethod("parseFloat");
                    Float value = (Float) m.invoke(null, valueStr);
                    props.put(name, new Property(name, value, hidden));
                } else if(type.equals(Double.class.toString())) {
                    Method m = clz.getMethod("parseDouble");
                    Double value = (Double) m.invoke(null, valueStr);
                    props.put(name, new Property(name, value, hidden));
                } else if(type.equals(Boolean.class.toString())) {
                    Method m = clz.getMethod("parseBoolean");
                    String value = (String) m.invoke(null, valueStr);
                    props.put(name, new Property(name, value, hidden));
                } else { //if(type.equals(String.class.toString())) {
                    props.put(name, new Property(name, valueStr, hidden));
                }
            }
        }
    }

    public void showConfiguration(Component parent) {
        JDialog dialog = DialogCreator.createDialog(parent);
        dialog.add(configGui);
        dialog.setVisible(true);
    }

    private LinkedHashMap<String, Object> getMiscHiddenPropertyHolder() {
        LinkedHashMap<String, Object> miscHiddenProps =
                (LinkedHashMap<String, Object>) properties.get(MISC_HIDDEN_PROPKEY);
        if(miscHiddenProps == null) {
            miscHiddenProps = new LinkedHashMap<String, Object>();
            properties.put(MISC_HIDDEN_PROPKEY, miscHiddenProps);
        }
        return miscHiddenProps;
    }

    public void setFileDialogDirProperty(String value) {
        LinkedHashMap<String, Object> miscHiddenProps = getMiscHiddenPropertyHolder();
        miscHiddenProps.put(
                MISC_FILE_DIALOG_DIR_PROPKEY,
                new Property(MISC_FILE_DIALOG_DIR_PROPKEY, value, true));
    }
    
    public String getFileDialogDirProperty() {
        LinkedHashMap<String, Object> miscHiddenProps = getMiscHiddenPropertyHolder();
        return (String)((Property)(miscHiddenProps.get(MISC_FILE_DIALOG_DIR_PROPKEY))).value;
    }

    private static class Property {
        public String name;
        public Object value;
        public Boolean hidden;

        public Property(String name, Object value, Boolean hidden) {
            this.name = name;
            this.value = value;
            this.hidden = hidden;
        }
    }

    public static class LoadException extends Exception {

        private LoadException(Throwable ex) {
            super(ex);
        }

    }

    /** for GUI editing of config */
    public static class ConfigurationGui extends JPanel {

        public ConfigurationGui(LinkedHashMap<String, Object> properties) {
            setLayout(new BorderLayout());
            //TODO:
        }
    }

}
