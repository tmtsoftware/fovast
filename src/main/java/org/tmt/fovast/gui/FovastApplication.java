/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.gui;

import org.tmt.fovast.util.Cache;
import java.io.File;
import java.io.IOException;
import org.jdesktop.application.Application;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.state.FovastApplicationState;
import org.tmt.fovast.util.AppConfiguration;
import voi.swing.util.ProxySettingsDialog;

/**
 *
 * @author vivekananda_moosani
 */
public class FovastApplication extends SingleFrameApplication {

    private static final Logger logger = LoggerFactory.getLogger(FovastApplication.class);

    public static final int ERROR_RETURN_CODE = 1;

    public static final String APPLICATION_ID_KEY = "Application.id";

    public static final String APPLICATION_VERSION_KEY = "Application.version";

    public static final String MAIN_FRAME_NAME = "mainFrame";

    public static final String XML_EXT = ".xml";

    public static final String DOWNLOAD_CACHE_INDEX_FILE = "downloadCache.ind";

    public static final String DOWNLOAD_CACHE_DIR = "downloadCache";

    public static final String PROXY_SETTINGS_FILE_KEY =
            "Application.proxySettingsFile";

    public static final String PREFERENCES_FILE_KEY = "Application.preferencesFile";

    private ApplicationContext applicationContext;

    private ResourceMap resourceMap;

    private FovastApplicationState fovastApplicationState;

    private String stateFile;

    private Cache dssImageCache;

    private AppConfiguration appConfig;

    public FovastApplication() {
    }

    @Override
    protected void initialize(String[] args) {
        try {
            super.initialize(args);

            //TODO: Show copyright and startup greeter with launch progress.

            applicationContext = getContext();
            resourceMap = applicationContext.getResourceMap();
            stateFile = resourceMap.getString(APPLICATION_ID_KEY) + resourceMap.getString(APPLICATION_VERSION_KEY) + XML_EXT;
            File localDir = applicationContext.getLocalStorage().getDirectory();
            if (!localDir.exists()) {
                if(!localDir.getParentFile().exists()) {
                    localDir.getParentFile().mkdir();
                }
                localDir.mkdir();
            }
            logger.info("App data will be stored in " + localDir.toString());

            //Initializae DSS image cache
            File downloadCacheFile =
                    new File(applicationContext.getLocalStorage().getDirectory(),
                    DOWNLOAD_CACHE_INDEX_FILE);
            //logger.info("App data will be stored in " + downloadCacheFile.toString());
            File downloadCacheDir =
                    new File(applicationContext.getLocalStorage().getDirectory(),
                    DOWNLOAD_CACHE_DIR);
            dssImageCache = new Cache(downloadCacheFile, downloadCacheDir);

            //Load proxy settings .. note this method consumes exceptions so need
            //not be put in a try-catch of its own.
            ProxySettingsDialog.readSettingsFile(new File(localDir,
                    resourceMap.getString(PROXY_SETTINGS_FILE_KEY)));

            //Load other app preferences
            try {
                appConfig = new AppConfiguration(new File(localDir,
                        resourceMap.getString(PREFERENCES_FILE_KEY)));
            } catch(Exception ex) {
                logger.error("Could not load configuration .. ", ex);
            }

            //Load app state
            fovastApplicationState =
                    (FovastApplicationState) applicationContext.getLocalStorage().load(stateFile);
            if (fovastApplicationState == null) {
                fovastApplicationState = new FovastApplicationState();
            }
            //TODO: loading saved state
            //fovastApplicationState = fovast.initFromSavedState();

        } catch (IOException ex) {
            logger.warn("Could not restore state .. ", ex);
            fovastApplicationState = new FovastApplicationState();
        }
    }

    @Override
    protected void startup() {

        //initialize main frame for the app
        FovastMainView mainView = new FovastMainView();
        
        show(mainView);

        //TODO:need to check how to only do this the first time
        //and on later invocations leave it to the BSAF framework to
        //set size to that saved.
        //TODO: we overrided pack in FovastMainFrame due to bug in bsaf to do nothing
        //Need to get rid of it
        //mainFrame.setExtendedState(FovastMainView.MAXIMIZED_BOTH);
        
    }

    @Override
    protected void shutdown() {
        try {
            super.shutdown();
        } catch (Exception ex) {
            //TODO: .. Frame bounds exception
            //logger.warn("Could not store application state ... ", ex);
        }

        //Save app preferences
        try {
            appConfig.saveConfiguration();
        } catch(Exception ex) {
            logger.error("Could not save configuration .. ", ex);
        }

        try {
            //TODO: custom code for saving visualizations
            applicationContext.getLocalStorage().save(
                    fovastApplicationState, stateFile);

            //save dssImageIndex cache
            dssImageCache.save();

        } catch (Exception ex) {
            logger.warn("Could not store application state ... ", ex);
        }
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of DesktopApplication1
     */
    public static FovastApplication getApplication() {
        return Application.getInstance(FovastApplication.class);
    }

    public Cache getDssImageCache() {
        return dssImageCache;
    }

    public AppConfiguration getConfiguration() {
        return appConfig;
    }

}
