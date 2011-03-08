/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.gui;

import java.io.IOException;
import javax.swing.event.ChangeEvent;
import nom.tam.fits.FitsException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.io.File;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.application.ApplicationAction;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.gui.VisualizationPanel.CatalogListener;
import org.tmt.fovast.instrumentconfig.ConfigHelper;
import org.tmt.fovast.state.FovastApplicationState;
import org.tmt.fovast.state.VisualizationState;
import org.tmt.fovast.swing.utils.ExtendedTabbedPane;
import org.tmt.fovast.swing.utils.StatusBar;
import org.tmt.fovast.util.AppConfiguration;
import org.tmt.fovast.util.Cache;
import org.xml.sax.SAXException;
import voi.swing.util.ProxySettingsDialog;

/**
 *
 */
public class FovastMainView extends FrameView
        implements FovastApplicationState.FovastApplicationStateListener, CatalogListener {

    private static final Logger logger = LoggerFactory.getLogger(FovastMainView.class);

    private static final String baseKey = FovastMainView.class.getName();

    private static final String keyPrefix = baseKey + ".";

    private static final String UNSAVED_VIS_PANEL_PREFIX_KEY =
            keyPrefix + "UnsavedVisualizationPanelPrefix";

    private static final String TAB_SAVE_MESSAGE_KEY =
            keyPrefix + "TabSaveMessage";

    private static final String TAB_CLOSE_MESSAGE_KEY =
            keyPrefix + "TabCloseMessage";

    private static final String TABS_MENU = "Menu.Tabs";

    private static final String TABS_MENUITEM = "Menu.Tabs.TabsMenuItem";

    private static final String CATALOG_MENU = "Menu.View.Catalogs";

    private static final String CATALOG_MENUITEM = "Menu.View.Catalogs.Show/Hide";

    private static final String CATALOG_MENU_CLOSE = "Menu.File.CloseCatalog";

    private static final String CATALOG_MENUITEM_CLOSE = "Menu.File.CloseCatalog.Select";

    private static final String TABS_MENUITEM_VIZID_CLIENTPROPERTY = "Viz.Id";

    private static final String CATALOG_CLIENTPROPERTY ="Catalogs";

    private static final String MENU_XML_FILE = "resources/menu.xml";

    private static final String MENU_XML_NAME_ATTRIBUTE = "name";

    private static final String MENU_XML_TYPE_ATTRIBUTE = "type";

    private static final String MENU_XML_IMPLSTATE_ATTRIBUTE = "implState";

    private static final String MENU_XML_NAME_ATTRIBUTE_VALUE_MENU = "Menu";

    private static final String MENU_XML_NAME_ATTRIBUTE_VALUE_MENUITEM = "MenuItem";

    private static final String MENU_XML_NAME_ATTRIBUTE_VALUE_SEPARATOR = "Separator";

    private static final String MENU_XML_TYPE_ATTRIBUTE_VALUE_CHECKBOX = "checkbox";

    private static final String MENU_XML_TYPE_ATTRIBUTE_VALUE_RADIO = "radio";

    private static final String MENU_XML_TYPE_ATTRIBUTE_VALUE_DEFAULT = "menuitem";

    private static final String MENU_XML_IMPLSTATE_ATTRIBUTE_VALUE_TODO = "todo";

    public static final String[] FITS_EXTENSIONS = {".fits",".fit"};

    public static final String FITS_EXTENSIONS_DESC = "FITS Files (*.fits, *.fit)";

    private ApplicationContext appContext;

    private ResourceMap resourceMap;

    private FovastApplicationState fovastState;

    //Id given to the visualization in this state
    private int newVisualizationId = 0;

    private JMenuBar menuBar = new JMenuBar();

    private ActionMap menuActions;

    private JToolBar toolBar = new JToolBar();

    private ExtendedTabbedPane centerTabbedPane = new ExtendedTabbedPane() {

        @Override
        protected boolean removeTabOrNot(int index) {
            //TODO: Also do the same logic when closing the visualization using
            //main menu and context menu in explorer panel
            return FovastMainView.this.closeVisPanel(index);
        }

    };

    private StatusBar statusBar;

    private ArrayList<JComponent> tabComponentList = new ArrayList<JComponent>();

    private JMenu tabsMenu;

    private JMenu catalogMenu;

    private JMenu catalogCloseMenu;

    private JMenuItem selectedMenuItem;

    private FovastActions fovastActions;
    
    private int openCount = 0;

    public FovastMainView() {
        this(new FovastApplicationState());
    }

    public FovastMainView(FovastApplicationState fovastState) {
        super(FovastApplication.getApplication());
        this.appContext = FovastApplication.getApplication().getContext();
        this.resourceMap = appContext.getResourceMap(FovastMainView.class);

        this.fovastState = fovastState;

        initComponents();

        fovastState.addListener(this);
    }

    private void initComponents() {

        JPanel contentPane = new JPanel();

        // Set layout
        BorderLayout layout = new BorderLayout();
        contentPane.setLayout(layout);

        //set menu
        try {
            prepareMenuBar();
        } catch (Exception ex) {
            logger.error("Closing app due to menu creation failure... ", ex);
            System.exit(FovastApplication.ERROR_RETURN_CODE);
        }
        setMenuBar(menuBar);

        //prepare toolbar
        prepareToolbar();
        contentPane.add(toolBar, BorderLayout.NORTH);

        //Prepare tabbed center window
        //TODO: Explorer panel to come in later
        //centerTabbedPane.addTab(resourceMap.getString(keyPrefix + "ExplorerTabTitle"),
        //        new ExplorerPanel(appContext));

        centerTabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        centerTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                //Need to invoke this in listener code to make sure
                //selection change event happens after tab and menu are added
                //
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JComponent comp =
                            ((JComponent) centerTabbedPane.getSelectedComponent());
                        //TODO: We need to worry about explorer panel if it comes into
                        //picture .. as selection of explorer panel .. should
                        //also remove any highlighting in Tabs menu .. should rise unselectVis
                        //on controller in this case
                        if (comp instanceof VisualizationPanel) {
                            Integer vizId =
                                    (Integer) comp.getClientProperty(TABS_MENUITEM_VIZID_CLIENTPROPERTY);
                            fovastState.selectVisualizationById(vizId);
                            updateUIForVisualizationSelected(vizId);
                        }
                    }
                });
            }

        });
        //centerTabbedPane.setMaximumSize(new Dimension(800, 800));
        contentPane.add(centerTabbedPane, BorderLayout.CENTER);


        //add statusbar
        prepareStatusBar();
        setStatusBar(statusBar);

        contentPane.setPreferredSize(new Dimension(600, 600));
        setComponent(contentPane);

    }

    void loadCatalog(String url,String source) throws MalformedURLException, SAXException, IOException{
        VisualizationPanel vis = getActiveVisPanel();
        if(vis.isImageLoaded()){
            Point2D.Double center=vis.getCenter();
            Cache cache = ((FovastApplication) appContext.getApplication()).getDssImageCache();
            ConeSearchDialog csd = new ConeSearchDialog(url.trim(),
              center.x,center.y,2,vis,this,source,cache);
        }
        else{
            JOptionPane.showMessageDialog(vis,"Load an image to load a catalog");
        }
    }

    void showHide(JCheckBoxMenuItem menuItem){
        VisualizationPanel vis = getActiveVisPanel();
        //Set<Catalog> catalogs=vis.getCatalogList();
        boolean state = menuItem.isSelected();
        Catalog c = (Catalog)menuItem.getClientProperty(CATALOG_CLIENTPROPERTY);
        vis.showHide(c,state);
    }

    void remove(JMenuItem menuItem){        
        VisualizationPanel vis = getActiveVisPanel();
        //Set<Catalog> catalogs=vis.getCatalogList();
        Catalog c = (Catalog)menuItem.getClientProperty(CATALOG_CLIENTPROPERTY);
        vis.remove(c);
    }

    private void prepareMenuBar() throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document document =
                builder.build(FovastMainView.class.getResource(
                MENU_XML_FILE));
        fovastActions = new FovastActions(this);
        menuActions =
                appContext.getActionMap(fovastActions);


        List<Element> children = document.getRootElement().getChildren();
        for (int i = 0; i < children.size(); i++) {
            Element child = children.get(i);
            JComponent menu = prepareMenu(child);
            menuBar.add(menu);
        }
    }

    private JComponent prepareMenu(Element menuElement) throws Exception {


        String elementName = menuElement.getName();

        String nameAttributeValue = menuElement.getAttributeValue(MENU_XML_NAME_ATTRIBUTE);

        String typeAttributeValue = menuElement.getAttributeValue(MENU_XML_TYPE_ATTRIBUTE);
        if (typeAttributeValue == null) {
            typeAttributeValue = MENU_XML_TYPE_ATTRIBUTE_VALUE_DEFAULT;
        }

        String implStateAttributeValue = menuElement.getAttributeValue(MENU_XML_IMPLSTATE_ATTRIBUTE);
        boolean todo = false;
        if (implStateAttributeValue != null &&
                implStateAttributeValue.equals(MENU_XML_IMPLSTATE_ATTRIBUTE_VALUE_TODO)) {
            todo = true;
        }

        //TODO: set text, icon, action-method, tooltip, accelerator properly
        if (elementName.equals(MENU_XML_NAME_ATTRIBUTE_VALUE_MENU)) {
            JMenu menu = new JMenu();
            menu.setName(nameAttributeValue);
            if (nameAttributeValue.equals(TABS_MENU)) {
                tabsMenu = menu;
            }
            if(nameAttributeValue.equals(CATALOG_MENU)){
                catalogMenu = menu;
            }
            if(nameAttributeValue.equals(CATALOG_MENU_CLOSE)){
                catalogCloseMenu = menu;
            }
            if (todo) {
                menu.setForeground(Color.BLUE);
            }
            menu.setText(nameAttributeValue);
            if (menuActions.get(nameAttributeValue) != null) {
                menu.setAction(menuActions.get(nameAttributeValue));
            }

            List<Element> children = menuElement.getChildren();
            for (int i = 0; i < children.size(); i++) {
                Element child = children.get(i);
                JComponent menuComponent = prepareMenu(child);
                menu.add(menuComponent);
            }

            return menu;
        } else if (elementName.equals(MENU_XML_NAME_ATTRIBUTE_VALUE_MENUITEM)) {
            JMenuItem menuItem = null;
            if (typeAttributeValue.equals(MENU_XML_TYPE_ATTRIBUTE_VALUE_CHECKBOX)) {
                menuItem = new JCheckBoxMenuItem();
            } else if (typeAttributeValue.equals(MENU_XML_TYPE_ATTRIBUTE_VALUE_RADIO)) {
                menuItem = new JRadioButtonMenuItem();
            } else {
                menuItem = new JMenuItem();
            }

            menuItem.setText(nameAttributeValue);
            if (todo) {
                menuItem.setForeground(Color.BLUE);
            }
            ApplicationAction action = (ApplicationAction) menuActions.get(nameAttributeValue);
            if (action != null) {
                menuItem.setAction(action);
            }

            return menuItem;
        } else if (elementName.equals(MENU_XML_NAME_ATTRIBUTE_VALUE_SEPARATOR)) {
            return new JSeparator();
        } else {
            logger.error("Unknown element type: " + menuElement.toString());
            throw new Exception("Unknown element type in menu.xml");
        }
    }

    private void prepareToolbar() {
        JButton button = new JButton(" .. TBD .. ");
        button.setPreferredSize(new Dimension(200, 30));
        button.setEnabled(false);
        toolBar.add(button);
        //TODO: prepare toolbar
    }

    public void prepareStatusBar() {
        statusBar = new StatusBar();
    }

    void createNewVisualization(File imageFile) {

        //Decide name for the visualization
        //TODO: As of now we check the open visualizations
        //and accordingly decide the name
        //If we will be automatically storing the visualizations at a specified
        //place under user home as gemini OT does .. then we have to search all
        //visualizations saved in that dir and not just the ones which are open.
        ArrayList<VisualizationState> visualizations =
                fovastState.getVisualizations();
        String newVisPanelPrefix = appContext.getResourceMap().getString(
                UNSAVED_VIS_PANEL_PREFIX_KEY);
        String prefixToCheck = newVisPanelPrefix;
        int ct = 0;
        boolean prefixCheckFlag = false;
        do {
            int i = 0;
            for (; i < visualizations.size(); i++) {
                if (fovastState.getVisualizationFileName(
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
        fovastState.addVisualization(visualization, newVisualizationId++,
                prefixToCheck);
        int vizId = fovastState.getVisualizationId(visualization);
        updateUIForVisualizationAdded(visualization, vizId,
                fovastState.getVisualizationFileName(visualization));
        
        //load image and set target to image center
        VisualizationPanel vPanel = getVisualizationPanel(vizId);
        if(imageFile != null) {
            try {
                vPanel.setImageAndCenter(imageFile.getAbsolutePath());
            } catch (IOException ex) {
                logger.error("Could not load image", ex);
                JOptionPane.showMessageDialog(vPanel,
                                    "DSS image could not be loaded");
            } catch (FitsException ex) {
                logger.error("Could not load image", ex);
                 JOptionPane.showMessageDialog(vPanel,
                                    "DSS image could not be loaded");
            }
        }


        //we donot return anything back to the view that calls the method ..
        //view should have registered as listener to the dispatcher (which is controller in fovast)
        //return visualization;
    }

    void removeForNewFile(){
        VisualizationPanel vis = getActiveVisPanel();
        for(int i = 0 ;i < catalogCloseMenu.getItemCount() ;i++){
            JMenuItem menuItem = catalogCloseMenu.getItem(i);
            Catalog c = (Catalog)menuItem.getClientProperty(CATALOG_CLIENTPROPERTY);
            vis.remove(c);
        }
        catalogMenu.removeAll();
        catalogCloseMenu.removeAll();
    }
    void createNewVisualizationFromImageFile(boolean newPanel) {
        //TODO: Use filters  (*.fits, *.fit, All files)              
        AppConfiguration config =
                FovastApplication.getApplication().getConfiguration();
        String dirToOpen = config.getFileDialogDirProperty();
        JFileChooser fc = new JFileChooser(dirToOpen);
        FileFilter filter = new CustomFilter();
        fc.addChoosableFileFilter(filter);
        fc.setFileFilter(filter);
        int retVal = fc.showOpenDialog(getFrame());
        if(retVal == JFileChooser.APPROVE_OPTION) {
            try {
                config.setFileDialogDirProperty(fc.getSelectedFile().getParent());
                if(openCount == 0 || newPanel) {
                    createNewVisualization(fc.getSelectedFile());
                }
                else {
                    //this creates a visualization object and updates the app-model with it
                    getActiveVisPanel().setImageAndCenter(fc.getSelectedFile().getAbsolutePath());
                    removeForNewFile();
                }
                //controller.createNewVisualization(fc.getSelectedFile());
            } catch (Exception ex) {
                logger.error("Could not load image", ex);
            }
        }
    }

    private boolean closeVisPanel(int index) {
        JComponent comp = tabComponentList.get(index);
        if(comp instanceof VisualizationPanel) {
            int vizId = (Integer)comp.getClientProperty(TABS_MENUITEM_VIZID_CLIENTPROPERTY);
            fovastState.removeVisualization(vizId);
            updateUIForVisualizationRemoved(vizId);
        }
        //Note that close happens in updateUIForVisualizationRemoved() call
        //So this should always return false.
        return false;
//TODO: implement properly .. called on any tab close
//        VisualizationPanel visPanel = visPanelList.get(index);
//        if(visPanel.isModified()) {
//            int closeOrNot = JOptionPane.showConfirmDialog(this,
//                    resourceMap.getString(TAB_SAVE_MESSAGE_KEY));
//            if(closeOrNot == JOptionPane.YES_OPTION) {
//                visPanel.save();
//            } else if(closeOrNot == JOptionPane.NO_OPTION) {
//                return true;
//            } else {
//                //dont close
//                return false;
//            }
//        }
//        else {
//            int closeOrNot = JOptionPane.showConfirmDialog(this,
//                    resourceMap.getString(TAB_CLOSE_MESSAGE_KEY),
//                    null, JOptionPane.YES_NO_OPTION);
//            if(closeOrNot == JOptionPane.YES_OPTION) {
//                visPanel.save();
//            } else if(closeOrNot == JOptionPane.NO_OPTION) {
//                return false;
//            }
//        }
//        visPanelList.remove(visPanel);
//        return true;
    }

    /**
     * Saves the active visualization
     */
    public void saveVisualization() {
        //How to do this .. 
//        VisualizationPanel visPanel = getActiveVisPanel();
//        visPanel.save();
    }

    /**
     * Closes the active visualization after a save
     *
     * @return
     */
    public void saveAndCloseVisualization() {
        //TODO: How to tackle this ?? 
//        int selIndex = getActiveVisPanelIndex();
//        if(closeVisPanel(selIndex))
//                centerTabbedPane.removeTabAt(selIndex);
    }

    private VisualizationPanel getActiveVisPanel() {
        int selIndex = centerTabbedPane.getSelectedIndex();
        if (selIndex != -1) {
            JComponent comp = tabComponentList.get(selIndex);
            if (comp instanceof VisualizationPanel) {
                return (VisualizationPanel) comp;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    void applicationExitAction() {
        //controller.applicationExitAction();
        FovastApplication.getApplication().exit();
    }

    void toggleGrid(){
        VisualizationPanel visPanel = getActiveVisPanel();
        visPanel.toggleGrid();
//        for (int i = 0; i < tabComponentList.size();i++) {
//            Component comp = tabComponentList.get(i);
//            if (comp instanceof VisualizationPanel) {
//                ((VisualizationPanel)comp).toggleGrid();
//            }
//        }
    }


    void selectVisualizationPanel(JMenuItem menuItem) {
        JMenuItem oldSelectedMenuItem = selectedMenuItem;
        if (oldSelectedMenuItem == menuItem) {
            return;
        }

        int visualizationId = (Integer) menuItem.getClientProperty(
                TABS_MENUITEM_VIZID_CLIENTPROPERTY);
        fovastState.selectVisualizationById(visualizationId);
        updateUIForVisualizationSelected(visualizationId);
    }

    @Override
    public void fasVisualizationAdded(VisualizationState visualization,
            int vizId, String fileName) {
        updateUIForVisualizationAdded(visualization, vizId, fileName);
    }

    @Override
    public void fasVisualizationRemoved(int vizId) {
        updateUIForVisualizationRemoved(vizId);
    }

    @Override
    public void fasVisualizationSelected(int vizId) {
        updateUIForVisualizationSelected(vizId);
    }            

    private void updateUIForVisualizationSelected(int selectedVizId) {

        VisualizationPanel activePanel = null;

        //update selection on tabbed pane
        Component tabContent = centerTabbedPane.getSelectedComponent();
        if (tabContent instanceof VisualizationPanel &&
                ((VisualizationPanel) tabContent).getClientProperty(
                TABS_MENUITEM_VIZID_CLIENTPROPERTY).equals(selectedVizId)) {
            //already selected
            activePanel = (VisualizationPanel)tabContent;
            //nothing to be done
        } else {
            for (int i = 0; i < centerTabbedPane.getTabCount(); i++) {
                JComponent comp = tabComponentList.get(i);
                if (comp.getClientProperty(
                        TABS_MENUITEM_VIZID_CLIENTPROPERTY).equals(selectedVizId)) {
                    centerTabbedPane.setSelectedComponent(comp);
                    activePanel = (VisualizationPanel) comp;
                    break;
                }
            }
        }

        //update tabs menu ..
        JMenuItem oldMenuItem = selectedMenuItem;
        JMenuItem newMenuItem = null;
        for (int i = 0; i < tabsMenu.getMenuComponentCount(); i++) {
            Component comp = tabsMenu.getMenuComponent(i);
            if (comp instanceof JMenuItem) {
                if (((JMenuItem) comp).getClientProperty(
                        TABS_MENUITEM_VIZID_CLIENTPROPERTY).equals(selectedVizId)) {
                    newMenuItem = (JMenuItem) comp;
                    break;
                }
            }
        }
        if (!newMenuItem.equals(oldMenuItem)) {
            //TODO: Can we do this out of code
            Font oldFont = newMenuItem.getFont();
            newMenuItem.setFont(new Font(oldFont.getName(), Font.ITALIC | Font.BOLD,
                    oldFont.getSize()));

            if (oldMenuItem != null) {
                oldMenuItem.setFont(oldFont);
            }

            selectedMenuItem = newMenuItem;
        }


        if(activePanel != null) {
            //select deselect grid menu
            if((activePanel).isGridShown()) {
                fovastActions.setShowGridMenuSelected(true);
            }
            else {
                fovastActions.setShowGridMenuSelected(false);
            }

            //enable disable other menus ..
            //activePanel.getMenuItems();

            //add menu item to catalog menu
            //TODO: On save and rename this menuitem text has to be updated ..
            catalogMenu.removeAll();
            catalogCloseMenu.removeAll();
            Set<Catalog> catalogs=activePanel.getCatalogList();
            Iterator iter = catalogs.iterator();
            int i=0;
            
            while(iter.hasNext())
            {
                Catalog c = (Catalog)iter.next();
                JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem();
                menuItem.setSelected(true);
                menuItem.setAction(menuActions.get(CATALOG_MENUITEM));
                menuItem.putClientProperty(CATALOG_CLIENTPROPERTY,c);
                menuItem.setText(c.getLabel());
                catalogMenu.add(menuItem);
                JMenuItem menuItem1 = new JMenuItem();
                menuItem1.setSelected(true);
                menuItem1.putClientProperty(CATALOG_CLIENTPROPERTY,c);
                menuItem1.setAction(menuActions.get(CATALOG_MENUITEM_CLOSE));
                menuItem1.setText(c.getLabel());
                catalogCloseMenu.add(menuItem1);
            }
        }

//        if(activePanel != null) {
//            catalogCloseMenu.removeAll();
//            Set<Catalog> catalogs=activePanel.getCatalogList();
//            Iterator iter = catalogs.iterator();
//            while(iter.hasNext())
//            {
//                Catalog c = (Catalog)iter.next();
//                JMenuItem menuItem1 = new JMenuItem(c.getLabel());
//                menuItem1.setSelected(true);
//                menuItem1.setAction(menuActions.get(CATALOG_MENUITEM_CLOSE));
//                catalogCloseMenu.add(menuItem1);
//            }
//        }
    }

    private void updateUIForVisualizationRemoved(int removedVizId) {
        VisualizationPanel vPanelRemoved = null;

        for (int i = 0; i < centerTabbedPane.getTabCount(); i++) {
            JComponent comp = tabComponentList.get(i);
            if (comp.getClientProperty(
                    TABS_MENUITEM_VIZID_CLIENTPROPERTY).equals(removedVizId)) {
                centerTabbedPane.removeTabAt(i);
                vPanelRemoved = (VisualizationPanel) tabComponentList.remove(i);
                break;
            }
        }
        for (int i = 0; i < tabsMenu.getMenuComponentCount(); i++) {
            Component comp = tabsMenu.getMenuComponent(i);
            if (comp instanceof JMenuItem) {
                if (((JMenuItem) comp).getClientProperty(
                        TABS_MENUITEM_VIZID_CLIENTPROPERTY).equals(removedVizId)) {
                    tabsMenu.remove(comp);
                    break;
                }
            }
        }
        
        if(vPanelRemoved != null) {
            //if all panels are closed .. 
            openCount--;
            if(openCount == 0) {
                fovastActions.setSaveVisualizationMenuEnabled(false);
                fovastActions.setCloseVisualizationMenuEnabled(false);
                fovastActions.setShowGridMenuEnabled(false);
                fovastActions.setShowCatalogMenuEnabled(false);
                fovastActions.setShowImageColorsMenuEnabled(false);
                fovastActions.setShowImageCutLevelsMenuEnabled(false);
                fovastActions.setShowImageExtensionsMenuEnabled(false);
                fovastActions.setShowImageKeywordsMenuEnabled(false);
            }

            //Stop running tasks of that tab
            vPanelRemoved.stopRunningTasks();
        }
    }

    private void updateUIForVisualizationAdded(VisualizationState visualization,
            int vizId, String fileName) {
        //if already added nothing to do.
        if(getVisualizationPanel(vizId) != null)
            return;
        
        //TODO: All this has to be done on viz-open from menu
        VisualizationPanel visPanel = new VisualizationPanel(appContext, visualization);
		visPanel.addCatalogListener(this);

        String visPanelLabel = fileName;
        if (fileName.contains(File.separator)) {
            visPanelLabel = new File(fileName).getName();
        }

        visPanel.putClientProperty(TABS_MENUITEM_VIZID_CLIENTPROPERTY, vizId);
        centerTabbedPane.addTab(fileName, visPanel);
        tabComponentList.add(visPanel);

        //add menu item to tabs menu
        //TODO: On save and rename this menuitem text has to be updated ..
        JMenuItem menuItem = new JMenuItem();
        menuItem.setAction(menuActions.get(TABS_MENUITEM));
        menuItem.setText(visPanelLabel);
        menuItem.putClientProperty(TABS_MENUITEM_VIZID_CLIENTPROPERTY, vizId);
        tabsMenu.add(menuItem);

        //select the new viz active
        updateUIForVisualizationSelected(vizId);

        //increase openCount
        openCount++;

        //enable showGrid here ..
        //enable .. the save viz menuitem
        fovastActions.setSaveVisualizationMenuEnabled(true);
        fovastActions.setCloseVisualizationMenuEnabled(true);
        fovastActions.setShowGridMenuEnabled(true);
        fovastActions.setShowCatalogMenuEnabled(true);
        fovastActions.setShowImageColorsMenuEnabled(true);
        fovastActions.setShowImageCutLevelsMenuEnabled(true);
        fovastActions.setShowImageExtensionsMenuEnabled(true);
        fovastActions.setShowImageKeywordsMenuEnabled(true);
    }

//    //TODO: to be removed after fixing bsaf api
//    @Override
//    public void pack() {
//        //super.pack();
//        // do nothing
//    }

    void showProxySettingsDialog() {
        File appLocalStorage = appContext.getLocalStorage().getDirectory();
        File file = new File(appLocalStorage,
                resourceMap.getString(FovastApplication.PROXY_SETTINGS_FILE_KEY));
        ProxySettingsDialog dialog = new ProxySettingsDialog(getFrame(), file);
    }

    void showImageColorsFrame() {
        VisualizationPanel visPanel = getActiveVisPanel();
        visPanel.showImageColorsFrame();
    }

    void showImageCutLevelsFrame() {
        VisualizationPanel visPanel = getActiveVisPanel();
        visPanel.showImageCutLevelsFrame();
    }

    void showImageExtensionsFrame() {
        VisualizationPanel visPanel = getActiveVisPanel();
        visPanel.showImageExtensionsFrame();
    }

    void showImageKeywordsFrame() {
        VisualizationPanel visPanel = getActiveVisPanel();
        visPanel.showImageKeywordsFrame();
    }

    void showPreferencesDialog() {
        FovastApplication.getApplication().
                getConfiguration().showConfiguration(getFrame());
    }

    void createNewVisualization() {
        createNewVisualization(null);
    }

@Override
    public void catalogAdded(Catalog c) {
        //update menu here ..
        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem();
        menuItem.setAction(menuActions.get(CATALOG_MENUITEM));
        menuItem.putClientProperty(CATALOG_CLIENTPROPERTY,c);
        menuItem.setText(c.getLabel());
        menuItem.setSelected(true);
        catalogMenu.add(menuItem);

        JMenuItem menuItem1 = new JMenuItem();
        menuItem1.setAction(menuActions.get(CATALOG_MENUITEM_CLOSE));
        menuItem1.putClientProperty(CATALOG_CLIENTPROPERTY,c);
        menuItem1.setText(c.getLabel());
        catalogCloseMenu.add(menuItem1);
    }

	@Override
    public void catalogRemoved(Catalog c){
        
        //catalogMenu.remove(menuItemToBeRemoved);
        
        for(int i=0;i < catalogMenu.getItemCount();i++)
        {
            if(catalogMenu.getItem(i).getClientProperty(CATALOG_CLIENTPROPERTY
                    ).equals(c)) {
                catalogMenu.remove(catalogMenu.getItem(i));
            }

            if(catalogCloseMenu.getItem(i).getClientProperty(CATALOG_CLIENTPROPERTY
                    ).equals(c)) {
                catalogCloseMenu.remove(catalogCloseMenu.getItem(i));
            }
        }
    }

    private VisualizationPanel getVisualizationPanel(int vizId) {
        for(int i=0; i<tabComponentList.size(); i++) {
            if(tabComponentList.get(i) instanceof VisualizationPanel) {
                VisualizationPanel vPanel = (VisualizationPanel)tabComponentList.get(i);
                if(vizId == vPanel.getClientProperty(TABS_MENUITEM_VIZID_CLIENTPROPERTY)) {
                    return vPanel;
                }
            }
        }
        return null;
    }
    
    static class CustomFilter extends javax.swing.filechooser.FileFilter {

        public boolean accept(File f) {
            if(f.isDirectory())
                return true;
            for(int i = 0; i < FITS_EXTENSIONS.length; i++) {
                if(f.getName().toLowerCase().endsWith(FITS_EXTENSIONS[i]))
                    return true;
            }
            
            return false;
        }

        public String getDescription() {
            return FITS_EXTENSIONS_DESC;
        }

    }

}
