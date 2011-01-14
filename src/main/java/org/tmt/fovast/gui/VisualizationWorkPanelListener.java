/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.gui;

/**
 *
 * @author vivekananda_moosani
 */
public interface VisualizationWorkPanelListener {

    /**
     * Background image fetch started
     */
    public void backgroundImageLoadStarted();

    /**
     * To track the progress of background image load
     * 
     * @param bytesRead - number of bytes read so far
     */
    public void backgroundImageBytesRead(long bytesRead);

    /**
     * Image load failed for some reason
     */
    public void backgroundImageLoadFailed();

    /**
     * Image load successfully completed
     */
    public void backgroundImageLoadCompleted();

}
