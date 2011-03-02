/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */
package org.tmt.fovast.gui;

import java.awt.BorderLayout;
import java.awt.geom.Point2D;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import nom.tam.fits.FitsException;
import org.jdesktop.application.ApplicationContext;
import org.tmt.fovast.state.VisualizationState;

/**
 *
 */
public class VisualizationPanel extends JPanel {

    private ApplicationContext appContext;

    private VisualizationState visualization;
    
    private VisualizationControlPanel controlPanel;

    private VisualizationWorkPanel workPanel;

    public void setWorkPanel (VisualizationWorkPanel workPanel)
    {
        this.workPanel=workPanel;
    }

    public VisualizationPanel(ApplicationContext appContext,
            VisualizationState visualization) {
        this.appContext = appContext;
        this.visualization = visualization;

        initComponents();

        //TODO: If the visualization is an old one (saved one)
        //we have to setup workpanel and control panel appropriately
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        controlPanel = new VisualizationControlPanel(appContext, visualization);
        workPanel = new VisualizationWorkPanel(appContext, visualization);
        workPanel.addVisualizationWorkPanelListener(controlPanel);

        splitPane.setLeftComponent(controlPanel);
        splitPane.setRightComponent(workPanel);
        add(splitPane);
    }

    //
    // TODO: UNUSED METHODS .. STILL IN TODO STATE
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

    void stopRunningTasks() {
        workPanel.stopRunningTasks();
    }

    void setImageAndCenter(String fitsImage) throws IOException, FitsException {
        workPanel.setImage(fitsImage);
//        //these invoke laters is an artifact of the swing utilities
//        //to setImage on ImageDisplay
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        Point2D.Double center = workPanel.getCenter();
//                        controller.setTarget(center.x, center.y);
//                        //controller.vslShowTarget(true);
//                        controlPanel.setCenter(center);
//                    }
//                });
//            }
//        });
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
