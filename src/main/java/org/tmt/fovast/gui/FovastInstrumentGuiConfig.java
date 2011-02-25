/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author vivekananda_moosani
 */
public class FovastInstrumentGuiConfig {

    public static FovastInstrumentGuiConfig getFovastInstrumentConfig(URL configURL)
            throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(configURL);

        FovastInstrumentGuiConfig guiConfig = new FovastInstrumentGuiConfig();

        return guiConfig;
    }


    public abstract static class Element {
        
        private String id;

        private String label;

        private Value value;

        private String selectByDefault;

        private EnableConditions enableConditions;

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

    public static class Instrument extends Element {

        private boolean selected;

        public Instrument(String id) {
            super(id);
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    public static class ConfigOption extends Element {

        private String label;

        private String type;

        private Values possibleValues;

        private Value value;

        private EnableConditions enableConditions;
        
        public ConfigOption(String id) {
            super(id);
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Values getPossibleValues() {
            return possibleValues;
        }

        public void getPossibleValues(Values values) {
            this.possibleValues = values;
        }

        public Value getValue() {
            return value;
        }

        public void setValue(Value value) {
            this.value = value;
        }
    }

    public static class EnableConditions {

        private Condition condition;

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }
    }

    public abstract static class Condition {
        public abstract boolean isTrue();
    }

    public static class AlwaysTrueCondition extends Condition {
        public boolean isTrue() {
            return true;
        }
    }

    public static class EqualityCondition extends Condition {
        
        private Element element;

        private String value;

        public EqualityCondition(Element element, String value) {
            this.element = element;
            this.value = value;
        }

        @Override
        public boolean isTrue() {
            if(element instanceof Instrument) {
                return ((Instrument)element).isSelected();
            }
            else if(element instanceof ConfigOption) {
                ConfigOption co = (ConfigOption)element;
                return value.equals(co.getValue());
            }
            else {
                new RuntimeException("Unknow element type encountered");
            }

            return false;
        }
    }

    public static class OrCondition {
        
        private Condition leftCondition;

        private Condition rightCondition;

        public OrCondition(Condition leftCondition, Condition rightCondition) {
            this.leftCondition = leftCondition;
            this.rightCondition = rightCondition;
        }

        public boolean isTrue() {
            if(leftCondition.isTrue())
                return true;
            else if(rightCondition.isTrue())
                return true;
            else
                return false;
        }
    }
    
    public static class Values {

        public static int SELECT_MODE_MUTUALLY_EXCLUSIVE = 1;

        private ArrayList<Value> values = new ArrayList<Value>();

        private Value defaultValue;

        private int selectMode;

        public int size() {
            return values.size();
        }

    }

    public static abstract class Value {
        
    }

    public static class StringValue {
        
        private String value;
        
        public StringValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof StringValue) {
                if(value == null)
                    return false;
                else
                    return value.equals(((StringValue)obj).value);
            }
            else
                return false;
        }

        @Override
        public int hashCode() {
            if(value != null)
                return value.hashCode();
            else
                return super.hashCode();
        }

        public String getValue() {
            return value;
        }

    }

    public class BooleanValue extends Value {

        private boolean value;

        public BooleanValue(boolean value) {
            this.value = value;
        }

        public boolean getValue() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null)
                return  false;
            else if(obj instanceof BooleanValue) {
                return (value == ((BooleanValue)obj).value);
            }
            return false;
        }

        public int hashCode1() {
            if(value)
                return 1;
            else
                return 2;
        }

    }

    public class PositionValue extends Value {

        private double x;

        private double y;

        public PositionValue(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null)
                return false;
            if(obj instanceof PositionValue) {
                PositionValue pv = (PositionValue)obj;
                if(pv.x == x && pv.y == y)
                    return true;
            }

            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 29 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
            hash = 29 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
            return hash;
        }

    }

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

    /** 
     * This class is for grouping elements under a node.
     */
    public static class ConfigGroupOption extends ConfigOption {

        public ConfigGroupOption(String id) {
            super(id);
        }
        //nothing to be done
        //This is purely required to show an addition wrapper config option
        //in GUI

    }
}
