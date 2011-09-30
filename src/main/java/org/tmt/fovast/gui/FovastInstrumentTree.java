/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.JTree;
import javax.swing.tree.*;
import java.net.URL;
import javax.swing.event.CellEditorListener;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.instrumentconfig.BooleanValue;
import org.tmt.fovast.instrumentconfig.Config.ConfigListener;
import org.tmt.fovast.instrumentconfig.ConfigHelper;
import org.tmt.fovast.instrumentconfig.ConfigOption;
import org.tmt.fovast.instrumentconfig.Value;

/**
 * <p>Handles showing a JTree from InstrumentTree.xml. On actions on the JTree, it
 * would invoke functions on {@link FovastConfigHelper}. This class also listens
 * to changes in instrument config by implementing ConfigListener.</p>
 *
 */
public class FovastInstrumentTree implements ConfigListener, CellEditorListener {

    private static Logger logger = LoggerFactory.getLogger(FovastInstrumentTree.class);

    private final static String NODE_NAME = "Node";

    private final static String CHECKBOXNODE_NAME = "CheckboxNode";

    private final static String RADIONODE_NAME = "RadioNode";

    private final static String CHECKBOXGROUPNODE_NAME = "CheckboxGroupNode";

    private final static String LABEL_ATTRIBUTE = "label";

    private final static String CONFIGOPTIONID_ATTRIBUTE = "configOptionId";

    private final static String CONFIGOPTIONVALUE_LABEL_ATTRIBUTE = "configOptionValue";

    private final static String INSTRUMENT_TREE_XML = "resources/InstrumentTree.xml";    

    private ConfigHelper configHelper;

    private JTree tree;
    
    private DefaultTreeModel treeModel;

    private String defaultFocusSelected="no probe";

    private HashMap<String, ArrayList<UserObject>> confIdUserObjectMap =
            new HashMap<String, ArrayList<UserObject>>();

    /**
     * The passed config if null shows a dummy tree
     *
     * @param config
     * @throws org.tmt.fovast.gui.FovastInstrumentTree.SomeException
     */
    public FovastInstrumentTree(ConfigHelper configHelper) throws SomeException{
        try {
            this.configHelper = configHelper;
            if(configHelper != null)
                configHelper.addConfigListener(this);
            
            //The load method has to either read from default
            //InstrumentTree.xml or the tree state file (which would be similar
            //except that it would also note if the node has been expanded
            //and if the checkbox was selected)
            //TODO: still to code from the saved state file ...
            //TODO: whether to select a checkbox can be made from config object
            //as well .. expansion state has to be made out from state file. 
            loadAndInitializeInstrumentTree();
        } catch(Exception ex) {
            throw new SomeException(ex);
        }
    }

    @Override
    public void updateConfigElementValue(String confElementId, Value value, boolean isDisplayElement) {
        selectNode(confElementId, value);
    }

    @Override
    public void enableConfig(String confElementId, boolean enable, boolean isDisplayElement) {
        enableNode(confElementId, enable);
    }

    
    private void loadAndInitializeInstrumentTree() 
            throws IOException, JDOMException {
        
        //read the XML file for tree
        SAXBuilder builder = new SAXBuilder();
        URL configURL = getClass().getResource(INSTRUMENT_TREE_XML);
        Document document = builder.build(configURL);
        Element rootElement = document.getRootElement();

        //add nodes to the tree
        CustomDefaultMutableTreeNode rootNode =
                new CustomDefaultMutableTreeNode(new UserObject("Instrument Config"));
        treeModel = new DefaultTreeModel(rootNode);
        makeTreeNodes(rootElement, rootNode);

        //create tree
        //overriding as nimbus l&f does not respect the setBackgroundColor on tree.
        //fix thanks to http://www.jroller.com/santhosh/date/20060216
        tree = new JTree(treeModel) {

            @Override
            protected void paintComponent(Graphics g) {
                Color c = g.getColor();
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(c);
                boolean b = isOpaque();
                //so that actual paint code does not draw background.
                setOpaque(false);
                super.paintComponent(g);
                setOpaque(b);
            }
            
        };

        //tree settings
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setSelectionModel(null);
        //tree.setEnabled(false);
        tree.setEditable(true);
        FovastInstrumentTreeCellRenderer renderer = new FovastInstrumentTreeCellRenderer(tree);
        tree.setCellRenderer(renderer);
        FovastInstrumentTreeCellEditor editor =
                new FovastInstrumentTreeCellEditor(tree, renderer);
        tree.setCellEditor(editor);
        editor.addCellEditorListener(this);
        
        //show tree fully expanded
        //for( int i = 0; i < tree.getRowCount(); ++i )
        //{
        // tree.expandRow(i);
        //}

        //TODO:
        //1. show proper editing components
        //2. attach to config manager
        //3. track config changes .. 
    }

