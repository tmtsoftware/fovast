/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */
package org.tmt.fovast.gui;

import java.awt.BorderLayout;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jsky.coords.CoordinateConverter;
import jsky.graphics.CanvasFigure;
import jsky.image.fits.codec.FITSImage;
import jsky.image.graphics.DivaImageGraphics;
import jsky.image.graphics.ShapeUtil;
import jsky.image.gui.ImageDisplayControl;
import nom.tam.fits.FitsException;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.gui.VisualizationPanel.CatalogListener;
import org.tmt.fovast.instrumentconfig.Config;
import org.tmt.fovast.instrumentconfig.Value;
import org.tmt.fovast.state.VisualizationState;
import org.tmt.fovast.util.Cache;
import org.tmt.fovast.vo.client.SiaClient;

/**
 *
 */
public class VisualizationWorkPanel extends JPanel
        implements VisualizationState.VisualizationStateListener,
        Config.ConfigListener {

    private static Logger logger = LoggerFactory.getLogger(VisualizationWorkPanel.class);

    private static String DSS_SIA_ENDPOINT = "http://archive.eso.org/bin/dss_sia/dss.sia?VERSION=1.0&";

    private static String DSS_SIA_SURVEY_UCD = "survey";

    private static String DEFAULT_DSS_SURVEY = "DSS-1";

    private static double DEFAULT_SIZE = 0.6;

    private ApplicationContext appContext;

    private final VisualizationState visualization;

    private ImageDisplayControl displayComp;

    private Task task = null;

    private ArrayList<VisualizationWorkPanelListener> listeners =
            new ArrayList<VisualizationWorkPanelListener>();

    private boolean targetSet = false;

    private Double targetRa = 0.0;

    private Double targetDec = 0.0;

    private CanvasFigure targetMarker;

    private boolean gridShown = false;

    private boolean inSetTargetEvent = false;

    private ArrayList<CatalogListener> catalogListeners = new ArrayList<CatalogListener>();

    public VisualizationWorkPanel(ApplicationContext appContext,
            VisualizationState visualization) {
        this.appContext = appContext;
        this.visualization = visualization;

        //TODO: initialize from viz
        
        initComponents();

        visualization.addListener(this);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        displayComp = new FovastImageDisplayControl(); //new ImageDisplayControl();
        //TODO: We should form a blank FITS image using nom.tam.Fits API and load it.
        //The dummy FITS should have TAN project and should be atleast 0.6 degree on each side
        //This would get rid of the ugly fix below.
        //We are doing this in invokeLater as 
        //the dummy WCS setting on blank image done by JSkyCat gets nullified
        //in an component resized event .. 
        //Seems to be a problem with the JSky API
        //We are doing this in double invokeLater so that VisualizationWorkPanel is
        //realized before the blank image is set and getWidth/Height() calls give
        //meaningful values. These methods are called internally called in JSkyCat
        //If they return 0 then image paint is deferred in invokeLater internally.
        //We need two invokeLaters here as the workpanel is not yet visible.
        //setVisible / realization happens after the whole VisPanel is visualized.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        displayComp.getImageDisplay().setAutoCenterImage(true);
                        displayComp.getImageDisplay().blankImage(targetRa, targetDec);
                    }
                });
            }
        });
        
        //ScrollPane does not work properly with JSky display comps
        //add(new JScrollPane(displayComp), BorderLayout.CENTER);
        add(displayComp, BorderLayout.CENTER);
    }


    @Override
    public void vslTargetChanged(double ra, double dec, String raEntered, String decEntered) {
        if(!inSetTargetEvent)
            loadImage(ra, dec);
    }

    @Override
    public void vslShowTarget(boolean show) {
        showTarget(show);
    }

    @Override
    public void vslConfigChanged(Config config) {
        clearConfigDisplayElements();
        config.addConfigListener(this);
    }


    private void loadImage(final double ra, final double dec) {

        Set<Catalog> catalogs = getCatalogList();
        Iterator iter = catalogs.iterator();
        while(iter.hasNext())
        {
            Catalog c = (Catalog)iter.next();
            iter.remove();
            remove(c);
        }
        

        if(targetRa == ra && targetDec == dec) {
            return; // nothing to do as image is already loaded. 
        }
        
        targetSet = true;
        targetRa = ra;
        targetDec = dec;

        //cancel existing task
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }

        displayComp.getImageDisplay().blankImage(ra, dec);
        //displayComp.getImageDisplay().setImage(
        //        new FITSImage("/home/vivekananda_moosani/.jsky3/" +
        //        "cache/jsky1608254944564360809.fits"));

        task = new Task(appContext.getApplication()) {

            private URL urlToDownload = null;
            //private URLConnection conn = null;

            //TODO: We should pass the Cache class to the constructor
            //or have a setter
            Cache cache = ((FovastApplication) appContext.getApplication()).getDssImageCache();


            @Override
            protected Object doInBackground() throws Exception {
                if (!isCancelled()) {
                    SiaClient siaClient = new SiaClient(DSS_SIA_ENDPOINT);
                    HashMap<String, Object> otherConstraints = new HashMap<String, Object>();
                    otherConstraints.put(DSS_SIA_SURVEY_UCD, DEFAULT_DSS_SURVEY);
                    fireBackgroundImageLoadStartedEvent();
                    String[] urls =
                            siaClient.fetchFitsImages(ra, dec, DEFAULT_SIZE, DEFAULT_SIZE,
                            otherConstraints);
                    if (!isCancelled()) {
                        if (urls.length > 0) {
                            urlToDownload = new URL(urls[0]);
                            if (cache.getFile(urlToDownload) == null) {
                                logger.info("Downloading image from " + urlToDownload.toString());
                                cache.save(urlToDownload, new Cache.SaveListener() {

                                    @Override
                                    public void bytesRead(long bytes) {
                                        fireBytesReadEvent(bytes);
                                    }

                                    @Override
                                    public void setTotalBytes(long length) {
                                        fireSetTotalBytesEvent(length);
                                    }
                                });
                            } else {
                                logger.info("Using image from local cache instead of from " + urlToDownload.toString());
                                logger.info("Cached image path " + cache.getFile(urlToDownload).toString());
                            }
                            return cache.getFile(urlToDownload);
                        }
                    }
                    else {
                        if(urlToDownload != null)
                            logger.info("Task cancelled: " + urlToDownload.toString());
                        else
                            logger.info("Task cancelled");
                    }
                }
                else {
                    if(urlToDownload != null)
                        logger.info("Task cancelled: " + urlToDownload.toString());
                    else
                        logger.info("Task cancelled");
                }
                return null;
            }

            @Override
            protected void failed(Throwable cause) {
                super.failed(cause);                
                if (!isCancelled()) {
                    logger.warn("Some thing went wrong while loading image", cause);
                    JOptionPane.showMessageDialog(VisualizationWorkPanel.this,
                            "DSS image load failed");
                    fireBackgroundImageLoadFailedEvent();
                }
                else {
                    logger.debug("[TASK CANCELLED] in failed method", cause);
                }
            }

            @Override
            protected void succeeded(Object result) {
                super.succeeded(result);                
                if (!isCancelled()) {
                    File imageFile = (File) result;
                    if (imageFile != null) {
                        try {
                            displayComp.getImageDisplay().setImage(
                                    new FITSImage(
                                    imageFile.getAbsolutePath()));
                            fireBackgroundImageLoadCompletedEvent();
                        } catch (Exception ex) {
                            //Remove the corrupt image from cache
                            cache.remove(urlToDownload);
                            
                            logger.info("Image load failed", ex);
                            JOptionPane.showMessageDialog(VisualizationWorkPanel.this,
                                    "DSS image could not be loaded");
                            fireBackgroundImageLoadFailedEvent();
                        }
                    } else {
                        logger.info("No image fetched");
                        JOptionPane.showMessageDialog(VisualizationWorkPanel.this,
                                "No DSS image found");
                        fireBackgroundImageLoadCompletedEvent();
                    }
                    
                }
                else {
                    if(result != null)
                        logger.debug("[TASK CANCELLED] in success method" + result);
                    else
                        logger.debug("[TASK CANCELLED] in success method");
                }

            }

            @Override
            protected void finished() {
                super.finished();
                if (isCancelled()) {
                    logger.info("[TASK CANCELLED] in finished method");
                }
            }


        };
        task.execute();

    }

    public boolean isImageLoaded(){
       return targetSet;
    }

    private void showTarget(boolean show) {
        DivaImageGraphics canvasGraphics =
                (DivaImageGraphics) displayComp.getImageDisplay().getCanvasGraphics();

        if(targetSet) {
            if(targetMarker != null) {
                canvasGraphics.remove(targetMarker);
                targetMarker = null;
                canvasGraphics.repaint();
            }
            if(show) {
                CoordinateConverter converter = displayComp.getImageDisplay(
                        ).getCoordinateConverter();
                Point2D.Double centerPixel = (Point2D.Double) converter.getImageCenter().clone();
                //makeing clone
                centerPixel = new Point2D.Double(centerPixel.x, centerPixel.y);
                converter.imageToScreenCoords(centerPixel, false);
                int halfWidth = 20;
                //Rectangle2D.Double shape = new Rectangle2D.Double((centerPixel.x - halfWidth),
                //        (centerPixel.y - halfWidth), halfWidth*2, halfWidth*2);
                //targetMarker = canvasGraphics.makeRectangle(rect, CoordinateConverter.SCREEN,
                //        Color.WHITE, Color.WHITE, 1.0f, null);
                //Shape shape = ShapeUtil.makeCompass(centerPixel,
                //        new Point2D.Double(centerPixel.x + halfWidth, centerPixel.y + halfWidth),
                //        new Point2D.Double(centerPixel.x - halfWidth, centerPixel.y - halfWidth));                
                Shape shape = ShapeUtil.makePlus(centerPixel,
                        new Point2D.Double(centerPixel.x, centerPixel.y - halfWidth),
                        new Point2D.Double(centerPixel.x - halfWidth, centerPixel.y));
                //Shape shape = ShapeUtil.makeEllipse(centerPixel,
                //        new Point2D.Double(centerPixel.x, centerPixel.y - halfWidth),
                //        new Point2D.Double(centerPixel.x - halfWidth, centerPixel.y));
                //Shape shape = makeTargetShape(centerPixel, halfWidth);

                targetMarker = canvasGraphics.makeFigure(shape, null, Color.WHITE, 2.0f);
                canvasGraphics.add(targetMarker);
                canvasGraphics.repaint();
            } 
        }
    }

    //
    //VisualizationWorkPanelListener support methods .. 
    //
    public void addVisualizationWorkPanelListener(
            VisualizationWorkPanelListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeVisualizationWorkPanelListener(
            VisualizationWorkPanelListener listener) {
        listeners.remove(listener);
    }

    private void fireBackgroundImageLoadStartedEvent() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).backgroundImageLoadStarted();
        }
    }

    private void fireBytesReadEvent(long bytesRead) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).backgroundImageBytesRead(bytesRead);
        }
    }

    private void fireSetTotalBytesEvent(long length) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).setImageSize(length);
        }
    }

    private void fireBackgroundImageLoadFailedEvent() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).backgroundImageLoadFailed();
        }
    }

    private void fireBackgroundImageLoadCompletedEvent() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).backgroundImageLoadCompleted();
        }
    }

    void stopRunningTasks() {
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }
    }

    void setImage(final String fitsImage) throws IOException, FitsException {
        
        Set<Catalog> tempCatalogs = getCatalogList();
        Iterator iter = tempCatalogs.iterator();
        Catalog tempC;
        while(iter.hasNext())
        {
            tempC = (Catalog)iter.next();
            iter.remove();
            remove(tempC);
        }
//        try {
//                    Thread th=new Thread() {
//                    @Override
//                        public void run() {
//                            super.run();
//                            try {
//                                //TODO: Need to get rid of this hack
//                                Thread.sleep(250);
//                            }catch (InterruptedException ex) {
//                                java.util.logging.Logger.getLogger(VisualizationWorkPanel.class.getName()).log(Level.SEVERE, null, ex);
//                            }
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    try {
                                        try {
                                            inSetTargetEvent = true;
                                            //if targetSet was false its a new panel loaded from image
                                            boolean newPanel = !targetSet;

                                            //there was a problem with nio channel created on the FITS image
                                            //when zooming if this is done outside invoke later
                                            //ok .. the above comment sounds as a wierd fix but it works
                                            //TODO: Need to find out exact problem.
                                            displayComp.getImageDisplay().setImage(new FITSImage(new File(fitsImage).getAbsolutePath()));
                                            targetSet = true;
                                            //double invoke later as center marker placement is not happening
                                            //properly if not.
                                            CoordinateConverter converter = displayComp.getImageDisplay().getCoordinateConverter();
                                            Point2D.Double centerPixel = (Point2D.Double) converter.getWCSCenter();
                                            targetRa = centerPixel.x;
                                            targetDec = centerPixel.y;
                                            
                                            visualization.setTarget(targetRa, targetDec);
                                            if(newPanel)
                                                //note corresponding update event shows the target in this case.
                                                visualization.showTarget(true);
                                            else if(targetMarker != null)
                                                showTarget(true);
                                        }
                                        finally {
                                            inSetTargetEvent = false;
                                        }

//                                      marker is automatically shown when target cb is checked on control panel
//                                        //makeing clone
//                                        centerPixel = new Point2D.Double(centerPixel.x, centerPixel.y);
//                                        converter.imageToScreenCoords(centerPixel, false);
//                                        int halfWidth = 20;
//                                        Shape shape = ShapeUtil.makePlus(centerPixel, new Point2D.Double(centerPixel.x, centerPixel.y - halfWidth), new Point2D.Double(centerPixel.x - halfWidth, centerPixel.y));
//                                        targetMarker = canvasGraphics.makeFigure(shape, null, Color.WHITE, 2.0f);
//                                        canvasGraphics.add(targetMarker);
//                                        canvasGraphics.repaint();
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(
                                                VisualizationWorkPanel.this, "Image load failed");
                                        logger.error("Could not load image", ex);
                                    }
                                }
                           });
                            }
                       });

