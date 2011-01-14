/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.swing.utils;

import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author vivekananda_moosani
 */
public class StatusBar extends JPanel {

    //private final Dimension minDimension = new Dimension(50, 100);
    private JLabel centerStatusLabel;

    public StatusBar() {

        //TODO: UI settings should be from props file
        setBorder(BorderFactory.createEtchedBorder());
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        centerStatusLabel = new JLabel(" ");
        Font oldFont = centerStatusLabel.getFont();
        centerStatusLabel.setFont(
                new Font(oldFont.getName(), Font.PLAIN, oldFont.getSize() - 1));
        this.add(centerStatusLabel);

        //setMinimumSize(minDimension);
    }

    public void setText(final String text) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                centerStatusLabel.setText(text);
            }

        });
    }

    public void clearText() {
        centerStatusLabel.setText(" ");
    }

}
