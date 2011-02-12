/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.util;

import java.awt.Component;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.JDialog;
import voi.swing.util.DialogCreator;

/**
 * Utility class for app configuration (preferences) load/save and editing
 *
 * @author vivekananda_moosani
 */
public class AppConfiguration {

    private Configuration configuration = new Configuration();

    private ConfigurationGui configGui =
            new ConfigurationGui(configuration);

    private File preferencesFile;
    
    public AppConfiguration(File preferencesFile) {
        this.preferencesFile = preferencesFile;
        if(preferencesFile.exists())
            loadConfiguration();
    }

    public void saveConfiguration() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void loadConfiguration() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void showConfiguration(Component parent) {
        JDialog dialog = DialogCreator.createDialog(parent);
        dialog.add(configGui);
        dialog.setVisible(true);
    }

    public static class Configuration {
        
    }

    /** for GUI editing of config */
    public static class ConfigurationGui extends JPanel {

        public ConfigurationGui(Configuration config) {
            
        }
    }
}
