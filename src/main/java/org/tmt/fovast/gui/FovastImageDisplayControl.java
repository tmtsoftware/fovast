/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.gui;

import java.net.*;

import jsky.image.gui.ImageDisplayControl;
import jsky.image.gui.DivaMainImageDisplay;


/**
 * Mimicing NavigatorImageDisplayControl here ..
 * 
 * Extends the ImageDisplayControl class by adding support for
 * browsing catalogs and plotting catalog symbols on the image.

 * 
 * @author vivekananda_moosani
 */
public class FovastImageDisplayControl extends ImageDisplayControl{


    /**
     * Construct a NavigatorImageDisplayControl widget.
     *
     * @param size   the size (width, height) to use for the pan and zoom windows.
     */
    public FovastImageDisplayControl(int size) {
        super(size);
    }

    /**
     * Make a NavigatorImageDisplayControl widget with the default settings.
     */
    public FovastImageDisplayControl() {
        super();
    }


    /**
     * Make a NavigatorImageDisplayControl widget with the default settings and display the contents
     * of the image file pointed to by the URL.
     *
     * @param url The URL for the image to load
     */
    public FovastImageDisplayControl(URL url) {
        super(url);
    }


    /**
     * Make a NavigatorImageDisplayControl widget with the default settings and display the contents
     * of the image file.
     *
     * @param filename The image file to load
     */
    public FovastImageDisplayControl(String filename) {
        super(filename);
    }

    /** Make and return the image display window */
    protected DivaMainImageDisplay makeImageDisplay() {
        return new FovastImageDisplay();
    }
}

