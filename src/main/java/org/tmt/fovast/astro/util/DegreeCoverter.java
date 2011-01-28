/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */
package org.tmt.fovast.astro.util;

import java.util.StringTokenizer;

/**
 * Util class to convert RA, DEC to degrees
 */
public class DegreeCoverter {

    /**      
     * @param ravalue (in sexagesimal formats 12:30 or 12h 13m 56.34s etc)
     * @return ra in degrees
     * @throws NumberFormatException
     * @throws IllegalFormatException
     */
    public static double parseAndConvertRa(String value)
    throws IllegalFormatException, NumberFormatException {

        String raValue = value.trim();
        double h = 0, m = 0, s = 0;
        double deg;
        String temp = "";
        if (raValue.contains("h") || raValue.contains("m") || raValue.contains("s")) {
            int index[] = new int[3];
            if (raValue.contains("h")) {
                index[0] = raValue.indexOf('h');
                temp = raValue.substring(0, index[0]).trim();
                h = Double.parseDouble(temp);
            }
            if (raValue.contains("m")) {
                index[1] = raValue.indexOf('m');
                if (raValue.contains("h")) {
                    temp = raValue.substring(index[0] + 1, index[1]).trim();
                } else {
                    temp = raValue.substring(0, index[1]).trim();
                }
                m = Double.parseDouble(temp);
            }
            if (raValue.contains("s")) {
                index[2] = raValue.indexOf('s');
                if (raValue.contains("h") && raValue.contains("m")) {
                    temp = raValue.substring(index[1] + 1, index[2]).trim();
                } else if (!raValue.contains("h") && !raValue.contains("m")) {
                    temp = raValue.substring(0, index[2]).trim();
                } else if (raValue.contains("h") && !raValue.contains("m")) {
                    temp = raValue.substring(index[0] + 1, index[2]).trim();
                } else if (!raValue.contains("h") && raValue.contains("m")) {
                    temp = raValue.substring(index[1] + 1, index[2]).trim();
                }
                s = Double.parseDouble(temp);
            }
        }
        else {
            String sep = ":";
            if(!raValue.contains(":")) {
                sep = " ";
            }
            
            StringTokenizer tokenizer = new StringTokenizer(raValue, sep);
            int ct = 0;
            while(tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if(ct == 0) {
                    h = Double.parseDouble(token.trim());
                } else if(ct == 1) {
                    m = Double.parseDouble(token.trim());
                } else if(ct == 2) {
                    s = Double.parseDouble(token.trim());
                } else {
                    throw new IllegalFormatException();
                }
            }
        }


//        else if (raValue.contains(":")) {
//            int index = raValue.indexOf(":");
//            temp = raValue.substring(0, index).trim();
//            h = Double.parseDouble(temp);
//            raValue = raValue.substring(index + 1);
//
//            index = raValue.indexOf(":");
//            temp = raValue.substring(0, index).trim();
//            m = Double.parseDouble(temp);
//            raValue = raValue.substring(index + 1);
//
//            temp = raValue.trim();
//            s = Double.parseDouble(temp);
//        } else if (!raValue.contains("h") && !raValue.contains("m") && !raValue.contains("s") && !raValue.contains(":") && !raValue.contains(" ")) {
//            double degTemp = Double.parseDouble(raValue);
//            return degTemp;
//        } else {
//            raValue = raValue.trim();
//            int index = raValue.indexOf(" ");
//            temp = raValue.substring(0, index).trim();
//            h = Double.parseDouble(temp);
//            raValue = raValue.substring(index + 1);
//
//            index = raValue.indexOf(" ");
//            temp = raValue.substring(0, index).trim();
//            m = Double.parseDouble(temp);
//            raValue = raValue.substring(index + 1);
//
//            temp = raValue.trim();
//            s = Double.parseDouble(temp);
//        }
        
        double m1 = m / 60;
        double s1 = s / 3600;
        double h1 = h + m1 + s1;
        deg = h1 * 15;
        return deg;
    }

    /**
     * @param dec value (in sexagesimal formats 12:30 or 12d 13m 56.34s etc)
     * @return dec in degrees
     * @throws NumberFormatException
     * @throws IllegalFormatException
     *
     */
    public static double parseAndConvertDec(String value) 
            throws IllegalFormatException, NumberFormatException {

        String decValue = value;
        double d = 0, m = 0, s = 0;
        double deg;
        String temp = "";
        if (decValue.contains("d") || decValue.contains("m") || decValue.contains("s")) {
            int index[] = new int[3];
            if (decValue.contains("d")) {
                index[0] = decValue.indexOf('d');
                temp = decValue.substring(0, index[0]).trim();
                d = Double.parseDouble(temp);
            }
            if (decValue.contains("m")) {
                index[1] = decValue.indexOf('m');
                if (decValue.contains("d")) {
                    temp = decValue.substring(index[0] + 1, index[1]).trim();
                } else {
                    temp = decValue.substring(0, index[1]).trim();
                }

                m = Double.parseDouble(temp);
            }
            if (decValue.contains("s")) {
                index[2] = decValue.indexOf('s');
                if (decValue.contains("h") && decValue.contains("m")) {
                    temp = decValue.substring(index[1] + 1, index[2]).trim();
                } else if (!decValue.contains("h") && !decValue.contains("m")) {
                    temp = decValue.substring(0, index[2]).trim();
                } else if (decValue.contains("h") && !decValue.contains("m")) {
                    temp = decValue.substring(index[0] + 1, index[2]).trim();
                } else if (!decValue.contains("h") && decValue.contains("m")) {
                    temp = decValue.substring(index[1] + 1, index[2]).trim();
                }
                s = Double.parseDouble(temp);
            }
        }
        else {
            String sep = ":";
            if(!decValue.contains(":")) {
                sep = " ";
            }

            StringTokenizer tokenizer = new StringTokenizer(decValue, sep);
            int ct = 0;
            while(tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if(ct == 0) {
                    d = Double.parseDouble(token.trim());
                } else if(ct == 1) {
                    m = Double.parseDouble(token.trim());
                } else if(ct == 2) {
                    s = Double.parseDouble(token.trim());
                } else {
                    throw new IllegalFormatException();
                }
            }
        }


//        else if (decValue.contains(":")) {
//            int index = decValue.indexOf(":");
//            temp = decValue.substring(0, index).trim();
//            d = Double.parseDouble(temp);
//            decValue = decValue.substring(index + 1);
//
//            index = decValue.indexOf(":");
//            temp = decValue.substring(0, index).trim();
//            m = Double.parseDouble(temp);
//            decValue = decValue.substring(index + 1);
//
//            temp = decValue.trim();
//            s = Double.parseDouble(temp);
//        } else if (!decValue.contains("d") && !decValue.contains("m") && !decValue.contains("s") && !decValue.contains(":") && !decValue.contains(" ")) {
//            double degTemp = Double.parseDouble(decValue);
//            return degTemp;
//        } else {
//            decValue = decValue.trim();
//            int index = decValue.indexOf(" ");
//            temp = decValue.substring(0, index).trim();
//            d = Double.parseDouble(temp);
//            decValue = decValue.substring(index + 1);
//
//            index = decValue.indexOf(" ");
//            temp = decValue.substring(0, index).trim();
//            m = Double.parseDouble(temp);
//            decValue = decValue.substring(index + 1);
//
//            temp = decValue.trim();
//            s = Double.parseDouble(temp);
//
//        }
        
        double m1 = m / 60;
        double s1 = s / 3600;
        double h1 = m1 + s1;
        deg = h1 + d;
        return deg;
    }


    public  static class IllegalFormatException extends Exception {
    }
}