    public JTree getJTree() {
        return tree;
    }

    /**
     * <p>Make tree nodes from document element children and add them to passed
     * treeNode.</p>
     * Creates a UserObject corresponding to children of documentElement
     * and attaches them as children to treeNode's userobject
     * 
     * @param documentElement
     * @param treeNode
     */
    private void makeTreeNodes(Element documentElement, CustomDefaultMutableTreeNode treeNode) {
        List<Element> children = documentElement.getChildren();
        for(int i=0; i<children.size(); i++) {
            Element child = children.get(i);
            String name = child.getName();
            String label = child.getAttributeValue(LABEL_ATTRIBUTE);
            UserObject parentUo = (UserObject)treeNode.getUserObject();
            UserObject uo = null;
            if(name.equals(NODE_NAME)) {
                uo = new UserObject(label);
                String configOptId = child.getAttributeValue(CONFIGOPTIONID_ATTRIBUTE);
                addToConfigIdUserObjectMap(configOptId, uo);
            } else if (name.equals(CHECKBOXNODE_NAME) || name.equals(RADIONODE_NAME)) {
                String configOptionValue =
                        child.getAttributeValue(CONFIGOPTIONVALUE_LABEL_ATTRIBUTE);
                Value value = null;
                //TODO: We should also support other value types
                //TODO: Adding a static method in Value class to create a
                //proper object given a string representation would help. 
                if(configOptionValue == null) {
                    //leave it null .. 
                    //value = new BooleanValue(false);
                } else {
                    //TODO: take a call on this method
                    //this is needed  assuming all types of values (string, position..)
                    //have a string form.
                    //Alternately complex value objects can be put in InstrumentTree.xml by
                    //allowing the CheckboxUserGroupObject to have a <Value> child
                    //(like the one in InstrumentConfig.xml)
                    value = Value.createValueFromStringForm(configOptionValue);
                }

                String configOptId = child.getAttributeValue(CONFIGOPTIONID_ATTRIBUTE);
                if(name.equals(CHECKBOXNODE_NAME)) {
                    uo = new CheckboxUserObject(label, configOptId, value);
                } else if(name.equals(RADIONODE_NAME)) {
                    uo = new RadioUserObject(label, configOptId, value);
                } else {
                    assert false : "Unknow type of node " + name;
                }
                addToConfigIdUserObjectMap(configOptId, uo);

            } else if (name.equals(CHECKBOXGROUPNODE_NAME)) {
                uo = new CheckboxGroupUserObject(label);
            } else {
                assert false : "Unknown element type: " + name;
            }

            CustomDefaultMutableTreeNode tNode = new CustomDefaultMutableTreeNode(uo);
              if(tNode.getUserObject() instanceof CheckboxUserObject){
                  if(((CheckboxUserObject)tNode.getUserObject()).getLabel().equalsIgnoreCase("Default Focus")){
                ((CheckboxUserObject)tNode.getUserObject()).setDisabled(true);                
              
                  }
            }       
            treeNode.add(tNode);

            if(treeNode.getChildCount() > 0) {
                makeTreeNodes(child, tNode);
            }
        }
    }

