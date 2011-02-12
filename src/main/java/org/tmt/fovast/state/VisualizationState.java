/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.state;

import java.util.HashMap;
import org.tmt.fovast.mvc.ChangeSupport;
import org.tmt.fovast.mvc.StateSupport;

/**
 *
 * @author vivekananda_moosani
 */
public class VisualizationState extends StateSupport {

    //TODO: these should be again replicated in corresponding controller class.
    public static final String TARGET_CHANGED_EVENT_KEY = "targetChanged";

    public static final String TARGET_RA_ARG_KEY = "ra";

    public static final String TARGET_DEC_ARG_KEY = "dec";

    public static final String TARGET_RA_ENTERED_ARG_KEY = "raEntered";

    public static final String TARGET_DEC_ENTERED_ARG_KEY = "decEntered";

    public static final String SHOW_TARGET_EVENT_KEY = "showTargetChanged";

    public static final String SHOW_TARGET_ARG_KEY = "showTarget";


    private Double targetRa;

    private Double targetDec;

    private boolean showTarget;

    public VisualizationState() {
    }

    public void setTarget(double ra, double dec) {
        this.targetRa = ra;
        this.targetDec = dec;

        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put(TARGET_RA_ARG_KEY, ra);
        args.put(TARGET_DEC_ARG_KEY, dec);
        changeSupport.fireChange(this, TARGET_CHANGED_EVENT_KEY, args);
    }

    public void setTarget(double ra, double dec, String raEntered, String decEntered) {
        this.targetRa = ra;
        this.targetDec = dec;

        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put(TARGET_RA_ARG_KEY, ra);
        args.put(TARGET_DEC_ARG_KEY, dec);
        args.put(TARGET_RA_ENTERED_ARG_KEY, raEntered);
        args.put(TARGET_DEC_ENTERED_ARG_KEY, decEntered);
        changeSupport.fireChange(this, TARGET_CHANGED_EVENT_KEY, args);
    }

    public Double[] getTarget() {
        return new Double[]{targetRa, targetDec};
    }

    public void showTarget(boolean show) {
        showTarget = show;
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put(SHOW_TARGET_ARG_KEY, (Boolean)show);
        changeSupport.fireChange(this, SHOW_TARGET_EVENT_KEY, args);

    }

    //TODO: Code equals and .. other methods .. 
}
