/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tmt.fovast.gui;

import java.awt.Color;
import java.awt.Shape;



/**
 *
 * @author Disha_Gujrathi
 */
public class SymbolTable {
//    private String symbol;
//
//    private String colour;
//
//    private Color symbolColor;

    private static Color[] colours = {Color.YELLOW,
                               Color.BLUE,
                               Color.ORANGE,
                               Color.GREEN,
                               Color.PINK,                                                              
                               Color.WHITE,
                               Color.RED,
                               Color.MAGENTA,
                               Color.BLACK,
                               Color.CYAN};

    private static String[] symbols = {"square",
                                "circle",
                                "plus",
                                "cross",
                                "triangle",
                                "diamond",
                                "ellipse",
                                "compass",
                                "line",
                                "arrow"};

    private double ratio;

    private double angle;

    private String label;

    private int size;

    private String units;

    /**
     * @return the symbol
     */
//    public String getSymbol() {
//        return symbol;
//    }

    /**
     * @param symbol the symbol to set
     */
//    public void setSymbol(String symbol) {
//        this.symbol = symbol;
//    }

    /**
     * @return the colour
     */
//    public Color getColour() {
//        return symbolColor;
//    }

    /**
     * @param colour the colour to set
     */
//    public void setColour(String colour) {
//        this.colour = colour;
//        symbolColor=Color.red;
//    }

    /**
     * @return the ratio
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * @param ratio the ratio to set
     */
    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    /**
     * @return the angle
     */
    public double getAngle() {
        return angle;
    }

    /**
     * @param angle the angle to set
     */
    public void setAngle(double angle) {
        this.angle = angle;
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
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the units
     */
    public String getUnits() {
        return units;
    }

    /**
     * @param units the units to set
     */
    public void setUnits(String units) {
        this.units = units;
    }

    
    public static Color getColours(int n) {
        return colours[n];
    }

    public static String getSymbols(int n){
        return symbols[n];
    }

    public static int getColorSetSize() {
        return colours.length;
    }

    public static int getSymbolSetSize() {
        return symbols.length;
    }

}
