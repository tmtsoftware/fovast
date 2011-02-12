/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */
package org.tmt.fovast.gui;

import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JOptionPane;
import nom.tam.fits.FitsException;
import org.jdesktop.application.ApplicationContext;
import org.tmt.fovast.controller.VisualizationController;
import org.tmt.fovast.state.VisualizationState;

/**
 *
 * @author vivekananda_moosani
 */
public class VisualizationPanel extends JPanel {

    private ApplicationContext appContext;

    private VisualizationControlPanel controlPanel;

    private VisualizationWorkPanel workPanel;

    private VisualizationController controller;

    public VisualizationWorkPanel getWorkPanel()
    {
        return workPanel;
    }
    public void setWorkPanel (VisualizationWorkPanel workPanel)
    {
        this.workPanel=workPanel;
    }

    public VisualizationPanel(ApplicationContext appContext,
            VisualizationController visController) {
        this.appContext = appContext;
        this.controller = visController;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        controlPanel = new VisualizationControlPanel(appContext, controller);
        workPanel = new VisualizationWorkPanel(appContext, controller);
        workPanel.addVisualizationWorkPanelListener(controlPanel);

        splitPane.setLeftComponent(controlPanel);
        splitPane.setRightComponent(workPanel);
        add(splitPane);
    }

    //
    // UNUSED METHODS .. STILL IN TODO STATE 
    //
    boolean isNew() {
        //TODO: check if visualization is new
        return true;
    }

    void save() {
        //TODO: Code to save the vis panel
        JOptionPane.showMessageDialog(this, "To be done");
    }

    boolean isModified() {
        //TODO: check if vispanel is modified
        return true;
    }

    public void initializeFromState(VisualizationState visualization) {
        //TODO: load visualization to panel
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    void stopRunningTasks() {
        workPanel.stopRunningTasks();
    }

    void setImageAndCenter(String fitsImage) throws IOException, FitsException {
        workPanel.setImage(fitsImage);
        controlPanel.setEnable(workPanel.getCenter());
    }

    public void toggleGrid(){
      workPanel.toggleGrid();
    }

    public boolean isGridShown() {
        return workPanel.isGridShown();
    }

    void showImageColorsFrame() {
        workPanel.showImageColorsFrame();
    }

    void showImageCutLevelsFrame() {
        workPanel.showImageCutLevelsFrame();
    }

    void showImageExtensionsFrame() {
        workPanel.showImageExtensionsFrame();
    }

    void showImageKeywordsFrame() {
        workPanel.showImageKeywordsFrame();
    }
}
