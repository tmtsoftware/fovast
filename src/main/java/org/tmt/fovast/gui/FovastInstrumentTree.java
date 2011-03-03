/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import javax.swing.JComponent;
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
import org.tmt.fovast.swing.utils.MyTreeCellRenderer;

/**
 * <p>Handles showing a JTree from InstrumentTree.xml. On actions on the JTree, it
 * would invoke functions on {@link FovastConfigHelper}. This class also listens
 * to changes in instrument config by implementing ConfigListener.</p>
 *
 */
public class FovastInstrumentTree implements ConfigListener {

    private static Logger logger = LoggerFactory.getLogger(FovastInstrumentTree.class);

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
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        makeTreeNodes(rootElement, rootNode);

        //create tree
        tree = new JTree(treeModel);

        //tree settings
        tree.setRootVisible(false);
        tree.setEditable(true);
        //tree.setExpandsSelectedPaths(true);
        tree.setCellRenderer(new MyTreeCellRenderer(tree));
        tree.setCellEditor(new MyTreeCellRenderer(tree));
        
        //show tree fully expanded
        for( int i = 0; i < tree.getRowCount(); ++i )
        {
          tree.expandRow(i);
        }

        //TODO:
        //1. show proper rendering components
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
            DefaultMutableTreeNode tNode = new DefaultMutableTreeNode(child);
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
}
