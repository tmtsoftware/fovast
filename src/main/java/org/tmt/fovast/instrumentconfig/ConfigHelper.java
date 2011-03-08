/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */

package org.tmt.fovast.instrumentconfig;

import org.tmt.fovast.gui.FovastInstrumentTree;

/**
 *
 * @author vivekananda_moosani
 */
public class ConfigHelper {

    public Config config;

    public ConfigHelper(Config config) {
        this.config = config;
    }


    public void fireInitialEvents() {
        // TODO: We have to do this so that tree components are enabled and disabled
        // as per the initial state.
    }

    public void addConfigListener(Config.ConfigListener l) {
        config.addConfigListener(l);
    }

    public void setConfig(String configOptionId, Value value) {
        config.setConfig(configOptionId, value);
        //TODO: take decisions on what all elements are to be enabled / disabled
        //and automatically selected
    }

}
