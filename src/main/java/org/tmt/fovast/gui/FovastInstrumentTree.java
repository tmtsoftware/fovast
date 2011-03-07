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
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.JTree;
import javax.swing.tree.*;
import java.net.URL;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.instrumentconfig.BooleanValue;
import org.tmt.fovast.instrumentconfig.ConfigHelper;
import org.tmt.fovast.instrumentconfig.Config.ConfigListener;
import org.tmt.fovast.instrumentconfig.Value;

/**
 * <p>Handles showing a JTree from InstrumentTree.xml. On actions on the JTree, it
 * would invoke functions on {@link FovastConfigHelper}. This class also listens
 * to changes in instrument config by implementing ConfigListener.</p>
 *
 */
public class FovastInstrumentTree implements ConfigListener, TreeModelListener {

    private static Logger logger = LoggerFactory.getLogger(FovastInstrumentTree.class);

    private final static String NODE_NAME = "Node";

    private final static String CHECKBOXNODE_NAME = "CheckboxNode";

    private final static String CHECKBOXGROUPNODE_NAME = "CheckboxGroupNode";

    private final static String LABEL_ATTRIBUTE = "label";

    private final static String CONFIGOPTIONID_ATTRIBUTE = "configOptionId";

    private final static String CONFIGOPTIONVALUE_LABEL_ATTRIBUTE = "configOptionValue";

    private final static String INSTRUMENT_TREE_XML = "resources/InstrumentTree.xml";    

    private ConfigHelper configHelper;

    private JTree tree;
    
    private DefaultTreeModel treeModel;

    public FovastInstrumentTree(ConfigHelper configHelper) throws SomeException{
        try {
            this.configHelper = configHelper;
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
    public void updateConfig(String confElementId, Value value) {
        selectNode((CustomDefaultMutableTreeNode)treeModel.getRoot(), confElementId, value);
    }

    @Override
    public void batchUpdateConfig(ArrayList<String> confElementIds, ArrayList<Value> values) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        treeModel.addTreeModelListener(this);

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
        tree.setCellEditor(new FovastInstrumentTreeCellEditor(tree, renderer));
        
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
            } else if (name.equals(CHECKBOXNODE_NAME)) {
                String configOptionValue =
                        child.getAttributeValue(CONFIGOPTIONVALUE_LABEL_ATTRIBUTE);
                Value value = null;
                //TODO: We should also support other value types
                //TODO: Adding a static method in Value class to create a
                //proper object given a string representation would help. 
                if(configOptionValue == null) {
                    value = new BooleanValue(false);
                } else {
                    //TODO: take a call on this method
                    //this is needed  assuming all types of values (string, position..)
                    //have a string form.
                    //Alternately complex value objects can be put in InstrumentTree.xml by
                    //allowing the CheckboxUserGroupObject to have a <Value> child
                    //(like the one in InstrumentConfig.xml)
                    value = Value.createValueFromStringForm(configOptionValue);
                }

                uo = new CheckboxUserObject(label,
                        child.getAttributeValue(CONFIGOPTIONID_ATTRIBUTE), value);
            } else if (name.equals(CHECKBOXGROUPNODE_NAME)) {
                uo = new CheckboxGroupUserObject(label);
            } else {
                assert false : "Unknown element type: " + name;
            }

            CustomDefaultMutableTreeNode tNode = new CustomDefaultMutableTreeNode(uo);
            treeNode.add(tNode);

            if(treeNode.getChildCount() > 0) {
                makeTreeNodes(child, tNode);
            }
        }
    }

    private synchronized void selectNode(CustomDefaultMutableTreeNode node, String confElementId, Value value) {
        UserObject uo = (UserObject) node.getUserObject();
        //if value is not boolean
        if(uo instanceof CheckboxUserObject) {
            CheckboxUserObject cuo = (CheckboxUserObject)uo;
            if(cuo.getConfigOptionId().equals(confElementId) &&
                    cuo.getConfigOptionValue().equals(value)) {
                //if not already selected select it and adjust the representation
                if(!cuo.isSelected()) {
                    cuo.setSelected(true);
                    treeModel.nodeChanged(node);                    
                }
                return;
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
            }
        }
    }

    // TREE LISTENER METHODS START =====================
    private static boolean inTreeNodesChanged = false;
    ArrayList<TreeNode> nodesToFireChange = new ArrayList<TreeNode>();
    
    @Override
    public synchronized void treeNodesChanged(TreeModelEvent e) {

        Object[] nodesChanged = e.getChildren();
        for(int i=0; i<nodesChanged.length; i++) {
            CustomDefaultMutableTreeNode tNode =
                    (CustomDefaultMutableTreeNode) nodesChanged[i];
            UserObject uo = (UserObject) tNode.getUserObject();           
            
            if (uo instanceof CheckboxGroupUserObject) {
                boolean b = ((CheckboxGroupUserObject)uo).isSelected();
                //also set child nodes to have been modified
                for(int j=0; j<uo.getTreeNode().getChildCount(); j++) {
                    CustomDefaultMutableTreeNode childTNode =
                            (CustomDefaultMutableTreeNode) uo.getTreeNode().getChildAt(j);
                    UserObject childUserObject = (UserObject) childTNode.getUserObject();
                    if(childUserObject instanceof UserObject.Editable) {
                        if( b != ((UserObject.Editable)childUserObject).isSelected()) {
                            ((UserObject.Editable)childUserObject).setSelected(b);
                            //nodesToFireChange.add(childTNode);
                            treeModel.nodeChanged(childTNode);
                        }
                    }
                }
            }

            //setSelectedCheckboxGroupNodeTraversingUp(tNode);

        }
    }

    
    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
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

        private String label;
        private UserObject parentUo;
        private CustomDefaultMutableTreeNode associatedTreeNode;

        public UserObject(String label) {
            this.label = label;
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

        private String configOptionId;

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
            super(label);
            this.configOptionId = configOptionId;
            this.configOptionValue = configOptionValue;
        }
        
        public String getConfigOptionId() {
            return configOptionId;
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

}
