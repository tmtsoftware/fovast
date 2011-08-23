/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tmt.fovast.gui;

/**
 *
 * @author Disha_Gujrathi
 */
public class PointInfoForXML {
    private double ra , dec ,mag;
    private String catalogLabel , elementId ,pointId;
    private int focus;
   
    /**
     * @return the ra
     */
    public double getRa() {
        return ra;
    }

    /**
     * @param ra the ra to set
     */
    public void setRa(double ra) {
        this.ra = ra;
    }

    /**
     * @return the dec
     */
    public double getDec() {
        return dec;
    }

    /**
     * @param dec the dec to set
     */
    public void setDec(double dec) {
        this.dec = dec;
    }

    /**
     * @return the catalogLabel
     */
    public String getCatalogLabel() {
        return catalogLabel;
    }

    /**
     * @param catalogLabel the catalogLabel to set
     */
    public void setCatalogLabel(String catalogLabel) {
        this.catalogLabel = catalogLabel;
    }

    /**
     * @return the elementId
     */
    public String getElementId() {
        return elementId;
    }

    /**
     * @param elementId the elementId to set
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * @return the mag
     */
    public double getJmag() {
        return mag;
    }

    /**
     * @param mag the mag to set
     */
    public void setJmag(double jmag) {
        this.mag = jmag;
    }

    /**
     * @return the pointId
     */
    public String getPointId() {
        return pointId;
    }

    /**
     * @param pointId the pointId to set
     */
    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    /**
     * @return the focus
     */
    public int getFocus() {
        return focus;
    }

    /**
     * @param focus the focus to set
     */
    public void setFocus(int focus) {
        this.focus = focus;
    }

    

}
