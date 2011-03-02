/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */
package org.tmt.fovast.vo.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.votable.TableElement;
import uk.ac.starlink.votable.VOElement;
import uk.ac.starlink.votable.VOElementFactory;
import uk.ac.starlink.votable.VOStarTable;

/**
 * Client class to fetch images from SIA service
 *
 * 
 */
public class SiaClient {
    // As of now we use a DOMParser .. Should we change to SAX parser.
    //
    // TODO: Make constants of hardcoded strings 

    private static Logger logger = LoggerFactory.getLogger(SiaClient.class);

    public static final String SIAP_RESOURCE_TYPE_VALUE_RESULTS = "results";

    public static final String QUERY_STATUS = "QUERY_STATUS";

    public static final String QUERY_STATUS_VALUE_OK = "OK";

    public static final String RA_UCD = "POS_EQ_RA_MAIN";

    public static final String DEC_UCD = "POS_EQ_DEC_MAIN";

    public static final String TITLE_UCD = "VOX:Image_Title";

    public static final String NAXES_UCD = "VOX:Image_Naxes";

    public static final String NAXIS_UCD = "VOX:Image_Naxis";

    public static final String FORMAT_UCD = "VOX:Image_Format";

    public static final String SCALE_UCD = "VOX:Image_Scale";

    public static final String ACCESS_REF_UCD = "VOX:Image_AccessReference";

    public static final String BANDPASS_ID_UCD = "VOX:BandPass_ID";

    private String url;

    /**     
     * @param url - end point of the SIA service
     */
    public SiaClient(String url) {
        this.url = url;
    }

    /**
     *
     * @param ra - in degrees
     * @param dec - in degrees
     * @param angularWidthAlongRAAxis - width along RA axis
     * @param angularWidthAlongDecAxis - width along DEC axis
     * @param otherConstraints  - map of UCDKey -> Value. Only images whose
     *          column UCDs match the values are selected
     * @return array of fits image urls
     * @throws Exception -
     */
    public String[] fetchFitsImages(double ra, double dec,
            double angularWidthAlongRAAxis, double angularWidthAlongDecAxis,
            Map<String, Object> otherConstraints) throws MalformedURLException,
            SAXException, IOException, NoValidResultsResourceFoundException,
            SiaQueryFailedException {
        //TODO: Need to take a call on the otherConstraints argument

        String modurl = url + "&FORMAT=image/fits";
        modurl += "&POS=" + ra + "," + dec;
        modurl += "&SIZE=" + angularWidthAlongRAAxis + "," + angularWidthAlongDecAxis;

        URL siaUrl = new URL(modurl);
        logger.info("Connecting to SIA URL: " + siaUrl);


        String interestedImageFormat = "image/fits";
        ArrayList<String> imageUrls = new ArrayList<String>();

        VOElement votable = new VOElementFactory().makeVOElement(siaUrl);
        NodeList resources = votable.getElementsByTagName("RESOURCE");

        //check for a "results" resource with of sia votable resu
        int resInd = 0;
        for (; resInd < resources.getLength(); resInd++) {
            VOElement resource = (VOElement) resources.item(resInd);
            if (SIAP_RESOURCE_TYPE_VALUE_RESULTS.equals(resource.getAttribute("type"))) {
                break;
            }
        }

        if (resInd == resources.getLength()) {
            throw new NoValidResultsResourceFoundException();
        } else {
            String queryStatus = null;
            String queryStatusDescription = null;

            //check query status
            VOElement resource = (VOElement) resources.item(resInd);
            NodeList infos = resource.getElementsByTagName("INFO");
            for (int infoInd = 0; infoInd < infos.getLength(); infoInd++) {
                VOElement info = (VOElement) infos.item(infoInd);
                String name = info.getAttribute("name");
                if (name.equals(QUERY_STATUS)) {
                    queryStatus = info.getAttribute("value");
                    queryStatusDescription = info.getNodeValue();
                }
            }

            //if query successful.equalsIgnoreCase()
            if (!queryStatus.equals(QUERY_STATUS_VALUE_OK)) {
                throw new SiaQueryFailedException(queryStatusDescription);
            }

            //handle the table
            VOElement[] tables = resource.getChildrenByName("TABLE");
            if (tables.length > 0) {
                //take the first table and look for URLs in it.
                TableElement tableElement = (TableElement) tables[0];

                //create star-table
                StarTable table = new VOStarTable(tableElement);

                //ideally its mandatory to have RA_UCD, DEC_UCD, NAXES_UCD, 
                //NAXIS_UCD, FORMAT_UCD, SCALE_UCD in each row but we will not
                //check for those as of now.

                int formatColIndex = -1;
                int urlColIndex = -1;
                int otherConstraintsSize = (otherConstraints != null)
                        ? otherConstraints.size() : 0;
                HashMap<String, Integer> otherConstraintsIndices =
                        new HashMap<String, Integer>();

                for (int colInd = 0; colInd < table.getColumnCount(); colInd++) {
                    ColumnInfo colInfo = table.getColumnInfo(colInd);
                    if (colInfo.getUCD().equalsIgnoreCase(FORMAT_UCD)) {
                        formatColIndex = colInd;
                    } else if (colInfo.getUCD().equalsIgnoreCase(ACCESS_REF_UCD)) {
                        urlColIndex = colInd;
                    }
                    if (otherConstraintsSize > 0) {
                        Iterator<String> keyIte = otherConstraints.keySet().iterator();
                        while (keyIte.hasNext()) {
                            String key = keyIte.next();
                            if (key.equalsIgnoreCase(colInfo.getUCD())) {
                                otherConstraintsIndices.put(key, colInd);
                            }
                        }
                    }
                }

                for (int rowInd = 0; rowInd < table.getRowCount(); rowInd++) {
                    Object[] cells = table.getRow(rowInd);
                    //looking for only FITS images or JPG images .. ..
                    if (interestedImageFormat.equalsIgnoreCase(
                            (String) cells[formatColIndex])) {
                        Iterator<String> keyIte = otherConstraintsIndices.keySet().iterator();
                        boolean allConstraintsPassed = true;
                        while (keyIte.hasNext()) {
                            String key = keyIte.next();
                            int keyInd = otherConstraintsIndices.get(key);
                            if (!cells[keyInd].equals(otherConstraints.get(key))) {
                                allConstraintsPassed = false;
                                break;
                            }
                        }

                        if (allConstraintsPassed) {
                            imageUrls.add((String) cells[urlColIndex]);
                        }
                    }
                }
            }

            return imageUrls.toArray(new String[]{});
        }
    }

    /** If the result VOTable from SIA service has an empty table */
    public static class SiaQueryFailedException extends Exception {

        private SiaQueryFailedException(String queryStatusDescription) {
            super(queryStatusDescription);
        }

    }

    /** Resource with type 'results' not found in the VOTable */
    public static class NoValidResultsResourceFoundException extends Exception {
    }

    /** Mandatory meta data could not be found in the returned VOTable fields */
    public static class BadMetaDataException extends Exception {

        private BadMetaDataException(String msg) {
            super(msg);
        }

    }
}
