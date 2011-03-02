/*
 *  Copyright 2010 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */
package org.tmt.fovast.main;

import org.jdesktop.application.Application;
import org.tmt.fovast.gui.FovastApplication;

/**
 * <p>Class which accepts arguments and launches the FOVAST GUI
 * This is just a wrapper class to quickly identify app's starting point.</p>
 * 
 *
 */
public class Main {

    /**
     * @param args - command line arguments
     */
    public static void main(String[] args) {


        Application app = new FovastApplication();
        app.launch(FovastApplication.class, args);

    }

}