    private synchronized void selectNode(String confElementId, Value value) {
        logger.debug("in selectNode ... " + confElementId);
        if(confElementId.equalsIgnoreCase("iris")){
            if(((BooleanValue)value).getValue()==true){
                System.out.println("IRISSSSSSSS");                
             }
        }
        if(confElementId.equalsIgnoreCase("mobie")){
            if(((BooleanValue)value).getValue()==true){
                System.out.println("MOBIEEEEEEEEEE");
             }
        }
        ArrayList<UserObject> uoList = confIdUserObjectMap.get(confElementId);
        if(uoList == null) {
            logger.warn("in selectNode ... element unknown " + confElementId);
            return;
        }

        //TODO: will be changed if multi valued attributes come into picture ..
        
        if(uoList.size() == 1) {
            //check if the uoList is of size 1 and if the value is null ..
            UserObject uo = uoList.get(0);
            if(uo instanceof CheckboxUserObject) {
                CheckboxUserObject cuo = (CheckboxUserObject)uo;
                //if null it means its a simple config which is either turned on / off
                //in this case use the selected flag
                if(cuo.getConfigOptionValue() == null) {
                    boolean isSel = false;
                    if(value != null)
                        isSel = ((BooleanValue)value).getValue();
                    if(isSel != cuo.isSelected()) {
                        if(!cuo.isDisabled()) {
                            logger.debug("in selectNode (changing selection)... " + confElementId);
                            cuo.setSelected(isSel);
                            treeModel.nodeChanged(uo.getTreeNode());
                            setSelectedCheckboxGroupNodeTraversingUp(uo.getTreeNode());
                        }
                    }
                 if(value != null){
                    if(cuo.getLabel().equalsIgnoreCase("Default Focus")){
                        if(((BooleanValue)value).getValue()==true)
                            defaultFocusSelected = cuo.getConfigOptionId();
                        cuo.setDisabled(true);
                }
               
                    if(cuo.getLabel().equalsIgnoreCase("Show Arm")){
                        //show getting deselected
                        Value prevFocusValue;
                        if(((BooleanValue)value).getValue()==false){
                            if(confElementId.contains("probe1")){
                                //prevFocusValue = configHelper.config.getConfig("iris.oiwfs.probe1.focus");
                                configHelper.setConfig("iris.oiwfs.probe1.focus", value);
                            }
                            else if(confElementId.contains("probe2")){
                                //prevFocusValue = configHelper.config.getConfig("iris.oiwfs.probe2.focus");
                                configHelper.setConfig("iris.oiwfs.probe2.focus", value);
                            }
                            else if(confElementId.contains("probe3")){
                                //prevFocusValue = configHelper.config.getConfig("iris.oiwfs.probe3.focus");
                                configHelper.setConfig("iris.oiwfs.probe3.focus", value);
                            }
                        }
                        //show getting selected
                        else{
                            if(confElementId.contains("probe1")){
                                if(defaultFocusSelected.contains("probe1"))
                                    configHelper.setConfig("iris.oiwfs.probe1.focus", new BooleanValue(true));
                            }
                            else if(confElementId.contains("probe2")){
                                if(defaultFocusSelected.contains("probe2"))
                                    configHelper.setConfig("iris.oiwfs.probe2.focus", new BooleanValue(true));
                            }
                            else if(confElementId.contains("probe3")){
                                if(defaultFocusSelected.contains("probe3"))
                                    configHelper.setConfig("iris.oiwfs.probe3.focus", new BooleanValue(true));
                            }
                        }
                                                 
                    }
                    }
                    return;
                }
            } else if(uo instanceof UserObject) {
                //nothing to do .. its a simple non editable user object
            }
            else {
                assert false : "As of now only CheckboxUserObject/UserObject have confElementId";
            }
        }
        
        for(int i=0; i<uoList.size(); i++) {
            UserObject uo = uoList.get(i);
            //if value is not boolean
            if(uo instanceof CheckboxUserObject) {
                CheckboxUserObject cuo = (CheckboxUserObject)uo;
                if(cuo.getConfigOptionValue().equals(value)) {
                    //if not already selected select it and adjust the representation
                    if(!cuo.isSelected()) {
                        if(!cuo.isDisabled()) {
                            logger.debug("in selectNode (changing selection)... " + confElementId);
                            cuo.setSelected(true);
                            treeModel.nodeChanged(uo.getTreeNode());
                            setSelectedCheckboxGroupNodeTraversingUp(uo.getTreeNode());
                        }
                    }
                    //return;
                } else { //we only support single valued config .. so disabling other options
                    if(cuo.isSelected()) {
                        if(!cuo.isDisabled()) {
                            logger.debug("in selectNode (changing selection)... " + confElementId);
                            cuo.setSelected(false);
                            treeModel.nodeChanged(uo.getTreeNode());
                            setSelectedCheckboxGroupNodeTraversingUp(uo.getTreeNode());
                        }
                    }
                }
            } else if(uo instanceof UserObject) {
                //nothing to do .. its a simple non editable user object
            } else {
                assert false : "As of now only CheckboxUserObject/UserObject have confElementId";
            }
        }

    }

