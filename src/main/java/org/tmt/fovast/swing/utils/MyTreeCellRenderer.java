/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tmt.fovast.swing.utils;

import java.awt.Color;
import java.awt.Component;
import java.util.EventObject;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author vivekananda_moosani
 */
//public class MyTreeCellRenderer extends DefaultTreeCellRenderer {
//
//    JCheckBox checkbox = new JCheckBox();
//
//    public Component getTreeCellRendererComponent(JTree tree, Object value,
//        boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
//        JLabel label =
//                (JLabel)super.getTreeCellRendererComponent(tree, value, leaf, expanded, leaf,
//                row, hasFocus);
//        if(((DefaultMutableTreeNode)value).getUserObject() instanceof Boolean) {
//            //label.remove(checkbox);
//            if(value.equals(Boolean.TRUE))
//                checkbox.setSelected(true);
//            else
//                checkbox.setSelected(false);
//            label.add(checkbox);
//            label.validate();
//        }
//
//        return label;
//    }
//
//}




public class MyTreeCellRenderer implements TreeCellRenderer, TreeCellEditor {

    private final JTree tree;
    
    private DefaultTreeCellRenderer defaultTreeCellRenderer =
            new DefaultTreeCellRenderer();
    private DefaultTreeCellEditor defaultTreeCellEditor;

    private JCheckBox checkbox = new JCheckBox();
    private JPanel panel = new JPanel();
    private JLabel label =  new JLabel();



    public MyTreeCellRenderer(JTree tree) {
        this.tree = tree;
        defaultTreeCellEditor = new DefaultTreeCellEditor(tree,
                defaultTreeCellRenderer);
        panel.setBackground(defaultTreeCellRenderer.getBackground());
        label.setBackground(defaultTreeCellRenderer.getBackground());
    }


    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        if(value instanceof DefaultMutableTreeNode) {
            panel.remove(checkbox);
            panel.add(label);

            Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
            if(userObject != null) {
                label.setText(userObject.toString());

                if(userObject instanceof Boolean) {
                    //label.remove(checkbox);
                    if(value.equals(Boolean.TRUE))
                        checkbox.setSelected(true);
                    else
                        checkbox.setSelected(false);
                    panel.add(checkbox);
                }
            } else {
                label.setText("");
            }

            return panel;
        } else {
            return defaultTreeCellRenderer.getTreeCellRendererComponent(
                    tree, value, selected, expanded, leaf, row, hasFocus);
        }
    }

    public Component getTreeCellEditorComponent(JTree tree, Object value,
            boolean isSelected, boolean expanded, boolean leaf, int row) {
        if(value instanceof DefaultTreeCellEditor) {
            return getTreeCellRendererComponent(tree, value, isSelected, expanded,
                    leaf, row, false);
        } else {
            return defaultTreeCellEditor.getTreeCellEditorComponent(tree, value,
                    isSelected, expanded, leaf, row);
        }
    }

    public Object getCellEditorValue() {
        return true;
    }

    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    public boolean stopCellEditing() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void cancelCellEditing() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addCellEditorListener(CellEditorListener l) {
        
    }

    public void removeCellEditorListener(CellEditorListener l) {
        
    }

}