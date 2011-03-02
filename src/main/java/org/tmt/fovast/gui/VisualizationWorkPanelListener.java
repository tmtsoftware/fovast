/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.gui;

/**
 *
 */
public interface VisualizationWorkPanelListener {

    /**
     * Background image fetch started
     */
    public void backgroundImageLoadStarted();

    /**
     * Not necessarily called
     */
     public void setImageSize(long length);
    
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