    private synchronized void enableNode(String confElementId,
            boolean enabled) {
//        if(confElementId.equals("nfiraos")) {
//            logger.debug("in enableNode ... " + confElementId);
//        }
        logger.debug("in enableNode ... " + confElementId);

        ArrayList<UserObject> uoList = confIdUserObjectMap.get(confElementId);

        if(uoList == null) {
            logger.warn("in enableNode ... element unknown " + confElementId);
            return;
        }
        
        for(int i=0; i<uoList.size(); i++) {
            UserObject uo = uoList.get(i);
            //if value is not boolean
            if(uo instanceof CheckboxUserObject || uo instanceof UserObject) {
                if(uo.isDisabled() == enabled) { //note we are checking disabled with enabled
                    logger.debug("in enableNode (changing enable)... " + confElementId);
                    uo.setDisabled(!enabled);
                    treeModel.nodeChanged(uo.getTreeNode());
                    setSelectedCheckboxGroupNodeTraversingUp(uo.getTreeNode());
                }
            } else {
                assert false : "As of now only CheckboxUserObject/UserObject have confElementId";
            }
        }
    }

    private void setEnabledCheckboxGroupNodeTraversingUp(CustomDefaultMutableTreeNode tNode) {
        tNode = (CustomDefaultMutableTreeNode) tNode.getParent();
        UserObject uo = (UserObject) tNode.getUserObject();
        TreeNode[] nodesOnPath = tNode.getPath();
        for(int i=nodesOnPath.length-1; i>=0; i--) {
            CustomDefaultMutableTreeNode currNode =
                    (CustomDefaultMutableTreeNode) nodesOnPath[i];
            Object currentUserObject  = currNode.getUserObject();
            if(currentUserObject instanceof CheckboxGroupUserObject) {
                CheckboxGroupUserObject cguo = (CheckboxGroupUserObject)currentUserObject;
                if(cguo.isDisabled() != cguo.areChildrenDisabled()) {
                    cguo.setDisabled(cguo.areChildrenDisabled());
                    //just reevaluate the show logic
                    treeModel.nodeChanged(currNode);
                }
            }
        }
    }
    /**
     * Selects/Unselects the CheckboxGroupNodes in the tree up to root
     * Note: the passed associatedTreeNode is not checked to be CheckboxGroupNode
     * @param associatedTreeNode
     */
    private void setSelectedCheckboxGroupNodeTraversingUp(CustomDefaultMutableTreeNode tNode) {
        tNode = (CustomDefaultMutableTreeNode) tNode.getParent();
        UserObject uo = (UserObject) tNode.getUserObject();
        TreeNode[] nodesOnPath = tNode.getPath();
        for(int i=nodesOnPath.length-1; i>=0; i--) {
            CustomDefaultMutableTreeNode currNode =
                    (CustomDefaultMutableTreeNode) nodesOnPath[i];
            Object currentUserObject  = currNode.getUserObject();
            if(currentUserObject instanceof CheckboxGroupUserObject) {
                CheckboxGroupUserObject cguo = (CheckboxGroupUserObject)currentUserObject;
                if(cguo.isSelected() != cguo.areChildrenSelected()) {
                    cguo.setSelected(cguo.areChildrenSelected());
                    //just reevaluate the show logic
                    treeModel.nodeChanged(currNode);
                }
                if(cguo.isDisabled() != cguo.areChildrenDisabled()) {
                    cguo.setDisabled(cguo.areChildrenDisabled());
                    //just reevaluate the show logic
                    treeModel.nodeChanged(currNode);
                }

            }
        }
    }

    // TREE LISTENER METHODS START =====================
    private static boolean inTreeNodesChanged = false;
    ArrayList<TreeNode> nodesToFireChange = new ArrayList<TreeNode>();

