/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;
import org.xml.sax.SAXException;

/**
 * This class has several actions which user can perform on the UI (in particular 
 * menu actions)
 *
 * Action methods simply call methods from the FovastmainComponent class.
 *
 * Note that we denote a method as Action method using @Action annotation.
 *
 * Each action annotation has a unique name attribute. (like Menu.File)
 * In the FovastApplication.properties, each action has settings like
 *  Menu.File.Action.text = File
 *  Menu.File.Action.mnemonic = F *  
 *  Menu.File.NewVisualization.Action.accelerator
 * 
 * The fovast action class is parsed by using BSAF API 
 *             ApplicationContext.getActionMap(fovastActions);
 * in FovastMainView.prepareMenuBar() function. For each action an object of
 * ApplicationAction class which implements Swing Action interface is created.
 * The above call returns a swing ActionMap instance with the names of Actions
 * annotations as the keys and corresponding Action objects as values.
 *
 * BSAF Action annotations also support enabled and selected attributes. Check
 * how Menu.File.CloseVisualization action is initialized and check usages of
 * related attributes and constants. 
 *
 */
public class FovastActions {

    // HELPER CONSTANTS for below enabled/selected variables
    private static final String SAVE_VISUALIZATION_MENU_ENABLED = "saveVisualizationMenuEnabled";

    private static final String CLOSE_VISUALIZATION_MENU_ENABLED = "closeVisualizationMenuEnabled";

    private static final String SHOW_DSS_BACKGROUND_MENU_SELECTED = "showDssBackgroundMenuSelected";

    private static final String SHOW_ANNOTATIONS_MENU_SELECTED = "showAnnotationsMenuSelected";

    private static final String SHOW_GRID_MENU_SELECTED = "showGridMenuSelected";

    private static final String SHOW_GRID_MENU_ENABLED = "showGridMenuEnabled";

    private static final String SHOW_CATALOG_MENU_SELECTED = "showCatalogMenuSelected";

    private static final String SHOW_CATALOG_MENU_ENABLED = "showCatalogMenuEnabled";

    private static final String SHOW_IMAGE_COLORS_MENU_ENABLED = "showImageColorsMenuEnabled";

    private static final String SHOW_IMAGE_CUTLEVELS_MENU_ENABLED= "showImageCutLevelsMenuEnabled";

    private static final String SHOW_IMAGE_SCALINGALGORITHM_MENU_ENABLED = "showImageScalingAlgorithmMenuEnabled";

    private static final String SHOW_IMAGE_EXTENSIONS_MENU_ENABLED = "showImageExtensionsMenuEnabled";

    private static final String SHOW_IMAGE_KEYWORDS_MENU_ENABLED = "showImageKeywordsMenuEnabled";

    private static final String MENU_ENABLE_WHEN_NO_VIZ_PROPERTY = "menuEnableWhenNoVisualization";

    private static final String MENU_SELECT_WHEN_NO_VIZ_PROPERTY = "menuSelectWhenNoVisualization";


    // properties which can be set to disable / enable menu components or buttons
    // tied to various action objects.
    // All these variables have getters and setters. To disable all 
    private boolean saveVisualizationMenuEnabled = false;

    private boolean closeVisualizationMenuEnabled = false;

    private boolean showDssBackgroundMenuSelected = false;

    private boolean showAnnotationsMenuSelected = false;

    private boolean showGridMenuSelected = false;

    private boolean showGridMenuEnabled = false;

    private boolean showCatalogMenuSelected = false;

    private boolean showCatalogMenuEnabled = false;

    private boolean showImageColorsMenuEnabled = false;

    private boolean showImageCutLevelsMenuEnabled = false;

    private boolean showImageScalingAlgorithmMenuEnabled = false;

    private boolean showImageExtensionsMenuEnabled = false;

    private boolean showImageKeywordsMenuEnabled = false;

    private boolean menuEnableWhenNoVisualization = false;

    private boolean menuSelectWhenNoVisualization = false;

    private FovastMainView mainView;

    private Component mainComponent;

    private ArrayList<PropertyChangeListener> propertyChangeListeners =
            new ArrayList<PropertyChangeListener>();

    public FovastActions(FovastMainView mainView) {
        this.mainView = mainView;
        this.mainComponent = mainView.getFrame();
    }

    @Action(name = "Menu.File")
    public void fileAction() {
        //Dummy action method for JMenu instances
    }

