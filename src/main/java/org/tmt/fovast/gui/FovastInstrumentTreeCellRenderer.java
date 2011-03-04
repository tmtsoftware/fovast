/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tmt.fovast.gui;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import org.tmt.fovast.gui.FovastInstrumentTree.*;

/**
 * <p>Custom TreeCellRenderer for FOVAST instrument config panel.</p>
 *
 * <p>There are no getters and setters for UI related stuff. Background color
 * used is that of the JTree component attached to the cell renderer.</p>
 *
 * 
 * @author vivekananda_moosani
 */
public class FovastInstrumentTreeCellRenderer implements TreeCellRenderer {

    private final JTree tree;
    
    private JCheckBox checkbox = new JCheckBox();
    private JPanel panel = new JPanel();
    private JLabel label =  new JLabel();



    public FovastInstrumentTreeCellRenderer(JTree tree) {
        this.tree = tree;
        panel.setOpaque(true);
        label.setOpaque(true);
    }


    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        panel.remove(checkbox);
        panel.remove(label);

        boolean enable = tree.isEnabled();
        panel.setEnabled(enable);
        label.setEnabled(enable);
        checkbox.setEnabled(enable);

        checkbox.setFocusPainted(hasFocus);

        panel.setBackground(tree.getBackground());        
        label.setBackground(tree.getBackground());


        //We know the value is of type DefaultMutableTreeNode
        UserObject userObj = (UserObject)((DefaultMutableTreeNode)
                                        value).getUserObject();
        if(userObj != null) {
            //go from more specific to less specific in ifelses
            if(userObj instanceof CheckboxUserObject) {
                CheckboxUserObject cuo = (CheckboxUserObject) userObj;
                checkbox.setText(userObj.getLabel());
                checkbox.setSelected(cuo.isSelected());
                panel.add(checkbox);
            } else if(userObj instanceof FovastInstrumentTree.CheckboxGroupUserObject) {
                CheckboxGroupUserObject cguo = (CheckboxGroupUserObject) userObj;
                checkbox.setText(userObj.getLabel());
                checkbox.setSelected(cguo.isSelected());
                panel.add(checkbox);
            } else {
                label.setText(userObj.getLabel());
                panel.add(label);
            }
            
        } else {
            assert false : "there is no userobject associated with the tree node";
        }
        
        return panel;

    }

    JCheckBox getCheckbox() {
        return checkbox;
    }

}