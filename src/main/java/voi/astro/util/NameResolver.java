/*
 * Copyright (c) 2010 Virtual Observatory - India.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

/**
 * This file has been modified to a good amount for FOVAST to use STILTS library
 * instead of Savot for VOTable parsing. Also some cleaning up has been done in
 * terms of return values and exceptions
 * 
 * (Modifications) Copyright 2011 TMT.
 */


package voi.astro.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import org.xml.sax.SAXException;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.votable.TableElement;
import uk.ac.starlink.votable.VOElement;
import uk.ac.starlink.votable.VOElementFactory;
import uk.ac.starlink.votable.VOStarTable;

/**
 * NameResolver class has static methods to resolve object names to RA, DEC
 * getCoordsFromSimbad() uses Simbad name -> RA, DEC resolution service
 * while getCoordsFromNED() uses the NED service
 *
 */
public class NameResolver {

    private static Logger logger = LoggerFactory.getLogger(NameResolver.class);

    // General constants
    private static final String URL_ENCODING_FORMAT = "utf-8";

    // NED related constants
    public static final String NED_QUERY_STATUS = "QUERY_STATUS";

    public static final String NED_QUERY_STATUS_VALUE_OK = "OK";

    public static final String NED_ERROR_PARAM_NAME = "Error";

    private static final String NED_URL_PREFIX = "http://nedwww.ipac.caltech.edu/"
            + "cgi-bin/nph-objsearch?objname=";

    private static final String NED_URL_SUFFIX = "&extend=no&out_csys=Equatorial&"
            + "out_equinox=J2000.0&obj_sort=RA+or+Longitude&of=xml_main&" + "zv_breaker=30000.0&list_limit=5&img_stamp=NO";

    private static final String NED_VOTABLE_RESOURCE_TYPE = "results";

    private static final String NED_RA_NAME = "RA(deg)";

    private static final String NED_DEC_NAME = "DEC(deg)";

    // Sesame(simbad) related constants
    private static final String SESAME_URL_PREFIX = "http://cdsws.u-strasbg.fr/"
            + "axis/services/Sesame?method=sesame&resultType=x&name=";

    /**
     * This method resolves the object name using Simbad Service.
     * 
     * @param objectName like m31, m1 etc
     * @return double array of size 2 containing ra and dec
     * @throws CouldNotResolveException - if resolution fails
     * @throws OtherException - for other exceptions - used to wrap SAXExceptions,
     *                          IOException etc
     *         
     */
    public static double[] getCoordsFromSimbad(String objectName)
            throws CouldNotResolveException, OtherException {
        try {
            String sesameURLString = SESAME_URL_PREFIX + URLEncoder.encode(objectName,
                    URL_ENCODING_FORMAT);
            logger.info("Resolving the " + objectName + " from SIMBAD " + sesameURLString);
            URLConnection urlConnection = new URL(sesameURLString).openConnection();
            urlConnection.connect();
            InputStream urlStream = urlConnection.getInputStream();
            DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
            dBF.setValidating(false);
            DocumentBuilder docBuilder = dBF.newDocumentBuilder();
            Document doc = docBuilder.parse(new InputSource(urlStream));
            NodeList nodeList = doc.getElementsByTagName("return");
            if (nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                NodeList childNodes = node.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    if (childNodes.item(i).getNodeType() == Node.TEXT_NODE) {
                        // the return xml as string
                        String result = childNodes.item(i).getNodeValue();

                        double ra = Double.NaN;
                        double dec = Double.NaN;

                        if (result.indexOf("<jpos>") > 0) {
                            ra = Double.parseDouble(result.substring(result.indexOf("<jradeg>") + 8, result.indexOf("</jradeg>")));
                            dec = Double.parseDouble(result.substring(result.indexOf("<jdedeg>") + 8, result.indexOf("</jdedeg>")));
                            return new double[]{ra, dec};
                        } else {
                           throw new CouldNotResolveException("Most probably simbad " +
                                   "could not resolve object");
                        }
                    }
                }
            }
        } catch (SAXException ex) {
            throw new OtherException(ex);
        } catch (ParserConfigurationException ex) {
            throw new OtherException(ex);
        } catch (IOException ex) {
            throw new OtherException(ex);
        }

