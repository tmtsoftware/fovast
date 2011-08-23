/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tmt.fovast.astro.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jdesktop.application.ApplicationContext;
import org.tmt.fovast.gui.FovastApplication;
import org.tmt.fovast.gui.PointInfoForXML;
import org.tmt.fovast.util.AppConfiguration;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Disha_Gujrathi
 */
public class XMLFileGenerator {

    public static final String DOWNLOAD_CACHE_INDEX_FILE = "downloadCache.ind";

    public static final String DOWNLOAD_CACHE_DIR = "guideStarInfo.xml";

    public static final String[] XML_EXTENSIONS = {".xml"};

    public static final String XML_EXTENSIONS_DESC = "*.xml";

	public void generateXML(ArrayList<PointInfoForXML> infoList) {

	  try {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        String str = "";

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("config");
		doc.appendChild(rootElement);
        for(int i = 0; i<infoList.size() ; i++){

        str = infoList.get(i).getPointId();
		// element elements
		Element element = doc.createElement("Element");
		rootElement.appendChild(element); 

		// set attribute to element element
		Attr attr = doc.createAttribute("id");
		attr.setValue(infoList.get(i).getElementId());
		element.setAttributeNode(attr);
		
		Element catalog = doc.createElement("catalog");
		catalog.appendChild(doc.createTextNode(""+infoList.get(i).getCatalogLabel()));
		element.appendChild(catalog);

//        attr = doc.createAttribute("id");
//		attr.setValue(infoList.get(i).getCatalogLabel());
//		catalog.setAttributeNode(attr);

        Element pointId = doc.createElement("ID");
		pointId.appendChild(doc.createTextNode(""+infoList.get(i).getPointId()));
		element.appendChild(pointId);
	
		Element ra = doc.createElement("ra");
		ra.appendChild(doc.createTextNode(""+infoList.get(i).getRa()));
		element.appendChild(ra);
		
		Element dec = doc.createElement("dec");
		dec.appendChild(doc.createTextNode(""+infoList.get(i).getDec()));
		element.appendChild(dec);

        Element mag ;
        if(str.startsWith("N")){
            mag = doc.createElement("jmag");
        }else if(str.startsWith("U")){
            mag = doc.createElement("r_mag");
        }else{
            mag = doc.createElement("k_m");
        }
		mag.appendChild(doc.createTextNode(""+infoList.get(i).getJmag()));
		element.appendChild(mag);

        Element focus = doc.createElement("focus");
		focus.appendChild(doc.createTextNode(""+infoList.get(i).getFocus()));
		element.appendChild(focus);
        }

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
        ApplicationContext appContext = FovastApplication.getApplication().getContext();
        File downloadCacheDir =
                    new File(appContext.getLocalStorage().getDirectory(),
                    DOWNLOAD_CACHE_DIR);
        StreamResult result = new StreamResult(downloadCacheDir);
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);

		System.out.println("File saved in cache!");

	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
	}

    public void saveXML(ApplicationContext appContext){
        AppConfiguration config =
                    FovastApplication.getApplication().getConfiguration();
                String dirToOpen = config.getFileDialogDirProperty();
                final JFileChooser fc = new JFileChooser(dirToOpen);

                fc.setDialogTitle("Save");
                fc.setFileFilter(new CustomFilter());
                final String defaultFileName = "guideStarInfo.xml";
                fc.setSelectedFile(
                new File(fc.getCurrentDirectory().getAbsolutePath() +
                "\\" + defaultFileName));
                // add listener to filter changes
                fc.addPropertyChangeListener(JFileChooser.DIRECTORY_CHANGED_PROPERTY,
                new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {

                fc.setSelectedFile(
                new File(fc.getCurrentDirectory().getAbsolutePath() +
                "\\" + defaultFileName));


                fc.updateUI();

                }
                });
                 int retVal = fc.showSaveDialog(null);

                if(retVal == JFileChooser.APPROVE_OPTION) {
                    File cachedFile = new File(appContext.getLocalStorage().getDirectory(),
                    DOWNLOAD_CACHE_DIR);
                    String newFilePath = fc.getSelectedFile().getAbsolutePath();
                    if(!newFilePath.contains(".xml")){
                        newFilePath += ".xml";
                    }
                    FileInputStream in =null;
                    FileOutputStream out =null;
                    try {
                               in = new FileInputStream(
                                          cachedFile);
                                     out = new FileOutputStream(
                                                newFilePath);

                                          byte[] buf = new byte[1048576];
                                          int len;
                                          while ((len = in.read(buf)) > 0) {
                                                out.write(buf, 0, len);
                                          }
                                          System.out.println("File copied.");
                                    } catch (FileNotFoundException ex) {
                                          ex.printStackTrace();
                                    } catch (IOException e) {
                                          e.printStackTrace();
                                    } finally {
                                          try {
                                                in.close();
                                          } catch(Throwable th) {
                                                th.printStackTrace();
                                          }
                                          try {
                                                out.close();
                                          } catch(Throwable th) {
                                                th.printStackTrace();
                                          }
                                    }
                }
               }

    static class CustomFilter extends javax.swing.filechooser.FileFilter {

        public boolean accept(File f) {
            if(f.isDirectory())
                return true;
            for(int i = 0; i < XML_EXTENSIONS.length; i++) {
                if(f.getName().toLowerCase().endsWith(XML_EXTENSIONS[i]))
                    return true;
            }

            return false;
        }

        public String getDescription() {
            return XML_EXTENSIONS_DESC;
        }

    }

}