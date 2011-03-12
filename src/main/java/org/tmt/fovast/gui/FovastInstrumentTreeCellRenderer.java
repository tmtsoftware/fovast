/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tmt.fovast.gui;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.gui.FovastInstrumentTree.*;
import org.tmt.fovast.gui.FovastInstrumentTree.UserObject.Editable;

/**
 * <p>Custom TreeCellRenderer for FOVAST instrument config panel.</p>
 *
 * <p>There are no getters and setters for UI related stuff. Background color
 * used is that of the JTree component attached to the cell renderer.</p>
 *
 * 
 */
public class FovastInstrumentTreeCellRenderer implements TreeCellRenderer {

    private static final Logger logger = LoggerFactory.getLogger(FovastInstrumentTreeCellRenderer.class);
    
    private final JTree tree;
    
    private JCheckBox checkbox = new JCheckBox();
    private JRadioButton radio = new JRadioButton();
    private JPanel panel = new JPanel();
    private JLabel label =  new JLabel();



    public FovastInstrumentTreeCellRenderer(JTree tree) {
        this.tree = tree;
        panel.setOpaque(false);
        label.setOpaque(false);
        checkbox.setOpaque(false);
        radio.setOpaque(false);
    }


    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        panel.remove(checkbox);
        panel.remove(radio);
        panel.remove(label);
        checkbox.setSelected(false);
        radio.setSelected(false);

        

        //checkbox.setFocusPainted(hasFocus);

        panel.setBackground(tree.getBackground());        
        label.setBackground(tree.getBackground());

        //We know the value is of type DefaultMutableTreeNode
        UserObject userObj = (UserObject)((DefaultMutableTreeNode)
                                        value).getUserObject();

        boolean enable = tree.isEnabled() && !userObj.isDisabled();
        panel.setEnabled(enable);
        label.setEnabled(enable);
        checkbox.setEnabled(enable);
        radio.setEnabled(enable);



        if(userObj != null) {
            //go from more specific to less specific in ifelses
            if(userObj instanceof Editable) {
                if(userObj instanceof RadioUserObject) {
                    radio.setText(userObj.getLabel());
                    radio.setSelected(((Editable)userObj).isSelected());
                    panel.add(radio);
                } else {
                    checkbox.setText(userObj.getLabel());
                    checkbox.setSelected(((Editable)userObj).isSelected());
                    panel.add(checkbox);
                }
                logger.trace("userObj: " + userObj.getLabel() + " being shown, selected: "
                        + ((Editable)userObj).isSelected());
                
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

    JRadioButton getRadioButton() {
        return radio;
    }

}