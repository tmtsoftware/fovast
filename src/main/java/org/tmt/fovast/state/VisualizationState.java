/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.state;

import java.util.ArrayList;
import org.tmt.fovast.gui.Catalog;
import org.tmt.fovast.mvc.ListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.instrumentconfig.Config;
import org.tmt.fovast.instrumentconfig.ConfigHelper;


/**
 *
 */
public class VisualizationState
        extends ListenerSupport<VisualizationState.VisualizationStateListener> {

    private static Logger logger = LoggerFactory.getLogger(VisualizationState.class);
    
    private Double targetRa;

    private Double targetDec;

    private boolean showTarget;

    private Config config;

    private ArrayList<Catalog> catalogList = new ArrayList<Catalog>();

    public VisualizationState() {
    }

    public void setTarget(double ra, double dec) {
        setTarget(ra, dec, null, null);
    }

    public void setTarget(double ra, double dec, String raEntered, String decEntered) {
        this.targetRa = ra;
        this.targetDec = dec;
        try {
            this.config = Config.loadDefaultConfig();
            //fireVslConfigChanged(config);
        } catch(Exception ex) {
            throw new RuntimeException(ex);
            //logger.error("!!!!! Could not read instrument config .. ", ex);
        }
        
        fireVslTargetChanged(ra, dec, raEntered, decEntered);

    }

    public Double[] getTarget() {
        return new Double[]{targetRa, targetDec};
    }

    public Config getConfig() {
        return config;
    }

    public void showTarget(boolean show) {
        showTarget = show;
        fireVslTargetChanged(show);
    }

    private void fireVslTargetChanged(double ra, double dec, Object object, Object object0) {
        for(int i=0; i<listeners.size(); i++) {
            try {
                ((VisualizationStateListener)(listeners.get(i))).vslTargetChanged(
                        targetRa, targetDec, null, null);
            } catch (Exception ex) {
                logger.error("Could not call listener method", ex);
            }
        }
    }

    private void fireVslTargetChanged(boolean show) {
        for(int i=0; i<listeners.size(); i++) {
            try {
                ((VisualizationStateListener)(listeners.get(i))).vslShowTarget(
                        show);
            } catch (Exception ex) {
                logger.error("Could not call listener method", ex);
            }
        }
    }

    private void fireVslConfigChanged(Config c) {
        for(int i=0; i<listeners.size(); i++) {
            try {
                ((VisualizationStateListener)(listeners.get(i))
                        ).vslConfigChanged(c);
            } catch (Exception ex) {
                logger.error("Could not call listener method", ex);
            }
        }
    }

    public void addCatalog(Catalog c) {
        catalogList.add(c);
        //TODO:add catalog added event for 2 visualisation state
    }

    public void removeCatalog(Catalog c) {
        catalogList.remove(c);
        //TODO:add catalog removed event for 2 visualisation state
    }

    public ArrayList<Catalog> getCatalogs() {
        return catalogList;
    }
    //TODO: Code equals and .. other methods ..


    public static interface VisualizationStateListener {

        public void vslTargetChanged(double ra, double dec,
                String raEntered, String decEntered);

        public void vslShowTarget(boolean show);

        public void vslConfigChanged(Config config);
    }
}