    @Action(name = "Menu.File.NewVisualization")
    public void newVisualizationAction() {
        mainView.createNewVisualization();
    }

    @Action(name = "Menu.File.NewVisualizationFromTemplates")
    public void newVisualizationFromTemplateAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.OpenVisualization")
    public void openVisualizationAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.SaveVisualization", enabledProperty = SAVE_VISUALIZATION_MENU_ENABLED)
    public void saveVisualizationAction() {
        mainView.saveVisualization();
    }

    @Action(name = "Menu.File.CloseVisualization", enabledProperty = CLOSE_VISUALIZATION_MENU_ENABLED)
    public void closeVisualizationAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
        //mainComponent.saveAndCloseVisualization();
    }

    @Action(name = "Menu.File.CloseAllVisualizations", enabledProperty = CLOSE_VISUALIZATION_MENU_ENABLED)
    public void closeAllVisualizationsAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.NewVisualizationGroup")
    public void newVisualizationGroupAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.LoadFitsImage")
    public void loadFitsImageAction() {
        mainView.createNewVisualizationFromImageFile(true);
    }

    @Action(name = "Menu.File.LoadFitsImageIntoActiveVisualization")
    public void loadFitsImageActionIntoActiveVisualization() {
        mainView.createNewVisualizationFromImageFile(false);
    }

    @Action(name = "Menu.File.LoadFitsImageFromVoArchives")
    public void loadFitsImageFromVoArchivesAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.LoadFitsImageFromVoArchives.Sdss")
    public void loadFitsImageFromSdssAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.LoadFitsImageFromVoArchives.Dss")
    public void loadFitsImageFromDssAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.LoadFitsImageFromVoArchives.LookupSiaInVORegistries")
    public void loadFitsImageByLookupSiaServicesInVoRegistriesAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.ShowImageList")
    public void showImageListAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.CloseFitsImage")
    public void closeFitsImageAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.CloseAllFitsImages")
    public void closeAllFitsImagesAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.LoadCatalog", enabledProperty = MENU_ENABLE_WHEN_NO_VIZ_PROPERTY)
    public void loadCatalogAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

