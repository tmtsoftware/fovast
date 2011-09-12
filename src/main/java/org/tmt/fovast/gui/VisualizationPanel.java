/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */
package org.tmt.fovast.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import nom.tam.fits.FitsException;
import org.jdesktop.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.astro.util.XMLFileGenerator;
import org.tmt.fovast.state.VisualizationState;
import org.tmt.fovast.util.Cache;

/**
 *
 */
public class VisualizationPanel extends JPanel implements PlotHandler {

    private static Logger logger = LoggerFactory.getLogger(VisualizationPanel.class);

    private ApplicationContext appContext;

    private VisualizationState visualization;
    
    private VisualizationControlPanel controlPanel;

    private VisualizationWorkPanel workPanel;

    private ArrayList<CatalogListener> catalogListeners = new ArrayList<CatalogListener>();

    public VisualizationPanel(ApplicationContext appContext,
            VisualizationState visualization, Cache imageCache) {
        this.appContext = appContext;
        this.visualization = visualization;

        initComponents(imageCache);

        //TODO: If the visualization is an old one (saved one)
        //we have to setup workpanel and control panel appropriately
    }

    @Override
    public void addCatalog(Catalog c) {
        try {
            workPanel.plotCatalog(c);
            visualization.addCatalog(c);
//            for(int i=0; i<catalogListeners.size(); i++) {
//                try {
//                    CatalogListener cl = catalogListeners.get(i);
//                    cl.catalogAdded(c);
//                } catch(Exception ex) {
//                    logger.error(null, ex);
//                }
//            }
        } catch (MalformedURLException ex) {
            logger.error(null, ex);
        } catch (IOException ex) {
            logger.error(null, ex);
        }
    }

    public boolean isImageLoaded(){
        return workPanel.isImageLoaded();
    }

    public Set<Catalog> getCatalogList(){
        return workPanel.getCatalogList();
    }

    private void initComponents(Cache imageCache) {
        setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        controlPanel = new VisualizationControlPanel(appContext, visualization);
        workPanel = new VisualizationWorkPanel(appContext, visualization, imageCache);
        workPanel.addVisualizationWorkPanelListener(controlPanel);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        splitPane.setDividerLocation((int)dim.width/3);
        splitPane.setLeftComponent(controlPanel);        
        splitPane.setRightComponent(workPanel);       
        add(splitPane);
        

        System.out.println("hello"+dim.width);
    }

    //
    // TODO: UNUSED METHODS .. STILL IN TODO STATE
    //
    boolean isNew() {
        //TODO: check if visualization is new
        return true;
    }

    public void save(String fName) {
        //TODO: Code to save the vis panel
        //JOptionPane.showMessageDialog(this, "To be done");
        XMLFileGenerator xf = new XMLFileGenerator();
        xf.saveXML(appContext,fName);
    }

    boolean isModified() {
        //TODO: check if vispanel is modified
        return true;
    }

    void stopRunningTasks() {
        workPanel.stopRunningTasks();
    }

    void setImageAndCenter(String fitsImage) throws IOException, FitsException {
        workPanel.setImage(fitsImage);
//        //these invoke laters is an artifact of the swing utilities
//        //to setImage on ImageDisplay
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        Point2D.Double center = workPanel.getCenter();
//                        controller.setTarget(center.x, center.y);
//                        //controller.vslShowTarget(true);
//                        controlPanel.setCenter(center);
//                    }
//                });
//            }
//        });
    }

    public void toggleGrid(){
      workPanel.toggleGrid();
    }

    public boolean isGridShown() {
        return workPanel.isGridShown();
    }

    void showImageColorsFrame() {
        workPanel.showImageColorsFrame();
    }

    void showImageCutLevelsFrame() {
        workPanel.showImageCutLevelsFrame();
    }

    void showImageExtensionsFrame() {
        workPanel.showImageExtensionsFrame();
    }

    void showImageKeywordsFrame() {
        workPanel.showImageKeywordsFrame();
    }

    void showHide(Catalog c,boolean state){
        workPanel.showHide(c,state);
    }

    public Point2D.Double getCenter(){
       return workPanel.getCenter();
    }

    void remove(Catalog c){
        workPanel.remove(c);
        visualization.removeCatalog(c);
//        for(int i=0; i<catalogListeners.size(); i++) {
//                try {
//                    CatalogListener cl = catalogListeners.get(i);
//                    cl.catalogRemoved(c);
//                } catch(Exception ex) {
//                    logger.error(null, ex);
//                }
//            }
    }

    public boolean isCatalogShown(Catalog c) {
        return workPanel.isCatalogShown(c);
        //TODO:this should be ideally stored in visualisation state
    }

//    public void plot() throws MalformedURLException, SAXException, IOException{
//        workPanel.plot();
//    }

    public static interface CatalogListener {
        public void catalogAdded(Catalog c);
         public void catalogRemoved(Catalog c);
    }

    public void addCatalogListener(CatalogListener cl) {
        workPanel.addCatalogListener(cl);
    }

    public void removeCatalogListener(CatalogListener cl) {
        workPanel.removeCatalogListener(cl);
    }


}
