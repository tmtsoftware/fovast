/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.controller;

import java.net.PasswordAuthentication;
import org.tmt.fovast.util.Cache;
import java.io.File;
import java.io.IOException;
import org.tmt.fovast.gui.FovastMainFrame;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.state.FovastApplicationState;

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

    private ApplicationContext applicationContext;

    private ResourceMap resourceMap;

    private FovastApplicationState fovastApplicationState;

    private String stateFile;

    private Cache dssImageCache;

    public FovastApplication() {
    }

    @Override
    protected void initialize(String[] args) {
        try {
            super.initialize(args);
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
            File downloadCacheFile =
                    new File(applicationContext.getLocalStorage().getDirectory(),
                    DOWNLOAD_CACHE_INDEX_FILE);
            //logger.info("App data will be stored in " + downloadCacheFile.toString());
            File downloadCacheDir =
                    new File(applicationContext.getLocalStorage().getDirectory(),
                    DOWNLOAD_CACHE_DIR);
            dssImageCache = new Cache(downloadCacheFile, downloadCacheDir);

            //TODO: Show copyright and startup greeter with launch progress.
            fovastApplicationState =
                    (FovastApplicationState) applicationContext.getLocalStorage().load(stateFile);
            if (fovastApplicationState == null) {
                fovastApplicationState = new FovastApplicationState();
            }
            //fovastApplicationState = fovast.initFromSavedState();

        } catch (IOException ex) {
            logger.warn("Could not restore state .. ", ex);
            fovastApplicationState = new FovastApplicationState();
        }
    }

    @Override
    protected void startup() {

        FovastApplicationController fovastMainFrameController =
                new FovastApplicationController(this);
        fovastMainFrameController.setFovastApplicationState(fovastApplicationState);

        //initialize main frame for the app
        FovastMainFrame mainFrame = new FovastMainFrame(applicationContext,
                fovastMainFrameController);
        mainFrame.setName(MAIN_FRAME_NAME);
        mainFrame.initializeFromState(fovastApplicationState);
        setMainFrame(mainFrame);
        //so that restore button does not show a small window
        mainFrame.setSize(600, 600);
        
        show(mainFrame);

        //TODO:need to check how to only do this the first time
        //and on later invocations leave it to the BSAF framework to
        //set size to that saved.
        //TODO: we overrided pack in FovastMainFrame due to bug in bsaf to do nothing
        //Need to get rid of it
        mainFrame.setExtendedState(FovastMainFrame.MAXIMIZED_BOTH);
        
    }

    @Override
    protected void shutdown() {
        try {
            super.shutdown();
        } catch (Exception ex) {
            //TODO: .. Frame bounds exception
            //logger.warn("Could not store application state ... ", ex);
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

    public Cache getDssImageCache() {
        return dssImageCache;
    }

}
