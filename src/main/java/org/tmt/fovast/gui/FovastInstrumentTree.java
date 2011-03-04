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
import javax.swing.JTree;
import javax.swing.tree.*;
import java.net.URL;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.instrumentconfig.FovastConfigHelper;
import org.tmt.fovast.instrumentconfig.FovastConfigHelper.ConfigListener;
import org.tmt.fovast.instrumentconfig.Value;

/**
 * <p>Handles showing a JTree from InstrumentTree.xml. On actions on the JTree, it
 * would invoke functions on {@link FovastConfigHelper}. This class also listens
 * to changes in instrument config by implementing ConfigListener.</p>
 *
 */
public class FovastInstrumentTree implements ConfigListener {

    private static Logger logger = LoggerFactory.getLogger(FovastInstrumentTree.class);

    private final static String NODE_NAME = "Node";

    private final static String CHECKBOXNODE_NAME = "CheckboxNode";

    private final static String CHECKBOXGROUPNODE_NAME = "CheckboxGroupNode";

    private final static String LABEL_ATTRIBUTE = "label";

    private final static String CONFIGOPTIONID_ATTRIBUTE = "configOptionId";

    private final static String CONFIGOPTIONVALUE_LABEL_ATTRIBUTE = "configOptionValue";

    private final static String INSTRUMENT_TREE_XML = "resources/InstrumentTree.xml";    

    private FovastConfigHelper configHelper;

    private JTree tree; 

    public FovastInstrumentTree(FovastConfigHelper configHelper) throws SomeException{
        try {
            this.configHelper = configHelper;
            //this methods makes a JTree from the xml file and also
            //sets the state of the tree according to current config state.
            loadAndInitializeInstrumentTree();
        } catch(Exception ex) {
            throw new SomeException(ex);
        }
    }

    @Override
    public boolean updateConfig(String confElementId, Value value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean batchUpdateConfig(ArrayList<String> confElementIds, ArrayList<Value> values) {
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
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Instrument Config");
        rootNode.setUserObject(new GroupUserObject("root"));
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
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
     *
     * @param documentElement
     * @param treeNode
     */
    private void makeTreeNodes(Element documentElement, DefaultMutableTreeNode treeNode) {
        List<Element> children = documentElement.getChildren();
        for(int i=0; i<children.size(); i++) {
            Element child = children.get(i);
            String name = child.getName();
            String label = child.getAttributeValue(LABEL_ATTRIBUTE);
            GroupUserObject parentUo = (GroupUserObject)treeNode.getUserObject();
            UserObject uo = null;
            if(name.equals(NODE_NAME)) {
                uo = new GroupUserObject(label);
            } else if (name.equals(CHECKBOXNODE_NAME)) {
                uo = new CheckboxUserObject(label,
                        child.getAttributeValue(CONFIGOPTIONID_ATTRIBUTE),
                        child.getAttributeValue(CONFIGOPTIONVALUE_LABEL_ATTRIBUTE));
            } else if (name.equals(CHECKBOXGROUPNODE_NAME)) {
                uo = new CheckboxGroupUserObject(label);
            } else {
                assert false : "Unknown element type: " + name;
            }
            parentUo.addChild(uo);
            
            DefaultMutableTreeNode tNode = new DefaultMutableTreeNode(uo);
            treeNode.add(tNode);

            if(treeNode.getChildCount() > 0) {
                makeTreeNodes(child, tNode);
            }
        }
    }

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
     */
    static abstract class UserObject implements Cloneable {

        private String label;

        public UserObject(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public abstract Object clone();


        public static interface Editable {
            public void setSelected(boolean b);

            public boolean isSelected();
        }
    }

    /**
     * UserObject which has children
     */
    static class GroupUserObject extends UserObject {

        protected ArrayList<UserObject> children = new ArrayList<UserObject>();

        public GroupUserObject(String label) {
            super(label);
        }

        public void addChild(UserObject uo) {
            children.add(uo);
        }

        public int getChildCount() {
            return children.size();
        }

        public UserObject getChild(int index) {
            return children.get(index);
        }

        @Override
        public Object clone() {
            //NOTE: Make sure all subtypes of UserObject do implement this method
            //properly
            //TODO: this does a shallow copy 
            GroupUserObject clonedObj = new GroupUserObject(getLabel());
            clonedObj.children = (ArrayList<UserObject>) children.clone();
            return clonedObj;
        }

    }
    
    static class CheckboxUserObject extends GroupUserObject 
            implements UserObject.Editable {

        private String configOptionId;

        private Object configOptionValue;

        private boolean selected;

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
        public Object clone() {
            CheckboxUserObject newCuo = new CheckboxUserObject(getLabel(),
                    getConfigOptionId(), getConfigOptionValue());
            newCuo.children = (ArrayList<UserObject>) children.clone();
            return newCuo;
        }

    }

    static class CheckboxGroupUserObject extends GroupUserObject
        implements UserObject.Editable {

        public CheckboxGroupUserObject(String label) {
            super(label);
        }

        @Override
        public void setSelected(boolean b) {
            for(int i=0; i<children.size(); i++) {
                UserObject child = children.get(i);
                if(child instanceof CheckboxUserObject) {
                    ((CheckboxUserObject)child).setSelected(b);
                } else if(child instanceof CheckboxGroupUserObject) {
                    ((CheckboxGroupUserObject)child).setSelected(b);
                }
            }
        }

        @Override
        public boolean isSelected() {
            for(int i=0; i<children.size(); i++) {
                UserObject child = children.get(i);
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
        public Object clone() {
            CheckboxGroupUserObject newCguo = new CheckboxGroupUserObject(
                    getLabel());
            newCguo.children = (ArrayList<UserObject>) children.clone();
            return newCguo;
        }

    }

}
