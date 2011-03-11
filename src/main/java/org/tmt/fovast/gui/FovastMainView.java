/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.gui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.Icon;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
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
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.application.ApplicationAction;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.openide.awt.DropDownButtonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.gui.VisualizationPanel.CatalogListener;
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

    private static final String ICON_FOR_MISSING_ICON_KEY = "Application.iconForMissingIcon";

    private static final String TABS_MENU = "Menu.Tabs";

    private static final String TABS_MENUITEM = "Menu.Tabs.TabsMenuItem";

    private static final String LOAD_CATALOG_MENU_ITEM_PREFIX = "Menu.File.LoadCatalogFrom.";

    private static final String CATALOG_MENU = "Menu.View.Catalogs";

    private static final String CATALOG_MENUITEM = "Menu.View.Catalogs.Show/Hide";

    private static final String CATALOG_MENU_CLOSE = "Menu.File.CloseCatalog";

    private static final String CATALOG_MENUITEM_CLOSE = "Menu.File.CloseCatalog.Select";

    private static final String TABS_MENUITEM_VIZID_CLIENTPROPERTY = "Viz.Id";

    private static final String CATALOG_CLIENTPROPERTY ="Catalogs";

    private static final String MENU_XML_FILE_PROPERTY = "Application.menuFile";

    private static final String TOOLBAR_XML_FILE_PROPERTY = "Application.toolbarFile";

    private static final String MENU_XML_NAME_ATTRIBUTE = "name";

    private static final String MENU_XML_TYPE_ATTRIBUTE = "type";

    private static final String MENU_XML_IMPLSTATE_ATTRIBUTE = "implState";

    private static final String MENU_XML_MENU_ELEMENT_NAME = "Menu";

    private static final String MENU_XML_MENUITEM_ELEMENT_NAME = "MenuItem";

    private static final String MENU_XML_SEPARATOR_ELEMENT_NAME = "Separator";

    private static final String MENU_XML_TYPE_ATTRIBUTE_VALUE_CHECKBOX = "checkbox";

    private static final String MENU_XML_TYPE_ATTRIBUTE_VALUE_RADIO = "radio";

    private static final String MENU_XML_TYPE_ATTRIBUTE_VALUE_DEFAULT = "menuitem";

    private static final String MENU_XML_IMPLSTATE_ATTRIBUTE_VALUE_TODO = "todo";

    private static final String TOOLBAR_XML_TOOLBAR_ELEMENT_NAME = "ToolBar";

    private static final String TOOLBAR_XML_TOOLBARITEM_ELEMENT_NAME = "ToolBarItem";

    private static final String TOOLBAR_XML_SEPARATOR_ELEMENT_NAME = "Separator";

    private static final String TOOLBAR_XML_TOOLBARDROPDOWNITEM_ELEMENT_NAME = "ToolBarDropDownItem";

    public static final String[] FITS_EXTENSIONS = {".fits",".fit"};

    public static final String FITS_EXTENSIONS_DESC = "FITS Files (*.fits, *.fit)";

    private ApplicationContext appContext;

    private ResourceMap resourceMap;

    private FovastApplicationState fovastState;

    //Id given to the visualization in this state
    private int newVisualizationId = 0;

    private JMenuBar menuBar = new JMenuBar();

    private ActionMap menuActions;

    private ActionMap toolBarActions;

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

    private JPopupMenu gsc2Menu = new JPopupMenu();

    private JPopupMenu massMenu = new JPopupMenu();

    private JPopupMenu usnoMenu = new JPopupMenu();

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
        try {
            prepareToolbar();
        } catch (Exception ex) {
            logger.error("Closing app due to toolbar creation failure... ", ex);
            System.exit(FovastApplication.ERROR_RETURN_CODE);
        }        
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
            Cache cache = ((FovastApplication) appContext.getApplication()).getCatalogCache();
            Set<Catalog> catalogs = vis.getCatalogList();
            Iterator iter = catalogs.iterator();
            HashMap<String,Object> prop;
            ArrayList<Catalog> tempList=new ArrayList<Catalog>();
            while(iter.hasNext())
            {
                Catalog c = (Catalog)iter.next();
                prop = c.getProperties();
                if(source.equals(prop.get("type"))){
                    tempList.add(c);
                }
            }
            Catalog tempC;
            if(!tempList.isEmpty())
               tempC = tempList.get(tempList.size()-1);
            else
               tempC=null;
            ConeSearchDialog csd = new ConeSearchDialog(url.trim(),
              center.x,center.y,2,vis,this,source,cache,tempC);
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
                builder.build(FovastMainView.class.getResource(resourceMap.getString(
                            MENU_XML_FILE_PROPERTY)));
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

        //TODO: set text, icon, viewAction-method, tooltip, accelerator properly
        if (elementName.equals(MENU_XML_MENU_ELEMENT_NAME)) {
            JMenu menu = new JMenu();
            menu.setName(nameAttributeValue);
            if (nameAttributeValue.equals(TABS_MENU)) {
                tabsMenu = menu;
            } else if(nameAttributeValue.equals(CATALOG_MENU)){
                catalogMenu = menu;
            } else if(nameAttributeValue.equals(CATALOG_MENU_CLOSE)){
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
        } else if (elementName.equals(MENU_XML_MENUITEM_ELEMENT_NAME)) {
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

            if(nameAttributeValue.startsWith(LOAD_CATALOG_MENU_ITEM_PREFIX)) {
                menuItem.setIcon(null);
            }

            return menuItem;
            
        } else if (elementName.equals(MENU_XML_SEPARATOR_ELEMENT_NAME)) {
            return new JSeparator();
        } else {
            logger.error("Unknown element type: " + menuElement.toString());
            throw new Exception("Unknown element type in menu.xml");
        }
    }

    private void prepareToolbar() throws JDOMException, IOException, Exception {
//        JButton button = new JButton(" .. TBD .. ");
//        button.setPreferredSize(new Dimension(200, 30));
//        button.setEnabled(false);
//        toolBar.add();
        //TODO: prepare toolbar
        SAXBuilder builder = new SAXBuilder();
        Document document =
                builder.build(FovastMainView.class.getResource(
                    resourceMap.getString(TOOLBAR_XML_FILE_PROPERTY)));
        //fovastActions = new FovastActions(this);
        toolBarActions = menuActions;
                //appContext.getActionMap(fovastActions);


        List<Element> children = document.getRootElement().getChildren();
        for (int i = 0; i < children.size(); i++) {
            Element child = children.get(i);
            JComponent item = prepareToolBarItems(child);
            //toolBar.add(item);
        }
    }

    private JComponent prepareToolBarItems(Element toolBarElement) throws Exception{
        String elementName = toolBarElement.getName();
        String nameAttributeValue = toolBarElement.getAttributeValue(MENU_XML_NAME_ATTRIBUTE);
        if (elementName.equals(TOOLBAR_XML_TOOLBAR_ELEMENT_NAME)) {
           List<Element> children = toolBarElement.getChildren();
            for (int i = 0; i < children.size(); i++) {
                Element child = children.get(i);
                JComponent menuComponent = prepareToolBarItems(child);
                toolBar.add(menuComponent);
            }

            return toolBar;
        } else if (elementName.equals(TOOLBAR_XML_TOOLBARITEM_ELEMENT_NAME)) {
            JButton button= new JButton();
            ApplicationAction action = (ApplicationAction) toolBarActions.get(nameAttributeValue);
            if (action != null) {
                button.setAction(action);
            }
            button.setText("");
            return button;
        } else if (elementName.equals(TOOLBAR_XML_SEPARATOR_ELEMENT_NAME)) {
            return new JToolBar.Separator();
        }
//       else if (elementName.equals("special")) {
//           final JPopupMenu popup = new JPopupMenu();
//           JButton dropDownButton = dropDownButton = DropDownButtonFactory.createDropDownButton(new ImageIcon(
//            getClass().getResource("Palette.gif")),popup);
//           dropDownButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//              // VisualizationPanel vis = getActiveVisPanel();
//            for(int i = 0 ;i < catalogMenu.getItemCount() ;i++){
//                JMenuItem menuItem = catalogMenu.getItem(i);
//                popup.add(menuItem);
//            }
//                }
//            });
//           return dropDownButton;
//
//        }
        else if (elementName.equals(TOOLBAR_XML_TOOLBARDROPDOWNITEM_ELEMENT_NAME)) {
            JButton dropDownButton;
            Icon missingIcon = new ImageIcon(getApplication().getClass(
                    ).getResource(resourceMap.getString(ICON_FOR_MISSING_ICON_KEY)));
            if(nameAttributeValue.equalsIgnoreCase("Menu.File.LoadCatalogFrom.GSC2")){
               dropDownButton = dropDownButton = DropDownButtonFactory.createDropDownButton(
                       missingIcon, gsc2Menu);
            }else if(nameAttributeValue.equalsIgnoreCase("Menu.File.LoadCatalogFrom.2MassPsc")){
               dropDownButton = dropDownButton = DropDownButtonFactory.createDropDownButton(
                       missingIcon, massMenu);
            }else if(nameAttributeValue.equalsIgnoreCase("Menu.File.LoadCatalogFrom.USNO")){
               dropDownButton = dropDownButton = DropDownButtonFactory.createDropDownButton(
                       missingIcon, usnoMenu);
            }else{
               dropDownButton = dropDownButton = DropDownButtonFactory.createDropDownButton(
                       missingIcon, new JPopupMenu());
            }
            ApplicationAction action = (ApplicationAction) toolBarActions.get(nameAttributeValue);
            if (action != null) {
                dropDownButton.setAction(action);
            }
            dropDownButton.setText("");
            return dropDownButton;
        }
        else {
            logger.error("Unknown element type: " + toolBarElement.toString());
            throw new Exception("Unknown element type in menu.xml");
        }
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

    void removeAll(){
        VisualizationPanel vis = getActiveVisPanel();
        //for(int i = 0 ;i < catalogCloseMenu.getItemCount() ;i++){
        while(catalogCloseMenu.getItemCount()>0){
            JMenuItem menuItem = catalogCloseMenu.getItem(0);
            Catalog c = (Catalog)menuItem.getClientProperty(CATALOG_CLIENTPROPERTY);
            vis.remove(c);
        }
        catalogMenu.removeAll();
        catalogCloseMenu.removeAll();
        gsc2Menu.removeAll();
        usnoMenu.removeAll();
        massMenu.removeAll();
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
                    //removeAll();
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
            gsc2Menu.removeAll();
            usnoMenu.removeAll();
            massMenu.removeAll();
            Set<Catalog> catalogs=activePanel.getCatalogList();
            Iterator iter = catalogs.iterator();
            int i=0;
            
            while(iter.hasNext())
            {
                Catalog c = (Catalog)iter.next();
                JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem();
                menuItem.setSelected(true);
                Action viewAction = makeCatalogViewAction(
                        activePanel.isCatalogShown(c));
                menuItem.setAction(viewAction);
                menuItem.putClientProperty(CATALOG_CLIENTPROPERTY,c);
                menuItem.setText(c.getLabel());
                catalogMenu.add(menuItem);

                JCheckBoxMenuItem menuItem2 = new JCheckBoxMenuItem();
                menuItem2.setSelected(true);
                menuItem2.setAction(viewAction); //toolBarActions.get(CATALOG_MENUITEM));
                menuItem2.putClientProperty(CATALOG_CLIENTPROPERTY,c);
                menuItem2.setName(c.getLabel());
                menuItem2.setText(c.getLabel());
                if(c.getLabel().contains("GSC2")){
                    gsc2Menu.add(menuItem2);
               }else if(c.getLabel().contains("2Mass")){
                    massMenu.add(menuItem2);
                }else if(c.getLabel().contains("USNO")){
                    usnoMenu.add(menuItem2);
                }

                //For close menu
                JMenuItem menuItem1 = new JMenuItem();
                menuItem1.setSelected(true);
                menuItem1.putClientProperty(CATALOG_CLIENTPROPERTY,c);
                //NOTE: We are attaching the same viewAction for different catalog
                //close menuitems ..
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
        Action viewAction = makeCatalogViewAction(true);
        menuItem.setAction(viewAction);
        menuItem.putClientProperty(CATALOG_CLIENTPROPERTY,c);
        menuItem.setText(c.getLabel());
        //need not be done as viewAction would have the selected prop set
        //menuItem.setSelected(true);
        catalogMenu.add(menuItem);

        JCheckBoxMenuItem menuItem2 = new JCheckBoxMenuItem();
        menuItem2.setSelected(true);
        menuItem2.setAction(viewAction);
        menuItem2.putClientProperty(CATALOG_CLIENTPROPERTY,c);
        menuItem2.setName(c.getLabel());
        menuItem2.setText(c.getLabel());
        if(c.getLabel().contains("GSC2")){
            gsc2Menu.add(menuItem2);
        }else if(c.getLabel().contains("2Mass")){
            massMenu.add(menuItem2);
        }else if(c.getLabel().contains("USNO")){
            usnoMenu.add(menuItem2);
        }

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
            if(c.getLabel().contains("GSC2")){
                for(int j=0; j<gsc2Menu.getComponentCount();j++){
                    if(c.getLabel().equals(gsc2Menu.getComponent(j).getName())) {
                        gsc2Menu.remove(gsc2Menu.getComponent(j));
                    }
                }
            }
            if(c.getLabel().contains("USNO")){
                for(int j=0; j<usnoMenu.getComponentCount();j++){
                    if(c.getLabel().equals(usnoMenu.getComponent(j).getName())) {
                        usnoMenu.remove(usnoMenu.getComponent(j));
                    }
                }
            }
            if(c.getLabel().contains("2Mass")){
                for(int j=0; j<massMenu.getComponentCount();j++){
                    if(c.getLabel().equals(massMenu.getComponent(j).getName())) {
                        massMenu.remove(massMenu.getComponent(j));
                    }
                }
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

    private Action makeCatalogViewAction(boolean selected) {
        Action commonViewAction = menuActions.get(CATALOG_MENUITEM);
        AbstractAction viewAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //boolean selectedVal = (Boolean) getValue(Action.SELECTED_KEY);
                fovastActions.viewCatalogAction(e);
                //putValue(Action.SELECTED_KEY, !selectedVal);

            }
        };
        viewAction.setEnabled(commonViewAction.isEnabled());
        //ACCELERATOR_KEY, ACTION_COMMAND_KEY, DEFAULT, LONG_DESCRIPTION, MNEMONIC_KEY, NAME, SHORT_DESCRIPTION,
        //SMALL_ICON, SELECTED_KEY ...
        viewAction.putValue(Action.ACCELERATOR_KEY,
                commonViewAction.getValue(Action.ACCELERATOR_KEY));
        viewAction.putValue(Action.LONG_DESCRIPTION,
                commonViewAction.getValue(Action.LONG_DESCRIPTION));
        viewAction.putValue(Action.MNEMONIC_KEY,
                commonViewAction.getValue(Action.MNEMONIC_KEY));
        viewAction.putValue(Action.SHORT_DESCRIPTION,
                commonViewAction.getValue(Action.SHORT_DESCRIPTION));
        viewAction.putValue(Action.SMALL_ICON,
                commonViewAction.getValue(Action.SMALL_ICON));
//        Boolean selected = (Boolean) commonViewAction.getValue(Action.SELECTED_KEY);
//        if(selected == null)
//            selected = false;
//        viewAction.putValue(Action.SELECTED_KEY, selected);
        viewAction.putValue(Action.SELECTED_KEY, selected);
        return viewAction;
    }

    void doPostStartupWork() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                //show a vispanel by default
                createNewVisualization();
            }
        });
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