    @Override
    public void editingStopped(ChangeEvent e) {
        FovastInstrumentTreeCellEditor editor = 
                (FovastInstrumentTreeCellEditor) e.getSource();

        UserObject uo = (UserObject) editor.getCellEditorValue();        

        editingStopped(uo);
    }

    private void editingStopped(UserObject uo) {

        CustomDefaultMutableTreeNode tNode =
                (CustomDefaultMutableTreeNode) uo.getTreeNode();
        //this would just select CheckGroupNodes up the hierarchy
        //nothing to be sent through ConfigHelpe
        //note this call also enables above group nodes .. 
        setSelectedCheckboxGroupNodeTraversingUp(tNode);

        //need to ConfigHelper methods for these
        if (uo instanceof CheckboxGroupUserObject) {
            boolean recentlySelected = ((CheckboxGroupUserObject)uo).isSelected();
            //also set child nodes to have been modified
            for(int j=0; j<uo.getTreeNode().getChildCount(); j++) {
                CustomDefaultMutableTreeNode childTNode =
                        (CustomDefaultMutableTreeNode) uo.getTreeNode().getChildAt(j);
                UserObject childUserObject = (UserObject) childTNode.getUserObject();
                if(childUserObject instanceof UserObject.Editable) {
                    if( recentlySelected != ((UserObject.Editable)childUserObject).isSelected()) {
                        ((UserObject.Editable)childUserObject).setSelected(recentlySelected);
                        //nodesToFireChange.add(childTNode);
                        treeModel.nodeChanged(childTNode);
                        //update config
                        if(childUserObject instanceof CheckboxUserObject) {
                            CheckboxUserObject cuo = (CheckboxUserObject)childUserObject;
                            Object value = cuo.getConfigOptionValue();
                            if(value == null) //means its simple on/off config
                                value = new BooleanValue(cuo.isSelected());
                            configHelper.setConfig(cuo.getConfigOptionId(), (Value) value);
                        } else if(childUserObject instanceof CheckboxGroupUserObject) {
                            editingStopped(childUserObject);
                        }
                    }
                }
            }
        } else if(uo instanceof CheckboxUserObject) {
            CheckboxUserObject cuo = (CheckboxUserObject)uo;
            Object value = cuo.getConfigOptionValue();
            if(value == null) //means its simple on/off config
                value = new BooleanValue(cuo.isSelected());
            configHelper.setConfig(cuo.getConfigOptionId(), (Value) value);
        }
    }

    @Override
    public void editingCanceled(ChangeEvent e) {
        //nothing to be done here
    }

    private void addToConfigIdUserObjectMap(String configOptId, UserObject uo) {
        ArrayList<UserObject> uoList = confIdUserObjectMap.get(configOptId);
        if(uoList == null) {
            uoList = new ArrayList<UserObject>();
            confIdUserObjectMap.put(configOptId, uoList);
        }
        uoList.add(uo);
    }

    @Override
    public void updateConfigElementProperty(String confElementId, String propKey, String propValue) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    // TREE LISTENER METHODS END =====================

    /**
     * <p>Exception class which wraps the exceptions thrown during making of
     * instrument tree. Check the cause of the exception for actual error.</p>
     */
    public static class SomeException extends Exception {

        public SomeException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public SomeException(Throwable cause) {
            super(cause);
        }

        public SomeException(String msg) {
            super(msg);
        }
    }

    /**
     * Only visible to this class and corresponding cell renderer class
     * UserObject holds info about a Node in InstrumentTree.xml
     * //TODO: Should we implement equals and hashCode for this and subclasses.
     */
    static class UserObject {

        private String configOptionId;
        private String label;
        private UserObject parentUo;
        private CustomDefaultMutableTreeNode associatedTreeNode;
        private boolean disabled = true;

        public UserObject(String label) {
            this.label = label;
        }
        
        public UserObject(String label, String configOptionId) {
            this.label = label;
            this.configOptionId = configOptionId;
        }

        public String getConfigOptionId() {
            return configOptionId;
        }


        public String getLabel() {
            return label;
        }

        public void setTreeNode(CustomDefaultMutableTreeNode tNode) {
            this.associatedTreeNode = tNode;
        }

        public CustomDefaultMutableTreeNode getTreeNode() {
            return associatedTreeNode;
        }

        public static interface Editable {

            public void setEditState(boolean b);

