/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.event.ChangeEvent;
import javax.swing.event.CellEditorListener;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.mvc.ListenerSupport;
import org.tmt.fovast.gui.FovastInstrumentTree.*;


/**
 *
 */
public class FovastInstrumentTreeCellEditor implements TreeCellEditor {

    private static Logger logger = LoggerFactory.getLogger(FovastInstrumentTreeCellEditor.class);
    
    private JTree tree;

    private FovastInstrumentTreeCellRenderer renderer;

    private FovastInstrumentTreeCellRenderer delegate;

    private FovastInstrumentTree.UserObject currentNodeValue = null;

    private ListenerSupport<CellEditorListener> listenerSupport =
            new ListenerSupport<CellEditorListener>();

    private ChangeEvent changeEvent;

    public FovastInstrumentTreeCellEditor(JTree tree,
            FovastInstrumentTreeCellRenderer renderer) {
        this.tree = tree;
        this.renderer = renderer;
        this.delegate = new FovastInstrumentTreeCellRenderer(tree);
        final JCheckBox checkbox = this.delegate.getCheckbox();
        checkbox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UserObject.Editable editable = (UserObject.Editable) currentNodeValue;
                    editable.setSelected(checkbox.isSelected());
                    //}
                } catch (Exception ex) {
                    logger.error(null, ex);
                }
                FovastInstrumentTreeCellEditor.this.stopCellEditing();
            }
            
        });
    }

    public Component getTreeCellEditorComponent(JTree tree, Object value,
            boolean isSelected, boolean expanded, boolean leaf, int row) {
        //return delegate.getTreeCellRendererComponent(tree, value, isSelected, expanded,
        //        leaf, row, false);
        UserObject userObj = (UserObject)((DefaultMutableTreeNode) value).getUserObject();        
        
        if(userObj != null) {
            UserObject.Editable editable = (UserObject.Editable) userObj;
            logger.debug("Read value from user object with label "
                    + userObj.getLabel() + " is " + editable.isSelected());
            currentNodeValue = (UserObject) userObj.clone();
        } else {
            assert false : "there is no userobject associated with the tree node";
        }

        return delegate.getTreeCellRendererComponent(
                tree, value, isSelected, expanded, leaf, row, false);
        
    }

    public Object getCellEditorValue() {
        return currentNodeValue;
    }

    public boolean isCellEditable(EventObject event) {
        JTree tree = (JTree) event.getSource();
        //We only allow edit on mouse events
        if (event instanceof MouseEvent) {
            TreePath path = tree.getPathForLocation(
                    ((MouseEvent) event).getX(),
                    ((MouseEvent) event).getY());
            if (path != null) {
                DefaultMutableTreeNode value = (DefaultMutableTreeNode)
                        path.getLastPathComponent();
                UserObject uo = (UserObject) value.getUserObject();
                return uo instanceof UserObject.Editable;
            }
        }

        return false;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }

    public void cancelCellEditing() {
        fireEditingCancelled();
    }

    public void addCellEditorListener(CellEditorListener l) {
        listenerSupport.addListener(l);
    }

    public void removeCellEditorListener(CellEditorListener l) {
        listenerSupport.removeListener(l);
    }

    private void fireEditingStopped() {
        for (int i=0; i<listenerSupport.getListeners().size(); i++) {
            if(changeEvent == null)
                changeEvent = new ChangeEvent(this);
            listenerSupport.getListeners().get(i).editingStopped(changeEvent);
        }
    }

    private void fireEditingCancelled() {
        for (int i=0; i<listenerSupport.getListeners().size(); i++) {
            if(changeEvent == null)
                changeEvent = new ChangeEvent(this);
            listenerSupport.getListeners().get(i).editingCanceled(changeEvent);
        }
    }


}