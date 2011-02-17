/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.state;

import java.io.File;
import org.tmt.fovast.mvc.StateSupport;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author vivekananda_moosani
 */
public class FovastApplicationState extends StateSupport {

    //TODO: Events should be redefined in Controller classes .. as UI class use
    //these constants as of now.
    public final static String VISUALIZATION_ADDED_EVENT_KEY = "visualizationAdded";

    public final static String VISUALIZATION_REMOVED_EVENT_KEY = "visualizationRemoved";

    public final static String VISUALIZATION_SELECTED_EVENT_KEY = "visualizationSelected";

    public final static String VISUALIZATION_ARG_KEY = "visualization";

    public final static String VISUALIZATION_FILENAME_ARG_KEY = "visualizationFileName";

    public final static String VISUALIZATION_ID_ARG_KEY = "visualizationId";

//    public final static String VISUALIZATION_IMAGE_ARG_KEY = "imageName";

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
            args.put(VISUALIZATION_ARG_KEY, visualization);
            args.put(VISUALIZATION_ID_ARG_KEY, vizId);
            args.put(VISUALIZATION_FILENAME_ARG_KEY, fileName);
            changeSupport.fireChange(this, VISUALIZATION_ADDED_EVENT_KEY, args);
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
            
            HashMap<String, Object> args = new HashMap<String, Object>();

            //args.put(VISUALIZATION_ARG_KEY, visualization);
            args.put(VISUALIZATION_ID_ARG_KEY, associatedId);
            changeSupport.fireChange(this, VISUALIZATION_REMOVED_EVENT_KEY, args);

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
            HashMap<String, Object> args = new HashMap<String, Object>();
            args.put(VISUALIZATION_ID_ARG_KEY, id);
            changeSupport.fireChange(this, VISUALIZATION_SELECTED_EVENT_KEY, args);
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
            HashMap<String, Object> args = new HashMap<String, Object>();
            args.put(VISUALIZATION_ID_ARG_KEY, id);
            changeSupport.fireChange(this, VISUALIZATION_SELECTED_EVENT_KEY, args);
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

    //TODO: methods to store loaded images / catalogs ...
}
