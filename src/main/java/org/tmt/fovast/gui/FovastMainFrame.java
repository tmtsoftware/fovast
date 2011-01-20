/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.gui;

import javax.swing.event.ChangeEvent;
import org.tmt.fovast.controller.FovastApplication;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jdesktop.application.ApplicationAction;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.controller.FovastApplicationController;
import org.tmt.fovast.controller.VisualizationController;
import org.tmt.fovast.mvc.ChangeListener;
import org.tmt.fovast.state.FovastApplicationState;
import org.tmt.fovast.state.VisualizationState;
import org.tmt.fovast.swing.utils.ExtendedTabbedPane;
import org.tmt.fovast.swing.utils.StatusBar;
import voi.swing.util.ProxySettingsDialog;

/**
 *
 * @author vivekananda_moosani
 */
public class FovastMainFrame extends JFrame implements ChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(FovastMainFrame.class);

    private static final String baseKey = FovastMainFrame.class.getName();

    private static final String keyPrefix = baseKey + ".";

    private static final String TAB_SAVE_MESSAGE_KEY =
            keyPrefix + "TabSaveMessage";

    private static final String TAB_CLOSE_MESSAGE_KEY =
            keyPrefix + "TabCloseMessage";

    private static final String TABS_MENU = "Menu.Tabs";

    private static final String TABS_MENUITEM = "Menu.Tabs.TabsMenuItem";

    private static final String TABS_MENUITEM_VIZID_CLIENTPROPERTY = "Viz.Id";

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

    private ApplicationContext appContext;

    private ResourceMap resourceMap;

    private FovastApplicationController controller;

    private JMenuBar menuBar = new JMenuBar();

    private ActionMap menuActions;

    private JToolBar toolBar = new JToolBar();

    private ExtendedTabbedPane centerTabbedPane = new ExtendedTabbedPane() {

        @Override
        protected boolean removeTabOrNot(int index) {
            //TODO: Also do the same logic when closing the visualization using
            //main menu and context menu in explorer panel
            return FovastMainFrame.this.closeVisPanel(index);
        }

    };

    private StatusBar statusBar;

    private ArrayList<JComponent> tabComponentList = new ArrayList<JComponent>();

    private JMenu tabsMenu;

    private JMenuItem selectedMenuItem;

    private FovastActions fovastActions;
    
    private int openCount = 0;

    public FovastMainFrame(ApplicationContext appContext,
            FovastApplicationController controller) {
        this.appContext = appContext;
        this.resourceMap = appContext.getResourceMap(FovastMainFrame.class);
        //controller object is needed for making state changes on user actions on UI
        this.controller = controller;

        initComponents();

        //UI needs to register as listener to controller for listening to
        //state changes indirectly
        controller.addChangeListener(this);

    }

    private void initComponents() {

        Container contentPane = getContentPane();
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
        setJMenuBar(menuBar);

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
                            controller.selectVisualization(vizId);
                        }
                    }
                });
            }

        });
        //centerTabbedPane.setMaximumSize(new Dimension(800, 800));
        contentPane.add(centerTabbedPane, BorderLayout.CENTER);


        //add statusbar
        prepareStatusBar();
        contentPane.add(statusBar, BorderLayout.SOUTH);

    }

    private void prepareMenuBar() throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document document =
                builder.build(FovastMainFrame.class.getResource(
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

    public void initializeFromState(FovastApplicationState fovastApplicationState) {
        //TODO: Load from saved state ..
    }

    void createNewVisualization() {
        //this create a visualization object and updates the app-model with it 
        controller.createNewVisualization();
    }

    private boolean closeVisPanel(int index) {
        JComponent comp = tabComponentList.get(index);
        if(comp instanceof VisualizationPanel)
            controller.removeVisualization((Integer)
                    comp.getClientProperty(TABS_MENUITEM_VIZID_CLIENTPROPERTY));
        //TODO: close happens in the lister .. think of this again
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
        controller.applicationExitAction();
    }

    void selectVisualizationPanel(JMenuItem menuItem) {
        JMenuItem oldSelectedMenuItem = selectedMenuItem;
        if (oldSelectedMenuItem == menuItem) {
            return;
        }

        int visualizationId = (Integer) menuItem.getClientProperty(
                TABS_MENUITEM_VIZID_CLIENTPROPERTY);
        controller.selectVisualization(visualizationId);
    }

    @Override
    public void update(Object source, String eventKey, HashMap<String, Object> args) {
        //handle new vis
        if (source == controller && eventKey ==
                FovastApplicationState.VISUALIZATION_ADDED_EVENT_KEY) {
            VisualizationState viz =
                    (VisualizationState) args.get(FovastApplicationState.VISUALIZATION_ARG_KEY);
            String fileName =
                    (String) args.get(FovastApplicationState.VISUALIZATION_FILENAME_ARG_KEY);
            int vizId =
                    (Integer) args.get(FovastApplicationState.VISUALIZATION_ID_ARG_KEY);
            updateUIForVisualizationAdded(viz, vizId, fileName);
        } else if (source == controller && eventKey ==
                FovastApplicationState.VISUALIZATION_REMOVED_EVENT_KEY) {
            int selectedVizId =
                    (Integer) args.get(FovastApplicationState.VISUALIZATION_ID_ARG_KEY);
            updateUIForVisualizationRemoved(selectedVizId);
        } else if (source == controller && eventKey ==
                FovastApplicationState.VISUALIZATION_SELECTED_EVENT_KEY) {
            int selectedVizId =
                    (Integer) args.get(FovastApplicationState.VISUALIZATION_ID_ARG_KEY);
            updateUIForVisualizationSelected(selectedVizId);
        } else {
            throw new RuntimeException("Unknown event " + eventKey + " from " +
                    "source " + source.toString() + "");
        }
    }

    private void updateUIForVisualizationSelected(int selectedVizId) {
        //update selection on tabbed pane
        Component tabContent = centerTabbedPane.getSelectedComponent();
        if (tabContent instanceof VisualizationPanel &&
                ((VisualizationPanel) tabContent).getClientProperty(
                TABS_MENUITEM_VIZID_CLIENTPROPERTY).equals(selectedVizId)) {
            //already selected
            //nothing to be done
        } else {
            for (int i = 0; i < centerTabbedPane.getTabCount(); i++) {
                JComponent comp = tabComponentList.get(i);
                if (comp.getClientProperty(
                        TABS_MENUITEM_VIZID_CLIENTPROPERTY).equals(selectedVizId)) {
                    centerTabbedPane.setSelectedComponent(comp);
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

        //enable .. the save viz menuitem
        //TODO: make sure these are disabled when all viz's are closed ..
        fovastActions.setSaveVisualizationMenuEnabled(true);
        fovastActions.setCloseVisualizationMenuEnabled(true);
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
        
        //TODO: finding if visualization tabs are open
        openCount--;
        if(openCount == 0) {
            fovastActions.setSaveVisualizationMenuEnabled(false);
            fovastActions.setCloseVisualizationMenuEnabled(false);
        }

        //Stop running tasks of that tab
        //TODO: Should this be done by a listener
        if(vPanelRemoved != null) {
            vPanelRemoved.stopRunningTasks();
        }
    }

    private void updateUIForVisualizationAdded(VisualizationState visualization,
            int vizId, String fileName) {
        //TODO: All this has to be done on viz-open from menu
        VisualizationController visController = new VisualizationController();
        visController.setState(visualization);

        VisualizationPanel visPanel = new VisualizationPanel(appContext, visController);
        visPanel.initializeFromState(visualization);

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
    }

    //TODO: to be removed after fixing bsaf api
    @Override
    public void pack() {
        //super.pack();
        // do nothing
    }

    void showProxySettingsDialog() {
        File appLocalStorage = appContext.getLocalStorage().getDirectory();
        File file = new File(appLocalStorage,
                resourceMap.getString(FovastApplication.PROXY_SETTINGS_FILE_KEY));
        ProxySettingsDialog dialog = new ProxySettingsDialog(this, file);
    }



}