        throw new CouldNotResolveException("Most probably parse error");
    }

    /**
     * method to resolve object name from NED Service. 
     *
     * @param objectName like m31, m1 etc
     * @return double array of size 2 containing ra and dec
     * @throws CouldNotResolveException - for other exceptions
     * @throws OtherException - for other exceptions - used to wrap SAXExceptions,
     *                          IOException etc
     *
     */
    public static double[] getCoordsFromNED(String objectName)
            throws CouldNotResolveException, OtherException {

        java.io.BufferedReader is = null;
        try {
            String nedURLString = NED_URL_PREFIX + URLEncoder.encode(objectName,
                    URL_ENCODING_FORMAT) + NED_URL_SUFFIX;
            logger.info("Resolving the " + objectName + " from NED " + nedURLString);

            double[] ra_dec_vals = new double[2];

            //Reading XML into string as the STILTS/xerces-SAX library included throws error
            //on encountering empty spaces
            //before initial <?xml ..
            //TODO: Need to check if it has been solved in recent versions.
            URL nedURL = new URL(nedURLString);
            is = new java.io.BufferedReader(new 
                    java.io.InputStreamReader(nedURL.openConnection().getInputStream()));
            String line;
            StringBuffer buff = new StringBuffer();
            while((line = is.readLine()) != null) {
                buff.append("\n").append(line);
            }
            String xmlString = buff.toString().trim();
            
            VOElement votable = new VOElementFactory().makeVOElement(
                    new javax.xml.transform.stream.StreamSource(
                        new java.io.StringReader(xmlString)));

            NodeList resources = votable.getElementsByTagName("RESOURCE");

            int resInd = 0;
            for (; resInd < resources.getLength(); resInd++) {
                VOElement resource = (VOElement) resources.item(resInd);
                if (NED_VOTABLE_RESOURCE_TYPE.equals(resource.getAttribute("type"))) {
                    break;
                }
            }

            if (resInd == resources.getLength()) {
                throw new CouldNotResolveException("No resources found in the returned VOTable");
            } else {
                String queryStatus = null;
                String queryStatusDescription = null;

                VOElement resource = (VOElement) resources.item(resInd);

                NodeList infos = resource.getElementsByTagName("INFO");
                for (int infoInd = 0; infoInd < infos.getLength(); infoInd++) {
                    VOElement info = (VOElement) infos.item(infoInd);
                    String name = info.getAttribute("name");
                    if (name.equals(NED_QUERY_STATUS)) {
                        queryStatus = info.getAttribute("value");
                        queryStatusDescription = info.getNodeValue();
                    }
                }

                //if query successful.equalsIgnoreCase()
                if (!queryStatus.equals(NED_QUERY_STATUS_VALUE_OK)) {
                    throw new CouldNotResolveException("Query return status was found to be not OK");
                }

                NodeList params = resource.getElementsByTagName("PARAM");
                for (int paramInd = 0; paramInd < params.getLength(); paramInd++) {
                    VOElement param = (VOElement) params.item(paramInd);
                    String name = param.getAttribute("name");
                    if (name.equals(NED_ERROR_PARAM_NAME)) {
                        String paramValue = param.getAttribute("value");
                        if(paramValue == null) {
                            VOElement value = (VOElement) param.getElementsByTagName("VALUE");
                            Node valueTextNode = value.getChildNodes().item(0);
                            paramValue = valueTextNode.getNodeValue();
                        }

                        throw new CouldNotResolveException(paramValue);
                    }
                }
                
                String decField = "";
                String raField = "";
                int raIndex = 0, decIndex = 0;
                //handle the table
                VOElement[] tables = resource.getChildrenByName("TABLE");
                if (tables.length > 0) {
                    //take the first table and look for URLs in it.
                    TableElement tableElement = (TableElement) tables[0];

                    //create star-table
                    StarTable table = new VOStarTable(tableElement);
                    for (int colInd = 0; colInd < table.getColumnCount(); colInd++) {
                        ColumnInfo colInfo = table.getColumnInfo(colInd);
                        String name = colInfo.getName();
                        if (name.equals(NED_RA_NAME)) {
                            raIndex = colInd;

                        } else if (name.equals(NED_DEC_NAME)) {
                            decIndex = colInd;
                        }

                    }

                    Object[] row = table.getRow(0);
                    raField = row[raIndex].toString();
                    decField = row[decIndex].toString();
                    if ((raField == null) || (decField == null)) {
                        // _logger.info("The VOTable is missing one of the MUST
                        // present UCDs");
                        throw new CouldNotResolveException(
                                "The VOTable is missing one of the MUST present UCDs");

                    }
                    ra_dec_vals[0] = Double.parseDouble(raField);
                    ra_dec_vals[1] = Double.parseDouble(decField);

                    return ra_dec_vals;

                }
                else {
                    throw new CouldNotResolveException("No coordinates found in returned message");
                }                
            }
        } catch (SAXException ex) {
            throw new OtherException(ex);
        } catch (IOException ex) {
            throw new OtherException(ex);
        }
    }

    
    public final static void main(String args[]) throws Exception {

        String objectToResolve = "m51";

        //Set system proxy
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "5865");


        //Resolve using NED
        double[] raDec = NameResolver.getCoordsFromNED(objectToResolve);
        System.out.println("RA, DEC obtained from NED: " + raDec[0] + ", " + raDec[1]);

        //Resolve using Simbad
        raDec = NameResolver.getCoordsFromSimbad(objectToResolve);
        System.out.println("RA, DEC obtained from Simbad: " + raDec[0] + ", " + raDec[1]);
    }

    /**
     * Check the cause for actual reason
     */
    public static class OtherException extends Exception {

        public OtherException(Exception ex) {
            super(ex);
        }
    }
}