//                      }
//                   };
//                   th.start();
//           } catch (IOException ex) {
//                 logger.error("Could not load image", ex);
//           } catch (FitsException ex) {
//                 logger.error("Could not load image", ex);
//           }
    }
    
    public Point2D.Double getCenter(){
        DivaImageGraphics canvasGraphics =
                (DivaImageGraphics) displayComp.getImageDisplay().getCanvasGraphics();
        CoordinateConverter converter = displayComp.getImageDisplay(
                        ).getCoordinateConverter();
        Point2D.Double centerPixel = (Point2D.Double) converter.getWCSCenter();
        return new Point2D.Double(centerPixel.x, centerPixel.y);
    }

    public void toggleGrid(){
       ((FovastImageDisplay)displayComp.getImageDisplay()).toggleGrid();
       gridShown = !gridShown;
    }

    public boolean isGridShown() {
        return gridShown;
    }

    void showImageColorsFrame() {
        displayComp.getImageDisplay().editColors();
    }

    void showImageCutLevelsFrame() {
        displayComp.getImageDisplay().editCutLevels();
    }

    void showImageExtensionsFrame() {
        displayComp.getImageDisplay().viewFitsExtensions();
    }

    void showImageKeywordsFrame() {
        displayComp.getImageDisplay().viewFitsKeywords();
    }

    public void plotCatalog(Catalog c) throws MalformedURLException, IOException{
      FovastSymbolLayer layer =((FovastImageDisplay)(displayComp.getImageDisplay())
              ).getSymbolLayer();
      //FovastTablePlotter plotter = new FovastTablePlotter();
      FovastTablePlotter plotter=layer.getPlotter();
      plotter.makeList(c);
     // layer.setPlotter(plotter);
      layer.repaint();
      for(int i=0; i<catalogListeners.size(); i++) {
                try {
                    CatalogListener cl = catalogListeners.get(i);
                    cl.catalogAdded(c);
                } catch(Exception ex) {
                    logger.error(null, ex);
                }
            }
    }

    public void showHide(Catalog c,boolean state){
        FovastSymbolLayer layer =((FovastImageDisplay)(displayComp.getImageDisplay())
                  ).getSymbolLayer();
          FovastTablePlotter plotter=layer.getPlotter();
          plotter.showHide(c,state);
          layer.repaint();
    }

    public void remove(Catalog c){
        FovastSymbolLayer layer =((FovastImageDisplay)(displayComp.getImageDisplay())
                  ).getSymbolLayer();
          FovastTablePlotter plotter=layer.getPlotter();
          plotter.remove(c);
          layer.repaint();
          for(int i=0; i<catalogListeners.size(); i++) {
                try {
                    CatalogListener cl = catalogListeners.get(i);
                    cl.catalogRemoved(c);
                } catch(Exception ex) {
                    logger.error(null, ex);
                }
          }
    }


    public Set<Catalog> getCatalogList(){
      FovastSymbolLayer layer =((FovastImageDisplay)(displayComp.getImageDisplay())
              ).getSymbolLayer();
      FovastTablePlotter plotter=layer.getPlotter();
      HashMap<Catalog,ArrayList> catalogDetails=plotter.getCatalogList();     
      return catalogDetails.keySet();
      
    }

    public void addCatalogListener(CatalogListener cl) {
        catalogListeners.add(cl);
    }

    public void removeCatalogListener(CatalogListener cl) {
        catalogListeners.remove(cl);
    }

    private void clearConfigDisplayElements() {
        
        //TODO: throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void updateConfig(String confElementId, Value value) {
        //TODO: throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void batchUpdateConfig(ArrayList<String> confElementIds, ArrayList<Value> values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void enableConfig(String confElementId, boolean enable) {
        //TODO: throw new UnsupportedOperationException("Not supported yet.");
    }

}
