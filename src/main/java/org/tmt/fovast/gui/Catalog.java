package org.tmt.fovast.gui;

import java.util.HashMap;
import uk.ac.starlink.table.StarTable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Disha_Gujrathi
 */
public class Catalog {

    private Object[][] data;

    private String label;

    private String[] ColNames;

    private HashMap<String , Object> properties = new HashMap<String, Object>();

    private StarTable starTable;

    /**
     * @return the data
     */
    public Object[][] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Object[][] data) {
        this.data = data;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the ColNames
     */
    public String[] getColNames() {
        return ColNames;
    }

    /**
     * @param ColNames the ColNames to set
     */
    public void setColNames(String[] ColNames) {
        this.ColNames = ColNames;
    }

    /**
     * @return the properties
     */
    public HashMap<String, Object> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(HashMap<String, Object> properties) {
        this.properties=properties;
    }

    /**
     * @return the starTable
     */
    public StarTable getStarTable() {
        return starTable;
    }

    /**
     * @param starTable the starTable to set
     */
    public void setStarTable(StarTable starTable) {
        this.starTable = starTable;
    }
}
