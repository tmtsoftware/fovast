/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.gui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;

/**
 * This class has several actions which user can perform on the UI (in particular menu
 * actions)
 *
 * Action methods simply call methods from the FovastMainFrame class.
 *
 * @author vivekananda_moosani
 */
public class FovastActions {

    // HELPER CONSTANTS for below enabled/selected variables
    private static final String SAVE_VISUALIZATION_MENU_ENABLED = "saveVisualizationMenuEnabled";

    private static final String CLOSE_VISUALIZATION_MENU_ENABLED = "closeVisualizationMenuEnabled";

    private static final String SHOW_DSS_BACKGROUND_MENU_SELECTED = "showDssBackgroundMenuSelected";

    private static final String SHOW_ANNOTATIONS_MENU_SELECTED = "showAnnotationsMenuSelected";

    // properties which can be set to disable / enable menu components or buttons
    // tied to various action objects
    private boolean saveVisualizationMenuEnabled = false;

    private boolean closeVisualizationMenuEnabled = false;

    private boolean showDssBackgroundMenuSelected = false;

    private boolean showAnnotationsMenuSelected = false;

    private FovastMainFrame mainFrame;

    private ArrayList<PropertyChangeListener> propertyChangeListeners =
            new ArrayList<PropertyChangeListener>();

    public FovastActions(FovastMainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Action(name = "Menu.File")
    public void fileAction() {
        //Dummy action method for JMenu instances
    }

    @Action(name = "Menu.File.NewVisualization")
    public void newVisualizationAction() {
        mainFrame.createNewVisualization();
    }

    @Action(name = "Menu.File.NewVisualizationFromTemplates")
    public void newVisualizationFromTemplateAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.OpenVisualization")
    public void openVisualizationAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.SaveVisualization", enabledProperty = SAVE_VISUALIZATION_MENU_ENABLED)
    public void saveVisualizationAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
        //mainFrame.saveVisualization();
    }

    @Action(name = "Menu.File.CloseVisualization", enabledProperty = CLOSE_VISUALIZATION_MENU_ENABLED)
    public void closeVisualizationAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
        //mainFrame.saveAndCloseVisualization();
    }

    @Action(name = "Menu.File.CloseAllVisualizations", enabledProperty = CLOSE_VISUALIZATION_MENU_ENABLED)
    public void closeAllVisualizationsAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.NewVisualizationGroup")
    public void newVisualizationGroupAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.LoadFitsImage")
    public void loadFitsImageAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.LoadFitsImageFromVoArchives")
    public void loadFitsImageFromVoArchivesAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.LoadFitsImageFromVoArchives.Sdss")
    public void loadFitsImageFromSdssAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.LoadFitsImageFromVoArchives.Dss")
    public void loadFitsImageFromDssAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.LoadFitsImageFromVoArchives.LookupSiaInVORegistries")
    public void loadFitsImageByLookupSiaServicesInVoRegistriesAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.ShowImageList")
    public void showImageListAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.CloseFitsImage")
    public void closeFitsImageAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.CloseAllFitsImages")
    public void closeAllFitsImagesAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.LoadCatalog")
    public void loadCatalogAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.LoadCatalogFromVoArchives")
    public void loadCatalogFromVoArchivesAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.CloseCatalog")
    public void closeCatalogAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.CloseAllCatalogs")
    public void closeAllCatalogsAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.ShowCatalogList")
    public void showCatalogListAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.ProxySettings")
    public void showProxySettingsDialogAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.Preferences")
    public void showPreferencesDialogAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.File.Exit")
    public void applicationExitAction() {
        mainFrame.applicationExitAction();
    }

    @Action(name = "Menu.View")
    public void viewAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.View.DssBackground", selectedProperty = SHOW_DSS_BACKGROUND_MENU_SELECTED)
    public void viewDssBackgroundAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.View.Catalogs")
    public void viewCatalogsAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.View.Images")
    public void viewImagesAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.View.Annotations", selectedProperty = SHOW_ANNOTATIONS_MENU_SELECTED)
    public void viewAnnotationsAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Tabs")
    public void tabsAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Tabs.TabsMenuItem")
    public void tabsAction(ActionEvent ae) {
        mainFrame.selectVisualizationPanel(((JMenuItem) ae.getSource()));
    }

    @Action(name = "Menu.Interop")
    public void interopAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Interop.Samp")
    public void sampAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.LaunchExternalHub")
    public void launchExternalHubAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.LaunchInternalHub")
    public void launchInternalHubAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.StopInternalHub")
    public void stopInternalHubAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.Register")
    public void sampRegisterAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.Unregister")
    public void sampUnregisterAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.SendImage")
    public void sendImageBySampAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.SendCatalog")
    public void sendCataloBySampAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.SampPreferences")
    public void sampPreferencesAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Help")
    public void helpAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Help.UserGuide")
    public void userGuideAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Help.ContextHelp")
    public void contextualHelpAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Help.OnlineHelp")
    public void onlineHelpAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Help.CheckUpdates")
    public void checkUpdatesAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Help.ReportBug")
    public void reportBugAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Help.ViewBugTracker")
    public void viewBugTrackerAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    @Action(name = "Menu.Help.About")
    public void aboutAction() {
        JOptionPane.showMessageDialog(mainFrame, "To be done");
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        propertyChangeListeners.add(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        propertyChangeListeners.remove(pcl);
    }

    public boolean isCloseVisualizationMenuEnabled() {
        return closeVisualizationMenuEnabled;
    }

    public void setCloseVisualizationMenuEnabled(boolean newValue) {
        boolean oldValue = this.closeVisualizationMenuEnabled;
        this.closeVisualizationMenuEnabled = newValue;
        firePropertyChangeEvent(CLOSE_VISUALIZATION_MENU_ENABLED, oldValue,
                newValue);
    }

    public boolean isSaveVisualizationMenuEnabled() {
        return saveVisualizationMenuEnabled;
    }

    public void setSaveVisualizationMenuEnabled(boolean newValue) {
        boolean oldValue = this.saveVisualizationMenuEnabled;
        this.saveVisualizationMenuEnabled = newValue;
        firePropertyChangeEvent(SAVE_VISUALIZATION_MENU_ENABLED, oldValue,
                newValue);
    }

    public boolean isShowAnnotationsMenuSelected() {
        return showAnnotationsMenuSelected;
    }

    public void setShowAnnotationsMenuSelected(boolean newValue) {
        boolean oldValue = this.showAnnotationsMenuSelected;
        this.showAnnotationsMenuSelected = newValue;
        firePropertyChangeEvent(SHOW_ANNOTATIONS_MENU_SELECTED, oldValue, newValue);
    }

    public boolean isShowDssBackgroundMenuSelected() {
        return showDssBackgroundMenuSelected;
    }

    public void setShowDssBackgroundMenuSelected(boolean newValue) {
        boolean oldValue = this.showDssBackgroundMenuSelected;
        this.showDssBackgroundMenuSelected = newValue;
        firePropertyChangeEvent(SHOW_DSS_BACKGROUND_MENU_SELECTED, oldValue, newValue);
    }

    private void firePropertyChangeEvent(String propertyName, Object oldValue,
            Object newValue) {

        if (oldValue == newValue) {
            return;
        }

        for (int i = 0; i < propertyChangeListeners.size(); i++) {
            propertyChangeListeners.get(i).propertyChange(
                    new PropertyChangeEvent(this, propertyName,
                    oldValue, newValue));
        }

    }

}
