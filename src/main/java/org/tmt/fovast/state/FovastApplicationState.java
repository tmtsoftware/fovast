/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.state;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.mvc.ListenerSupport;

/**
 *
 */
public class FovastApplicationState
        extends ListenerSupport<FovastApplicationState.FovastApplicationStateListener> {

    private static Logger logger = LoggerFactory.getLogger(FovastApplicationState.class);

    private ArrayList<VisualizationState> visualizations = new ArrayList<VisualizationState>();

    private int activeVisualizationId = -1;

    private HashMap<VisualizationState, Integer> visualizationIdMap =
            new HashMap<VisualizationState, Integer>();

    private HashMap<Integer, VisualizationState> idVisualizationMap =
            new HashMap<Integer, VisualizationState>();

    private HashMap<VisualizationState, String> visualizationFileMap =
            new HashMap<VisualizationState, String>();

    public ArrayList<VisualizationState> getVisualizations() {
        return visualizations;
    }

    public void addVisualization(VisualizationState visualization, int vizId, String fileName) {
        if (!visualizations.contains(visualization)) {
            visualizations.add(visualization);
            visualizationIdMap.put(visualization, new Integer(vizId));
            idVisualizationMap.put(new Integer(vizId), visualization);
            visualizationFileMap.put(visualization, fileName);

            HashMap<String, Object> args = new HashMap<String, Object>();

            fireFasVisualizationAdded(visualization, vizId, fileName);
        }
    }

//    public void addVisualization(VisualizationState visualization, int vizId, String fileName,
//            File imageFile) {
//        if (!visualizations.contains(visualization)) {
//            visualizations.add(visualization);
//            visualizationIdMap.put(visualization, new Integer(vizId));
//            idVisualizationMap.put(new Integer(vizId), visualization);
//            visualizationFileMap.put(visualization, fileName);
//
//            HashMap<String, Object> args = new HashMap<String, Object>();
//            args.put(VISUALIZATION_ARG_KEY, visualization);
//            args.put(VISUALIZATION_ID_ARG_KEY, vizId);
//            args.put(VISUALIZATION_FILENAME_ARG_KEY, fileName);
//            args.put(VISUALIZATION_IMAGE_ARG_KEY, imageFile);
//            changeSupport.fireChange(this, VISUALIZATION_ADDED_EVENT_KEY, args);
//        }
//    }

    public void removeVisualization(int vizId) {
        VisualizationState vis = idVisualizationMap.get((Integer)vizId);
        removeVisualization(vis);
    }

    public void removeVisualization(VisualizationState visualization) {
        if (visualizations.remove(visualization)) {            
            //clean data structures
            Integer associatedId = visualizationIdMap.remove(visualization);
            idVisualizationMap.remove(associatedId);

            fireFasVisualizationRemoved(associatedId);

            //set back to default state
            if(visualizations.size() == 0)                
                selectVisualizationById(-1);
        }
    }

    public void selectVisualization(VisualizationState vis) {
        Integer id = visualizationIdMap.get(vis);
        if (id == null) {
            //TODO: Should this be a checked exception .. ? 
            throw new RuntimeException("Vizualtion not added to the state");
        } else if (id == activeVisualizationId) {
            return;
        } else {
            activeVisualizationId = id;
            fireFasVisualizationSelected(id);
        }
    }

//    public void selectVisualization(int index) {
//        if(visualizations.size() > index && index >= 0) {
//            activeVisualizationIndex = index;
//            changeSupport.fireChangeEvent(this);
//        }
//        else {
//            //TODO: should this be checked exception ?
//            throw new IndexOutOfBoundsException("Index passed is out of bounds: "
//                    + index);
//        }
//    }

    public void selectVisualizationById(int id) {
        if(id == -1) {
            //TODO: should we raise a active visualization change here ??
            activeVisualizationId = -1;
        }
        else if (idVisualizationMap.get((Integer)id) != null) {
            if (activeVisualizationId == id) {
                return;
            }
            activeVisualizationId = id;
            fireFasVisualizationSelected(id);
        } else {
            //TODO: should this be checked exception ?
            throw new RuntimeException("Visualization Id passed is out of bounds: " + id);
        }
    }

    public int getActiveVisualizationId() {
        return activeVisualizationId;
    }

    public String getVisualizationFileName(VisualizationState viz) {
        return visualizationFileMap.get(viz);
    }

    public Integer getVisualizationId(VisualizationState viz) {
        return visualizationIdMap.get(viz);
    }

    private void fireFasVisualizationAdded(VisualizationState visualization, int vizId, String fileName) {
        for(int i=0; i<genericListeners.size(); i++) {
            try {
                ((FovastApplicationStateListener)(genericListeners.get(i))).
                        fasVisualizationAdded(visualization, vizId, fileName);
            } catch (Exception ex) {
                logger.error("Could not call listener method", ex);
            }
        }
    }

    private void fireFasVisualizationSelected(Integer id) {
        for(int i=0; i<genericListeners.size(); i++) {
            try {
                ((FovastApplicationStateListener)(genericListeners.get(i))).
                        fasVisualizationSelected(id);
            } catch (Exception ex) {
                logger.error("Could not call listener method", ex);
            }
        }
    }

    private void fireFasVisualizationRemoved(Integer id) {
        for(int i=0; i<genericListeners.size(); i++) {
            try {
                ((FovastApplicationStateListener)(genericListeners.get(i))).
                        fasVisualizationRemoved(id);
            } catch (Exception ex) {
                logger.error("Could not call listener method", ex);
            }
        }
    }

    //TODO: methods to store loaded images / catalogs ...


    public static interface FovastApplicationStateListener {

        public void fasVisualizationAdded(VisualizationState visualization, 
                int vizId, String fileName);

        public void fasVisualizationRemoved(int vizId);

        public void fasVisualizationSelected(int vizId);
    }
}