            public boolean getEditState();

            public boolean isEditStateSet();

            public void clearEditState();
            
            public void setSelected(boolean b);

            public boolean isSelected();

        }

        public boolean isDisabled() {
            return disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

    }

//TODO: Leave hierarchy management to DefaultTreeModel
//    /**
//     * UserObject which has children
//     */
//    static class GroupUserObject extends UserObject {
//
//        protected ArrayList<UserObject> children = new ArrayList<UserObject>();
//
//        public GroupUserObject(String label) {
//            super(label);
//        }
//
//        public void addChild(UserObject uo) {
//            children.add(uo);
//        }
//
//        public int getChildCount() {
//            return children.size();
//        }
//
//        public UserObject getChild(int index) {
//            return children.get(index);
//        }
//
//    }
    
    static class CheckboxUserObject extends UserObject
            implements UserObject.Editable {

        private Object configOptionValue;

        private boolean selected;

        private boolean editStateSet;

        private boolean editState;

        public CheckboxUserObject(String label, String configOptionId) {
            this(label, configOptionId, null);
        }

        /**
         *
         * @param label
         * @param configOptionId
         * @param configOptionValue - as of now this is either a string or boolean
         */
        public CheckboxUserObject(String label, String configOptionId, Object configOptionValue) {
            super(label, configOptionId);
            this.configOptionValue = configOptionValue;
        }

        public Object getConfigOptionValue() {
            return configOptionValue;
        }

        @Override
        public void setSelected(boolean b) {
            this.selected = b;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        @Override
        public void setEditState(boolean b) {
            editState = b;
            editStateSet = true;
        }

        @Override
        public boolean getEditState() {
            return editState;
        }

        @Override
        public boolean isEditStateSet() {
            return editStateSet;
        }

        @Override
        public void clearEditState() {
            editStateSet = false;
        }

    }

    static class CheckboxGroupUserObject extends UserObject
        implements UserObject.Editable {

        private boolean editState = false;

        private boolean editStateSet = false;

        private boolean selected = false;

        public CheckboxGroupUserObject(String label) {
            super(label);
        }


        @Override
        public void setSelected(boolean b) {
            selected = b;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        boolean areChildrenSelected() {
            //we use the tree node associated for looping through the
            //child hierarchy
            CustomDefaultMutableTreeNode tNode = getTreeNode();
            for(int i=0; i<tNode.getChildCount(); i++) {
                UserObject child =
                        (UserObject) ((CustomDefaultMutableTreeNode)tNode
                            .getChildAt(i)).getUserObject();
                if(child instanceof CheckboxUserObject) {
                    if(!((CheckboxUserObject)child).isSelected())
                        return false;
                } else if(child instanceof CheckboxGroupUserObject) {
                    if(!((CheckboxGroupUserObject)child).isSelected())
                        return false;
                }
            }

            return true;
        }

        private boolean areChildrenDisabled() {
            //we use the tree node associated for looping through the
            //child hierarchy
            CustomDefaultMutableTreeNode tNode = getTreeNode();
            for(int i=0; i<tNode.getChildCount(); i++) {
                UserObject child =
                        (UserObject) ((CustomDefaultMutableTreeNode)tNode
                            .getChildAt(i)).getUserObject();
                if(!child.isDisabled())
                    return false;
            }

            return true;
        }

        @Override
        public void setEditState(boolean b) {
            editStateSet = true;
            editState = b;
        }

        @Override
        public boolean isEditStateSet() {
            return editStateSet;
        }

        @Override
        public boolean getEditState() {
            return editState;
        }

        @Override
        public void clearEditState() {
            editStateSet = false;
        }

    }

    private static class CustomDefaultMutableTreeNode extends DefaultMutableTreeNode {

        public CustomDefaultMutableTreeNode(UserObject uo) {
            super(uo);
            ((UserObject)userObject).setTreeNode(this);
        }

        @Override
        public void setUserObject(Object userObject) {
            super.setUserObject(userObject);
            ((UserObject)userObject).setTreeNode(this);
        }
    }

    public class RadioUserObject extends CheckboxUserObject {

        public RadioUserObject(String label, String configOptionId, Object configOptionValue) {
            super(label, configOptionId, configOptionValue);
        }
        
    }
}
