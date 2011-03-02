/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.state;

import org.tmt.fovast.mvc.ListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class VisualizationState
        extends ListenerSupport<VisualizationState.VisualizationStateListener> {

    private static Logger logger = LoggerFactory.getLogger(VisualizationState.class);
    
    private Double targetRa;

    private Double targetDec;

    private boolean showTarget;

    public VisualizationState() {
    }

    public void setTarget(double ra, double dec) {
        this.targetRa = ra;
        this.targetDec = dec;

        fireVslTargetChanged(ra, dec, null, null);
    }

    public void setTarget(double ra, double dec, String raEntered, String decEntered) {
        this.targetRa = ra;
        this.targetDec = dec;

        fireVslTargetChanged(ra, dec, raEntered, decEntered);
    }

    public Double[] getTarget() {
        return new Double[]{targetRa, targetDec};
    }

    public void showTarget(boolean show) {
        showTarget = show;
        fireVslTargetChanged(show);
    }

    private void fireVslTargetChanged(double ra, double dec, Object object, Object object0) {
        for(int i=0; i<genericListeners.size(); i++) {
            try {
                ((VisualizationStateListener)(genericListeners.get(i))).vslTargetChanged(
                        targetRa, targetRa, null, null);
            } catch (Exception ex) {
                logger.error("Could not call listener method", ex);
            }
        }
    }

    private void fireVslTargetChanged(boolean show) {
        for(int i=0; i<genericListeners.size(); i++) {
            try {
                ((VisualizationStateListener)(genericListeners.get(i))).vslShowTarget(
                        show);
            } catch (Exception ex) {
                logger.error("Could not call listener method", ex);
            }
        }
    }

    //TODO: Code equals and .. other methods ..


    public static interface VisualizationStateListener {

        public void vslTargetChanged(double ra, double dec,
                String raEntered, String decEntered);

        public void vslShowTarget(boolean show);
        
    }
}