//    @Action(name = "Menu.File.LoadCatalogFromVoArchives")
//    public void loadCatalogFromVoArchivesAction() {
//        JOptionPane.showMessageDialog(mainComponent, "To be done");
//    }

    @Action(name = "Menu.File.LoadCatalogFrom", enabledProperty = MENU_ENABLE_WHEN_NO_VIZ_PROPERTY)
    public void loadCatalogFromAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.LoadCatalogFrom.GSC2", enabledProperty = MENU_ENABLE_WHEN_NO_VIZ_PROPERTY)
    public void loadCatalogFromGSC2Action() throws MalformedURLException, SAXException, IOException{
        //mainView.loadCatalog("http://gsss.stsci.edu/webservices/vo/ConeSearch.aspx?CAT=GSC23","GSC2");
        mainView.loadCatalog("http://archive.eso.org/skycat/servers/gsc2query?","GSC2");
    }

    @Action(name = "Menu.File.LoadCatalogFrom.2MassPsc", enabledProperty = MENU_ENABLE_WHEN_NO_VIZ_PROPERTY)
    public void loadCatalogFrom2MassPscAction() throws MalformedURLException, SAXException, IOException {     
       // mainView.loadCatalog("http://irsa.ipac.caltech.edu/cgi-bin/Oasis/CatSearch/nph-catsearch?CAT=fp_psc","2MassPsc");
         mainView.loadCatalog("http://irsa.ipac.caltech.edu/cgi-bin/Oasis/CatSearch/nph-catsearch?CAT=fp_psc","2MassPsc");
       // mainView.loadCatalog("http://gsss.stsci.edu/webservices/vo/ConeSearch.aspx?CAT=GSC23","2MassPsc");
    }

    @Action(name = "Menu.File.LoadCatalogFrom.USNO", enabledProperty = MENU_ENABLE_WHEN_NO_VIZ_PROPERTY)
    public void loadCatalogFromUSNOAction() throws MalformedURLException, SAXException, IOException {      
       // mainView.loadCatalog("http://www.nofs.navy.mil/cgi-bin/vo_cone.cgi?CAT=USNO-B1","USNO");
        mainView.loadCatalog("http://archive.eso.org/skycat/servers/usnoa-server?","USNO");
    }

    @Action(name = "Menu.File.LoadCatalogFrom.OtherVoCss")
    public void loadCatalogFromOtherVoCssAction() throws MalformedURLException, SAXException, IOException {
        JOptionPane.showMessageDialog(mainComponent, "To be done");     
    }

    @Action(name = "Menu.File.CloseCatalog", enabledProperty = MENU_ENABLE_WHEN_NO_VIZ_PROPERTY)
    public void closeCatalogAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.CloseCatalog.Select")
    public void closeCatalogAction(ActionEvent ae) {
        mainView.remove((JMenuItem)ae.getSource());
    }

    @Action(name = "Menu.File.CloseAllCatalogs", enabledProperty = MENU_ENABLE_WHEN_NO_VIZ_PROPERTY)
    public void closeAllCatalogsAction() {
        mainView.removeAll();
    }

    @Action(name = "Menu.File.ShowCatalogList")
    public void showCatalogListAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.File.ProxySettings")
    public void showProxySettingsDialogAction() {
        mainView.showProxySettingsDialog();
    }

    @Action(name = "Menu.File.Preferences")
    public void showPreferencesDialogAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
        //mainView.showPreferencesDialog();
    }

    @Action(name = "Menu.File.Exit")
    public void applicationExitAction() {
        mainView.applicationExitAction();
    }

    @Action(name = "Menu.View")
    public void viewAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.View.DssBackground", selectedProperty = SHOW_DSS_BACKGROUND_MENU_SELECTED)
    public void viewDssBackgroundAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.View.Catalogs", enabledProperty = MENU_ENABLE_WHEN_NO_VIZ_PROPERTY)
    public void viewCatalogsAction() {
        //Dummy action method for JMenu instances
    }

    @Action(name = "Menu.View.Catalogs.Show/Hide")
    public void viewCatalogAction(ActionEvent ae) {
       mainView.showHide(((JCheckBoxMenuItem) ae.getSource()));
    }

    @Action(name = "Menu.View.Images")
    public void viewImagesAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.View.Annotations", selectedProperty = SHOW_ANNOTATIONS_MENU_SELECTED)
    public void viewAnnotationsAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.View.ShowGrid", enabledProperty = SHOW_GRID_MENU_ENABLED,
        selectedProperty = SHOW_GRID_MENU_SELECTED)
    public void viewShowGridAction() {
        mainView.toggleGrid();
    }

    @Action(name = "Menu.View.ImageColors", enabledProperty = SHOW_IMAGE_COLORS_MENU_ENABLED)
    public void viewImageColorsAction() {
        mainView.showImageColorsFrame();
    }

    @Action(name = "Menu.View.ImageCutLevels", enabledProperty = SHOW_IMAGE_CUTLEVELS_MENU_ENABLED)
    public void viewImageCutLevelsAction() {
        mainView.showImageCutLevelsFrame();
    }

    @Action(name = "Menu.View.ImageExtensions", enabledProperty = SHOW_IMAGE_EXTENSIONS_MENU_ENABLED)
    public void viewImageExtensionsAction() {
        mainView.showImageExtensionsFrame();
    }

    @Action(name = "Menu.View.ImageKeywords", enabledProperty = SHOW_IMAGE_KEYWORDS_MENU_ENABLED)
    public void viewImageKeywordsAction() {
        mainView.showImageKeywordsFrame();
    }

    @Action(name = "Menu.Tabs")
    public void tabsAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Tabs.TabsMenuItem")
    public void tabsAction(ActionEvent ae) {
        mainView.selectVisualizationPanel(((JMenuItem) ae.getSource()));
    }

    @Action(name = "Menu.Interop")
    public void interopAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Interop.Samp")
    public void sampAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.LaunchExternalHub")
    public void launchExternalHubAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.LaunchInternalHub")
    public void launchInternalHubAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.StopInternalHub")
    public void stopInternalHubAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.Register")
    public void sampRegisterAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.Unregister")
    public void sampUnregisterAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.SendImage")
    public void sendImageBySampAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.SendCatalog")
    public void sendCataloBySampAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Interop.Samp.SampPreferences")
    public void sampPreferencesAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Help")
    public void helpAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Help.UserGuide")
    public void userGuideAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Help.ContextHelp")
    public void contextualHelpAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Help.OnlineHelp")
    public void onlineHelpAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Help.CheckUpdates")
    public void checkUpdatesAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Help.ReportBug")
    public void reportBugAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Help.ViewBugTracker")
    public void viewBugTrackerAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
    }

    @Action(name = "Menu.Help.About")
    public void aboutAction() {
        JOptionPane.showMessageDialog(mainComponent, "To be done");
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

    public boolean isShowGridMenuEnabled() {
        return showGridMenuEnabled;
    }

    public void setShowGridMenuEnabled(boolean newValue) {
        boolean oldValue = this.showGridMenuEnabled;
        this.showGridMenuEnabled = newValue;
        firePropertyChangeEvent(SHOW_GRID_MENU_ENABLED, oldValue, newValue);

    }

    public boolean isShowGridMenuSelected() {
        return showGridMenuSelected;
    }

    public void setShowCatalogMenuEnabled(boolean newValue) {
        boolean oldValue = this.showCatalogMenuEnabled;
        this.showCatalogMenuEnabled = newValue;
        firePropertyChangeEvent(SHOW_CATALOG_MENU_ENABLED, oldValue, newValue);

    }

    public boolean isShowCatalogMenuSelected() {
        return showCatalogMenuSelected;
    }

    public void setShowGridMenuSelected(boolean newValue) {
        boolean oldValue = this.showGridMenuSelected;
        this.showGridMenuSelected = newValue;
        firePropertyChangeEvent(SHOW_GRID_MENU_SELECTED, oldValue, newValue);
    }

    public boolean isShowImageColorsMenuEnabled() {
        return showImageColorsMenuEnabled;
    }

    public void setShowImageColorsMenuEnabled(boolean newValue) {
        boolean oldValue = this.showImageColorsMenuEnabled;
        this.showImageColorsMenuEnabled = newValue;
        firePropertyChangeEvent(SHOW_IMAGE_COLORS_MENU_ENABLED, oldValue, newValue);
    }

    public boolean isShowImageCutLevelsMenuEnabled() {
        return showImageCutLevelsMenuEnabled;
    }

    public void setShowImageCutLevelsMenuEnabled(boolean newValue) {
        boolean oldValue = this.showImageCutLevelsMenuEnabled;
        this.showImageCutLevelsMenuEnabled = newValue;
        firePropertyChangeEvent(SHOW_IMAGE_CUTLEVELS_MENU_ENABLED, oldValue, newValue);
    }

    public boolean isShowImageExtensionsMenuEnabled() {
        return showImageExtensionsMenuEnabled;
    }

    public void setShowImageExtensionsMenuEnabled(boolean newValue) {
        boolean oldValue = this.showImageExtensionsMenuEnabled;
        this.showImageExtensionsMenuEnabled = newValue;
        firePropertyChangeEvent(SHOW_IMAGE_EXTENSIONS_MENU_ENABLED, oldValue, newValue);
    }

    public boolean isShowImageKeywordsMenuEnabled() {
        return showImageKeywordsMenuEnabled;
    }

    public void setShowImageKeywordsMenuEnabled(boolean newValue) {
        boolean oldValue = this.showImageKeywordsMenuEnabled;
        this.showImageKeywordsMenuEnabled = newValue;
        firePropertyChangeEvent(SHOW_IMAGE_KEYWORDS_MENU_ENABLED, oldValue, newValue);
    }



    public boolean isShowDssBackgroundMenuSelected() {
        return showDssBackgroundMenuSelected;
    }

    public void setShowDssBackgroundMenuSelected(boolean newValue) {
        boolean oldValue = this.showDssBackgroundMenuSelected;
        this.showDssBackgroundMenuSelected = newValue;
        firePropertyChangeEvent(SHOW_DSS_BACKGROUND_MENU_SELECTED, oldValue, newValue);
    }

    public boolean isMenuEnableWhenNoVisualization() {
        return menuEnableWhenNoVisualization;
    }

    public void setMenuEnableWhenNoVisualization(boolean newValue) {
        boolean oldValue = this.menuEnableWhenNoVisualization;
        this.menuEnableWhenNoVisualization = newValue;
        firePropertyChangeEvent(MENU_ENABLE_WHEN_NO_VIZ_PROPERTY, oldValue, newValue);
    }

    public boolean isMenuSelectWhenNoVisualization() {
        return menuSelectWhenNoVisualization;
    }

    public void setMenuSelectWhenNoVisualization(boolean newValue) {
        boolean oldValue = this.menuSelectWhenNoVisualization;
        this.menuSelectWhenNoVisualization = newValue;
        firePropertyChangeEvent(MENU_SELECT_WHEN_NO_VIZ_PROPERTY, oldValue, newValue);
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
