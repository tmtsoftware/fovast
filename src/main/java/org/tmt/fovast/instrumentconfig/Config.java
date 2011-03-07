/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.instrumentconfig;

import java.util.ArrayList;

/**
 * Just a holder of main config objects ..
 */
public class Config {

    private ArrayList<Element> children = new ArrayList<Element>();

    public void add(Element element) {
        children.add(element);
    }

    public ArrayList<Element> getChildren() {
        return children;
    }

    public static interface ConfigListener {

        public void updateConfig(String confElementId, Value value);

        public void batchUpdateConfig(ArrayList<String> confElementIds,
                ArrayList<Value> values);
    }
}
