/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */
package org.tmt.fovast.gui;

import java.awt.BorderLayout;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.net.URL;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import jsky.coords.CoordinateConverter;
import jsky.graphics.CanvasFigure;
import jsky.image.fits.codec.FITSImage;
import jsky.image.graphics.DivaImageGraphics;
import jsky.image.graphics.ShapeUtil;
import jsky.image.gui.ImageDisplayControl;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.controller.FovastApplication;
import org.tmt.fovast.mvc.ChangeListener;
import org.tmt.fovast.state.VisualizationState;
import org.tmt.fovast.controller.VisualizationController;
import org.tmt.fovast.util.Cache;
import org.tmt.fovast.vo.client.SiaClient;

/**
 *
 * @author vivekananda_moosani
 */
public class VisualizationWorkPanel extends JPanel implements ChangeListener {

    private static Logger logger = LoggerFactory.getLogger(VisualizationWorkPanel.class);

    private static String DSS_SIA_ENDPOINT = "http://archive.eso.org/bin/dss_sia/dss.sia?VERSION=1.0&";

    private static String DSS_SIA_SURVEY_UCD = "survey";

    private static String DEFAULT_DSS_SURVEY = "DSS-1";

    private static double DEFAULT_SIZE = 0.6;

    private ApplicationContext appContext;

    private final VisualizationController controller;

    private ImageDisplayControl displayComp;

    private Task task = null;

    private ArrayList<VisualizationWorkPanelListener> listeners =
            new ArrayList<VisualizationWorkPanelListener>();

    private boolean targetSet = true;

    private Double targetRa = 0.0;

    private Double targetDec = 0.0;

    private CanvasFigure targetMarker;

    public VisualizationWorkPanel(ApplicationContext appContext,
            VisualizationController controller) {
        this.appContext = appContext;
        this.controller = controller;
        initComponents();

        controller.addChangeListener(this);
    }

    private void initComponents() {
        //TODO: Work panel components ...
        setLayout(new BorderLayout());
        displayComp = new ImageDisplayControl();        
        displayComp.getImageDisplay().setAutoCenterImage(true);
        //TODO: WCS conversion does not happen with a blank image ..
        //should be fixed .. jskycat tool though works perfect
        displayComp.getImageDisplay().blankImage(targetRa, targetDec);
        
        //TODO: ScrollPane seems to not work with JSky display comps .. Should we fix ??
        //add(new JScrollPane(displayComp), BorderLayout.CENTER);
        add(displayComp, BorderLayout.CENTER);
    }

    @Override
    public void update(Object source, String eventKey, HashMap<String, Object> args) {
        if (source.equals(controller) && eventKey.equals(VisualizationState.TARGET_CHANGED_EVENT_KEY)) {

            Double ra = (Double) args.get(VisualizationState.TARGET_RA_ARG_KEY);
            Double dec = (Double) args.get(VisualizationState.TARGET_DEC_ARG_KEY);

            loadImage(ra, dec);

        } else if (source.equals(controller) && eventKey.equals(VisualizationState.SHOW_TARGET_EVENT_KEY)) {
            Boolean show = (Boolean) args.get(VisualizationState.SHOW_TARGET_ARG_KEY);
            showTarget(show);

        } else {
            //TODO: this msg should be from resource bundle (should be parameterised msg
            throw new RuntimeException("Unknown event key : " + eventKey +
                    "from source " + source.toString());
        }
    }

    private void loadImage(final Double ra, final Double dec) {

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

        //TODO: Update some image panel label for progress
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

    private void showTarget(boolean show) {
        DivaImageGraphics canvasGraphics =
                (DivaImageGraphics) displayComp.getImageDisplay().getCanvasGraphics();

        if(targetSet) {
            if(targetMarker != null) {
                canvasGraphics.remove(targetMarker);
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

    /**
     * TODO: Trying to make a something like LaTeX \oplus .. but the outplut not really
     * smooth yet
     *
     * @param centerPixel
     * @param halfSize
     * @return
     */
    private GeneralPath makeTargetShape(Point2D.Double centerPixel, double halfSize) {
        Point2D.Double north = new Point2D.Double(centerPixel.x, centerPixel.y - halfSize);
        Point2D.Double south = new Point2D.Double(centerPixel.x, centerPixel.y + halfSize);
        Point2D.Double east = new Point2D.Double(centerPixel.x + halfSize*2, centerPixel.y);
        Point2D.Double west = new Point2D.Double(centerPixel.x - halfSize*2, centerPixel.y);
        Point2D.Double eastA = new Point2D.Double(centerPixel.x + halfSize*4, centerPixel.y);
        Point2D.Double westA = new Point2D.Double(centerPixel.x - halfSize*4, centerPixel.y);
        
        // XXX Note: This is not an ellipse. What we really want is a "smooth polygon"...
        GeneralPath p = new GeneralPath();
        p.moveTo((float) north.x, (float) north.y);
        p.quadTo((float) westA.x, (float) westA.y, (float) south.x, (float) south.y);
        p.quadTo((float) eastA.x, (float) eastA.y, (float) north.x, (float) north.y);
        p.lineTo(south.x, south.y);
        p.moveTo(west.x, west.y);
        p.lineTo(east.x, east.y);
        //p.closePath();
        return p;
    }

    void stopRunningTasks() {
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }
    }


}
