/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.jdesktop.application.ApplicationContext;
import org.tmt.fovast.mvc.ChangeListener;
import org.tmt.fovast.mvc.ChangeSupport;
import org.tmt.fovast.state.FovastApplicationState;
import org.tmt.fovast.state.VisualizationState;

/**
 * Controller class for the app. 
 * Fovast UI use controller to make state changes ..
 * They also register for events with the controller ..
 *
 * Here is how things work :
 *                   (result in calls to)
 *  User actions UI ---------------------> Controller
 *        |                                     |  (does business logic
 *        | (listens for  events fired          |     and state changes)
 *        |        by Controller                V
 *        V                                 State classes
 *    Controller <------------------------------|
 *               (state class fire change events
 *                     and controller listens to them)
 *
 * @author vivekananda_moosani
 */
public class FovastApplicationController implements ChangeListener {

    private static final String baseKey = FovastApplicationController.class.getName();

    private static final String keyPrefix = baseKey + ".";

    private static final String UNSAVED_VIS_PANEL_PREFIX_KEY =
            keyPrefix + "UnsavedVisualizationPanelPrefix";

    private FovastApplication fovastApplication;

    private ApplicationContext appContext;

    private FovastApplicationState fovastApplicationState;

    //Id given to the visualization in this state
    private int newVisualizationId = 0;

    private ChangeSupport changeSupport = new ChangeSupport();

    public FovastApplicationController(FovastApplication fovastApplication) {
        this.fovastApplication = fovastApplication;
        this.appContext = fovastApplication.getContext();
    }

    /**
     * This method will attach state to the controller and also
     * register controller as listener to state changes
     *
     * Note the FovastApplicationController acts as controller as well as
     * dispatcher.
     *
     * @param fas
     */
    public void setFovastApplicationState(FovastApplicationState fas) {
        this.fovastApplicationState = fas;
        this.fovastApplicationState.addChangeListener(this);
    }

    public void createNewVisualization() {

        //Decide name for the visualization
        //TODO: As of now we check the open visualizations
        //and accordingly decide the name
        //If we will be automatically storing the visualizations at a specified
        //place under user home as gemini OT does .. then we have to search all
        //visualizations saved in that dir and not just the ones which are open.
        ArrayList<VisualizationState> visualizations =
                fovastApplicationState.getVisualizations();
        String newVisPanelPrefix = appContext.getResourceMap().getString(
                UNSAVED_VIS_PANEL_PREFIX_KEY);
        String prefixToCheck = newVisPanelPrefix;
        int ct = 0;
        boolean prefixCheckFlag = false;
        do {
            int i = 0;
            for (; i < visualizations.size(); i++) {
                if (fovastApplicationState.getVisualizationFileName(
                        visualizations.get(i)).equals(prefixToCheck)) {
                    prefixToCheck = newVisPanelPrefix + " (" + ++ct + ")";
                    break;
                }
            }
            if (i == visualizations.size()) {
                prefixCheckFlag = true;
            }
        } while (!prefixCheckFlag);

        //create visualization and add to model
        VisualizationState visualization = new VisualizationState();
        fovastApplicationState.addVisualization(visualization, newVisualizationId++, prefixToCheck);

        //we donot return anything back to the view that calls the method ..
        //view should have registered as listener to the dispatcher (which is controller in fovast)
        //return visualization;
    }

    public void applicationExitAction() {
        fovastApplication.getMainFrame().setVisible(true);
        fovastApplication.exit();
    }

    public void selectVisualization(int id) {
        fovastApplicationState.selectVisualizationById(id);
    }
    
    public void removeVisualization(int vizId) {
        fovastApplicationState.removeVisualization(vizId);
    }

    @Override
    public void update(Object source, String eventKey, HashMap<String, Object> args) {
        changeSupport.fireChange(this, eventKey, args);
    }

    public void addChangeListener(ChangeListener cl) {
        changeSupport.addChangeListener(cl);
    }

    public void removeChangeListener(ChangeListener cl) {
        changeSupport.removeChangeListener(cl);
    }

    public void addChangeListener(String eventKey, ChangeListener cl) {
        changeSupport.addChangeListener(eventKey, cl);
    }

    public void removeChangeListener(String eventKey, ChangeListener cl) {
        changeSupport.removeChangeListener(eventKey, cl);
    }

}
